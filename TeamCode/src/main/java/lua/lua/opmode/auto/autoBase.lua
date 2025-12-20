require("modules.action");
require("modules.action.robot");

require("modules.telemPanes");

---@class AutoPathGate
---@field gate Action
---@field noGate Action

---@class AutoPaths
---@field start vec3
---@field turretTarget vec2
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

---@type vec2
local turretTarget = nil;

function autoUpdate(dt, et)
	robotPane:addData("x", follower.getPositionX());
	robotPane:addData("y", follower.getPositionY());
	robotPane:addData("h", follower.getPositionH());
	--currentPane:addData("fl", drive.frontLeft:getCurrent());
	--currentPane:addData("fr", drive.frontRight:getCurrent());
	--currentPane:addData("bl", drive.backLeft:getCurrent());
	--currentPane:addData("br", drive.backRight:getCurrent());
	currentPane:addData("sl", shooter.motorL:getCurrent());
	currentPane:addData("sr", shooter.motorR:getCurrent());
	currentPane:addData("in", intake.motor:getCurrent());

	shooter:telem();
	TelemPaneManager:update();
	follower.update();
	follower.telem();

	local dx = turretTarget.x - follower.getPositionX();
	local dy = turretTarget.y - follower.getPositionY();
	local angle = math.atan(dy, dx) - follower.getPositionH();
	angle = math.deg(angle);
	if(angle > 180) then
		angle = angle - 360;
	end
	if(angle < -180) then
		angle = angle + 360;
	end
	turret.turnTo(angle + 2);

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
	turret.startAutomatic();
	action:start(0);
end

---@param name string
---@param genFun fun(number)
---@param count number
function loadOpmodes(name, genFun, count)
	count = count or 3
	for i = 0, count do
		addOpmode({
			name = "a_" .. name .. tostring((i + 1) * 3),
			init = function ()
				shooter:init();
				intake:init();
				turret.init();
				genFun(i);
			end,
			start = autoStart,
			update = autoUpdate
		});
	end
end

---@param name string
---@param prefix number
---@param config AutoPaths
---@param a Action
function addConfig(name, prefix, config, a)
	addOpmode({
		name = "a_" .. name .. tostring(prefix),
		init = function ()
			--drive = HDrive.new(false);
			require("modules.telemetry");
			follower.setPosition(config.start.x, config.start.y, config.start.z);
			action = a;
			turretTarget = config.turretTarget;
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
	addConfig(name, 3, config, SeqAction.new(config.preload, config.park));
	addConfig(name, 6, config, SeqAction.new(config.preload, config.line1.noGate, config.park));
	addConfig(name, 9, config, SeqAction.new(config.preload, config.line1.noGate, config.line2.noGate, config.park));
	addConfig(name, 12, config,
		SeqAction.new(config.preload, config.line1.gate, config.line2.noGate, config.line3, config.park));
	addConfig(name, 15, config,
		SeqAction.new(config.preload, config.line1.noGate, config.line2.gate, config.line3, config.line4, config.park));
end