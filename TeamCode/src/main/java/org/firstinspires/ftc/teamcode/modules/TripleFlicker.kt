package org.firstinspires.ftc.teamcode.modules

import com.qualcomm.hardware.rev.RevColorSensorV3
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo

class TripleFlicker(hardwareMap: HardwareMap)
{
	enum class Color
	{
		Green,
		Purple,
		None
	}

	class Flicker
	{
		lateinit var colorSensor: RevColorSensorV3;
		lateinit var servo: Servo;
		var start = 0.0;
		var end = 0.0;
		var color = Color.None;
		var lastDetection = 0.0;
	}

	enum class State
	{
		Up,
		Down
	};

	val upDelay = 0.1;
	val downDelay = 0.1;

	val flickers = arrayOf(Flicker(), Flicker(), Flicker());

	val queue = ArrayDeque<Flicker>();
	var currentFlicker: Flicker? = null;

	var state: State = State.Down;
	var endTime = 0.0;

	var spinner: Servo = hardwareMap.servo.get("spinner");
	val spinnerDelay = 0.4;
	var spinnerTime = 0.0;
	var spinnerPos = 0;

	init
	{
		flickers[0].servo = hardwareMap.servo.get("servo0");
		flickers[0].start = 0.0;
		flickers[0].end = 0.4;

		flickers[1].servo = hardwareMap.servo.get("servo1");
		flickers[1].start = 0.15;
		flickers[1].end = 0.55;

		flickers[2].servo = hardwareMap.servo.get("servo2");
		flickers[2].start = 0.95;
		flickers[2].end = 0.55;
	}

	fun update(et: Double)
	{
		detectColor(flickers[0], et);
		detectColor(flickers[1], et);
		detectColor(flickers[2], et);

		if (et >= spinnerTime)
		{
			if (spinnerPos == 1)
			{
				spinnerPos = 0;
				spinner.position = 0.0;
				spinnerTime = et + spinnerDelay;
			}
			else
			{
				spinnerPos = 1;
				spinner.position = 1.0;
				spinnerTime = et + spinnerDelay;
			}
		}

		if (currentFlicker == null)
		{
			if (queue.isEmpty())
				return;

			currentFlicker = queue.removeFirst();
		}
		if (state == State.Down)
		{
			if (endTime == 0.0)
			{
				currentFlicker?.servo?.position = currentFlicker?.start!!;
				endTime = et + upDelay;
			}
			else if (et >= endTime)
			{
				state == State.Up;
				endTime = 0.0;
			}
		}
		else
		{
			if (endTime == 0.0)
			{
				currentFlicker?.servo?.position = currentFlicker?.end!!;
				endTime = et + downDelay;
			}
			else if (et >= endTime)
			{
				state == State.Down;
				endTime = 0.0;
			}
		}
	}

	fun pushFlicker(ind: Int)
	{
		queue.addLast(flickers[ind]);
	}

	fun detectColor(flicker: Flicker, et: Double)
	{
		if (et >= flicker.lastDetection + 1)
			flicker.color = Color.None;
	}
}