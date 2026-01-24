--Define the april tag functions for lua to use here
--Functions are directly imported to lua via opmodeloaderbase
--This just removes the undefined function warnings in opmodes
--This file (and def.lua) can be used as a simpler format of documentation

---@class Pose3D
Pose3D = {}
---@return number
function Pose3D:x() end
---@return number
function Pose3D:y() end
---@return number
function Pose3D:z() end
---@return number
function Pose3D:pitch() end
---@return number
function Pose3D:yaw() end
---@return number
function Pose3D:roll() end

---@class FtcPos
FtcPos = {}
---@return number
function FtcPos:x() end
---@return number
function FtcPos:y() end
---@return number
function FtcPos:bearing() end
---@return number
function FtcPos:range() end

---@class AprilTag
AprilTag = {}
---@return boolean
function AprilTag:valid() end
---@return FtcPos
function AprilTag:ftcPos() end
---@return Pose3D
function AprilTag:robotPos() end

aprilTagProcessor = {}

---@param width integer
---@param height integer
---@param exposureMS integer
---@param gain integer
---@param decimation number
function aprilTagProcessor.init(width, height, exposureMS, gain, decimation) end

---@param id integer
---@return AprilTag
function aprilTagProcessor.getTag(id) end

function aprilTagProcessor.update() end

---@param dist number
---@return number
function apirlDis(dist) end