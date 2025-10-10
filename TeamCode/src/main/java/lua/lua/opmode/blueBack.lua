require("modules.telemetry");
require("modules.action.init");
require("modules.action.robot");

---@type Opmode
local opmode = { name = "blueBack" };

---@type Action
local a;

function opmode.init()
	shooter:init();
	intake:init();
	follower.setPosition(0, 0, 90);

	a = SeqAction.new(
		ShooterEnableAction.new(1460),
		PathAction.new(
			path.chain()
			:add(path.line(0, 0, 0, 10))
			:linearHeading(90, 112)
			:build()
		),
		SleepAction.new(2),
		ShootAction.new(4),
		SleepAction.new(2),
		ShooterDisableAction.new(),
		PathAction.new(
			path.chain()
			:add(path.line(0, 10, 0, 15))
			:constantHeading(112)
			:build()
		)
	)
end

function opmode.start()
	shooter:close();
	follower.initTelem();
	a:start(0);
end

function opmode.update(dt, et)
	shooter:telem();
	drivePane:addData("x", follower.getPositionX());
	drivePane:addData("y", follower.getPositionY());
	drivePane:addData("h", follower.getPositionH());
	TelemPaneManager:update();
	follower.telem();
	local state = a:update(dt, et);
	if (state ~= ActionState.Running) then
		profiler.genString("redBack.txt", a);
		if (state ~= ActionState.Done) then
			error(("root action '%s' failed"):format(tostring(a)));
		end
		return true;
	end
	return false;
end

addOpmode(opmode);
