package org.firstinspires.ftc.teamcode.modules

import android.util.Size
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.GainControl
import org.firstinspires.ftc.vision.VisionPortal
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor
import java.util.concurrent.TimeUnit

@TeleOp
class ApriltagDistance: LinearOpMode()
{
	override fun runOpMode()
	{

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
		//the transfer
		val servo = hardwareMap.servo.get("transfer")
		// to keep gate shut
		servo.position = 1.0
		//the intake motor
		val motori = hardwareMap.dcMotor.get("intake") as DcMotorEx
		//the motor for shooting
		val motor = hardwareMap.dcMotor.get("shooter") as DcMotorEx
		//velocitym is what the velocity of the motor is being set to
		var velocitym = 0
		motor.direction = DcMotorSimple.Direction.REVERSE

			while (opModeIsActive())
		{
			//velocityreal is what the velocity is
			var velocityreal = motor.velocity
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

				if (pos.range >= 126)
				{
					telemetry.addLine("big distance is working")
					velocitym = 1600
				}
					else if (pos.range >= 45)
				{
					telemetry.addLine("medium distance is working")
					velocitym = 1300
				}
						else
				{
					telemetry.addLine("small distance is working")
					velocitym = 1200
				}
				telemetry.addLine("velocity:${velocitym}")
				motor.velocity = velocitym.toDouble()
				telemetry.addLine("current velocity:${velocityreal}")


			}
			if (gamepad1.crossWasPressed())
			{
				servo.position = 0.85
				motori.power = 1.0
			}
			if (gamepad1.triangleWasPressed())
			{
				servo.position = 1.0
			}

			telemetry.update();
		}
	}

	fun delay(ms: Float)
	{
		val elapsedTime = ElapsedTime();
		elapsedTime.reset();
		while (elapsedTime.milliseconds() < ms);
	}

	//the following code sets up the camera
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