package org.firstinspires.ftc.teamcode.opmode

import android.util.Size
import com.pedropathing.ftc.localization.localizers.TwoWheelLocalizer
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotorSimple
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
		val frontLeft = hardwareMap.dcMotor.get("frontLeft");
		val frontRight = hardwareMap.dcMotor.get("frontRight");
		val backLeft = hardwareMap.dcMotor.get("backLeft");
		val backRight = hardwareMap.dcMotor.get("backRight");

		frontRight.direction = DcMotorSimple.Direction.REVERSE;
		backRight.direction = DcMotorSimple.Direction.REVERSE;

		//val localizer = TwoWheelLocalizer(hardwareMap, Constants.localizerConstants);

		telemetry.setDisplayFormat(Telemetry.DisplayFormat.MONOSPACE);
		val processor = AprilTagProcessor.Builder().build();

		processor.setDecimation(1.0f);
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
		var targetAngle = 0.0;

		var str = "";

		while (opModeIsActive())
		{
			val detections = processor.detections;
			telemetry.addLine("found ${detections.size}");
			for (detection in detections)
			{
				if (detection.metadata == null)
					continue;
				if (detection.id != 20)
					continue;
				val pos = detection.ftcPose;
				telemetry.addLine("tag ${detection.id}");
				telemetry.addLine("  dist:    ${pos.range}");
				telemetry.addLine("  strafe:  ${pos.bearing}");
				telemetry.addLine("  pitch:   ${pos.pitch}");
				telemetry.addLine("  yaw:     ${pos.yaw}");
				telemetry.addLine("  roll:    ${pos.roll}");
				val a = pos.bearing * if (pos.bearing < 0.0) 1.5 else 1.3;
				telemetry.addLine("  turning: $a");
				angle = a;
			}
			/*
			if (gamepad1.crossWasPressed())
			{
				targetAngle = Math.toDegrees(localizer.pose.heading) + angle;
				str = "turning $angle degreese";
			}
			localizer.update();
			telemetry.addLine(str);

			if (targetAngle != 0.0)
			{
				val curAngle = Math.toDegrees(localizer.pose.heading);
				val offset = curAngle - targetAngle;

				val pwr = -offset / 360;

				telemetry.addLine("target angle: $targetAngle");
				telemetry.addLine("currentAngle: $curAngle");
				telemetry.addLine("offset angle: $offset");
				telemetry.addLine("power:        $pwr");

				frontLeft.power = -pwr;
				frontRight.power = pwr;
				backLeft.power = -pwr;
				backRight.power = pwr;
			}
*/
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