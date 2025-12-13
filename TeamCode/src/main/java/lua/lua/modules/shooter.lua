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
shooter = {
	gateOpen = 0.3,
	gateClosed = 0,
	openDelay = 0.15,
	closeDelay = 0.3,
	state = shooterState.Close,
	waitForReady = false
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
	self.motorL:setPower(1);
	self.motorR:setPower(1);
	self.motorL:setVelocity(vel);
	self.motorR:setVelocity(vel);
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
end

---@param et number
---@param count number
function shooter:shootNum(et, count)
	self.state = shooterState.Open;
	self.count = count;
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
				self.delay = self.openDelay;
			end
		end
	end
	return false;
end

function shooter:ready()
	local vel = self.motorL:getVelocity();
	return vel >= self.vel - 40 and vel <= self.vel + 40;
end

function shooter:close()
	self.gate:setPosition(self.gateClosed);
end

function shooter:telem()
	robotPane:addData("shooterCurL", shooter.motorL:getCurrent());
	robotPane:addData("shooterVelL", shooter.motorL:getVelocity());
	robotPane:addData("shooterCurR", shooter.motorR:getCurrent());
	robotPane:addData("shooterVelR", shooter.motorR:getVelocity());
end