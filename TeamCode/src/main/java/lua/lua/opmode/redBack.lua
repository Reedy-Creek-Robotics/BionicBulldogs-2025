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
			:add(path.line(24, 24, 24, 72))
			:constantHeading(90)
			:add(path.curve3(24, 72, 24, 96, 48, 96))
			:constantHeading(90)
			:add(path.line(48, 96, 96, 96))
			:constantHeading(90)
			:add(path.curve3(96, 96, 120, 96, 120, 72))
			:constantHeading(90)
			:add(path.line(120, 72, 120, 24))
			:constantHeading(90)
			:build()
		)
	);
end


SeqAction.new(
  PathAction.new(
      path.chain()
      :add(path.line(24, 24, 24, 72))
      :constantHeading(90)
      :add(path.curve3(24, 72, 24, 96, 48, 96))
      :constantHeading(90)
      :add(path.line(48, 96, 96, 96))
      :constantHeading(90)
      :add(path.curve3(96, 96, 120, 96, 120, 72))
      :constantHeading(90)
      :add(path.line(120, 72, 120, 24))
      :constantHeading(90)
      :build()
  )
)

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