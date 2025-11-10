require("modules.telemetry");
require("modules.action.init");
require("modules.action.robot");

require("modules.telemPanes");

---@type Action
local a;

---@param num integer
function genPath(num)
	require("modules.telemetry");
	--aprilTagProcessor.init(1920, 1080, 2, 255, 1.0);
	--shooter:init();
	--intake:init();
	--follower.setPosition(55, 133, 180);
	local first = SeqAction.newl(
		"preload",
		ShooterEnableAction.new(),
		SleepAction.new(1),
		ShootAction.new(3),
		SleepAction.new(1)
	);
	local second = SeqAction.newl(
		"first row",
		PathAction.new(
			path.chain()
			:add(path.line(55, 133, 37, 82))
			:constantHeading(180)
			:build()
		),
		IntakeAction.new(),
		PathAction.new(
			path.chain()
			:add(path.line(37, 82, 17, 82))
			:build()
		),
		IntakeStopAction.new(),
		PathAction.new(
			path.chain()
			:add(path.line(17, 82, 33, 89.5))
			:linearHeading(180, 135)
			:build()
		),
		ShooterEnableAction.new(1300),
		SleepAction.new(0.5),
		ShootAction.new(3),
		SleepAction.new(1)
	);
	local third = SeqAction.newl(
		"second row",
		PathAction.new(
			path.chain()
			:add(path.line(33, 89.5, 37, 58))
			:linearHeading(135, 180)
			:build()
		),
		IntakeAction.new(),
		PathAction.new(
			path.chain()
			:add(path.line(37, 58, 17, 58))
			:build()
		),
		IntakeStopAction.new(),
		PathAction.new(
			path.chain()
			:add(path.line(17, 58, 33, 89.5))
			:linearHeading(180, 135)
			:build()
		),
		SleepAction.new(0.5),
		ShootAction.new(3),
		SleepAction.new(1)
	);
	local park = PathAction.new(
		path.chain()
		:add(path.line(33, 89.5, 33, 96))
		:constantHeading(135)
		:build()
	);
	if (num == 1) then
		a = SeqAction.new(first);
	end
	if (num == 2) then
		a = SeqAction.new(first, second, park);
	end
	if (num == 3) then
		a = SeqAction.new(first, second, third, park);
	end
		profiler.genString("blueFront.txt", a);
end

function start()
	a:start(0);
end

function update(dt, et)
	drivePane:addData("x", follower.getPositionX());
	drivePane:addData("y", follower.getPositionY());
	drivePane:addData("h", follower.getPositionH());
	TelemPaneManager:update();
	follower.telem();
	local state = a:update(dt, et);
	if (state ~= ActionState.Running) then
		profiler.genString("blueFront.txt", a);
		if (state ~= ActionState.Done) then
			error(("root action '%s' failed"):format(tostring(a)));
		end
		return true;
	end
	return false;
end

---@type Opmode
local opmode3 = {
	name = "a_blueFront3",
	init = function ()
		genPath(1);
	end,
	start = start,
	update = update
};
addOpmode(opmode3);
---@type Opmode
local opmode6 = {
	name = "a_blueFront6",
	init = function ()
		genPath(2);
	end,
	start = start,
	update = update
};
addOpmode(opmode6);
---@type Opmode
local opmode9 = {
	name = "a_blueFront9",
	init = function ()
		genPath(3);
	end,
	start = start,
	update = update
};
addOpmode(opmode9);