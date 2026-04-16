---@type Opmode
local opmode = {
	type = OpmodeType.Auto,
	name = "testAuto"
};

--x - 14.5
--y - 62
--x + 30


---@type Action
local action;
function opmode.init()
	action = PathAction.new(
		path.chain()
		:add(path.line(-33, 136, -47.5, 136))
		:constantHeading(0)
		:add(path.line(-47.5, 136, 47.5, 74))
		:constantHeading(0)
		:add(path.line(-47.5, 74, -17.5, 74))
		:constantHeading(0)
		:build()
	);

	follower.setPosition(-33, 136, 0);
end

function opmode.start()
	action:start(0);
end

local running = true;

---@param dt number
---@param et number
function opmode.update(dt, et)
	follower.update();
	if (running) then
		local state = action:update(dt, et);
		if (state ~= ActionState.Running) then
			running = false;
		end
	end

	telemetry.addDataf("x", follower.getPositionX());
	telemetry.addDataf("y", follower.getPositionX());
	telemetry.addDataf("h", follower.getPositionX());
	telemetry.update();
	return false;
end

addOpmode(opmode);