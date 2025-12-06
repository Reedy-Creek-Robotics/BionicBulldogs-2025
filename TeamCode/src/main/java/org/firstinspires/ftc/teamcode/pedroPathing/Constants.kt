package org.firstinspires.ftc.teamcode.pedroPathing;

import com.pedropathing.follower.Follower
import com.pedropathing.follower.FollowerConstants
import com.pedropathing.ftc.FollowerBuilder
import com.pedropathing.ftc.drivetrains.MecanumConstants
import com.pedropathing.ftc.localization.Encoder
import com.pedropathing.ftc.localization.constants.TwoWheelConstants
import com.pedropathing.paths.PathConstraints
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap

object Constants
{
	val followerConstants: FollowerConstants = FollowerConstants()
		.mass(12.6)
		.forwardZeroPowerAcceleration(-34.059)
		.lateralZeroPowerAcceleration(-70.539);

	val mecanumConstants: MecanumConstants = MecanumConstants()
		.maxPower(1.0)
		.leftFrontMotorName("frontLeft")
		.rightFrontMotorName("frontRight")
		.leftRearMotorName("backLeft")
		.rightRearMotorName("backRight")
		.leftFrontMotorDirection(DcMotorSimple.Direction.FORWARD)
		.rightFrontMotorDirection(DcMotorSimple.Direction.REVERSE)
		.leftRearMotorDirection(DcMotorSimple.Direction.FORWARD)
		.rightRearMotorDirection(DcMotorSimple.Direction.REVERSE)
		.xVelocity(72.0)
		.yVelocity(57.4);


	val localizerConstants: TwoWheelConstants = TwoWheelConstants()
		.forwardEncoder_HardwareMapName("backLeft")
		.strafeEncoder_HardwareMapName("frontLeft")
		.forwardEncoderDirection(Encoder.REVERSE)
		.strafeEncoderDirection(Encoder.FORWARD)
		.IMU_HardwareMapName("imu")
		.IMU_Orientation(
			RevHubOrientationOnRobot(
				RevHubOrientationOnRobot.LogoFacingDirection.LEFT,
				RevHubOrientationOnRobot.UsbFacingDirection.UP
			)
		)
		.forwardPodY(6.75)
		.strafePodX(0.0)
		.forwardTicksToInches(0.0020127)
		.strafeTicksToInches(0.002023);

	val pathConstraints = PathConstraints(0.99, 100.0, 1.0, 1.0);

	@JvmStatic
	fun createFollower(hardwaremap: HardwareMap): Follower =
		FollowerBuilder(followerConstants, hardwaremap)
			.pathConstraints(pathConstraints)
			.mecanumDrivetrain(mecanumConstants)
			.twoWheelLocalizer(localizerConstants)
			.build();
};