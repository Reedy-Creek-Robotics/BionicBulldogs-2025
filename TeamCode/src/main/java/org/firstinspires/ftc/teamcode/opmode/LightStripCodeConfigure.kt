package org.firstinspires.ftc.teamcode.opmode

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.Animations
import org.firstinspires.ftc.teamcode.Color
import org.firstinspires.ftc.teamcode.LightStripDriver

@TeleOp
class LightStripCodeConfigure : LinearOpMode()
{
	override fun runOpMode()
	{
		val driver = hardwareMap.get(LightStripDriver::class.java, "lightStrip");

		val bootAnim = Animations.Pulsing();
		bootAnim.startInd = 0;
		bootAnim.endInd = 24;
		bootAnim.brightness = 50;
		bootAnim.period = 5000;
		bootAnim.primaryColor = Color(0xff, 0xa5, 0x00);
		bootAnim.secondaryColor = Color(0, 0, 0);

		val police = Animations.PoliceLights();
		police.startInd = 0;
		police.endInd = 24;


		driver.clearAnimations();
		sleep(100);
		driver.saveAnimation(bootAnim, 0);
		sleep(100);
		driver.saveArtBoard(0);
		sleep(100);
		driver.enableBootAnimation(0);
		sleep(100);
		driver.clearAnimations();
		sleep(100);
		driver.saveAnimation(police, 0);
		sleep(100);
		driver.saveArtBoard(7);

		val colors = HashMap<Char, Color>();
		colors['W'] = Color(255, 255, 255);
		colors['P'] = bootAnim.primaryColor;
		colors['G'] = Color(0, 255, 0);
		colors['R'] = Color(255, 0, 0);
		var str = "NNNNNNNNNNNN_NNNNNNNNNNNN";
		setArtBoard(driver, str, colors, 1);
		str = "PPPPNNNNNNNN_NNNNNNNNPPPP";
		setArtBoard(driver, str, colors, 2);
		str = "PPPPPPPPNNNN_NNNNPPPPPPPP";
		setArtBoard(driver, str, colors, 3);
		str = "GGGGGGGGGGGG_GGGGGGGGGGGG";
		setArtBoard(driver, str, colors, 4);
		str = "WWWWWWWWWWWW_WWWWWWWWWWWW";
		setArtBoard(driver, str, colors, 5);
		str = "RRRRRRRRRRRR_RRRRRRRRRRRR";
		setArtBoard(driver, str, colors, 6);
		sleep(100);
		driver.displayArtBoard(0);
	}

	fun setArtBoard(driver: LightStripDriver, str: String, colors: Map<Char, Color>, artBoard: Int)
	{
		driver.clearAnimations();
		sleep(200);
		setAnimations(driver, str, colors);
		driver.saveArtBoard(artBoard);
		sleep(200);
		driver.displayArtBoard(artBoard);
	}

	fun setAnimations(driver: LightStripDriver, str: String, colors: Map<Char, Color>)
	{
		var animId = 0;
		val animation = Animations.SolidColor();
		animation.brightness = 50;
		animation.startInd = 0;

		var prevColor = 'N';
		var skipCount = 0;
		var startInd = 0;
		var color = Color();
		for ((i, k) in str.withIndex())
		{
			if (k == '_')
			{
				skipCount++;
				continue;
			}
			val ledIndex = i - skipCount;
			if (prevColor != k)
			{
				if (prevColor != 'N')
				{
					animation.color = color;
					animation.startInd = startInd;
					animation.endInd = ledIndex - 1;
					driver.saveAnimation(animation, animId);
					sleep(100);
					animId++;
				}
				startInd = ledIndex;
				if(k != 'N')
				{
					if (colors.contains(k))
						color = colors[k]!!;
					else
						error("invalid color $k");
				}
				prevColor = k;
			}
		}
		if (prevColor != 'N')
		{
			animation.color = color;
			animation.startInd = startInd;
			animation.endInd = 24;
			driver.saveAnimation(animation, animId);
			sleep(100);
		}
	}
}