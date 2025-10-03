---@class Shoot : Action
---@field delay number
---@field startTime number
Shoot = {
	mt = {
		__tostring = function (self)
			return ("Shoot(%.2fs)"):format(self.delay);
		end
	}
};

---@param time number
---@return SleepAction
function Shoot.new(time)
	local a = new(Shoot);
	a.delay = time;
	a.startTime = -1;
	return a;
end

function Shoot:start(et)
	self.startTime = et;
end

---@param dt number
---@param et number
---@return ActionState
function Shoot:update(dt, et)
	if (self.startTime + self.delay <= et) then
		return ActionState.Done;
	end
	return ActionState.Running;
end



---@class Load
---@field delay number
---@field startTime number
Load = {
	mt = {
		__tostring = function (self)
			return ("Load(%.2fs)"):format(self.delay);
		end
	}
};

---@param time number
---@return SleepAction
function Load.new(time)
	local a = new(Load);
	a.delay = time;
	a.startTime = -1;
	return a;
end

function Load:start(et)
	self.startTime = et;
end

---@param dt number
---@param et number
---@return ActionState
function Load:update(dt, et)
	if (self.startTime + self.delay <= et) then
		return ActionState.Done;
	end
	return ActionState.Running;
end