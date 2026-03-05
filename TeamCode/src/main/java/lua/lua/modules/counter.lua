---@class counter
---@field sensor BeamBreak
---@field count number
---@field time number
---@field prevState boolean
counter = {
	count = 0,
	time = 0,
	prevState = false,
	stoppedIntake = false
};

function counter:init()
	self.sensor = hardwareMap.beamBreakGet("intakeBreak");
end

---@param et number
function counter:update(et)
	local state = self.sensor:isPressed();

	if (not self.prevState and state) then
		if (self.time2 == nil) then
			self.count = self.count + 1;
		end
		self.time = et;
		self.prevState = true;
		self.time2 = nil;
	end

	if (self.count == 3 or (state and et - self.time > 0.5)) then
		if (not self.stoppedIntake) then
			--intake:stop();
			self.stoppedIntake = true;
			if (self.count < 3) then
				self.count = 3;
			end
			gamepad.vibrate(1, 1, 500);
		end
	end

	if (not state and self.prevState) then
		self.prevState = false;
		self.time2 = et;
	end

	if (self.time2 ~= nil) then
		if (et - self.time2 > 0) then
			self.time2 = nil;
		end
	end
end

function counter:reset()
	self.count = 0;
	self.stoppedIntake = false;
end

---@param et number
function counter:updatLeds(et)
	if (et - self.time > 1) then
		self.count = self.count + 1;
		if (self.count == 4) then
			self.count = 0;
		end
		self.time = et;
	end
end