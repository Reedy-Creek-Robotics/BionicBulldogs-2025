require("modules.hdrive");
require("modules.telemPanes");
require("modules.intake");
require("modules.shooter");

---@type HDrive
local drive;

---@type number[]
local shooterVelocity = { 900, 1120 }

---@type DcMotorEx
local turretMotor = {};

---@type vec3
local startPos;

---@type boolean
local shooterAutomatic = true;

---@type integer
local turretOffset = 0;

local fileId = os.time();

---@type Imu
local imu;

local logInterval = 0;
local logTimer = 5;

function telopInit()
	imu = hardwareMap.imuGet();
	chub = hardwareMap.chubGet();
	logFile = io.open(DATADIR .. "telop" .. tostring(fileId), "w");
	require("modules.telemetry");

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
	aprilTagProcessor.init(640, 480, 2, 255, 1.0)

	intake:init();
	shooter:init();

	--local p = dashboard.getp();
	--local i = dashboard.geti();
	--local d = dashboard.getd();
	--local f = dashboard.getf();
	--shooter.motorL:setPidf(p, i, d, f);
	--shooter.motorR:setPidf(p, i, d, f);
end

---@type vec3
local initPos = nil;

---@type vec2
local targetPos = nil;
---@type vec2
local targetPos2 = nil;

function telopStartBlue()
	drive.offset = math.pi2;
	turret.start();
	shooter:close();
	initPos = { x = 144 - 8.5, y = 8, z = math.pi2 };
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
	shooter.vel = shooterVelocity[1];
	shooter:start(shooter.vel);
	--turret.reset();
end

function telopStartRed()
	drive.offset = -math.pi2;
	turret.start();
	shooter:close();
	initPos = { x = -(144 - 8.5 / 2), y = 8, z = math.pi2 };
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
	shooter.vel = shooterVelocity[1];
	shooter:start(shooter.vel);
	--turret.reset();
end

function telopUpdate(dt, et)
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
	turret.update(angle + turretOffset);
	--turret.update(0);

	--Forward/stop intake
	if (gamepad.getRightBumper2()) then
		if (intake.state == IntakeState.Forward) then
			intake:stop();
		else
			intake:forward();
		end
	end

	--Reverse/stop intake
	if (gamepad.getLeftBumper2()) then
		if (intake.state == IntakeState.Reverse) then
			intake:stop();
		else
			intake:reverse();
		end
	end

	if (gamepad.getDpadUp2()) then
		turretOffset = turretOffset + 1;
		logFile:write(("%7.2f | x: %6.2f, y: %6.2f, h: %6.4f, angle: %6.2f, offset: %d\n"):format(et, x, y, h, angle,
			turretOffset));
	end
	if (gamepad.getDpadDown2()) then
		turretOffset = turretOffset - 1;
		logFile:write(("%7.2f | x: %6.2f, y: %6.2f, h: %6.4f, angle: %6.2f, offset: %d\n"):format(et, x, y, h, angle,
			turretOffset));
	end

	if (gamepad.getTouchpad2()) then
		shooter.vel = shooter.vel + 20;
		shooter:start(shooter.vel);
	end
	if (gamepad.getShare2()) then
		turret.reset();
		--shooter.vel = shooter.vel - 20;
		--shooter:start(shooter.vel);
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
		intake:forward();
		logTimer = -1;
		shooter:shootNum(et, 1);
	end

	if (gamepad.getSquare2()) then
		--local p = dashboard.getp();
		--local i = dashboard.geti();
		--local d = dashboard.getd();
		--local f = dashboard.getf();
		--shooter.motorL:setPidf(p, i, d, f);
		--shooter.motorR:setPidf(p, i, d, f);
		--turretMotor:setPidf(p, i, d, f);

		local id = 0;
		if(initPos.x > 0) then
			id = 20;
		else
			id = 24;
		end
		local tag = aprilTagProcessor.getTag(id);
		if (tag:valid()) then
			local pos = tag:robotPos();
			actionPane:addLine(("x: %6.2f, y: %6.2f, z: %6.2f"):format(pos:x(), pos:y(), pos:z()));
			actionPane:addLine(("pitch: %6.2f, yaw: %6.2f, roll: %6.2f"):format(pos:pitch(), pos:yaw(), pos:roll()));
			local pos2 = tag:ftcPos();
			actionPane:addLine(("x: %6.2f, y: %6.2f"):format(pos2:x(), pos2:y()));
			actionPane:addLine(("bearing: %6.2f, range: %6.2f"):format(pos2:bearing(), pos2:range()));
		else
			actionPane:addLine("tag no exist");
		end
	end
--if(gamepad.getTouchpad2()) then local tag = aprilTagProcessor.getTag(20);
	--	local pos = tag:ftcPos();
	--	pos:bearing();
	--end

	if (shooterAutomatic) then
		local dist = dx * dx + dy * dy;
		shooter:updateVelocity(x, y, 0);
	end

	if (gamepad.getStart()) then
		drive.pinpoint:setPosX(initPos.x);
		drive.pinpoint:setPosY(initPos.y);
		drive.pinpoint:setHeading(initPos.z);
	end

	aprilTagProcessor.update();

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

	--Automatically updates
	if(shooter:update(et)) then
		logTimer = 5;
	end

	local tps = 1 / dt;

	robotPane:addData("tps", tps);
	robotPane:addData("x", drive.pinpoint:getX());
	robotPane:addData("y", drive.pinpoint:getY());
	robotPane:addData("h", math.deg(h));
	--robotPane:addData("h2", math.deg(h2));
	robotPane:addData("tarPos", turretMotor:getTargetPosition());
	robotPane:addData("curPos", turretMotor:getCurrentPosition());
	robotPane:addData("angle", angle + turretOffset);
	robotPane:addData("offset", turretOffset);

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

function telopStop()
	logFile:close();
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