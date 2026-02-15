package org.firstinspires.ftc.teamcode.modules.luaHardware

import com.minerkid08.dynamicopmodeloader.FunctionBuilder
import com.minerkid08.dynamicopmodeloader.LuaError
import com.minerkid08.dynamicopmodeloader.LuaType
import com.minerkid08.dynamicopmodeloader.OpmodeLoaderFunction
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver
import com.qualcomm.hardware.sparkfun.SparkFunOTOS
import com.qualcomm.robotcore.hardware.CRServo
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.IMU
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.teamcode.LightStripDriver

class LuaHardwaremap(private val hardwareMap: HardwareMap)
{
	companion object
	{
		fun init(builder: FunctionBuilder, hardwareMap: HardwareMap)
		{
			builder.addClassAsClass(LuaDcMotor::class.java);
			builder.addClassAsClass(LuaDcMotorEx::class.java);
			builder.addClassAsClass(LuaServo::class.java);
			builder.addClassAsClass(LuaCrServo::class.java);
			builder.addClassAsClass(LuaImu::class.java);
			builder.addClassAsClass(LuaSparkFunImu::class.java);
			builder.addClassAsClass(LuaPinpoint::class.java);
			builder.addClassAsClass(LuaChub::class.java);

			builder.pushTable("hardwareMap");
			builder.addObjectAsGlobal(LuaHardwaremap(hardwareMap));
			builder.popTable();
		}
	}

	@OpmodeLoaderFunction
	fun dcmotorGet(name: String): LuaDcMotor
	{
		if (hardwareMap.dcMotor.contains(name))
			return LuaDcMotor(hardwareMap.dcMotor.get(name));
		throw LuaError("cannot find dcmotor with the name '$name'");
	}

	@OpmodeLoaderFunction
	fun dcmotorexGet(name: String): LuaDcMotorEx
	{
		if (hardwareMap.dcMotor.contains(name))
			return LuaDcMotorEx(hardwareMap.dcMotor.get(name) as DcMotorEx);
		throw LuaError("cannot find dcmotorex with the name '$name'");
	}

	@OpmodeLoaderFunction
	fun crservoGet(name: String): LuaCrServo
	{
		if (hardwareMap.crservo.contains(name))
			return LuaCrServo(hardwareMap.crservo.get(name));
		throw LuaError("cannot find crservo with the name '$name'");
	}

	@OpmodeLoaderFunction
	fun servoGet(name: String): LuaServo
	{
		if (hardwareMap.servo.contains(name))
			return LuaServo(hardwareMap.servo.get(name));
		throw LuaError("cannot find servo with the name '$name'");
	}

	@OpmodeLoaderFunction
	fun imuGet(): LuaImu
	{
		//if (hardwareMap.i2cDevice.contains("imu"))
			return LuaImu(hardwareMap.get(IMU::class.java, "imu"));
		//throw LuaError("cannot find imu with the name 'imu'");
	}

	@OpmodeLoaderFunction
	fun pinpointGet(): LuaPinpoint
	{
		//if (hardwareMap.i2cDevice.contains("pinpoint"))
			return LuaPinpoint(hardwareMap.get(GoBildaPinpointDriver::class.java, "pinpoint"));
		//throw LuaError("cannot find pinpoint with the name 'pinpoint'");
	}

	@OpmodeLoaderFunction
	fun spimuGet(): LuaSparkFunImu
	{
		if (hardwareMap.i2cDevice.contains("imu2"))
			return LuaSparkFunImu(hardwareMap.get(SparkFunOTOS::class.java, "imu2"));
		throw LuaError("cannot find sparkfun imu with the name 'imu2'");
	}

	@OpmodeLoaderFunction
	fun chubGet() = LuaChub(hardwareMap);

	@OpmodeLoaderFunction
	fun ledGet(): LightStripDriver
	{
		return hardwareMap.get(LightStripDriver::class.java, "lightStrip") as LightStripDriver;
	}
}

class LuaCrServo(private val m: CRServo)
{
	@OpmodeLoaderFunction
	fun setPower(power: Double)
	{
		m.power = power;
	}

	@OpmodeLoaderFunction
	fun setDirection(dir: Int)
	{
		m.direction =
			if (dir == 1) DcMotorSimple.Direction.FORWARD else DcMotorSimple.Direction.REVERSE;
	}
}

class LuaServo(private val m: Servo)
{
	@OpmodeLoaderFunction
	fun setPosition(power: Double)
	{
		m.position = power;
	}

	@OpmodeLoaderFunction
	fun setDirection(dir: Int)
	{
		m.direction = if (dir == 1) Servo.Direction.FORWARD else Servo.Direction.REVERSE;
	}
}

fun buildServo(builder: FunctionBuilder)
{
	builder.createClass("Servo");
	builder.addClassFunction(Servo::class.java, "setDirection", LuaType.Void, listOf(LuaType.Object(DcMotorSimple.Direction::class.java)));
	builder.addClassFunction(Servo::class.java, "getDirection", LuaType.Object(DcMotorSimple.Direction::class.java));
	builder.addClassFunction(Servo::class.java, "setPosition", LuaType.Void, listOf(LuaType.Double));
	builder.addClassFunction(Servo::class.java, "getPosition", LuaType.Double);
	builder.addClassFunction(Servo::class.java, "scaleRange", LuaType.Void, listOf(LuaType.Double, LuaType.Double));

	builder.createClass("CRServo");
	builder.addClassFunction(CRServo::class.java, "setDirection", LuaType.Void, listOf(LuaType.Object(DcMotorSimple.Direction::class.java)));
	builder.addClassFunction(CRServo::class.java, "getDirection", LuaType.Object(DcMotorSimple.Direction::class.java));
	builder.addClassFunction(CRServo::class.java, "getPower", LuaType.Void, listOf(LuaType.Double));
	builder.addClassFunction(CRServo::class.java, "setPower", LuaType.Double);
}