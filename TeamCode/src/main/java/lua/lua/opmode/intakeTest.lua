require("modules.hdrive");

---@type Opmode
local opmode = { name = "intakeTest" };

---@type HDrive
local drive;

---@type DcMotor
local intake;

function opmode.init()
	drive = HDrive.new();
	drive.imu = hardwareMap.spimuGet();
	intake = hardwareMap.dcmotorGet("intake");
end

function opmode.update()
	local forward = gamepad.getLeftStickY();
	local right = gamepad.getLeftStickX();
	local rotate = gamepad.getRightStickX();
	drive:driveFr(forward, right, rotate);

	if(gamepad.getDpadUp()) then
		intake:setPower(1.0);
	end
	if(gamepad.getDpadDown()) then
		intake:setPower(-1.0);
	end
	if(gamepad.getCross()) then
		intake:setPower(0.0);
	end

	return false;
end

addOpmode(opmode);