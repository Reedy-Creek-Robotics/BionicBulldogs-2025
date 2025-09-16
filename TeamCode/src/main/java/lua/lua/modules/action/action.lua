require("modules.class");

---@enum ActionState
ActionState = {
	Running = 1,
	Done = 2,
	Error = 3,
	ErrCont = 4
};

---@class Action
---@field start fun(self: Action, et: number)?
---@field update fun(self: Action, dt: number, et: number): ActionState
---@field finish fun(self: Action)?
---@field error fun(self: Action)?

---@class SleepAction : Action
---@field delay number
---@field startTime number
SleepAction = {
	mt = {
		__tostring = function (self)
			return ("SleepAction(%.2fs): %s"):format(self.delay, self.__id);
		end
	}
};

---@param time number
---@return SleepAction
function SleepAction.new(time)
	local a = new(SleepAction);
	a.delay = time;
	a.startTime = -1;
	return a;
end

function SleepAction:start(et)
	self.startTime = et;
end

---@param dt number
---@param et number
---@return ActionState
function SleepAction:update(dt, et)
	if (self.startTime + self.delay <= et) then
		return ActionState.Done;
	end
	return ActionState.Running;
end

---@class PathAction : Action
---@field path PathChain
PathAction = {
	mt = {
		__tostring = function (self)
			return "PathAction: " .. self.__id;
		end
	}
};

---@param path PathChain
---@return PathAction
function PathAction.new(path)
	local a = new(PathAction);
	a.path = path;
	return a;
end

function PathAction:start()
	follower.followPathc(self.path);
end

---@param dt number
---@param et number
---@return ActionState
function PathAction:update(dt, et)
	follower.update();
	if (follower.isBusy()) then
		return ActionState.Running;
	end
	return ActionState.Done;
end

---@class CallbackAction : Action
CallbackAction = {
	mt = {
		__tostring = function (self)
			return "CallbackAction: " .. self.__id;
		end
	}
};

---@param callback function
---@return CallbackAction
function CallbackAction.new(callback)
	local a = new(CallbackAction);
	a.start = callback;
	return a;
end

---@param dt number
---@param et number
---@return ActionState
function CallbackAction:update(dt, et)
	return ActionState.Done;
end