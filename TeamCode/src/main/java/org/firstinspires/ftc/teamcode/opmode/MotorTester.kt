package org.firstinspires.ftc.teamcode.opmode

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit
import java.io.File
import kotlin.math.max

@TeleOp
@Config
class MotorTester: LinearOpMode()
{
	companion object
	{
		@JvmField
		var ticksPerRev = 145;
	}

	/**
	 *enter encoder tick rate
	 * move 60 cm based on the encoder
	 * start timer
	 * while running, messure the peak of the current
	 * when the movement finishes end timer
	 * x to start the program
	 * y to stop the program
	 * pressing up on the d pad to increeces the decrement
	 * pressing down on the d pan to decreecea the decrement
	 * create a static map of the motors :117, 312 , 435,1150
	 * map also number of ticks to use for encoder movements
	 * logfile every loop , comma separated
	 * log file should use a uniqe name that includes time stamp
	 * and make sure to close file on stop
	 * output to the screen current , encoder ticks start
	 */

	override fun runOpMode()
	{
		val motor = hardwareMap.dcMotor.get("motor") as DcMotorEx
		motor.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER


		var current = motor.getCurrent(CurrentUnit.AMPS)
		//Tp =  ((-Distance)*ticks*GR)
		motor.targetPosition = (-60 * ticksPerRev * 10).toInt()
		motor.mode = DcMotor.RunMode.RUN_TO_POSITION

		val e = ElapsedTime();
		e.reset()

		waitForStart()
		var xpressed = 0
		var ypressed = 0
		var s = 1.0

		while(opModeIsActive())
		{
			if(gamepad1.xWasPressed())
			{
				xpressed += 1
				e.startTime()

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
        val file = Fi
			current = max(current, motor.getCurrent(CurrentUnit.AMPS))
			telemetry.addData("current", current)
			telemetry.addData("time", e.seconds())
			telemetry.addData("x was pressed", xpressed)
			telemetry.addData("y was pressed", ypressed)
			telemetry.update()


		}
	}
}