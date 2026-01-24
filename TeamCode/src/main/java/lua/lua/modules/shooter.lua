require("modules.utils")

---@enum shooterState
shooterState = {
	Open = 0,
	Close = 1,
	Wait = 2
}
---@class shooter
---@field motorL DcMotorEx
---@field motorR DcMotorEx
---@field gate Servo
---@field gateClosed number
---@field gateOpen number
---@field time number
---@field vel number
---@field count number
---@field state shooterState
---@field openDelay number
---@field closeDelay number
---@field delay number
---@field waitForReady boolean
---@field velocityDataPoints number[]
---@field running boolean
shooter = {
	gateOpen = 0.3,
	gateClosed = 0,
	openDelay = 1.1, -- 0.16
	openDelayEnd = 1,
	closeDelay = 0.35,
	state = shooterState.Close,
	waitForReady = false,
	velocityDataPoints = { 0, 0, 0, 0, 0 },
	running = false
}

function shooter:init()
	self.gate = hardwareMap.servoGet("transfer");
	self.motorL = hardwareMap.dcmotorexGet("flywheelLeft");
	self.motorR = hardwareMap.dcmotorexGet("flywheelRight");
	self.motorL:setMode(DcMotorRunMode.RunUsingEncoder);
	self.motorR:setMode(DcMotorRunMode.RunUsingEncoder);
	self.motorL:setDirection(Direction.Reverse);
end

---@param vel number
function shooter:start(vel)
	self.vel = vel;
	self.motorL:setVelocity(vel);
	self.motorR:setVelocity(vel);
end

local shooterVelMap = {
	{ -001, -001, -001, -001, 0840, 0840 },
	{ 1120, 1120, -001, 0840, 0840, 0840 },
	{ 1120, 1120, 0860, 0860, 0840, 0840 },
	{ 1180, 1180, 0940, 0940, 0940, 0940 },
	{ 1180, 1180, -001, 0940, 0940, 0940 },
	{ -001, -001, -001, -001, 0940, 0940 }
};

---@param x number
---@param y number
---@param dist number
function shooter:updateVelocity(x, y, dist)
	if (x < 0) then
		x = -x;
	end

	local vel = 0;
	local tx = math.floor(x / 24);
	local ty = math.floor(y / 24);
	vel = shooterVelMap[tx + 1][ty + 1];

	if(vel == -1) then
		return;
	end
	if (not self.running) then
		vel = 0;
	end
	if (vel ~= self.vel) then
		if (vel > 1000) then
			if (self.running) then
				intake.speed = 0.9;
				intake:forward(0.9);
			end
			self.motorL:setPidf(320, 3, 0, 7.5);
			self.motorR:setPidf(320, 3, 0, 7.5);
		else
			if (self.running) then
				intake.speed = 1.0;
			end
			self.motorL:setPidf(320, 3, 0, 0);
			self.motorR:setPidf(320, 3, 0, 0);
		end
		self:start(vel);
	end
end

function shooter:stop()
	self.motorL:setPower(0);
	self.motorR:setPower(0);
end

---@param et number
function shooter:shoot(et)
	self.state = shooterState.Open;
	self.count = 1;
	self.gate:setPosition(self.gateOpen);
	self.time = et;
	self.delay = self.openDelay;
	intake:forward();
end

---@param et number
---@param count number
function shooter:shootNum(et, count)
	self.state = shooterState.Open;
	self.count = count;
	if (logFile ~= nil) then
		logFile:write(
			("%7.2f | gate open:   left %4d %5.3f %5.2f, right %4d %5.3f %5.2f, bat: %5.2f\n")
			:format(et, self.motorL:getVelocity(), self.motorL:getVelocity(), self.motorL:getCurrent(),
				self.motorR:getVelocity(), self.motorR:getPower(),
				self.motorR:getCurrent(), chub:getVoltage()
			)
		);
	end
	--actionPane:addData("open gate", et);
	self.gate:setPosition(self.gateOpen);
	self.time = et;
	self.delay = self.openDelay;
end

---@param et number
---@return boolean
function shooter:update(et)
	if (self.time == nil) then
		return false;
	end

	if (self.time + self.delay <= et) then
		if (self.state == shooterState.Open) then
			self.gate:setPosition(self.gateClosed);
			if (logFile ~= nil) then
				logFile:write(
					("%7.2f | gate closed: left %4d %5.3f %5.2f, right %4d %5.3f %5.2f, bat: %5.2f\n")
					:format(et, self.motorL:getVelocity(), self.motorL:getVelocity(), self.motorL:getCurrent(),
						self.motorR:getVelocity(), self.motorR:getPower(),
						self.motorR:getCurrent(), chub:getVoltage()
					)
				);
			end
			self.time = et;
			self.state = shooterState.Close;
			self.delay = self.closeDelay;
		else
			self.state = shooterState.Open;
			if (self.count == 1) then
				self.count = 0;
				self.time = nil;
				return true;
			else
				self:shootNum(et, self.count - 1);
				if (self.count == 1) then
					self.delay = self.openDelayEnd;
				else
					self.delay = self.openDelay;
				end
			end
		end
	end
	return false;
end

function shooter:ready()
	local vel = self.motorL:getVelocity();
	self.velocityDataPoints[5] = self.velocityDataPoints[4];
	self.velocityDataPoints[4] = self.velocityDataPoints[3];
	self.velocityDataPoints[3] = self.velocityDataPoints[2];
	self.velocityDataPoints[2] = self.velocityDataPoints[1];
	self.velocityDataPoints[1] = vel;
	local sum = self.velocityDataPoints[5] - self.velocityDataPoints[1];
	local slope = sum / 5;
	return vel >= self.vel - 40 and vel <= self.vel + 40 and slope >= -20 and slope <= 20;
end

function shooter:close()
	self.gate:setPosition(self.gateClosed);
end

function shooter:telem()
	robotPane:addData("shooterTarV", self.vel);
	robotPane:addData("shooterVelL", self.motorL:getVelocity());
	robotPane:addData("shooterVelR", self.motorR:getVelocity());
end