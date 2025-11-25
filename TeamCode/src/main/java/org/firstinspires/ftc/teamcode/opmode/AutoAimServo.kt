/*package org.firstinspires.ftc.teamcode.opmode

import android.util.Size
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.Servo
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

@TeleOp
class AutoAimServo : LinearOpMode()
{
	enum class State
	{
		Tracking, Waiting
	}

	var targetPosition = 0.0;
	lateinit var servo: Servo;

	override fun runOpMode()
	{
		telemetry.setDisplayFormat(Telemetry.DisplayFormat.MONOSPACE);
		val processor = AprilTagProcessor.Builder().build();

    servo = hardwareMap.servo.get("servo");

		processor.setDecimation(3.0f);
		val camera = hardwareMap.get(WebcamName::class.java, "Webcam 1");
		val visionPortal = VisionPortal.Builder()
			.setCamera(camera)
			.addProcessor(processor)
			.setStreamFormat(VisionPortal.StreamFormat.MJPEG)
			.setCameraResolution(Size(1920, 1080))
			.build();

		setManualExposure(2, 255, visionPortal);

    val ticksPerRev = 355;
  
    val gearRatio = 208.0 / 114;

		var timeBetweenDetection = 0.0;

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

					if (tag.ftcPose.bearing > 5 || tag.ftcPose.bearing < -5)
					{
						writer.write("[%10f] moving to target".format(timer3.milliseconds()));
						val newpos = motor.currentPosition + (pos.bearing * ticksPerDeg).toInt();
						targetPosition = clampi(-limit, limit, newpos);
            runToAngle()
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
          runToAngle(0.0);
				}
			}
			telemetry.addLine("t:$timeBetweenDetection");
			telemetry.addData("targetPos", targetPosition);
			telemetry.update();
		}
	}

  fun runToAngle(angle: Double)
  {
    val pos = (angle  + 90) / 180; 
    servo.position = pos;
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
}*/
