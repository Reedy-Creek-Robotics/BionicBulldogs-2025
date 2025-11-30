--Uses silver bot instead of main bot for ONLY HDrive (no imu) and camera
require("modules.hdrive");
require("modules.telemPanes");

---@type Opmode
local opmode = { name = "AprilTelop" };

---@type HDrive
local drive;

function opmode.init()
	require("modules.telemetry");
  aprilTagProcessor.init(1280, 720, 2, 255)
end

--[[function opmode.start()

end]]--

function opmode.update()
    --Drive the bot
  drive = HDrive.new();
	drive.imu = hardwareMap.imuGet()
	local forward = gamepad.getLeftStickY();
	local right = gamepad.getLeftStickX();
	local rotate = gamepad.getRightStickX();
	drive:driveFr(forward, right, rotate);

--Obtain the blue goal april tag
	local bTag = aprilTagProcessor.getTag(20)

	--Calculate power to distance (const may be a function for regression)
	local const = 0.5
	--local dist = bTag:getDist * const
	local maxVelocity = 2600

	if bTag:valid() then aprilTagPane:addData("tag distance", bTag:getDist()) else aprilTagPane:addLine("tag distance: -1") end
	TelemPaneManager:update();
	return false;
end

addOpmode(opmode);