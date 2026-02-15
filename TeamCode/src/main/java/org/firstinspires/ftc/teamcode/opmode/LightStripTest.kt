package org.firstinspires.ftc.teamcode.opmode

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.Animations
import org.firstinspires.ftc.teamcode.Color
import org.firstinspires.ftc.teamcode.LightStripDriver

@TeleOp
class LightStripTest : LinearOpMode()
{
	override fun runOpMode()
	{
		val driver = hardwareMap.get(LightStripDriver::class.java, "lightStrip");

		driver.setLedCount(24);

		waitForStart();

		val s1 = 0;
		val e1 = 7;
		val s2 = 8;
		val e2 = 15;
		val s3 = 16;
		val e3 = 23;

		val animation = Animations.SolidColor();

		animation.startInd = 0;
		animation.endInd = 24;
		animation.brightness = 50;
		animation.color = Color(0u, 255u, 0u);

		driver.clearAnimations();
		driver.saveAnimation(animation, 0);
		driver.saveArtBoard(0);

		animation.color = Color(255u, 0u, 255u);
		driver.saveAnimation(animation, 0);
		driver.saveArtBoard(3);

		animation.startInd = 0;
		animation.endInd = 5;
		animation.color = Color(255u, 0u, 255u);
		driver.saveAnimation(animation, 0);
		animation.startInd = 18;
		animation.endInd = 24;
		driver.saveAnimation(animation, 2);

		animation.startInd = 6;
		animation.endInd = 17;
		animation.color = Color(0u, 255u, 0u);
		driver.saveAnimation(animation, 1);
		driver.saveArtBoard(1);

		animation.startInd = 0;
		animation.endInd = 5;
		animation.color = Color(0u, 255u, 0u);
		driver.saveAnimation(animation, 0);
		animation.startInd = 18;
		animation.endInd = 24;
		driver.saveAnimation(animation, 2);

		animation.startInd = 6;
		animation.endInd = 17;
		animation.color = Color(255u, 0u, 255u);
		driver.saveAnimation(animation, 1);
		driver.saveArtBoard(2);

		driver.enableBootAnimation(0);

		while(opModeIsActive())
		{
			driver.displayArtBoard(0);
			sleep(1000);
			driver.displayArtBoard(1);
			sleep(1000);
			driver.displayArtBoard(2);
			sleep(1000);
			driver.displayArtBoard(3);
			sleep(1000);
		}
	}
}