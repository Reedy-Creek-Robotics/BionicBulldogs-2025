require("modules.hdrive");
require("modules.telemPanes");

local ShooterOpen = 0.85;
local ShooterClosed = 1;

local shooterCurrentLimit = 2.25;

local shooterCurrentPower = 0;

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
local maxVelocity = 2100;

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

--Close shooter automatically after 3 secs
	if (shooterState == 1) then
		if (shooterTime + 3 <= et) then
			shooter:setPosition(ShooterClosed);
			shooterState = 0;
		end
	end

--Increment/decrement Shooter Power
	if (gamepad.getDpadUp2()) then
		shooterPower = shooterPower + 0.05;
	end
	if (gamepad.getDpadDown2()) then
		shooterPower = shooterPower - 0.05;
	end
--2240 | 2600 | 2100
--PIDF Shooter Acceleration to target RPM
	if (shooterRunning) then
		shooterMotor:setVelocity(maxVelocity * shooterCurrentPower)
	end

	robotPane:addData("shooterPwr", shooterPower);
	robotPane:addData("shooterPwr2", shooterCurrentPower);
	robotPane:addData("shooterCur", shooterMotor:getCurrent());
	robotPane:addData("shooterVel", shooterMotor:getVelocity());
	TelemPaneManager:update();

	return false;
end

addOpmode(opmode);