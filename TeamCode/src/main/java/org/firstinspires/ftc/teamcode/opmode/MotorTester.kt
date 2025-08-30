package org.firstinspires.ftc.teamcode.opmode

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit
import kotlin.math.max

@Config
class MotorTester : LinearOpMode ()
{
	companion object
	{
		@JvmStatic
		var ticksPerRev = 0;
	}

	override fun runOpMode()
	{
		val motor = hardwareMap.dcMotor.get("motor") as DcMotorEx
		motor.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER

		waitForStart()

		var current = motor.getCurrent(CurrentUnit.AMPS)
		motor.targetPosition = ((60 / 10.1) * ticksPerRev * 10).toInt()
		motor.mode = DcMotor.RunMode.RUN_TO_POSITION
		motor.setPower(1.0);
		val e = ElapsedTime();
		e.reset();
		while (opModeIsActive())
		{
			current = max(current, motor.getCurrent(CurrentUnit.AMPS))
		}
		e.seconds();
	}
}