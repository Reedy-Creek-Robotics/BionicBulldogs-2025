require("modules.class");
require("modules.utils");

---@enum LocalizerMode
LocalizerMode = {
	imu = 0,
	pinpoint = 1
};

---@class HDrive
---@field frontLeft DcMotor
---@field frontRight DcMotor
---@field backLeft DcMotor
---@field backRight DcMotor
---@field imu Imu?
---@field pinpoint Pinpoint?
---@field localizerMode LocalizerMode
HDrive = {
	maxPower = 1,
	localizerMode = LocalizerMode.imu
};

---@return HDrive
function HDrive.new()
	local motorNames = { "frontLeft", "frontRight", "backLeft", "backRight" };
	local m = new(HDrive);
	for _, name in pairs(motorNames) do
		m[name] = hardwareMap.dcmotorGet(name);
		m[name]:setZeroPowerBehavior(DcMotorZeroPowerBehavior.Brake);
	end
	m.frontRight:setDirection(Direction.Reverse);
	m.backRight:setDirection(Direction.Reverse);
	return m;
end

---@param forward number
---@param right number
---@param rotate number
function HDrive:driveFr(forward, right, rotate)
	local heading = 0;
	if (self.localizerMode == LocalizerMode.imu) then
		if (self.imu == nil) then
			error("imu must not be nil");
		end
		heading = self.imu:getHeading();
	else
		if (self.pinpoint == nil) then
			error("pinpoint must not be nil");
		end
		heading = self.pinpoint:getHeading();
	end
	local f = forward * math.cos(-heading) - right * math.sin(-heading);
	local r = forward * math.sin(-heading) + right * math.cos(-heading);
	self:drive(f, r, rotate);
end

---@param forward number
---@param right number
---@param rotate number
function HDrive:drive(forward, right, rotate)
	local flPwr = forward - right - rotate;
	local frPwr = forward + right + rotate;
	local blPwr = forward + right - rotate;
	local brPwr = forward - right + rotate;

	local intendedMaxPower = math.max(flPwr, frPwr, blPwr, brPwr);

	if (intendedMaxPower >= self.maxPower and intendedMaxPower ~= 0) then
		local powerCorrectionRatio = self.maxPower / intendedMaxPower;
		flPwr = flPwr * powerCorrectionRatio;
		frPwr = frPwr * powerCorrectionRatio;
		blPwr = blPwr * powerCorrectionRatio;
		brPwr = brPwr * powerCorrectionRatio;
	end
	self.frontLeft:setPower(flPwr);
	self.frontRight:setPower(frPwr);
	self.backLeft:setPower(blPwr);
	self.backRight:setPower(brPwr);
end