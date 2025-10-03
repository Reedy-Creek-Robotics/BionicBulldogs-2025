require("modules.telemetry");
require("modules.action.init");
require("modules.action.robot");

---@type Opmode
local opmode = { name = "blueFront" };

---@type Action
local a;

function opmode.init()
	follower.setPosition(24, 24, 90);
	a = SeqAction.new(
        PathAction.new(
            path.chain()
            :add(path.line(84.00, 7.00, 84.00, 12.00))
            :linearHeading(90.00, 113.28)
            :build()
        ),
        Shoot.new(3),
        PathAction.new(
            path.chain()
            :add(path.line(84.00, 12.00, 106.00, 36.00))
            :linearHeading(113.28, 0.00)
            :build()
        ),
        Load.new(3),
        PathAction.new(
            path.chain()
            :add(path.line(106.00, 35.00, 84.00, 12.00))
            :linearHeading(0.00, 113.28)
            :build()
        ),
        Shoot.new(3),
        PathAction.new(
            path.chain()
            :add(path.line(84.00, 12.00, 84.00, 36.00))
            :linearHeading(113.28, 90.00)
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
		profiler.genString("blueFront.txt", a);
		if (state ~= ActionState.Done) then
			error(("root action '%s' failed"):format(tostring(a)));
		end
		return true;
	end
	return false;
end

addOpmode(opmode);