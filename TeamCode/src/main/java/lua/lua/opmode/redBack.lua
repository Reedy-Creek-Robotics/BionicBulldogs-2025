require("modules.telemetry");
require("modules.action.init");
require("modules.action.robot");

---@type Opmode
local opmode = { name = "redBack" };

---@type Action
local a;

function opmode.init()
	shooter:init();
	intake:init();
	follower.setPosition(0, 0, 90);

	a = SeqAction.new(
		PathAction.new(
			path.chain()
			:add(path.line(0, 0, 0, 48))
			:linearHeading(90, 70)
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