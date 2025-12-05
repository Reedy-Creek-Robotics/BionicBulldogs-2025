require("modules.hdrive");
require("modules.telemPanes");
require("modules.intake");
require("modules.shooter");

---@type Opmode
local opmode = { name = "t_mainTelop" };

---@type HDrive
local drive;

---@type number[]
local shooterVelocity = { 1200, 1300, 1600 }

---@type DcMotor
local turretMotor;

-- Find where I found my distance values here: https://www.desmos.com/calculator/u0kuzoiwb1
-- Distances = { 39.9530975019, 73.8935044507, 139.256956738 }
-- Regression function: 4.10351x+1020.46204

---@type number
local id = 1

--Id to string label for telemetry
---@type string[]
local shooterLabel = { "Close", "Moderate", "Far" }

function opmode.init()
	require("modules.telemetry");
	turretMotor = hardwareMap.dcmotorGet("turret");
	drive = HDrive.new();
	drive.imu = hardwareMap.imuGet();
	--aprilTagProcessor.init(1920, 1080, 2, 255, 1.0)

	intake:init();
	shooter:init();
end

function opmode.start()
	shooter:close();
	turretMotor:setMode(DcMotorRunMode.StopAndResetEncoder);
	turretMotor:setMode(DcMotorRunMode.RunWithoutEncoder);
	turretMotor:setTargetPosition(0);
	turretMotor:setMode(DcMotorRunMode.RunToPosition);
	turretMotor:setPower(1);
end

function opmode.update(dt, et)
	--Drive the bot
	local forward = -gamepad.getLeftStickY();
	local right = gamepad.getLeftStickX();
	local rotate = gamepad.getRightStickX();
	drive:driveFr(forward, right, rotate);

	--Obtain the blue goal april tag
	--local bTag = aprilTagProcessor.getTag(20)
	--local dist = 0;

	--if (bTag:valid()) then
	--	dist = bTag:getDist()
	--end

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
			intake:reverse()
		end
	end

	if (gamepad.getDpadUp2()) then
		if (id == 3) then
			id = 1;
		else
			id = id + 1;
		end
	end

	--Run/don't run specifically the shooter
	if (gamepad.getCircle2()) then
		shooter:start(shooterVelocity[id]);
	end
	if (gamepad.getTriangle2()) then
		shooter:stop();
	end

	--Start intake and shooter
	if (gamepad.getCross2()) then
		intake:forward();
		shooter:shoot(et);
	end

	--Automatically updates
	shooter:update(et);

	robotPane:addData("shooterCur", shooter.motor:getCurrent());
	robotPane:addData("shooterVel", shooter.motor:getVelocity());
	robotPane:addLine(shooterLabel[id]);
	robotPane:addData("setVel", shooterVelocity[id]);
	--if bTag:valid() then
	--	aprilTagPane:addData("tag distance", bTag:getDist())
	--	--positive error means tag is to the right, and vice versa
	--	aprilTagPane:addData("angle error", bTag:bearing())
	--else
	--	aprilTagPane:addLine("tag distance: -1")
	--	aprilTagPane:addLine("angle error: -1")
	--end
	TelemPaneManager:update();

	return false;
end

addOpmode(opmode);