require("modules.action");
require("modules.action.robot");

require("modules.telemPanes");

---@class AutoPathGate
---@field gate Action
---@field noGate Action

---@class AutoPaths
---@field start vec3
---@field preload Action
---@field line1 AutoPathGate
---@field line2 AutoPathGate
---@field line3 Action
---@field line4 Action?
---@field park Action

if (DISABLE_ROBOT ~= nil) then
	local tmpAction = {
		update = function ()
			return ActionState.Done;
		end
	};
	tmpAction.new = function ()
		return tmpAction;
	end
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

---@type file*
local logFile = nil;

function autoUpdate(dt, et)
	--currentPane:addData("fl", drive.frontLeft:getCurrent());
	--currentPane:addData("fr", drive.frontRight:getCurrent());
	--currentPane:addData("bl", drive.backLeft:getCurrent());
	--currentPane:addData("br", drive.backRight:getCurrent());
	currentPane:addData("sl", shooter.motorL:getCurrent());
	currentPane:addData("sr", shooter.motorR:getCurrent());
	currentPane:addData("in", intake.motor:getCurrent());

	shooter:telem();
	TelemPaneManager:update();
	follower.telem();

	logFile:write(
		" x: " .. tostring(follower.getPositionX()) ..
		" y: " .. tostring(follower.getPositionY()) ..
		" z: " .. tostring(follower.getPositionH()) .. "\n"
	);

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
	logFile = io.open(DATADIR .. "/log.txt", "wb");
	follower.setPosition(startPosition.x, startPosition.y, startPosition.z);
	turret.startAutomatic();
	action:start(0);
end

---@param name string
---@param genFun fun(number)
---@param count
function loadOpmodes(name, genFun, count)
	count = count or 3
	for i = 0, count do
		addOpmode({
			name = "a_" .. name .. tostring((i + 1) * 3),
			init = function ()
				genFun(i);
			end,
			start = autoStart,
			update = autoUpdate
		});
	end
end

---@param name string
---@param prefix number
---@param startPos vec3
---@param a Action
function addConfig(name, prefix, startPos, a)
	addOpmode({
		name = "a_" .. name .. tostring(prefix),
		init = function ()
			--drive = HDrive.new(false);
			require("modules.telemetry");
			action = a;
			startPosition = startPos;
			shooter:init();
			intake:init();
			turret.init();
		end,
		start = autoStart,
		update = autoUpdate
	});
end

---@param name string
---@param config AutoPaths
function loadOpmodeConfigs(name, config)
	addConfig(name, 3, config.start, SeqAction.new(config.preload, config.park));
	addConfig(name, 6, config.start, SeqAction.new(config.preload, config.line1.noGate, config.park));
	addConfig(name, 9, config.start, SeqAction.new(config.preload, config.line1.gate, config.line2.noGate, config.park));
	addConfig(name, 12, config.start,
		SeqAction.new(config.preload, config.line1.gate, config.line2.noGate, config.line3, config.park));
	addConfig(name, 15, config.start,
		SeqAction.new(config.preload, config.line1.noGate, config.line2.gate, config.line3, config.line4, config.park));
end