require("modules.telemetry");
require("modules.action.init");

---@type Opmode
local opmode = { name = "redBack" };

---@type Action
local a;

function opmode.init()
	follower.setPosition(24, 24, 90);
	a = SeqAction.new(
		PathAction.new(
			path.chain()
			:add(path.line(24.00, 12.00, 24.00, 60.00))
			:constantHeading(90.00)
			:add(path.curve3(24.00, 60.00, 24.00, 84.00, 48.00, 84.00))
			:constantHeading(90.00)
			:add(path.line(48.00, 84.00, 96.00, 84.00))
			:linearHeading(90.00, 0.00)
			:add(path.curve3(96.00, 84.00, 120.00, 84.00, 120.00, 60.00))
			:constantHeading(0.00)
			:add(path.line(120.00, 60.00, 120.00, 12.00))
			:constantHeading(0.00)
			:build()
		)
	)
end

function opmode.start()
	follower.initTelem();
	a:start(0);
end

function opmode.update(dt, et)
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