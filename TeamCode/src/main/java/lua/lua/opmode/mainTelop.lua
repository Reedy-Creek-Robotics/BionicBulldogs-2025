require("modules.hdrive");
require("modules.telemPanes");

local ShooterOpen = 0.85;
local ShooterClosed = 1;

local shooterCurrentLimit = 2.25;

local shooterCurrentPower = 0;

---@type number
local shooterTime = 0;

local shooterState = 0;
local shooterPower = 1;

local shooterRunning = false;

---@type Opmode
local opmode = { name = "mainTelop" };

---@type HDrive
local drive;

---@type Servo
local shooter;
---@type DcMotorEx
local shooterMotor;
---@type DcMotor
local intakeMotor;
---@type number
local maxVelocity = 2600;
---@type number[]
local shooterVelocity = {1200, 1300, 1600}
---@type number
local id = 1

--Id to string label for telemetry
---@type string[]
local shooterLabel = {"Close", "Moderate", "Far"}

---@enum IntakeState
IntakeState = {
	Forward = 1,
	Stopped = 0,
	Reverse = -1
}



---@type IntakeState
local intakeState = IntakeState.Stopped;


function opmode.init()
	require("modules.telemetry");
	drive = HDrive.new();
	drive.imu = hardwareMap.spimuGet();
	--aprilTagProcessor.init(1280, 720, 2, 255)

	shooter = hardwareMap.servoGet("transfer");
	shooterMotor = hardwareMap.dcmotorexGet("shooter");
	intakeMotor = hardwareMap.dcmotorGet("intake");
	shooterMotor:setMode(DcMotorRunMode.RunUsingEncoder);
	shooterMotor:setDirection(Direction.Reverse);
end

function opmode.start()
	shooter:setPosition(ShooterClosed);
end

function opmode.update(dt, et)
	--Drive the bot
	local forward = gamepad.getLeftStickY();
	local right = gamepad.getLeftStickX();
	local rotate = gamepad.getRightStickX();
	drive:driveFr(forward, right, rotate);

	--Sets velocity off of Dpad
	if (gamepad.getDpadDown2() and id ~= 1) then
		id = id - 1
	end

	if (gamepad.getDpadUp2() and id ~= 3) then
		id = id + 1
	end

	--Forward/stop intake
	if (gamepad.getRightBumper2()) then
		if (intakeState == IntakeState.Forward) then
			intakeMotor:setPower(IntakeState.Stopped);
			intakeState = IntakeState.Stopped;
		else
			intakeMotor:setPower(IntakeState.Forward);
			intakeState = IntakeState.Forward;
		end
	end

	--Reverse/stop intake
	if (gamepad.getLeftBumper2()) then
		if (intakeState == IntakeState.Reverse) then
			intakeMotor:setPower(IntakeState.Stopped);
			intakeState = IntakeState.Stopped;
		else
			intakeMotor:setPower(IntakeState.Reverse);
			intakeState = IntakeState.Reverse;
		end
	end

	--Run/don't run specifically the shooter
	if (gamepad.getCircle2()) then
		shooterCurrentPower = shooterPower;
		shooterMotor:setPower(shooterPower);
		shooterRunning = true;
	end
	if (gamepad.getTriangle2()) then
		shooterCurrentPower = 0;
		shooterMotor:setPower(0);
		shooterRunning = false;
	end

	--Start intake and shooter
	if (gamepad.getCross2()) then
		intakeState = IntakeState.Forward;
		intakeMotor:setPower(IntakeState.Forward);
		shooter:setPosition(ShooterOpen);
		shooterTime = et;
		shooterState = 1;
	end

	--Close shooter automatically after 0.2 secs
	if (shooterState == 1) then
		if (shooterTime + 0.2 <= et) then
			shooter:setPosition(ShooterClosed);
			shooterState = 0;
		end
	end

--Obtain the blue goal april tag
	--local bTag = aprilTagProcessor.getTag(20)

	--Calculate power to distance (const may be a function for regression)
	local const = 0.5
	--local dist = bTag:getDist * const
	local maxVelocity = 2600

	if (shooterRunning) then
		shooterMotor:setVelocity(shooterVelocity[id])
	end

	robotPane:addData("shooterPwr2", shooterCurrentPower);
	robotPane:addData("shooterCur", shooterMotor:getCurrent());
	robotPane:addData("shooterVel", shooterMotor:getVelocity());
	robotPane:addLine(shooterLabel[id]);
	robotPane:addData("setVel", shooterVelocity[id]);
	--if bTag:valid() then aprilTagPane:addData("tag distance", bTag:getDist()) else aprilTagPane:addLine("tag distance: -1") end
	TelemPaneManager:update();

	return false;
end

addOpmode(opmode);