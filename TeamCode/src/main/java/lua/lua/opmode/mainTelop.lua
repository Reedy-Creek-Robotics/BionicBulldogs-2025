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
end

function opmode.start()
	shooter:setPosition(ShooterClosed);
end

function opmode.update(dt, et)
	local forward = gamepad.getLeftStickY();
	local right = gamepad.getLeftStickX();
	local rotate = gamepad.getRightStickX();
	drive:driveFr(forward, right, rotate);

	if (gamepad.getRightBumper2()) then
		if (intakeState == IntakeState.Forward) then
			intakeMotor:setPower(IntakeState.Stopped);
			intakeState = IntakeState.Stopped;
		else
			intakeMotor:setPower(IntakeState.Forward);
			intakeState = IntakeState.Forward;
		end
	end

	if (gamepad.getLeftBumper2()) then
		if (intakeState == IntakeState.Reverse) then
			intakeMotor:setPower(IntakeState.Stopped);
			intakeState = IntakeState.Stopped;
		else
			intakeMotor:setPower(IntakeState.Reverse);
			intakeState = IntakeState.Reverse;
		end
	end

	if (gamepad.getCircle2()) then
		shooterCurrentPower = shooterPower;
		shooterMotor:setPower(-shooterPower);
		shooterRunning = true;
	end
	if (gamepad.getTriangle2()) then
		shooterCurrentPower = 0;
		shooterMotor:setPower(0);
		shooterRunning = false;
	end

	if (gamepad.getCross2()) then
		intakeState = IntakeState.Forward;
		intakeMotor:setPower(IntakeState.Forward);
		shooter:setPosition(ShooterOpen);
		shooterTime = et;
		shooterState = 1;
	end

	if (shooterState == 1) then
		if (shooterTime + 3 <= et) then
			shooter:setPosition(ShooterClosed);
			shooterState = 0;
		end
	end

	if (gamepad.getDpadUp2()) then
		shooterPower = shooterPower + 0.05;
	end
	if (gamepad.getDpadDown2()) then
		shooterPower = shooterPower - 0.05;
	end

	if (shooterRunning) then
		if (shooterMotor:getCurrent() > shooterCurrentLimit) then
			shooterCurrentPower = 1;
			shooterMotor:setPower(-1);
		else
			shooterCurrentPower = shooterPower;
			shooterMotor:setPower(-shooterPower);
		end
	end

	robotPane:addData("shooterPwr", shooterPower);
	robotPane:addData("shooterPwr2", shooterCurrentPower);
	robotPane:addData("shooterCur", shooterMotor:getCurrent());
	robotPane:addData("shooterVel", shooterMotor:getVelocity());
	TelemPaneManager:update();

	return false;
end

addOpmode(opmode);