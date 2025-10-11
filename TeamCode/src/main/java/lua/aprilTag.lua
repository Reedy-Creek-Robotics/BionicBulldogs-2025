--Define the april tag functions for lua to use here
--Functions are directly imported to lua via opmodeloaderbase
--This just removes the undefined function warnings in opmodes
--This file (and def.lua) can be used as a simpler format of documentation

---@class AprilTag
AprilTag = {}
---@return boolean
function AprilTag:valid() end
---@return number
function AprilTag:getDist() end
---@return number
function AprilTag:y() end
---@return number
function AprilTag:x() end
---@return number
function AprilTag:bearing() end

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

