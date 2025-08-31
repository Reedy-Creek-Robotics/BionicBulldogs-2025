require("modules.action.action");
require("modules.class");

---@class ParallelAction: Action
---@field actions Action[]
ParallelAction = {
	mt = {
		__tostring = function (self)
			return "ParallelAction: " .. self.__id;
		end
	}
}

---@param ... Action
---@return ParallelAction
function ParallelAction.new(...)
	local a = new(ParallelAction);
	a.actions = {};
	for _, v in pairs({ ... }) do
		table.insert(a.actions, v);
	end
	return a;
end

---@param a Action
---@return ParallelAction
function ParallelAction:add(a)
	table.insert(self.actions, a);
	return self;
end

---@param et number
function ParallelAction:start(et)
	for _, action in ipairs(self.actions) do
		if (action.start ~= nil) then
			action:start(et);
		end
	end
end

---@param dt number
---@param et number
---@return ActionState
function ParallelAction:update(dt, et)
	local count = 0;
	for k, action in pairs(self.actions) do
		count = count + 1;
		local newState = action:update(dt, et);
		if (newState == ActionState.Running) then
			goto continue
		elseif (newState == ActionState.Done) then
			if (action.finish ~= nil) then
				action:finish();
			end
			self.actions[k] = nil;
		elseif (newState == ActionState.Error) then
			for _, a in pairs(self.actions) do
				if (action.error ~= nil) then
					a:error();
				end
			end
			log.e("actions", "action '" .. tostring(action) .. "' failed");
			actionPane:addLine("error: action '" .. tostring(action) .. "' failed");
			return ActionState.Error;
		elseif (newState == ActionState.ErrCont) then
			if (action.error ~= nil) then
				action:error();
			end
			log.e("actions", "action '" .. tostring(action) .. "' failed");
			actionPane:addLine("error: action '" .. tostring(action) .. "' failed");
			self.actions[k] = nil;
		end
		::continue::
	end
	if (count == 0) then
		return ActionState.Done;
	end
	return ActionState.Running;
end