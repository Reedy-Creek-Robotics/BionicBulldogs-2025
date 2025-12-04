package org.firstinspires.ftc.teamcode.modules;

import android.util.Size
import com.minerkid08.dynamicopmodeloader.FunctionBuilder
import com.minerkid08.dynamicopmodeloader.OpmodeLoaderFunction
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import org.firstinspires.ftc.teamcode.opmode.clampi
import org.firstinspires.ftc.vision.VisionPortal
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor
import kotlin.math.abs

class Turret(val hardwaremap: HardwareMap)
{
	companion object
	{
		fun init(builder: FunctionBuilder, hardwareMap: HardwareMap)
		{
			builder.pushTable("turret");
			builder.addObjectAsGlobal(Turret(hardwareMap))
			builder.popTable();
		}
	}

	enum class State
	{
		Tracking, Waiting, Manual
	}

	lateinit var motor: DcMotor;
	var state = State.Manual;
	lateinit var processor: AprilTagProcessor;

	val ticksPerRev = 537.7;
	val gearRatio = 208.0 / 114.0;
	val ticksPerDeg = -ticksPerRev / 360 * gearRatio;
	val limit = abs(ticksPerDeg * 90).toInt();

	val timer = ElapsedTime();

	@OpmodeLoaderFunction
	fun init()
	{
		motor = hardwaremap.dcMotor.get("turret");
		processor = AprilTagProcessor.Builder().build();
		val camera = hardwaremap.get(WebcamName::class.java, "Webcam 1");
		motor.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER;
		motor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER;

		val visionPortal = VisionPortal.Builder()
			.setCamera(camera)
			.addProcessor(processor)
			.setStreamFormat(VisionPortal.StreamFormat.MJPEG)
			.setCameraResolution(Size(1920, 1080))
			.build();

		cameraSetExposure(2, 255, visionPortal);
	}

	@OpmodeLoaderFunction
	fun getState(): Int
	{
		return when (state)
		{
			State.Tracking -> 0;
			State.Waiting  -> 1;
			State.Manual   -> 2;
		}
	}

	@OpmodeLoaderFunction
	fun startManual()
	{
		motor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER;
		state = State.Tracking;
	}

	@OpmodeLoaderFunction
	fun startAutomatic()
	{
		state = State.Waiting;
		motor.power = 0.0;
		motor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER;
		motor.targetPosition = 0;
		motor.mode = DcMotor.RunMode.RUN_TO_POSITION;
		motor.power = 1.0;
	}

	@OpmodeLoaderFunction
	fun resetHeading()
	{
		motor.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER;
		motor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER;
		state = State.Waiting;
	}

	@OpmodeLoaderFunction
	fun update(power: Double)
	{
		if (state == State.Manual)
		{
			motor.power = power;
		}
		else
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
					val pos = tag.ftcPose;

					if (tag.ftcPose.bearing > 10 || tag.ftcPose.bearing < -10)
					{
						val newpos = motor.currentPosition + (pos.bearing * ticksPerDeg).toInt();
						motor.power = 0.0;
						motor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER;
						motor.targetPosition = clampi(-limit, limit, newpos);
						motor.mode = DcMotor.RunMode.RUN_TO_POSITION;
						motor.power = 1.0;
					}
					state = State.Tracking;
					timer.reset();
				}
			}
			if (state == State.Tracking)
			{
				if (timer.seconds() >= 0.5)
				{
					state = State.Waiting;
					motor.power = 0.0;
					motor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER;
					motor.targetPosition = 0;
					motor.mode = DcMotor.RunMode.RUN_TO_POSITION;
					motor.power = 1.0;
				}
			}
		}
	}
}