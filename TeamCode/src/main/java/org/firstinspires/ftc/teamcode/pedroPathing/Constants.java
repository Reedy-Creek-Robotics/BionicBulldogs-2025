package org.firstinspires.ftc.teamcode.pedroPathing;

import com.pedropathing.control.FilteredPIDFCoefficients;
import com.pedropathing.control.PIDFCoefficients;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.ftc.FollowerBuilder;
import com.pedropathing.ftc.drivetrains.MecanumConstants;
import com.pedropathing.ftc.localization.Encoder;
import com.pedropathing.ftc.localization.constants.TwoWheelConstants;
import com.pedropathing.paths.PathConstraints;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Constants
{
	public static FollowerConstants followerConstants = new FollowerConstants()
					.mass(13)
					.forwardZeroPowerAcceleration(-32.247 / 2)
					.lateralZeroPowerAcceleration(-63.754 / 2)
					.centripetalScaling(0.0005)
					.drivePIDFCoefficients(new FilteredPIDFCoefficients(0.015, 0, 0.00001, 0.6, 0.01))
					.translationalPIDFCoefficients(new PIDFCoefficients(0.2, 0.0, 0.0, 0.0))
					.headingPIDFCoefficients(new PIDFCoefficients(0.75, 0, 0, 0.1));

	public static MecanumConstants driveConstants = new MecanumConstants()
					.leftFrontMotorName("frontLeft")
					.rightFrontMotorName("frontRight")
					.leftRearMotorName("backLeft")
					.rightRearMotorName("backRight")
					.leftFrontMotorDirection(DcMotorSimple.Direction.REVERSE)
					.rightFrontMotorDirection(DcMotorSimple.Direction.FORWARD)
					.leftRearMotorDirection(DcMotorSimple.Direction.REVERSE)
					.rightRearMotorDirection(DcMotorSimple.Direction.FORWARD)
					.xVelocity(66.7)
					.yVelocity(51.9);

	public static TwoWheelConstants localizerConstants = new TwoWheelConstants()
					//.forwardTicksToInches(0.001977958)
					//.strafeTicksToInches(0.0019282840)
					.forwardTicksToInches(0.0019707)
					.strafeTicksToInches(0.001980123)
					.forwardPodY(0)
					.strafePodX(0)
					.forwardEncoder_HardwareMapName("backRight")
					.strafeEncoder_HardwareMapName("frontLeft")
					.forwardEncoderDirection(Encoder.FORWARD)
					.strafeEncoderDirection(Encoder.FORWARD)
					.IMU_HardwareMapName("imu")
					.IMU_Orientation(
									new RevHubOrientationOnRobot(
													RevHubOrientationOnRobot.LogoFacingDirection.UP,
													RevHubOrientationOnRobot.UsbFacingDirection.RIGHT
									)
					);

	public static PathConstraints pathConstraints = new PathConstraints(0.995, 0.1, 0.1, 0.007, 500, 1, 10, 1);

	public static Follower createFollower(HardwareMap hardwareMap)
	{
		return new FollowerBuilder(followerConstants, hardwareMap)
						.mecanumDrivetrain(driveConstants)
						.twoWheelLocalizer(localizerConstants)
						.pathConstraints(pathConstraints)
						.build();
	}
}