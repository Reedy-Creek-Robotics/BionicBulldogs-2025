package org.firstinspires.ftc.teamcode.opmode

import android.util.Size
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.util.ElapsedTime
import com.qualcomm.robotcore.util.RobotLog
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.GainControl
import org.firstinspires.ftc.vision.VisionPortal
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor
import java.io.File
import java.util.concurrent.TimeUnit
import android.R.attr.data
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@TeleOp
class autoAim : LinearOpMode()
{
	override fun runOpMode()
	{
		telemetry.setDisplayFormat(Telemetry.DisplayFormat.MONOSPACE);
		val processor = AprilTagProcessor.Builder().build();

		processor.setDecimation(3.0f);
		val camera = hardwareMap.get(WebcamName::class.java, "Webcam 1");
		val motor = hardwareMap.dcMotor.get("turetM") as DcMotorEx
		val visionPortal = VisionPortal.Builder()
			.setCamera(camera)
			.addProcessor(processor)
			.setStreamFormat(VisionPortal.StreamFormat.MJPEG)
			.setCameraResolution(Size(1920, 1080))
			.build();

		setManualExposure(2, 255, visionPortal);
		//tpr = ticks per rev
		val tpr = 384.5
		//tpd = tick per degrees
		val tpd = tpr/360

		val cameravalues = "cameravalues" + System.nanoTime()
		val file = File("/sdcard/FIRST/java/src/Datalog/camera_values" + System.nanoTime())
		if(!file.exists())
		{
			file.parentFile.mkdirs();
			file.createNewFile();
		}
		val writer = file.bufferedWriter();

		waitForStart();

		while (opModeIsActive())
		{
				val detections = processor.freshDetections;
				if(detections != null)
				{
				telemetry.addLine("found ${detections.size}");
				var foundTag = false;
				for(detection in detections)
				{
					if(detection.metadata == null)
						continue;
					if(detection.id != 24)
						continue;
					foundTag = true;
					val pos = detection.ftcPose;
					telemetry.addLine("tag ${detection.id}")
					telemetry.addLine("  bearing:    ${pos.bearing}");
					val tagc = pos.bearing
					writer.write("bearing${pos.bearing} ,range${pos.range},${time.minutes}:${time.seconds}\n")

					motor.power = 0.0;
					motor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER;
					motor.targetPosition = (pos.bearing * tpd).toInt() + motor.currentPosition;
					motor.mode = DcMotor.RunMode.RUN_TO_POSITION;
					if (tagc < 10  && tagc > -10)
					{
						motor.power = 0.0
					}
					else
					motor.power = 0.2;
				}
				telemetry.update();
			}
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