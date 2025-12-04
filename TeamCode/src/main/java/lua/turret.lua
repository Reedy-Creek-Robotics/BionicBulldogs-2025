---@class turret
turret = {
}
---@enum turret.State
turret.State= {Waiting = 0, Tracking = 1, Manual = 2}

function turret.init() end
---@return turret.State
function turret.getState() end
function turret.startManual() end
function turret.startAutomatic() end
function turret.resetHeading() end
---@param power number
function turret.update(power) end