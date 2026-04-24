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
	waiting = true
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
	if (self.waiting == true) then
		if (shooter:ready()) then
			self.waiting = false;
			shooter:shootNum(et, self.n);
			actionPane:addData("start shoot", et);
		end
	else
		if (shooter:update(et)) then
			counter:reset();
			return ActionState.Done;
		end
	end
	return ActionState.Running;
end

function Shoot:error()
	shooter:close();
	counter:reset();
end

function Shoot:finish()
	shooter:close();
	counter:reset();
end

--function Shoot:update(dt, et)
--	if (shooter:update(et)) then
--		return ActionState.Done;
--	end
--	return ActionState.Running;
--end

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
		shooter.motorL:setPidf(1280, 3, 0, 2);
		shooter.motorR:setPidf(1280, 3, 0, 2);
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

---@class TurretTurnTo: Action
---@field angle number
local TurretTurnTo = {
	mt = {
		__tostring = function (self)
			return ("TurretTurnTo(%d)"):format(self.angle);
		end
	}
};

---@return TurretTurnTo
function TurretTurnTo.new(angle)
	local a = new(TurretTurnTo);
	a.angle = angle;
	return a;
end

function TurretTurnTo:start(et)
	turret.turnTo(self.angle)
end

---@param dt number
---@param et number
---@return ActionState
function TurretTurnTo:update(dt, et)
	return ActionState.Done;
end

---@class TurretTurnAngle: Action
---@field angle number
local TurretTurnAngle = {
	mt = {
		__tostring = function (self)
			return ("TurretTurnAngle(%d)"):format(self.angle);
		end
	}
};

---@return TurretTurnAngle
function TurretTurnAngle.new(angle)
	local a = new(TurretTurnAngle);
	a.angle = angle;
	return a;
end

function TurretTurnAngle:start(et)
	turret.turnAngle(self.angle)
end

---@param dt number
---@param et number
---@return ActionState
function TurretTurnAngle:update(dt, et)
	return ActionState.Done;
end

---@class BeamBreakWait: Action
---@field num number
local BeamBreakWait = {
	mt = {
		__tostring = function (self)
			return ("BeamBreakWait(%d)"):format(self.num);
		end
	}
};

---@return BeamBreakWait
---@param num number
function BeamBreakWait.new(num)
	local a = new(BeamBreakWait);
	a.num = num;
	return a;
end

function BeamBreakWait:start(et)
	counter:reset();
end

function BeamBreakWait:update(et)
	if (counter.ret) then
		return ActionState.Done;
	else
		return ActionState.Running;
	end
end

---@class BeamBreakStopIntake: Action
---@field s boolean
local BeamBreakStopIntake = {
	mt = {
		__tostring = function (self)
			return ("BeamBreakStopIntake(%s)"):format(tostring(self.s));
		end
	}
};

---@return BeamBreakStopIntake
---@param stop boolean
function BeamBreakStopIntake.new(stop)
	local a = new(BeamBreakStopIntake);
	a.s = stop;
	return a;
end

function BeamBreakStopIntake:start(et)
	counter.stopIntake = self.s;
end

function BeamBreakStopIntake:update(et)
	return ActionState.Done;
end

RobotActions = {
	Shoot = Shoot,
	ShooterDisable = ShooterDisable,
	ShooterStart = ShooterEnable,
	Intake = Intake,
	IntakeStop = IntakeStop,
	TurretTurnTo = TurretTurnTo,
	TurretTurnAngle = TurretTurnAngle,
	BeamBreakWait = BeamBreakWait,
	BeamBreakStopIntake = BeamBreakStopIntake
};

Delay = SleepAction;