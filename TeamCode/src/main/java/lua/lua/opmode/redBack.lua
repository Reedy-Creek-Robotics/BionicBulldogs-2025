require("modules.telemetry");
require("modules.action.init");
require("modules.action.robot");

---@type Opmode
local opmode = { name = "redBack" };

---@type Action
local a;

function opmode.init()
	follower.setPosition(24, 24, 90);
	a = SeqAction.new(
        Shoot.new(3),
        PathAction.new(
            path.chain()
            :add(path.line(60.00, 136.00, 40.00, 84.00))
            :linearHeading(0.00, 180.00)
            :build()
        ),
        Load.new(3),
        PathAction.new(
            path.chain()
            :add(path.line(40.00, 84.00, 61.00, 97.00))
            :linearHeading(180.00, 30.00)
            :build()
        ),
        Shoot.new(3)
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