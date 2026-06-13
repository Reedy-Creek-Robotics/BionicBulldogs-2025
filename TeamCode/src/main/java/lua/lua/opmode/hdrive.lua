require("modules.hdrive");

---@type Opmode
local opmode = { name = "hdriveTest", type = OpmodeType.Telop };

---@type HDrive
local drive;

function opmode.init()
	drive = HDrive.new();
end

function opmode.update()
	local forward = gamepad.getLeftStickY();
	local right = gamepad.getLeftStickX();
	local rotate = gamepad.getRightStickX();
	drive:drive(forward, right, rotate);

	telemetry.addDataf("fl", drive.frontLeft:getCurrent());
	telemetry.addDataf("fr", drive.frontRight:getCurrent());
	telemetry.addDataf("bl", drive.backLeft:getCurrent());
	telemetry.addDataf("br", drive.backRight:getCurrent());
	telemetry.update();

	return false;
end

addOpmode(opmode);