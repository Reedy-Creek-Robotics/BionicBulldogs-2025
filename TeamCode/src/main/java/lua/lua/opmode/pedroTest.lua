require("modules.action.action");
require("modules.action.seqAction");
require("modules.action.parallelAction");

---@type Opmode
local opmode = { name = "pedroTest" };

local slide = {};

function slide:init()
	self.m1 = hardwareMap.dcmotorGet("slide");
	self.m2 = hardwareMap.dcmotorGet("slide2");

	self.m1:setMode(DcMotorRunMode.StopAndResetEncoder);
	self.m2:setMode(DcMotorRunMode.StopAndResetEncoder);

	self.m1:setZeroPowerBehavior(DcMotorZeroPowerBehavior.Brake);
	self.m2:setZeroPowerBehavior(DcMotorZeroPowerBehavior.Brake);

	self.m2:setDirection(Direction.Reverse);
end

function slide:runToPosition(pos)
	self.m1:setPower(0);
	self.m2:setPower(0);

	self.m1:setMode(DcMotorRunMode.RunWithoutEncoder);
	self.m2:setMode(DcMotorRunMode.RunWithoutEncoder);

	self.m1:setTargetPosition(pos);
	self.m2:setTargetPosition(pos);

	self.m1:setMode(DcMotorRunMode.RunToPosition);
	self.m2:setMode(DcMotorRunMode.RunToPosition);

	self.m1:setPower(1);
	self.m2:setPower(1);
end

---@type Action
local a;

function opmode.init()
	a = ParallelAction.new(
		PathAction.new(
			path.chain()
			:add(path.line(0, 0, 72, 0))
			:constantHeading(0)
			:add(path.curve3(72, 0, 96, 0, 96, 24))
			:constantHeading(0)

			:add(path.line(96, 24, 96, 72))
			:constantHeading(0)
			:add(path.curve3(96, 72, 96, 96, 72, 96))
			:constantHeading(0)

			:add(path.line(72, 96, 24, 96))
			:constantHeading(0)
			:add(path.curve3(24, 96, 0, 96, 0, 72))
			:constantHeading(0)

			:add(path.line(0, 72, 0, 0))
			:constantHeading(0)
			:build()
		),
		SeqAction.new(
			CallbackAction.new(
				function ()
					slide:runToPosition(-500);
				end
			),
			SleepAction.new(2),
			CallbackAction.new(
				function ()
					slide:runToPosition(0);
				end
			),
			SleepAction.new(2),
			CallbackAction.new(
				function ()
					slide:runToPosition(-500);
				end
			),
			SleepAction.new(2),
			CallbackAction.new(
				function ()
					slide:runToPosition(0);
				end
			)
		)
	)

	slide:init();
end

function opmode.start()
	a:start(0);
end

function opmode.update(dt, et)
	follower.telem();
	local state = a:update(dt, et);
	if (state == ActionState.Running) then
		return false;
	elseif (state == ActionState.Done) then
		return true;
	end
	error("root action failed");
    return true;
end

addOpmode(opmode);