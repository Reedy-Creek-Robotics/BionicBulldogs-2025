require("modules.utils")

---@class shooter
---@field motor DcMotorEx
---@field gate Servo
---@field gateClosed number
---@field gateOpen number
---@field time number
shooter = {
	gateOpen = 0.85,
	gateClosed = 1
}

function shooter:init()
	self.gate = hardwareMap.servoGet("transfer");
	self.motor = hardwareMap.dcmotorexGet("shooter");
	self.motor:setMode(DcMotorRunMode.RunUsingEncoder);
	self.motor:setDirection(Direction.Reverse);
end

---@param vel number
function shooter:start(vel)
	self.motor:setPower(1);
	self.motor:setVelocity(vel);
end

function shooter:stop()
	self.motor:setPower(0);
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
		if (self.time + 0.2 <= et) then
			self.gate:setPosition(self.gateClosed);
			self.time = nil;
			return true;
		end
	end
	return false;
end

function shooter:close()
	self.gate:setPosition(self.gateClosed);
end

function shooter:telem()
	robotPane:addData("shooterPwr2", 1);
	robotPane:addData("shooterCur", shooter.motor:getCurrent());
	robotPane:addData("shooterVel", shooter.motor:getVelocity());
end