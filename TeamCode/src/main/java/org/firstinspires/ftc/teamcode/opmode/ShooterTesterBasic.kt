package org.firstinspires.ftc.teamcode.opmode

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx


@TeleOp
@Config
class ShooterTesterBasic: LinearOpMode()
{
	companion object
	{
		@JvmField
		var ticksPerRev = 145;
	}


	override fun runOpMode()
	{
		val motor = hardwareMap.dcMotor.get("motor") as DcMotorEx
		motor.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER

		waitForStart()
		var xpressed = 0
		var ypressed = 0
		var s = 1.0

		while(opModeIsActive())
		{
			if(gamepad1.xWasPressed())
			{
				xpressed += 1

			}
			else if(gamepad1.yWasPressed())
			{
				ypressed += 1
				motor.power = 0.0
			}
			if(gamepad1.dpadUpWasPressed())
			{
				s += 1.0
				motor.power = s;

			}
			else if(gamepad1.dpadDownWasPressed())
			{
				s -= 1.0
				motor.power = s

			}


			telemetry.addData("x was pressed", xpressed)
			telemetry.addData("y was pressed", ypressed)
			telemetry.update()


		}
	}
}