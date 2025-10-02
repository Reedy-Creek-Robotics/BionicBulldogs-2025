package.path = package.path .. ";./lua/?.lua";

require("modules.action.seqAction");
require("modules.action.parallelAction");

local t = os.time();

log = {};
function log.e(tag, value)
	print(("[%d] %10s | %s"):format(t, tag, value));
end

function log.d(tag, value)
	print(("[%d] %10s | %s"):format(t, tag, value));
end

local a = SeqAction.new(
	CallbackAction.new(
		function ()
			log.d("thread 1", "1");
		end
	),
	SleepAction.new(2),
	CallbackAction.new(
		function ()
			log.d("thread 1", "2");
		end
	),
	SleepAction.new(2),
	{
		name = "test",
		update = function (self, dt, et)
			return ActionState.Error;
		end
	},
	CallbackAction.new(
		function ()
			log.d("thread 1", "3");
		end
	)
);

local b = SeqAction.new(
	SleepAction.new(1),
	CallbackAction.new(
		function ()
			log.d("thread 2", "1");
		end
	),
	SleepAction.new(2),
	CallbackAction.new(
		function ()
			log.d("thread 2", "2");
		end
	),
	SleepAction.new(2),
	CallbackAction.new(
		function ()
			log.d("thread 2", "3");
		end
	)
);

local c = ParallelAction.new(a, b);

c:start(t);

while (true) do
	local s = c:update(0, t);
	t = os.time();
	if (s ~= ActionState.Running) then
		c:genProfileStr(io.stdout);
		return;
	end
end