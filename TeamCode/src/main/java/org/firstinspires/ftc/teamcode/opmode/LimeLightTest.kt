package org.firstinspires.ftc.teamcode.opmode

import com.qualcomm.hardware.limelightvision.Limelight3A
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp

@TeleOp
class LimeLightTest : LinearOpMode()
{
	override fun runOpMode()
	{
		val limelight = hardwareMap.get(Limelight3A::class.java, "limeLight");

		limelight.start();

		limelight.pipelineSwitch(0);
		waitForStart();
		while(opModeIsActive())
		{
			val res = limelight.latestResult;
			telemetry.addData("latency", res.targetingLatency);
			telemetry.update();
		}
	}
}