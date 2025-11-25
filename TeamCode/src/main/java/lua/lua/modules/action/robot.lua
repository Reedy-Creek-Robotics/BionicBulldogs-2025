require("modules.shooter")
require("modules.intake")

---@class Shoot : Action
---@field n number
local Shoot = {
	mt = {
		__tostring = function (self)
			return ("Shoot(%d)"):format(self.n);
		end
	},
	t = -1
};

---@return Shoot
---@param n number
function Shoot.new(n)
	local a = new(Shoot);
	a.n = n;
	return a;
end

function Shoot:start(et)
	intake:forward();
end

---@param dt number
---@param et number
---@return ActionState
function Shoot:update(dt, et)
	if (self.t == -1) then
		if (shooter:ready()) then
			self.t = et;
		end
	end
	if (self.t > 0 and self.t + 0.5 <= et) then
		shooter:shoot(et);
		--actionPane:addData("shoot", et);
		self.t = -2;
	end
	if (shooter:update(et)) then
		--actionPane:addData("shoot reset", et);
		self.n = self.n - 1;
		if (self.n == 0) then
			return ActionState.Done;
		end
		self.t = -1;
	end
	return ActionState.Running;
end

---@class ShooterEnable : Action
---@field vel number?
local ShooterEnable = {
	mt = {
		__tostring = function (self)
			if (self.vel == nil) then
				return ("ShooterEnable(april tag)");
			end
			return ("ShooterEnable(%d)"):format(self.vel);
		end
	}
};

---@return ShooterEnable
---@param vel number?
function ShooterEnable.new(vel)
	local a = new(ShooterEnable);
	a.vel = vel;
	return a;
end

function ShooterEnable:start(et)
	if (self.vel == nil) then
		local tag = aprilTagProcessor.getTag(20);
		local attempts = 0;
		while (not tag:valid()) do
			attempts = attempts + 1;
			tag = aprilTagProcessor.getTag(20);
			if (attempts > 10) then
				break
			end
		end
		local dist = tag:getDist();
		local vel = apirlDis(dist);
		shooter:start(vel);
		telemetry.addDataf("vel", vel);
		telemetry.addDataf("dist", dist);
		telemetry.update();
	else
		shooter:start(self.vel);
	end
end

---@param dt number
---@param et number
---@return ActionState
function ShooterEnable:update(dt, et)
	return ActionState.Done;
end

---@class ShooterDisable : Action
local ShooterDisable = {
	mt = {
		__tostring = function (self)
			return ("ShooterDisable"):format(self.vel);
		end
	}
};

---@return ShooterDisable
function ShooterDisable.new()
	local a = new(ShooterDisable);
	return a;
end

function ShooterDisable:start(et)
	shooter:stop();
end

---@param dt number
---@param et number
---@return ActionState
function ShooterDisable:update(dt, et)
	return ActionState.Done;
end

---@class IntakeStop : Action
local IntakeStop = {
	mt = {
		__tostring = function (self)
			return ("IntakeStop"):format(self.delay);
		end
	}
};
---@return IntakeStop
function IntakeStop.new(time)
	local a = new(IntakeStop);
	return a;
end

function IntakeStop:start(et)
	intake:stop();
end

---@param dt number
---@param et number
---@return ActionState
function IntakeStop:update(dt, et)
	return ActionState.Done;
end

---@class Intake : Action
---@field delay number?
---@field startTime number
local Intake = {
	mt = {
		__tostring = function (self)
			if (self.delay == nil) then
				return "Intake(inf)";
			end
			return ("Intake(%.2fs)"):format(self.delay);
		end
	}
};
---@param time number?
---@return Intake
function Intake.new(time)
	local a = new(Intake);
	a.delay = time;
	a.startTime = -1;
	return a;
end

function Intake:start(et)
	self.startTime = et;
	intake:forward();
end

---@param dt number
---@param et number
---@return ActionState
function Intake:update(dt, et)
	if (self.delay == nil) then
		return ActionState.Done;
	end
	if (self.startTime + self.delay <= et) then
		intake:stop();
		return ActionState.Done;
	end
	return ActionState.Running;
end

RobotActions = {
	Shoot = Shoot,
	ShooterDisable = ShooterDisable,
	ShooterStart = ShooterEnable,
	Intake = Intake,
	IntakeStop = IntakeStop
};

Delay = SleepAction;