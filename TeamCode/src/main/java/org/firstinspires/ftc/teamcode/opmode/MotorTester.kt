package org.firstinspires.ftc.teamcode.opmode

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit
import java.io.File
import java.io.FileInputStream
import kotlin.io.path.createTempFile
import kotlin.math.max
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds


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

		var current = motor.getCurrent(CurrentUnit.AMPS).toDouble()
		//Tp =  ((-Distance)*ticks*GR)
motor.mode =DcMotor.RunMode.RUN_WITHOUT_ENCODER
		val el = ElapsedTime();
		el.reset()

		waitForStart()
		var xpressed = 0
		var ypressed = 0
		var s = 0.1
			val motor_values = "motor_values" + System.nanoTime()
			val file = File(motor_values)




		while(opModeIsActive())
		{
			if(gamepad1.xWasPressed())
			{
				xpressed += 1
				el.startTime()
				motor.power = s

			}
			else if(gamepad1.yWasPressed())
			{
				ypressed += 1
				motor.power = 0.0
			}
			if(gamepad1.dpadUpWasPressed())
			{
				s += 0.1
				motor.power = s

			}
			else if(gamepad1.dpadDownWasPressed())
			{
				s -= 0.1
				motor.power = s

			}

			current = (motor.getCurrent(CurrentUnit.AMPS))
			telemetry.addData("current", current)
			telemetry.addData("time", el.seconds())
			telemetry.addData("x was pressed", xpressed)
			telemetry.addData("y was pressed", ypressed)
			telemetry.addData("power",s)
			telemetry.update()
			if (file.exists()) {
				file.writeText("current:$current time:${time.minutes}:${time.seconds}")
			} else {
				val motor_values = "motor_values.txt" + System.nanoTime()


			}

			}
		}
	}