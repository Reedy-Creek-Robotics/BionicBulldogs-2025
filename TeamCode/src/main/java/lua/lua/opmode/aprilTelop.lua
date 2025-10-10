--Uses silver bot instead of main bot for ONLY HDrive (no imu) and camera
require("modules.hdrive");
require("modules.telemPanes");

---@type Opmode
local opmode = { name = "AprilTelop" };

---@type HDrive
local drive;

---@integer
--Use nums between 20 (Blue goal) and 24 (Red goal)
local id = 24

function opmode.init()
    require("modules.telemetry");
    --For accurate distances, the camera resolution MUST be exact to the resolution calibrated at
    aprilTagProcessor.init(1920, 1080, 2, 255, 0.5)
end

--[[function opmode.start()

end]] --

function opmode.update()
    --Drive the bot
    drive = HDrive.new();
    drive.imu = hardwareMap.imuGet()
    local forward = gamepad.getLeftStickY();
    local right = gamepad.getLeftStickX();
    local rotate = gamepad.getRightStickX();
    drive:driveFr(forward, right, rotate);

    --Obtain the blue goal april tag
    local bTag = aprilTagProcessor.getTag(id)

    aprilTagPane:addData("is valid", bTag:valid())
    aprilTagPane:addData("target id", id)
    --get distance is exact to the pythagorean theorem
    aprilTagPane:addData("distance", bTag:getDist())
    TelemPaneManager:update();
    return false;
end

addOpmode(opmode);
