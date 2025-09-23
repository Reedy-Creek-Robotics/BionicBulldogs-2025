package org.firstinspires.ftc.teamcode.opmode

import android.util.Size
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.GainControl
import org.firstinspires.ftc.teamcode.pedroPathing.Constants
import org.firstinspires.ftc.vision.VisionPortal
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor
import java.util.concurrent.TimeUnit

@TeleOp
class AprilTagTest : LinearOpMode()
{
	override fun runOpMode()
	{
		val follower = Constants.createFollower(hardwareMap);

		telemetry.setDisplayFormat(Telemetry.DisplayFormat.MONOSPACE);
		val processor = AprilTagProcessor.Builder().build();

		processor.setDecimation(2.0f);
		val camera = hardwareMap.get(WebcamName::class.java, "Webcam 1");
		val visionPortal = VisionPortal.Builder()
			.setCamera(camera)
			.addProcessor(processor)
			.setStreamFormat(VisionPortal.StreamFormat.MJPEG)
			.setCameraResolution(Size(1920, 1080))
			.build();

		setManualExposure(2, 255, visionPortal);

		waitForStart();

		var angle = 0.0;

		var str = "";

		while (opModeIsActive())
		{
			val detections = processor.detections;
			telemetry.addLine("found ${detections.size}");
			for (detection in detections)
			{
				if (detection.metadata == null)
					continue;
				val pos = detection.ftcPose;
				telemetry.addLine("tag ${detection.id}");
				telemetry.addLine("  dist:    ${pos.range}");
				telemetry.addLine("  strafe:  ${pos.bearing}");
				telemetry.addLine("  pitch:   ${pos.pitch}");
				telemetry.addLine("  yaw:     ${pos.yaw}");
				telemetry.addLine("  roll:    ${pos.roll}");
				val a = pos.bearing * if(pos.bearing < 0.0) 1.5 else 1.3;
				telemetry.addLine("  turning: $a");
				if (detection.id == 20)
					angle = a;
			}
			if (gamepad1.crossWasPressed())
			{
				follower.turn(Math.toRadians(angle), false);
				str = "turning $angle degreese";
			}
			follower.update();
			telemetry.addLine(str);
			telemetry.update();
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