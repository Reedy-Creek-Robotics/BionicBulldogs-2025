package org.firstinspires.ftc.teamcode.modules

import com.minerkid08.dynamicopmodeloader.FunctionBuilder
import com.minerkid08.dynamicopmodeloader.LuaType
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
			LuaDcMotor.init(builder);
			LuaCrServo.init(builder);
			LuaImu.init(builder);

			builder.pushTable("hardwareMap");
			builder.setCurrentObject(LuaHardwaremap(hardwareMap));
			builder.addGlobalFunction(
				"dcmotorGet",
				LuaType.Object(LuaDcMotor::class.java),
				listOf(LuaType.String)
			);
			builder.addGlobalFunction(
				"crservoGet",
				LuaType.Object(LuaCrServo::class.java),
				listOf(LuaType.String)
			);
			builder.addGlobalFunction(
				"servoGet",
				LuaType.Object(LuaServo::class.java),
				listOf(LuaType.String)
			);
			builder.addGlobalFunction("imuGet", LuaType.Object(LuaImu::class.java));
			builder.addGlobalFunction("spimuGet", LuaType.Object(LuaSparkFunImu::class.java));
			builder.popTable();
		}
	}

	fun dcmotorGet(name: String) = LuaDcMotor(hardwareMap.dcMotor.get(name));
	fun crservoGet(name: String) = LuaCrServo(hardwareMap.crservo.get(name));
	fun servoGet(name: String) = LuaServo(hardwareMap.servo.get(name));
	fun imuGet(): LuaImu = LuaImu(hardwareMap.get(IMU::class.java, "imu"));
	fun spimuGet(): LuaSparkFunImu =
		LuaSparkFunImu(hardwareMap.get(SparkFunOTOS::class.java, "imu2"));
}

class LuaCrServo(private val m: CRServo)
{
	companion object
	{
		fun init(builder: FunctionBuilder)
		{
			builder.addClassFunction(
				LuaCrServo::class.java,
				"setPower",
				argTypes = listOf(LuaType.Double)
			);
			builder.addClassFunction(
				LuaCrServo::class.java,
				"setDirection",
				argTypes = listOf(LuaType.Int)
			);
		}
	}

	fun setPower(power: Double)
	{
		m.power = power;
	}

	fun setDirection(dir: Int)
	{
		m.direction =
			if (dir == 1) DcMotorSimple.Direction.FORWARD else DcMotorSimple.Direction.REVERSE;
	}
}

class LuaServo(private val m: Servo)
{
	companion object
	{
		fun init(builder: FunctionBuilder)
		{
			builder.addClassFunction(
				LuaCrServo::class.java,
				"setPos",
				argTypes = listOf(LuaType.Double)
			);
			builder.addClassFunction(
				LuaCrServo::class.java,
				"setDirection",
				argTypes = listOf(LuaType.Int)
			);
		}
	}

	fun setPower(power: Double)
	{
		m.position = power;
	}

	fun setDirection(dir: Int)
	{
		m.direction = if (dir == 1) Servo.Direction.FORWARD else Servo.Direction.REVERSE;
	}
}
