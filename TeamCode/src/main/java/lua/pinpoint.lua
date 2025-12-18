---@class Pinpoint
Pinpoint = {}
function Pinpoint:update() end
---@return number
function Pinpoint:getX() end
---@return number
function Pinpoint:getY() end
---@return number
function Pinpoint:getHeading() end
---@param x number
function Pinpoint:setPosX(x) end
---@param y number
function Pinpoint:setPosY(y) end
---@param heading number
function Pinpoint:setHeading(heading) end