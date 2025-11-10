---@class Opmode
---@field name string
---@field init fun()?
---@field start fun(recog: number)?
---@field update (fun(dt: number, elapsed: number): boolean)?

---@param opmode Opmode
function addOpmode(opmode) end

---@class DcMotor
DcMotor = {};
---@param power number
function DcMotor:setPower(power) end

---@param dir integer
function DcMotor:setDirection(dir) end

---@param target integer
function DcMotor:setTargetPosition(target) end

---@return integer
function DcMotor:getPosition() end

---@return integer
function DcMotor:getTargetPositon() end

---@param mode integer
function DcMotor:setMode(mode) end

---@param mode integer
function DcMotor:setZeroPowerBehavior(mode) end

---@class DcMotorEx : DcMotor
DcMotorEx = {};
---@return number
function DcMotorEx:getCurrent() end
---@return number
function DcMotorEx:getVelocity() end
---@param ticks number
function DcMotorEx:setVelocity(ticks) end

---@class Servo
Servo = {};
---@param power number
function Servo:setPosition(power) end

---@return number
function Servo:getPosition() end

---@class CrServo
CrServo = {};
---@param power number
function CrServo:setPower(power) end

---@return number
function CrServo:getPower() end

---@class Imu
Imu = {};
---@return number
function Imu:getHeading() end

function Imu:resetHeading() end

hardwareMap = {};

---@param name string
---@return DcMotor
function hardwareMap.dcmotorGet(name) end

---@param name string
---@return DcMotorEx
function hardwareMap.dcmotorexGet(name) end

---@param name string
---@return Servo
function hardwareMap.servoGet(name) end

---@param name string
---@return CrServo
function hardwareMap.crservoGet(name) end

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

log = {};

---@param tag string
---@param value string
function log.d(tag, value) end

---@param tag string
---@param value string
function log.i(tag, value) end

---@param tag string
---@param value string
function log.w(tag, value) end

---@param tag string
---@param value string
function log.e(tag, value) end

telemetry = {}

---@param tag string
---@param value string
function telemetry.addData(tag, value)
end

---@param line string
function telemetry.addLine(line)
end

function telemetry.update()
end

require("pedro");