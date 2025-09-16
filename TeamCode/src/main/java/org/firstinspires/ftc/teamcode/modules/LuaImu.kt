package org.firstinspires.ftc.teamcode.modules

import com.minerkid08.dynamicopmodeloader.OpmodeLoaderFunction
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot
import com.qualcomm.hardware.sparkfun.SparkFunOTOS
import com.qualcomm.robotcore.hardware.IMU
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit

class LuaImu(private val imu: IMU)
{
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

	@OpmodeLoaderFunction
	fun getHeading(): Double = imu.robotYawPitchRollAngles.getYaw(AngleUnit.RADIANS);

	@OpmodeLoaderFunction
	fun resetHeading()
	{
		imu.resetYaw();
	}
}

class LuaSparkFunImu(private val imu: SparkFunOTOS)
{
	init
	{
		imu.initialize();
		imu.angularUnit = AngleUnit.RADIANS;
		imu.angularScalar = 1.0;
		imu.calibrateImu();
		imu.resetTracking();
		imu.position = SparkFunOTOS.Pose2D(0.0, 0.0, 0.0);
	}

	@OpmodeLoaderFunction
	fun getHeading(): Double = imu.position.h;

	@OpmodeLoaderFunction
	fun resetHeading()
	{
		imu.position = SparkFunOTOS.Pose2D(0.0, 0.0, 0.0);
	}
}
