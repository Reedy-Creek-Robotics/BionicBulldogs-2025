---@class IfAction : Action
---@field cond fun(et: number): boolean
---@field action Action
---@field running boolean
IfAction = {};

---@param condition fun(et: number): boolean
---@param action Action
---@return IfAction
function IfAction.new(condition, action)
	local a = new(IfAction);
	a.cond = condition;
	a.action = action;
	a.running = true;
	return a;
end

---@param et number
function IfAction:start(et)
	if (self.cond(et)) then
		self.running = true;
		self.action:start(et);
	end
	self.running = false;
end

---@param dt number
---@param et number
---@return ActionState
function IfAction:update(dt, et)
	if (self.running) then
		return self.action:update(dt, et);
	else
		return ActionState.Done;
	end
end

function IfAction:genProfileStr(file, indent)
	return self.action:genProfileStr(file, indent);
end