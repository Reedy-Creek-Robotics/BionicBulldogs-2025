---@class Opmode
---@field name string
---@field init fun()?
---@field start fun(recog: number)?
---@field update fun(dt: number, elapsed: number)?

---@param opmode Opmode
function addOpmode(opmode) end

---@class DcMotor
---@field setPower fun(self: DcMotor, power: number)
---@field setDirection fun(self: DcMotor, dir: number)
---@field setTargetPosition fun(self: DcMotor, target: number)
---@field getPosition fun(self: DcMotor): number
---@field getTargetPositon fun(self: DcMotor): number
---@field setMode fun(self: DcMotor, mode: number): number
---@field setZeroPowerBehavior fun(self: DcMotor, mode: number): number
DcMotor = {};

---@class Servo 
---@field setPosition fun(self: Servo, power: number)
---@field getPosition fun(self: Servo): number
Servo = {};

---@class CrServo
---@field setPower fun(self: CrServo, power: number)
---@field getPower fun(self: CrServo): number
CrServo = {};

---@class Imu 
---@field getHeading fun(self: Imu): number
---@field resetHeading fun(self: Imu)
Imu = {};

hardwareMap = {};

---@param name string
---@return DcMotor
function hardwareMap.dcMotorGet(name) end

---@param name string
---@return Servo
function hardwareMap.servoGet(name) end

---@param name string
---@return CrServo 
function hardwareMap.crServoGet(name) end

---@return Imu
function hardwareMap.imuGet() end

---@return Imu
function hardwareMap.spimuGet() end


gamepad = {};

---@return number
function gamepad.getLeftStickX() end
---@return number
function gamepad.getLeftStickY() end
---@return number
function gamepad.getRightStickX() end
---@return number
function gamepad.getRightStickY() end

---@return boolean 
function gamepad.getDpadUp() end
---@return boolean 
function gamepad.getDpadDown() end
---@return boolean 
function gamepad.getDpadLeft() end
---@return boolean 
function gamepad.getDpadRight() end

---@return boolean 
function gamepad.getTriangle() end
---@return boolean 
function gamepad.getCircle() end
---@return boolean 
function gamepad.getCross() end
---@return boolean 
function gamepad.getSquare() end

---@return number
function gamepad.getLeftTrigger() end
---@return number
function gamepad.getRightTrigger() end
---@return boolean 
function gamepad.getLeftBumper() end
---@return boolean 
function gamepad.getRightBumper() end

---@return boolean 
function gamepad.getStart() end
---@return boolean 
function gamepad.getShare() end
---@return boolean 
function gamepad.getTouchpad() end