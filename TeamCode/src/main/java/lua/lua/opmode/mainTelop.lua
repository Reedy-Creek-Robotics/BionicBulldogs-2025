require("modules.hdrive");
require("modules.telemPanes");
require("modules.intake");
require("modules.shooter");
require("modules.counter");

---@type HDrive
local drive;

---@type DcMotorEx
local turretMotor = {};

---@type vec3
local startPos;

---@type boolean
local shooterAutomatic = true;

---@type integer
local turretOffset = 0;
local turretOffset2 = 0;

local fileId = os.time();

---@type Imu
local imu;

local logInterval = 0;
local logTimer = 5;
local logVel = false;

local prevLedState = 0;

local function telopInit()
	led = hardwareMap.ledGet();
	led:displayArtBoard(0);
	counter:init();
	imu = hardwareMap.imuGet();
	chub = hardwareMap.chubGet();
	limelight = hardwareMap.limelightGet();
	logFile = io.open(DATADIR .. "telop" .. tostring(fileId), "w");
	logFile2 = io.open(DATADIR .. "telop-turret" .. tostring(fileId), "w");
	require("modules.telemetry");

	if (logFile == nil) then
		error("failed to open log file");
	end
	if (logFile2 == nil) then
		error("failed to open log file 2");
	end

	logFile2:write("time, x, y, h, offset, turretAngle\n");

	actionPane:addData("telop file", fileId);

	drive = HDrive.new();
	drive.localizerMode = LocalizerMode.pinpoint;
	drive.pinpoint = hardwareMap.pinpointGet();

	if (save.containsb("resetTurret")) then
		actionPane:addLine("values from auto found");
		local x = save.loadd("x");
		local y = save.loadd("y");
		local h = save.loadd("h");

		startPos = { x = x, y = y, z = h };

		local resetTurret = save.loadb("resetTurret");
		turret.init(resetTurret);
	else
		actionPane:addLine("values from auto not found, reseting");
		turret.init(true);
	end

	turretMotor = turret.getMotor();
	--aprilTagProcessor.init(1280, 720, 2, 255, 1.0)
	--aprilTagProcessor.init(640, 480, 2, 255, 1.0)

	intake:init();
	shooter:init();

	--local p = dashboard.getp();
	--local i = dashboard.geti();
	--local d = dashboard.getd();
	--local f = dashboard.getf();
	local p = 640;
	local i = 3;
	local d = 0;
	local f = 0;
	--local pid = newPIDF(p, i, d, f);
	shooter.motorL:setPidf(p, i, d, f);
	shooter.motorR:setPidf(p, i, d, f);
end

---@type vec3
local initPos = nil;

---@type vec2
local targetPos = nil;
---@type vec2
local targetPos2 = nil;

local function telopStartBlue()
	drive.offset = math.pi2;
	turret.start();
	shooter:close();
	initPos = { x = 144 - 7.5, y = 9, z = math.pi2 };
	--targetPos = { x = 7, y = 138 };
	targetPos = { x = 0, y = 144 };
	targetPos2 = { x = 0, y = 140 };
	imu:resetHeading();

	if (startPos == nil) then
		startPos = initPos;
	end

	drive.pinpoint:setPosX(startPos.x);
	drive.pinpoint:setPosY(startPos.y);
	drive.pinpoint:setHeading(startPos.z);
	shooter:start(shooter.vel);
	--turret.reset();
	led:displayArtBoard(1);
end

local function telopStartRed()
	drive.offset = -math.pi2;
	turret.start();
	shooter:close();
	initPos = { x = -(144 - 7.5), y = 9, z = math.pi2 };
	--targetPos = { x = -7, y = 138 };
	targetPos = { x = 0, y = 144 };
	targetPos2 = { x = 0, y = 140 };
	imu:resetHeading();

	if (startPos == nil) then
		startPos = initPos;
	end

	drive.pinpoint:setPosX(startPos.x);
	drive.pinpoint:setPosY(startPos.y);
	drive.pinpoint:setHeading(startPos.z);
	--turret.reset();
	led:displayArtBoard(1);
end

local function telopUpdate(dt, et)
	drive.pinpoint:update();

	local x = drive.pinpoint:getX();
	local y = drive.pinpoint:getY();
	local h = drive.pinpoint:getHeading();
	local h2 = imu:getHeading();

	--Drive the bot
	local forward = gamepad.getLeftStickY();
	local right = gamepad.getLeftStickX();
	local rotate = gamepad.getRightStickX();
	drive:driveFr(forward, right, rotate);

	--Obtain the blue goal april tag
	--local bTag = aprilTagProcessor.getTag(20)
	--local dist = 0;

	--if (bTag:valid()) then
	--	dist = bTag:getDist()
	--end

	local dx = 0;
	local dy = 0;

	if (y > 120) then
		dx = targetPos2.x - x;
		dy = targetPos2.y - y;
	else
		dx = targetPos.x - x;
		dy = targetPos.y - y;
	end

	local angle = math.atan(dy, dx) - h;
	angle = math.deg(angle);
	if (angle > 180) then
		angle = angle - 360;
	end
	if (angle < -180) then
		angle = angle + 360;
	end
	if (y < 24) then
		if (x > 0) then
			angle = angle - 2;
		else
			angle = angle + 2;
		end
	end

	local vx = drive.pinpoint:getVelX();
	local vy = drive.pinpoint:getVelY();
	local vh = math.deg(drive.pinpoint:getVelH());

	local len = dx * dx + dy * dy;
	local da = math.deg(((dx * vy) - (dy * vx)) / (dx * dx + dy * dy));
	if (len < 20 * 20) then
		da = 0;
	end

	angle = angle + turretOffset + turretOffset2 - da * (4 / 8) - vh * (5 / 32);

	if (angle > 180) then
		angle = angle - 360;
	end
	if (angle < -180) then
		angle = angle + 360;
	end

	turret.update(angle);
	dashboard.addDataf("angle deriv", da);
	--turret.update(0);

	--Forward/stop intake
	if (gamepad.getRightBumper2()) then
		if (intake.state == IntakeState.Forward) then
			intake:stop();
		else
			counter:reset();
			intake:forward();
		end
	end

	--Reverse/stop intake
	if (gamepad.getLeftBumper2()) then
		if (intake.state == IntakeState.Reverse) then
			intake:stop();
		else
			--counter:reset();
			intake:reverse();
		end
	end

	if (gamepad.getDpadUp2()) then
		turretOffset = turretOffset + 1;
		logFile:write(("%7.2f | x: %6.2f, y: %6.2f, h: %6.4f, angle: %6.2f, offset: %f\n"):format(et, x, y, h, angle,
			turretOffset));
	end
	if (gamepad.getDpadDown2()) then
		turretOffset = turretOffset - 1;
		logFile:write(("%7.2f | x: %6.2f, y: %6.2f, h: %6.4f, angle: %6.2f, offset: %f\n"):format(et, x, y, h, angle,
			turretOffset));
	end

	--if (gamepad.getTouchpad2()) then
	--	shooter:reverse(et);
	--end

	if (gamepad.getShare2()) then
		turret.reset();
	end

	if (gamepad.getDpadLeft2()) then
		shooterAutomatic = false;
		--shooter.vel = shooterVelocity[1];
		--shooter:start(shooter.vel);
	end
	if (gamepad.getDpadRight2()) then
		shooterAutomatic = false;
		--shooter.vel = shooterVelocity[2];
		--shooter:start(shooter.vel);
	end

	--Run/don't run specifically the shooter
	if (gamepad.getCircle2()) then
		shooter:start(shooter.vel);
		shooterAutomatic = true;
		shooter.running = true;
	end
	if (gamepad.getTriangle2()) then
		shooter:stop();
		shooter.running = false;
	end

	--Start intake and shooter
	if (gamepad.getCross2()) then
		if (y < 48) then
			limelight:update();
			local tag = limelight:getTag(24);
			if (tag ~= nil) then
				turretOffset2 = turretOffset2 - tag:tx() - 3;
			else
				turretOffset2 = turretOffset2 - 6;
			end
		end
		counter.count = 5;
		intake:forward();
		logVel = true;
		shooter:shootNum(et, 1);
	end

	if (gamepad.getTouchpad2()) then
		limelight:update();
		local tag = limelight:getTag(24);
		if (tag ~= nil) then
			turretOffset2 = turretOffset2 - tag:tx() - 3;
		end
	end

	local ledState = 1 + counter.count;

	if (ledState ~= prevLedState) then
		led:displayArtBoard(ledState);
		prevLedState = ledState;
	end

	shooter:updateVelocity(x, y, drive.pinpoint:getVelX(), drive.pinpoint:getVelY());

	if (gamepad.getStart()) then
		drive.pinpoint:setPosX(initPos.x);
		drive.pinpoint:setPosY(initPos.y);
		drive.pinpoint:setHeading(initPos.z);
	end

	--aprilTagProcessor.update();

	if (logInterval > logTimer) then
		logInterval = 0;
		logFile:write(("%7.2f | fl: %5.2f, fr: %5.2f, bl: %5.2f, br: %5.2f, in: %5.2f, sl: %5.2f, sr: %5.2f, tu: %5.2f\n")
			:format(et,
				drive.frontLeft:getCurrent(),
				drive.frontRight:getCurrent(),
				drive.backLeft:getCurrent(),
				drive.backRight:getCurrent(),
				intake.motor:getCurrent(),
				shooter.motorL:getCurrent(),
				shooter.motorR:getCurrent(),
				turretMotor:getCurrent()
			));
	end
	logInterval = logInterval + 1;

	if (logVel) then
		logFile:write(("%7.2f | left vel: %4d, right vel: %4d\n"):format(et, shooter.motorL:getVelocity(),
			shooter.motorR:getVelocity()));
	end
	logFile2:write(("%7.2f, %6.2f, %6.2f, %6.2f, %f, %6.2f\n"):format(et, drive.pinpoint:getX(), drive.pinpoint:getY(),
		drive.pinpoint:getHeading(), turretOffset, angle));

	--Automatically updates
	if (shooter:update(et)) then
		logVel = false;
		counter:reset();
		if (y < 48) then
			turretOffset2 = turretOffset2 + 3;
		end
	end
	if(y > 48) then
		turretOffset2 = 0;
	elseif(turretOffset2 == 0) then
		turretOffset2 = 3;
	end

	local tps = 1 / dt;

	counter:update(et);
	--counter:updatLeds(et);

	if (logInterval > logTimer) then
		robotPane:addData("tps", tps);
		robotPane:addData("x", drive.pinpoint:getX());
		robotPane:addData("y", drive.pinpoint:getY());
		robotPane:addData("h", math.deg(h));
		robotPane:addData("curPos", turretMotor:getCurrentPosition());
		robotPane:addData("angle", angle + turretOffset);
		robotPane:addData("offset", turretOffset);
		robotPane:addData("ball count", counter.count);

		shooter:telem();

		robotPane:addData("setVel", shooter.vel);

		currentPane:addData("fl", drive.frontLeft:getCurrent());
		currentPane:addData("fr", drive.frontRight:getCurrent());
		currentPane:addData("bl", drive.backLeft:getCurrent());
		currentPane:addData("br", drive.backRight:getCurrent());
		currentPane:addData("sl", shooter.motorL:getCurrent());
		currentPane:addData("sr", shooter.motorR:getCurrent());
		currentPane:addData("in", intake.motor:getCurrent());
		currentPane:addData("tu", turretMotor:getCurrent());

		TelemPaneManager:update();
	end

	dashboard.addDataf("tps", tps);
	dashboard.addDataf("leftPower", shooter.motorL:getPower());
	dashboard.addDataf("leftVel", shooter.motorL:getVelocity());
	dashboard.addDataf("leftCur", shooter.motorL:getCurrent());
	dashboard.addDataf("rightPower", shooter.motorR:getPower());
	dashboard.addDataf("rightVel", shooter.motorR:getVelocity());
	dashboard.addDataf("rightCur", shooter.motorR:getCurrent());
	dashboard.addDataf("turretPos", turretMotor:getCurrentPosition());
	dashboard.addDataf("turretTargetPos", turretMotor:getTargetPosition());
	dashboard.update();

	return false;
end

local function telopStop()
	led:displayArtBoard(0);
	logFile:close();
	logFile2:close();
end

---@type Opmode
local telopRed = {
	name = "mainTelopRed",
	type = OpmodeType.Telop,
	init = telopInit,
	start = telopStartRed,
	update = telopUpdate,
	stop = telopStop
};

---@type Opmode
local telopBlue = {
	name = "mainTelopBlue",
	type = OpmodeType.Telop,
	init = telopInit,
	start = telopStartBlue,
	update = telopUpdate,
	stop = telopStop
};

addOpmode(telopRed);
addOpmode(telopBlue);