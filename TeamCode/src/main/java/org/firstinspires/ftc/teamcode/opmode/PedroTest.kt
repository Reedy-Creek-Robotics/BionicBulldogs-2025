package org.firstinspires.ftc.teamcode.opmode

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.pedropathing.follower.Follower
import com.pedropathing.localization.Pose
import com.pedropathing.pathgen.BezierCurve
import com.pedropathing.pathgen.BezierLine
import com.pedropathing.pathgen.PathChain
import com.pedropathing.pathgen.Point
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.firstinspires.ftc.teamcode.pedroPathing.constants.FConstants
import org.firstinspires.ftc.teamcode.pedroPathing.constants.LConstants

@Autonomous
class PedroTest : LinearOpMode()
{
	lateinit var follower: Follower;
	fun runPath(path: PathChain)
	{
		follower.followPath(path);
		while (opModeIsActive() && follower.isBusy)
		{
			follower.update();
			follower.telemetryDebug(telemetry);
		}
	}

	override fun runOpMode()
	{
		telemetry = MultipleTelemetry(telemetry, FtcDashboard.getInstance().telemetry);

		follower = Follower(hardwareMap, FConstants::class.java, LConstants::class.java);

		val path = follower.pathBuilder()
			.addBezierLine(Point(0.0, 0.0), Point(96.0 - 24.0, 0.0))
			.setConstantHeadingInterpolation(0.0)
			.addBezierCurve(Point(96.0 - 24.0, 0.0), Point(96.0, 0.0), Point(96.0, 24.0))
			.setConstantHeadingInterpolation(0.0)

			.addBezierLine(Point(96.0, 24.0), Point(96.0, 96.0 - 24.0))
			.setConstantHeadingInterpolation(0.0)
			.addBezierCurve(Point(96.0, 96.0 - 24.0), Point(96.0, 96.0), Point(96.0 - 24.0, 96.0))
			.setConstantHeadingInterpolation(0.0)

			.addBezierLine(Point(96.0 - 24.0, 96.0), Point(24.0, 96.0))
			.setConstantHeadingInterpolation(0.0)
			.addBezierCurve(Point(24.0, 96.0), Point(0.0, 96.0), Point(0.0, 96.0 - 24.0))
			.setConstantHeadingInterpolation(0.0)

			.addBezierLine(Point(0.0, 96.0 - 24.0), Point(0.0, 0.0))
			.setConstantHeadingInterpolation(0.0)
			.build();

		waitForStart();

		runPath(path);
	}
}