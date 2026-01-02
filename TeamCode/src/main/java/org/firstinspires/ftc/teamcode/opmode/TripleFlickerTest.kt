package org.firstinspires.ftc.teamcode.opmode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.modules.TripleFlicker

@TeleOp
class TripleFlickerTest : LinearOpMode()
{
	override fun runOpMode()
	{
		val flicker = TripleFlicker(hardwareMap);

		waitForStart();

		val elapsedTime = ElapsedTime();
		elapsedTime.reset()

		while(opModeIsActive())
		{
			flicker.update(elapsedTime.seconds());
			if(gamepad1.triangleWasPressed())
			{
				flicker.pushFlicker(0);
				flicker.pushFlicker(1);
				flicker.pushFlicker(2);
			}
		}
	}
}