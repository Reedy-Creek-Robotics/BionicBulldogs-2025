require("modules.action.action");
require("modules.class");

---@class SeqAction : Action
---@field actions Action[]
---@field index number
SeqAction = {
	mt = {
		__tostring = function (self)
			return "SeqAction: " .. self.__id;
		end
	}
}

---@param ... Action
---@return SeqAction
function SeqAction.new(...)
	local a = new(SeqAction);
	a.actions = {};
	for _, v in pairs({ ... }) do
		table.insert(a.actions, v);
	end
	return a;
end

---@param a Action
---@return SeqAction
function SeqAction:add(a)
	table.insert(self.actions, a);
	return self;
end

---@param et number
function SeqAction:start(et)
	self.index = 1;
	local a = self.actions[1];
	if (a.start ~= nil) then
		a:start(et);
	end
end

---@param dt number
---@param et number
---@return ActionState
function SeqAction:update(dt, et)
	local action = self.actions[self.index];
	local state = action:update(dt, et);

	if (state == ActionState.Running) then
		return ActionState.Running;
	end

	if (state == ActionState.Error) then
		if (action.error ~= nil) then
			action:error();
		end
		return ActionState.Error;
	end

	if (state == ActionState.Done) then
		if (action.finish ~= nil) then
			action:finish();
		end
	end

	if (state == ActionState.ErrCont) then
		if (action.error ~= nil) then
			action:error();
		end
		log.e("actions", "action '" .. tostring(action) .. "' failed");
		actionPane:addLine("error: action '" .. tostring(action) .. "' failed");
	end

	self.index = self.index + 1;
	local nextAction = self.actions[self.index];

	if (nextAction == nil) then
		return ActionState.Done;
	end
	if (nextAction.start ~= nil) then
		nextAction:start(et);
	end

	return ActionState.Running;
end