package org.firstinspires.ftc.teamcode.opmode

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.TouchSensor

@TeleOp
class BeamBreakTest : LinearOpMode()
{
	override fun runOpMode()
	{
		val sensor = hardwareMap.get(TouchSensor::class.java, "beamBreak");

		waitForStart();

		while (opModeIsActive())
		{
			telemetry.addData("value", sensor.isPressed)
			telemetry.update();
		}
	}
}