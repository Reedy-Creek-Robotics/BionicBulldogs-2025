require("class");
require("utils");

---@class HDrive
---@field frontLeft DcMotor
---@field frontRight DcMotor
---@field backLeft DcMotor
---@field backRight DcMotor
---@field imu Imu?
HDrive = {
	maxPower = 1
};

---@return HDrive
function HDrive.new()
	local motorNames = { "frontLeft", "frontRight", "backLeft", "backRight" };
	local m = new(HDrive);
	for k, name in pairs(motorNames) do
		m[name] = hardwareMap.dcMotorGet(name);
	end
	return m;
end

---@param forward number
---@param right number
---@param rotate number
function HDrive:driveFr(forward, right, rotate)
	if (self.imu == nil) then
		error("imu must not be nil");
	end
	local heading = self.imu:getHeading()
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