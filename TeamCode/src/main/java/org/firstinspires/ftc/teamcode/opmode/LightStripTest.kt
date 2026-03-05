package org.firstinspires.ftc.teamcode.opmode

import org.firstinspires.ftc.teamcode.Animations
import com.qualcomm.robotcore.hardware.Gamepad
import com.minerkid08.telemetryui.InputManager
import com.minerkid08.telemetryui.IntPtr
import com.minerkid08.telemetryui.Output
import com.minerkid08.telemetryui.Ui
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.teamcode.Color
import org.firstinspires.ftc.teamcode.LightStripDriver

class Input(private val gamepad: Gamepad): InputManager
{
	override fun getUp() = gamepad.dpadUpWasPressed();
	override fun getDown() = gamepad.dpadDownWasPressed();
	override fun getLeft() = gamepad.circleWasPressed();
	override fun getRight() = gamepad.crossWasPressed();
}

class TelemOutput(private val telemetry: Telemetry): Output
{
	override fun addLine(line: String)
	{
		telemetry.addLine(line);
	}

	override fun endFrame()
	{
		telemetry.update();
	}
}

@TeleOp
class LightStripConfig : LinearOpMode()
{
	override fun runOpMode()
	{
		val driver = hardwareMap.get(LightStripDriver::class.java, "lightStrip");

		val input = Input(gamepad1);
		val output = TelemOutput(telemetry);
		val ui = Ui(input, output, 18);

		val animations = ArrayList<Animations.AnimationBase>();
		val types = ArrayList<Int>();
		val typeNames = listOf("solid color", "blinking", "pulsing");

		val int = IntPtr();
		val ledCount = IntPtr();
		val artBoard = IntPtr();

		waitForStart();

		while (opModeIsActive())
		{
			if (ui.button("exit"))
				return;

			if (ui.button("save"))
			{
				driver.setLedCount(ledCount.value);
				driver.clearAnimations();
				for((i, anim) in animations.withIndex())
				{
					driver.saveAnimation(anim, i);
					sleep(100);
				}
				driver.saveArtBoard(artBoard.value);
			}

			if (ui.button("set boot animation"))
				driver.enableBootAnimation(artBoard.value);
			if (ui.button("clear boot animation"))
				driver.disableBootAnimation();

			ui.intInput("led count", ledCount, 1);
			if(ui.intInput("art board", artBoard, 1))
			{
				driver.displayArtBoard(artBoard.value);
			}

			if (ui.button("add"))
			{
				types.add(0);
				animations.add(Animations.SolidColor());
			}
			if (ui.button("clear"))
			{
				types.clear();
				animations.clear();
			}

			ui.seperator();

			ui.text("animation count ${animations.size}");

			for (i in animations.indices)
			{
				if (ui.treeNode(i.toString(), false))
				{
					var anim = animations[i];
					int.value = types[i];
					if (ui.dropdown("type", int, typeNames))
					{
						types[i] = int.value;
						val brightness = animations[i].brightness;
						val start = animations[i].startInd;
						val end = animations[i].endInd;
						animations[i] = when (types[i])
						{
							0    -> Animations.SolidColor();
							1    -> Animations.Blinking();
							2    -> Animations.Pulsing();
							else -> error("invalid type somehow");
						}
						anim = animations[i];
						animations[i].brightness = brightness;
						animations[i].startInd = start;
						animations[i].endInd = end;
					}
					int.value = anim.startInd;
					if (ui.intInput("start index", int, 1))
						anim.startInd = int.value;

					int.value = anim.endInd;
					if (ui.intInput("end ind", int, 1))
						anim.endInd = int.value;

					int.value = anim.brightness;
					if (ui.intInput("brightness", int, 1))
						anim.brightness = int.value;
					ui.seperator();
					ui.intInput("step", step, 1);

					when (types[i])
					{
						0 -> drawSolid(anim as Animations.SolidColor, ui);
						1 -> drawBlinking(anim as Animations.Blinking, ui);
						2 -> drawPulsing(anim as Animations.Pulsing, ui);
					}

					ui.treePop();
				}
			}

			ui.update();
		}
	}

	val colorPtr = IntPtr();
	val step = IntPtr(10);
	fun colorInput(c: Color, ui: Ui, label: String)
	{
		if (ui.treeNode(label))
		{
			colorPtr.value = c.r;
			if (ui.intInput("r", colorPtr, step.value))
				c.r = colorPtr.value;
			colorPtr.value = c.g;
			if (ui.intInput("g", colorPtr, step.value))
				c.g = colorPtr.value;
			colorPtr.value = c.b;
			if (ui.intInput("b", colorPtr, step.value))
				c.b = colorPtr.value;
			ui.treePop();
		}

	}

	fun drawSolid(anim: Animations.SolidColor, ui: Ui)
	{
		colorInput(anim.color, ui, "color");
	}

	fun drawPulsing(anim: Animations.Pulsing, ui: Ui)
	{
		colorInput(anim.primaryColor, ui, "primary color");
		colorInput(anim.secondaryColor, ui, "secondary color");
		colorPtr.value = anim.period;
		if (ui.intInput("period", colorPtr, step.value))
			anim.period = colorPtr.value;
	}

	fun drawBlinking(anim: Animations.Blinking, ui: Ui)
	{
		colorInput(anim.primaryColor, ui, "primary color");
		colorInput(anim.secondaryColor, ui, "secondary color");
		colorPtr.value = anim.primaryPeriod;
		if (ui.intInput("primary period", colorPtr, step.value))
			anim.primaryPeriod = colorPtr.value;
		colorPtr.value = anim.period;
		if (ui.intInput("period", colorPtr, step.value))
			anim.period = colorPtr.value;
	}
}