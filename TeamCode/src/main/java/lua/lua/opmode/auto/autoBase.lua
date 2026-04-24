require("modules.action");
require("modules.action.robot");

require("modules.telemPanes");

require("opmode.auto.config");

---@alias AutoPathFunc fun(): Action

---@class AutoPathGate
---@field gate AutoPathFunc
---@field noGate AutoPathFunc

---@class AutoPaths
---@field start vec3
---@field turretTarget vec2
---@field preload AutoPathFunc
---@field line1 AutoPathGate
---@field line2 AutoPathGate
---@field line3 AutoPathFunc
---@field line4 AutoPathFunc?
---@field park AutoPathFunc

if (DISABLE_ROBOT ~= nil) then
	local tmpAction = {
		update = function ()
			return ActionState.Done;
		end
	};
	tmpAction.new = function ()
		return tmpAction;
	end
	for k, v in RobotActions do
		RobotActions[k] = tmpAction;
	end
end

local prevLedState = 0;

---@type Action
action = nil;

---@type string
profileFileName = "unammed auto";

---@type file*
logFile = nil;

---@type vec2
local turretTarget = nil;

---@type DcMotor
local turretMotor = nil;

function autoUpdate(dt, et)
	counter:update(et);
	local ledState = 1 + counter.count;

	if (ledState ~= prevLedState) then
		led:displayArtBoard(ledState);
		prevLedState = ledState;
	end

	robotPane:addData("ms", dt);
	robotPane:addData("x", follower.getPositionX());
	robotPane:addData("y", follower.getPositionY());
	robotPane:addData("h", follower.getPositionH());
	robotPane:addData("count", counter.count);
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

	local x = follower.getPositionX();
	local y = follower.getPositionY();
	local h = follower.getPositionH();

	local dx = turretTarget.x - x;
	local dy = turretTarget.y - y;
	local angle = math.atan(dy, dx) - follower.getPositionH();
	angle = math.deg(angle);
	if (angle > 180) then
		angle = angle - 360;
	end
	if (angle < -180) then
		angle = angle + 360;
	end
	turret.update(angle);

	local dist = dx * dx + dy * dy;
	--	shooter:updateVelocity(x, y, dist);

	logFile:write(("%7.2f | x: %6.2f, y: %6.2f, h: %6.4f, curPos: %5d, tarPos: %5d, angle: %6.2f\n"):format(et, x, y, h,
		turretMotor:getCurrentPosition(), turretMotor:getTargetPosition(), angle
	));
	local state = action:update(dt, et);
	if (state ~= ActionState.Running) then
		if (state ~= ActionState.Done) then
			error(("root action '%s' failed"):format(tostring(action)));
		end
		return true;
	end
	return false;
end

function autoStart()
	logFile = io.open(DATADIR .. "/log" .. tostring(os.time()) .. ".txt", "wb");
	turret.start();
	shooter.running = true;
	--turret.reset();
	shooter:start(0);
	shooter:updateVelocity(follower.getPositionX(), follower.getPositionY());
	shooter:setInBack(false);
	action:start(0);
	led:displayArtBoard(0);
	counter:reset();
end

function autoStop()
	profiler.genString(profileFileName, action);
	save.saved("x", follower.getPositionX());
	save.saved("y", follower.getPositionY());
	save.saved("h", follower.getPositionH());
	save.saveb("resetTurret", false);
	logFile:close();
end

---@param name string
---@param prefix number | string
---@param config AutoPaths
function addConfig(name, prefix, config)
	addOpmode({
		name = name .. tostring(prefix),
		type = OpmodeType.Auto,
		init = function ()
			led = hardwareMap.ledGet();
			chub = hardwareMap.chubGet();
			--drive = HDrive.new(false);
			require("modules.telemetry");
			follower.setPosition(config.start.x, config.start.y, config.start.z);
			action = genPathFuncs[prefix](config);
			turretTarget = config.turretTarget;
			shooter:init();
			intake:init();
			turret.init(true);
			turretMotor = turret.getMotor();
			led:displayArtBoard(0);
			counter:init();
			shooter:close();
		end,
		start = autoStart,
		update = autoUpdate,
		stop = autoStop
	});
end

---@param name string
---@param config AutoPaths
function loadOpmodeConfigs(name, config)
	addConfig(name, 3, config);
	addConfig(name, 6, config);
	addConfig(name, 9, config);
	addConfig(name, 12, config);
	addConfig(name, 15, config);
	if (config.cycle ~= nil) then
		addConfig(name, "cycle", config);
	end
	addConfig(name, "partner", config);
end