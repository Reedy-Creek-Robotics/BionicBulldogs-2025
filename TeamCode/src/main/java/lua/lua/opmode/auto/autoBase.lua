require("modules.action");
require("modules.action.robot");

require("modules.telemPanes");

if (DISABLE_ROBOT ~= nil) then
	local tmpAction = {
		update = function ()
			return ActionState.Done;
		end
	};
	RobotActions.IntakeStop = tmpAction;
	RobotActions.Intake = tmpAction;
	RobotActions.ShooterDisable = tmpAction;
	RobotActions.Shoot = tmpAction;
	RobotActions.ShooterStart = tmpAction;
end

---@type Action
action = nil;

---@type string
profileFileName = "unammed auto";

function autoUpdate(dt, et)
	drivePane:addData("x", follower.getPositionX());
	drivePane:addData("y", follower.getPositionY());
	drivePane:addData("h", follower.getPositionH());
	TelemPaneManager:update();
	follower.telem();
	local state = action:update(dt, et);
	if (state ~= ActionState.Running) then
		profiler.genString(profileFileName, action);
		if (state ~= ActionState.Done) then
			error(("root action '%s' failed"):format(tostring(action)));
		end
		return true;
	end
	return false;
end

function autoStart()
	action:start(0);
end

---@param name string
---@param genFun fun(number)
function loadOpmodes(name, genFun)
	for i = 0, 3 do
		addOpmode({
			name = "a_" .. name .. tostring((i + 1) * 3),
			init = function ()
				genFun(1);
			end,
			start = autoStart,
			update = autoUpdate
		});
	end
end