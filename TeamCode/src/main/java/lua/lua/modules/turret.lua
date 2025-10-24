require("modules.utils")

---@class turret
---@field motor DcMotorEx
---@field gate Servo
---@field gateClosed number
---@field gateOpen number
---@field time number
---@field angle integer
turret = {
	gateOpen = 0.85,
	gateClosed = 1,
}

--Currently assuming that the turret is 180 degrees range of motion for simplicity

function turret:init()
	self.gate = hardwareMap.servoGet("transfer");
	self.gate2 = hardwareMap.dcmotorexGet("shooter");
	self.rotator = hardwareMap.dcmotorexGet("rotator");

	self.gate2:setMode(DcMotorRunMode.RunUsingEncoder);
	self.gate2:setDirection(Direction.Reverse);

	self.rotator:setMode(DcMotorRunMode.RunToPosition);
	--Turret should run clockwise, which is opposite of motor's counterclockwise
	self.rotator:setDirection(Direction.Reverse);
end

---@param vel number
function turret:setVelocity(vel)
	self.gate2:setVelocity(vel);
end

function turret:stop()
	self.gate2:setPower(0);
end

---@param et number
function turret:shoot(et)
	self.gate:setPosition(self.gateOpen);
	self.time = et;
end

---@param et number
function turret:update(et)
	if (self.time ~= nil) then
	--Automatically close gate after 0.2 seconds, then stop counting estimated time (set to nil)
		if (self.time + 0.2 <= et) then
			self.gate:setPosition(self.gateClosed);
			self.time = nil;
		end
	end
end

function turret:close()
	self.gate:setPosition(self.gateClosed);
end

---@param angle number
function turret:rotate(angle)
	angle = self.angle -- * (ticksPin/GearRatio) | Manually round the const to an int later
	self.rotator:setTargetPosition(angle)
end