package org.firstinspires.ftc.teamcode.opmode

import android.util.Size
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.GainControl
import org.firstinspires.ftc.vision.VisionPortal
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor
import java.io.File
import java.util.concurrent.TimeUnit
import com.qualcomm.robotcore.hardware.DcMotor
import kotlin.math.abs

fun clampf(min: Float, max: Float, num: Float): Float
{
	if (num < min)
		return min;
	if (num > max)
		return max;
	return num;
}

fun clampi(min: Int, max: Int, num: Int): Int
{
	if (num < min)
		return min;
	if (num > max)
		return max;
	return num;
}

@TeleOp
class autoAim : LinearOpMode()
{
	enum class State
	{
		Tracking, Waiting
	}

	var targetPosition = 0;
	lateinit var motor: DcMotor;

	override fun runOpMode()
	{
		telemetry.setDisplayFormat(Telemetry.DisplayFormat.MONOSPACE);
		val processor = AprilTagProcessor.Builder().build();

		processor.setDecimation(3.0f);
		val camera = hardwareMap.get(WebcamName::class.java, "Webcam 1");
		motor = hardwareMap.dcMotor.get("turetM") as DcMotorEx
		motor.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER;
		motor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER;
		val visionPortal = VisionPortal.Builder()
			.setCamera(camera)
			.addProcessor(processor)
			.setStreamFormat(VisionPortal.StreamFormat.MJPEG)
			.setCameraResolution(Size(1920, 1080))
			.build();

		setManualExposure(2, 255, visionPortal);

		//tpr = ticks per rev
    val ticksPerRev = 537.7;

		//tpd = tick per degrees
  
    val gearRatio = 208.0 / 114;

		val ticksPerDeg = -ticksPerRev / 360 * gearRatio;

		var timeBetweenDetection = 0.0;

		val limit = abs((ticksPerDeg * 90).toInt());

		val file = File("/sdcard/FIRST/java/src/Datalog/camera_values${System.nanoTime()}.txt")
		if (!file.exists())
		{
			file.parentFile?.mkdirs();
			file.createNewFile();
		}
		val writer = file.bufferedWriter();

		val timer = ElapsedTime();
		val timer2 = ElapsedTime();
		val timer3 = ElapsedTime();

		timer2.reset();

		var state = State.Waiting;

		waitForStart();

		timer3.reset();

		while (opModeIsActive())
		{
			val detections = processor.freshDetections;
			if (detections != null)
			{
				for (tag in detections)
				{
					if (tag.metadata == null)
						continue;
					if (tag.id != 24)
						continue;
					timeBetweenDetection = timer2.seconds();
					timer2.reset();
					val pos = tag.ftcPose;
					telemetry.addLine("tag ${tag.id}");
					telemetry.addLine("  bearing:    ${pos.bearing}");

					if (tag.ftcPose.bearing > 10 || tag.ftcPose.bearing < -10)
					{
						writer.write("[%10f] moving to target".format(timer3.milliseconds()));
						val newpos = motor.currentPosition + (pos.bearing * ticksPerDeg).toInt();
						targetPosition = clampi(-limit, limit, newpos);
						motor.power = 0.0;
						motor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER;
						motor.targetPosition = clampi(-limit, limit, newpos);
						motor.mode = DcMotor.RunMode.RUN_TO_POSITION;
						motor.power = 1.0;
					}
					else
						writer.write("[%10f] within +- 10 deg of target".format(timer3.milliseconds()));
					state = State.Tracking;
					timer.reset();
				}
			}
			if (state == State.Tracking)
			{
				if (timer.seconds() >= 0.5)
				{
					writer.write("[%10f] target lost, resetting".format(timer3.milliseconds()));
					state = State.Waiting;
					targetPosition = 0;
					motor.power = 0.0;
					motor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER;
					motor.targetPosition = 0;
					motor.mode = DcMotor.RunMode.RUN_TO_POSITION;
					motor.power = 1.0;
				}
			}
			telemetry.addLine("t:$timeBetweenDetection");
			telemetry.addData("targetPos", targetPosition);
			telemetry.addData("curPos", motor.currentPosition);
			//updateRunToPosition();
			telemetry.update();
		}
	}

	fun updateRunToPosition()
	{
		val curPos = motor.currentPosition;
		val dif = targetPosition - curPos;

		if (dif > -1 && dif < 1)
		{
			motor.power = 0.0;
			return;
		}
		if (dif > 0)
		{
			if (dif > 5)
				motor.power = 0.2;
			else
				motor.power = 0.1;
		}
		else
		{
			if (dif < -5)
				motor.power = -0.2;
			else
				motor.power = -0.1;
		}
	}

	fun delay(ms: Float)
	{
		val elapsedTime = ElapsedTime();
		elapsedTime.reset();
		while (elapsedTime.milliseconds() < ms);
	}

	fun setManualExposure(exposureMS: Int, gain: Int, visionPortal: VisionPortal)
	{
		if (visionPortal.cameraState != VisionPortal.CameraState.STREAMING)
		{
			telemetry.addData("Camera", "Waiting");
			telemetry.update();
			while (!isStopRequested && (visionPortal.cameraState != VisionPortal.CameraState.STREAMING));

			telemetry.addData("Camera", "Ready");
			telemetry.update();
		}

		if (!isStopRequested)
		{
			val exposureControl = visionPortal.getCameraControl(ExposureControl::class.java);
			if (exposureControl.mode != ExposureControl.Mode.Manual)
			{
				exposureControl.mode = ExposureControl.Mode.Manual;
				delay(50.0f);
			}
			exposureControl.setExposure(exposureMS.toLong(), TimeUnit.MILLISECONDS);
			delay(20.0f);
			val gainControl = visionPortal.getCameraControl(GainControl::class.java);
			gainControl.gain = gain;
			delay(20.0f);
		}
	}
}
