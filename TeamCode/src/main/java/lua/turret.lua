---@class turret
turret = {
}
---@enum turret.State
turret.State= {Waiting = 0, Tracking = 1, Manual = 2}

---@param reset boolean
function turret.init(reset) end
---@param tag number
function turret.setTargetTag(tag) end
---@return turret.State
function turret.getState() end
function turret.startManual() end
function turret.startAutomatic() end
function turret.reset() end
function turret.lockOnTag() end
function turret.resetHeading() end
---@param angle number
function turret.turnTo(angle) end
---@param angle number
function turret.turnAngle(angle) end
---@param power number
function turret.update(power) end