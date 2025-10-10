require("modules.shooter")
require("modules.intake")



---@class ShootAction : Action
---@field n number
ShootAction = {
	mt = {
		__tostring = function (self)
			return "Shoot";
		end
	},
	t = -1
};

---@return ShootAction
---@param n number
function ShootAction.new(n)
	local a = new(ShootAction);
	a.n = n;
	return a;
end

function ShootAction:start(et)
	intake:forward();
end

---@param dt number
---@param et number
---@return ActionState
function ShootAction:update(dt, et)
	if (self.t == -1) then
		if (shooter:ready()) then
			self.t = et;
		end
	end
	if (self.t > 0 and self.t + 0.1 <= et) then
		shooter:shoot(et);
		actionPane:addData("shoot", et);
		self.t = -2;
	end
	if (shooter:update(et)) then
		actionPane:addData("shoot reset", et);
		self.n = self.n - 1;
		if (self.n == 0) then
			return ActionState.Done;
		end
		self.t = -1;
	end
	return ActionState.Running;
end

---@class ShooterEnableAction : Action
---@field vel number
ShooterEnableAction = {
	mt = {
		__tostring = function (self)
			return ("ShooterEnable(%d)"):format(self.vel);
		end
	}
};

---@return ShooterEnableAction
---@param vel number
function ShooterEnableAction.new(vel)
	local a = new(ShooterEnableAction);
	a.vel = vel;
	return a;
end

function ShooterEnableAction:start(et)
	shooter:start(self.vel);
end

---@param dt number
---@param et number
---@return ActionState
function ShooterEnableAction:update(dt, et)
	return ActionState.Done;
end

---@class ShooterDisableAction : Action
ShooterDisableAction = {
	mt = {
		__tostring = function (self)
			return ("ShooterDisable"):format(self.vel);
		end
	}
};

---@return ShooterDisableAction
function ShooterDisableAction.new()
	local a = new(ShooterDisableAction);
	return a;
end

function ShooterDisableAction:start(et)
	shooter:stop();
end

---@param dt number
---@param et number
---@return ActionState
function ShooterDisableAction:update(dt, et)
	return ActionState.Done;
end

---@class IntakeAction : Action
---@field delay number
---@field startTime number
IntakeAction = {
	mt = {
		__tostring = function (self)
			return ("Intake(%.2fs)"):format(self.delay);
		end
	}
};
---@param time number
---@return IntakeAction
function IntakeAction.new(time)
	local a = new(IntakeAction);
	a.delay = time;
	a.startTime = -1;
	return a;
end

function IntakeAction:start(et)
	self.startTime = et;
	intake:forward();
end

---@param dt number
---@param et number
---@return ActionState
function IntakeAction:update(dt, et)
	if (self.startTime + self.delay <= et) then
		intake:stop();
		return ActionState.Done;
	end
	return ActionState.Running;
end