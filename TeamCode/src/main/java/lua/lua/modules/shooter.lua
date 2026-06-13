require("modules.utils");
require("modules.vec2");

---@enum shooterState
shooterState = {
	Open = 0,
	Close = 1,
	Wait = 2,
	Rev = 3
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
	gateOpen = 0,
	gateClosed = 0.25,
	openDelay = 1.3, -- 0.16
	openDelayEnd = 1,
	closeDelay = 0.0, -- 0.35
	state = shooterState.Close,
	waitForReady = false,
	velocityDataPoints = { 0, 0, 0, 0, 0 },
	running = false,
	onField = true,
	vel = 0,
	velOff = 0,
	inBack = false
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
	self.motorL:setVelocity(vel + self.velOff);
	self.motorR:setVelocity(vel + self.velOff);
end

--local shooterVelMap = {
--	{ -001, -001, -001, -001, 0700, 0700 },
--	{ 0940, 0940, -001, 0700, 0700, 0700 },
--	{ 0940, 0940, 0720, 0720, 0700, 0700 },
--	{ 0970, 0970, 0800, 0780, 0780, 0780 },
--	{ 0970, 0970, -001, 0780, 0780, 0780 },
--	{ -001, -001, -001, -001, 0780, 0780 }
--};

local shooterVelMap = {
	{ -001, -001, -001, -001, 0660, 0660 },
	{ 0860, 0860, -001, 0660, 0660, 0660 },
	{ 0860, 0860, 0680, 0680, 0660, 0660 },
	{ 0900, 0900, 0730, 0730, 0700, 0700 },
	{ 0900, 0900, -001, 0780, 0760, 0780 },
	{ -001, -001, -001, -001, 0780, 0780 }
};

--local shooterVelMap = {
--	{ -001, -001, -001, -001, 0740, 0740 },
--	{ 0960, 0940, -001, 0740, 0740, 0740 },
--	{ 0960, 0960, 0760, 0740, 0740, 0740 },
--	{ 1000, 1000, 0760, 0760, 0780, 0780 },
--	{ 1000, 1000, -001, 0920, 0960, 0860 },
--	{ -001, -001, -001, -001, 0960, 0860 }
--};

---@param b boolean
function shooter:setInBack(b)
	self.inBack = b;
end

---@param x number
---@param y number
---@param vx number?
---@param vy number?
function shooter:updateVelocity(x, y, vx, vy)
	dashboard.addDataf("targetVel", self.vel + self.velOff);
	if (self.state == shooterState.Rev) then
		return;
	end

	if (x < 0) then
		x = -x;
		--vx = -vx;
	end

	local vel = 0;
	local tx = math.floor(x / 24);
	local ty = math.floor(y / 24);

	dashboard.addDataf("tx", tx);
	dashboard.addDataf("ty", ty);

	local v1 = { tx, ty };
	local v2 = { vx, vy };

	local len = 0;
	local dot = 0;
	if (vx ~= nil) then
		len = v2[1] * v2[1] + v2[2] * v2[2];

		if (len > 20 * 20) then
			vec2.normalize(v1);
			vec2.normalize(v2);
			dot = vec2.dot(v1, v2);
			dot = -dot;
		end
	end

	self.inBack = (ty < 2);

	if (tx < 0 or tx >= 6 or ty < 0 or ty >= 6) then
		if (self.onField == true) then
			actionPane:addLine("robot left field");
		end
		self.onField = false;
		return;
	end
	self.onField = true;
	vel = shooterVelMap[tx + 1][ty + 1];

	if (vel == -1) then
		return;
	end

	local velMod = 0;
	if (vx ~= nil) then
		if (dot > 0) then
			velMod = vel * dot * 0.125;
		else
			velMod = vel * dot * 0.05;
		end
	end

	if (math.abs(dot) > 0.2) then
		vel = vel + velMod;
	end

	if (not self.running) then
		vel = 0;
	end
	if (vel ~= self.vel) then
		self:start(vel);
	end
end

function shooter:stop()
	self.motorL:setPower(0);
	self.motorR:setPower(0);
end

---@param et number
function shooter:reverse(et)
	self.time = et;
	self.delay = 1;
	self.motorL:setVelocity(-2000);
	self.motorR:setVelocity(-2000);
	intake:reverse();
	self.gate:setPosition(self.gateOpen);
	self.state = shooterState.Rev;
end

---@param et number
function shooter:shoot(et)
	self.state = shooterState.Open;
	self.count = 1;
	self.gate:setPosition(self.gateOpen);
	self.time = et;
	self.delay = self.openDelay;
	if (self.inBack) then
		intake:forward(0.7);
	else
		intake:forward(0.95);
	end
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
	if (self.inBack) then
		intake:forward(0.7);
	else
		intake:forward(0.95);
	end
end

---@param et number
---@return boolean
function shooter:update(et)
	if (self.time == nil) then
		return false;
	end

	--if(self.motorL:getVelocity() < self.vel - 40) then
	--	self.velOff = 300;
	--	self:start(self.vel);
	--	actionPane:addLine("velOff = 40");
	--end
	if (self.time + 0.25 <= et) then
		self.velOff = 300;
		self:start(self.vel);
		actionPane:addLine("velOff = 40");
	end
	if (self.time + self.delay <= et) then
		if (self.state == shooterState.Rev) then
			intake:stop();
			self.gate:setPosition(self.gateClosed);
			self.state = shooterState.Close;
			self.time = nil;
			return false;
		end

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
				if (self.count == 1) then
					self.count = 0;
					self.time = nil;
					self.velOff = 0;
					self:start(self.vel);
					actionPane:addLine("velOff = 0");
					intake:forward();
					return true;
				end
			end
			self.time = et;
			self.state = shooterState.Close;
			self.delay = self.closeDelay;
		else
			self.state = shooterState.Open;
			if (self.count == 1) then
				self.count = 0;
				self.time = nil;
				self.velOff = 0;
				self:start(self.vel);
				actionPane:addLine("velOff = 0");
				intake:forward();
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
	return vel >= self.vel - 40 and vel <= self.vel + 40; --and slope >= -10 and slope <= 10;
end

function shooter:close()
	self.gate:setPosition(self.gateClosed);
	self.velOff = 0;
	self:start(self.vel);
end

function shooter:telem()
	robotPane:addData("shooterTarV", self.vel);
	robotPane:addData("shooterVelL", self.motorL:getVelocity());
	robotPane:addData("shooterVelR", self.motorR:getVelocity());
end