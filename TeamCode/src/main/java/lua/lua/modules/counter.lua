---@class counter
---@field sensor BeamBreak
---@field count number
---@field debounceTimer number
---@field prevState boolean
---@field ret boolean
counter = {
	count = 0,
	debounceTimer = 0,
	stopIntakeTimer = 0,
	prevState = false,
	stoppedIntake = false,
	stopIntake = true
};

function counter:init()
	self.sensor = hardwareMap.beamBreakGet("intakeBreak");
end

---@param et number
function counter:update(et)
	self.ret = false;
	local state = self.sensor:isPressed();

	if (not self.prevState and state) then
		if (self.failsafeTimer == nil) then
			self.count = self.count + 1;
		end
		self.debounceTimer = et;
		self.prevState = true;
		self.failsafeTimer = nil;
	end

	if (self.count == 3 or (state and et - self.debounceTimer > 0.5)) then
		if (not self.stoppedIntake) then
			self.stoppedIntake = true;
			if (self.count < 3) then
				self.count = 3;
			end
			self.stopIntakeTimer = et;
			if (gamepad ~= nil) then
				gamepad.vibrate(1, 1, 500);
			end
		end
	end

	if (et - self.stopIntakeTimer > 0.1 and self.stopIntakeTimer > 0) then
		if (self.stopIntake) then
			intake:stop();
		end
		self.ret = true;
		self.stopIntakeTimer = 0;
	end

	if (not state and self.prevState) then
		self.prevState = false;
		self.failsafeTimer = et;
	end

	if (self.failsafeTimer ~= nil) then
		if (et - self.failsafeTimer > 0) then
			self.failsafeTimer = nil;
		end
	end
end

function counter:reset()
	self.count = 0;
	self.failsafeTimer = 0;
	self.stoppedIntake = false;
end

---@param et number
function counter:updatLeds(et)
	if (et - self.debounceTimer > 1) then
		self.count = self.count + 1;
		if (self.count == 4) then
			self.count = 0;
		end
		self.debounceTimer = et;
	end
end