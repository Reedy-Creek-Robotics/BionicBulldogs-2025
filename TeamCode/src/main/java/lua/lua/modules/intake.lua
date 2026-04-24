---@enum IntakeState
IntakeState = {
	Forward = 1,
	Stopped = 0,
	Reverse = -1
}

---@class intake
---@field motor DcMotorEx
---@field state IntakeState
---@field speed number
---@field stats number[]
intake = {
	speed = 1,
	stats = {},
	value = 0,
	total = 0
}
local valueCount = 50;
local mul = 1 / valueCount;

function intake:init()
	self.motor = hardwareMap.dcmotorexGet("intake");
	self.state = IntakeState.Stopped;

	for i = 0, valueCount do
		self.stats[i] = 0;
	end
end

---@param speed number?
function intake:forward(speed)
	actionPane:addData("intake", speed or self.speed);
	self.motor:setPower(speed or self.speed);
	self.state = IntakeState.Forward;
end

function intake:reverse()
	self.motor:setPower(-self.speed);
	self.state = IntakeState.Reverse;
end

function intake:stop()
	self.motor:setPower(0);
	self.state = IntakeState.Stopped;
end

function intake:updateStats()
	if(self.state == IntakeState.Stopped) then
		return;
	end
	self.total = self.total - self.stats[self.value];
	local cur = self.motor:getCurrent();
	self.stats[self.value] = cur;
	self.total = self.total + cur;
	self.avg = self.total * mul;
	self.value = self.value + 1;
	if(self.value >= valueCount) then
		self.value = 0;
	end
end