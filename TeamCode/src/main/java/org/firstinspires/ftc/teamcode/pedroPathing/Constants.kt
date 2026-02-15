package org.firstinspires.ftc.teamcode.pedroPathing;

import com.pedropathing.follower.Follower
import com.pedropathing.follower.FollowerConstants
import com.pedropathing.ftc.FollowerBuilder
import com.pedropathing.ftc.drivetrains.MecanumConstants
import com.pedropathing.ftc.localization.constants.PinpointConstants
import com.pedropathing.paths.PathConstraints
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit

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
		.leftFrontMotorDirection(DcMotorSimple.Direction.REVERSE)
		.rightFrontMotorDirection(DcMotorSimple.Direction.FORWARD)
		.leftRearMotorDirection(DcMotorSimple.Direction.REVERSE)
		.rightRearMotorDirection(DcMotorSimple.Direction.FORWARD)
		.xVelocity(76.9)
		.yVelocity(57.4);


	val localizerConstants: PinpointConstants = PinpointConstants()
		.forwardPodY(-6.5)
		.strafePodX(0.0)
		.distanceUnit(DistanceUnit.INCH)
		.hardwareMapName("pinpoint")
		.encoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD)
		.forwardEncoderDirection(GoBildaPinpointDriver.EncoderDirection.REVERSED)
		.strafeEncoderDirection(GoBildaPinpointDriver.EncoderDirection.FORWARD);

	//val localizerConstants: TwoWheelConstants = TwoWheelConstants()
	//	.forwardEncoder_HardwareMapName("backLeft")
	//	.strafeEncoder_HardwareMapName("frontLeft")
	//	.forwardEncoderDirection(Encoder.REVERSE)
	//	.strafeEncoderDirection(Encoder.FORWARD)
	//	.IMU_HardwareMapName("imu")
	//	.IMU_Orientation(
	//		RevHubOrientationOnRobot(
	//			RevHubOrientationOnRobot.LogoFacingDirection.LEFT,
	//			RevHubOrientationOnRobot.UsbFacingDirection.UP
	//		)
	//	)
	//	.forwardPodY(6.75)
	//	.strafePodX(0.0)
	//	.forwardTicksToInches(0.0020127)
	//	.strafeTicksToInches(0.002023);

	val pathConstraints = PathConstraints(0.99, 100.0, 0.4, 1.0);

	@JvmStatic
	fun createFollower(hardwaremap: HardwareMap): Follower =
		FollowerBuilder(followerConstants, hardwaremap)
			.pathConstraints(pathConstraints)
			.mecanumDrivetrain(mecanumConstants)
			.pinpointLocalizer(localizerConstants)
			.build();
};
