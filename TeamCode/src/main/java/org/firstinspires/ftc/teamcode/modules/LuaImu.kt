package org.firstinspires.ftc.teamcode.modules

import com.minerkid08.dynamicopmodeloader.FunctionBuilder
import com.minerkid08.dynamicopmodeloader.LuaType
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot
import com.qualcomm.hardware.sparkfun.SparkFunOTOS
import com.qualcomm.robotcore.hardware.IMU
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit

class LuaImu(private val imu: IMU)
{
	companion object
	{
		fun init(builder: FunctionBuilder)
		{
			builder.addClassFunction(LuaImu::class.java, "getHeading", LuaType.Double);
			builder.addClassFunction(LuaImu::class.java, "resetHeading");
		}
	}

	init
	{
		imu.initialize(
			IMU.Parameters(
				RevHubOrientationOnRobot(
					RevHubOrientationOnRobot.LogoFacingDirection.UP,
					RevHubOrientationOnRobot.UsbFacingDirection.LEFT
				)
			)
		);
		imu.resetYaw();
	}

	fun getHeading(): Double = imu.robotYawPitchRollAngles.getYaw(AngleUnit.RADIANS);
	fun resetHeading()
	{
		imu.resetYaw();
	}
}

class LuaSparkFunImu(private val imu: SparkFunOTOS)
{
	companion object
	{
		fun init(builder: FunctionBuilder)
		{
			builder.addClassFunction(LuaSparkFunImu::class.java, "getHeading", LuaType.Double);
			builder.addClassFunction(LuaSparkFunImu::class.java, "resetHeading");
		}
	}

	init
	{
		imu.initialize();
		imu.angularUnit = AngleUnit.RADIANS;
		imu.angularScalar = 1.0;
		imu.calibrateImu();
		imu.resetTracking();
		imu.position = SparkFunOTOS.Pose2D(0.0, 0.0, 0.0);
	}

	fun getHeading(): Double = imu.position.h;
	fun resetHeading()
	{
		imu.position = SparkFunOTOS.Pose2D(0.0, 0.0, 0.0);
	}
}
