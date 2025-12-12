require("modules.utils")

---@class shooter
---@field motorL DcMotorEx
---@field motorR DcMotorEx
---@field gate Servo
---@field gateClosed number
---@field gateOpen number
---@field time number
---@field vel number
shooter = {
	gateOpen = 0.3,
	gateClosed = 0,
	transferDelay = 0.9
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
	self.gate:setPosition(self.gateOpen);
	self.time = et;
end

---@param et number
---@return boolean
function shooter:update(et)
	if (self.time ~= nil) then
		if (self.time + self.transferDelay <= et) then
			self.gate:setPosition(self.gateClosed);
			self.time = nil;
			turret.reset();
			return true;
		end
	end
	return false;
end

function shooter:ready()
	local vel = self.motorL:getVelocity();
	local dif = vel - (self.prevVel or 40);
	local c = vel > self.vel - 10 and vel < self.vel + 10 and dif < 20;
	self.prevVel = vel;
	return c;
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