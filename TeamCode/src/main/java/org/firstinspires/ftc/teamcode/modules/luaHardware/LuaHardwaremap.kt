package org.firstinspires.ftc.teamcode.modules.luaHardware

import com.minerkid08.dynamicopmodeloader.FunctionBuilder
import com.minerkid08.dynamicopmodeloader.OpmodeLoaderFunction
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver
import com.qualcomm.hardware.sparkfun.SparkFunOTOS
import com.qualcomm.robotcore.hardware.CRServo
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.IMU
import com.qualcomm.robotcore.hardware.Servo

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
		error("cannot find dcmotor with the name '$name'");
	}

	@OpmodeLoaderFunction
	fun dcmotorexGet(name: String): LuaDcMotorEx
	{
		if (hardwareMap.dcMotor.contains(name))
			return LuaDcMotorEx(hardwareMap.dcMotor.get(name) as DcMotorEx);
		error("cannot find dcmotorex with the name '$name'");
	}

	@OpmodeLoaderFunction
	fun crservoGet(name: String): LuaCrServo
	{
		if (hardwareMap.crservo.contains(name))
			return LuaCrServo(hardwareMap.crservo.get(name));
		error("cannot find crservo with the name '$name'");
	}

	@OpmodeLoaderFunction
	fun servoGet(name: String): LuaServo
	{
		if (hardwareMap.servo.contains(name))
			return LuaServo(hardwareMap.servo.get(name));
		error("cannot find servo with the name '$name'");
	}

	@OpmodeLoaderFunction
	fun imuGet(): LuaImu
	{
		if (hardwareMap.i2cDevice.contains("imu"))
			return LuaImu(hardwareMap.get(IMU::class.java, "imu"));
		error("cannot find imu with the name 'imu'");
	}

	@OpmodeLoaderFunction
<<<<<<< HEAD:TeamCode/src/main/java/org/firstinspires/ftc/teamcode/modules/luaHardware/LuaHardwaremap.kt
	fun spimuGet(): LuaSparkFunImu =
		LuaSparkFunImu(hardwareMap.get(SparkFunOTOS::class.java, "imu2"));

	@OpmodeLoaderFunction
	fun pinpointGet(): LuaPinpoint=
		LuaPinpoint(hardwareMap.get(GoBildaPinpointDriver::class.java, "pinpoint"));
=======
	fun spimuGet(): LuaSparkFunImu
	{
		if (hardwareMap.i2cDevice.contains("imu2"))
			return LuaSparkFunImu(hardwareMap.get(SparkFunOTOS::class.java, "imu2"));
		error("cannot find imu2 with the name 'imu2'");
	}
>>>>>>> auto:TeamCode/src/main/java/org/firstinspires/ftc/teamcode/modules/LuaHardwaremap.kt
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
