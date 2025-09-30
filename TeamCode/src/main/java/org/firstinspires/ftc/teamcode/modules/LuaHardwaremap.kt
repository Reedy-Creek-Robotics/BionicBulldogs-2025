package org.firstinspires.ftc.teamcode.modules

import com.minerkid08.dynamicopmodeloader.FunctionBuilder
import com.minerkid08.dynamicopmodeloader.OpmodeLoaderFunction
import com.qualcomm.hardware.sparkfun.SparkFunOTOS
import com.qualcomm.robotcore.hardware.CRServo
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
			builder.addClassAsClass(LuaDcMotor::class.java)
			builder.addClassAsClass(LuaServo::class.java)
			builder.addClassAsClass(LuaCrServo::class.java)
			builder.addClassAsClass(LuaImu::class.java)
			builder.addClassAsClass(LuaSparkFunImu::class.java)

			builder.pushTable("hardwareMap");
			builder.addObjectAsGlobal(LuaHardwaremap(hardwareMap));
			builder.popTable();
		}
	}

	@OpmodeLoaderFunction
	fun dcmotorGet(name: String) = LuaDcMotor(hardwareMap.dcMotor.get(name));

	@OpmodeLoaderFunction
	fun crservoGet(name: String) = LuaCrServo(hardwareMap.crservo.get(name));

	@OpmodeLoaderFunction
	fun servoGet(name: String) = LuaServo(hardwareMap.servo.get(name));

	@OpmodeLoaderFunction
	fun imuGet(): LuaImu = LuaImu(hardwareMap.get(IMU::class.java, "imu"));

	@OpmodeLoaderFunction
	fun spimuGet(): LuaSparkFunImu =
		LuaSparkFunImu(hardwareMap.get(SparkFunOTOS::class.java, "imu2"));
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
	fun setPos(power: Double)
	{
		m.position = power;
	}

	@OpmodeLoaderFunction
	fun setDirection(dir: Int)
	{
		m.direction = if (dir == 1) Servo.Direction.FORWARD else Servo.Direction.REVERSE;
	}
}
