package org.firstinspires.ftc.teamcode.modules;

import com.minerkid08.dynamicopmodeloader.FunctionBuilder
import com.minerkid08.dynamicopmodeloader.OpmodeLoaderFunction
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.modules.luaHardware.LuaAprilTag
import org.firstinspires.ftc.teamcode.modules.luaHardware.LuaDcMotor
import org.firstinspires.ftc.teamcode.opmode.clampi
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
	private lateinit var processor: AprilTagProcessor;

	//145.1 for 1150
	//384.5 for 435

	private val ticksPerRev = 384.5;
	private val gearRatio = 208.0 / 50.0;
	private val ticksPerDeg = ticksPerRev / 360 * gearRatio;
	private val limit = abs(ticksPerDeg * 90).toInt();

	private val timer = ElapsedTime();

	var tagId = 24;

	var luaTag = LuaAprilTag(null);

	var targetPosition = 0;

	@OpmodeLoaderFunction
	fun init(reset: Boolean)
	{
		motor = hardwaremap.dcMotor.get("turret");
		//processor = AprilTagProcessor.Builder()
		//	.setLensIntrinsics(596.507, 596.507, 960.585, 536.890)
		//	.build();
		//val camera = hardwaremap.get(WebcamName::class.java, "Webcam 1");
		if (reset)
			motor.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER;
		motor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER;

		//val visionPortal = VisionPortal.Builder()
		//	.setCamera(camera)
		//	.addProcessor(processor)
		//	.setStreamFormat(VisionPortal.StreamFormat.YUY2)
		//	.setCameraResolution(Size(1920, 1080))
		//	.build();


		//cameraSetExposure(2, 255, visionPortal);
	}

	@OpmodeLoaderFunction
	fun getMotor() = LuaDcMotor(motor);

	@OpmodeLoaderFunction
	fun setTargetTag(t: Int)
	{
		tagId = t;
	}

	@OpmodeLoaderFunction
	fun getState(): Int
	{
		return when (state)
		{
			State.Tracking -> 1;
			State.Waiting  -> 2;
			State.Manual   -> 3;
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
		//motor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER;
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
	fun lockOnTag()
	{
		val detections = processor.detections;
		if (detections != null)
		{
			for (tag in detections)
			{
				if (tag.metadata == null)
					continue;
				if (tag.id != tagId)
					continue;
				luaTag = LuaAprilTag(tag);
				val pos = tag.ftcPose;

				if (tag.ftcPose.bearing > 5 || tag.ftcPose.bearing < -5)
					turnAngle(pos.bearing + 2.5);
			}
		}
	}

	@OpmodeLoaderFunction
	fun getTag() = luaTag;

	@OpmodeLoaderFunction
	fun reset()
	{
		motor.targetPosition = 0;
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
					if (tag.id != tagId)
						continue;
					val pos = tag.ftcPose;

					if (tag.ftcPose.bearing > 20 || tag.ftcPose.bearing < -20)
						turnAngle(pos.bearing);
					state = State.Tracking;
					timer.reset();
				}
			}
			if (state == State.Tracking)
			{
				if (timer.seconds() >= 0.5)
				{
					state = State.Waiting;
					//motor.power = 0.0;
					//motor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER;
					motor.targetPosition = 0;
					//motor.mode = DcMotor.RunMode.RUN_TO_POSITION;
					//motor.power = 1.0;
				}
			}
		}
	}

	@OpmodeLoaderFunction
	fun updateMotor()
	{
		val dir = if (motor.currentPosition < targetPosition) 1 else -1;
		val dif = abs(motor.currentPosition - targetPosition);
		if (dif <= 3)
			motor.power = 0.0;
		else if (dif <= 25)
			motor.power = dir * 0.2;
		else
			motor.power = dir.toDouble();
	}

	@OpmodeLoaderFunction
	fun turnAngle(angle: Double)
	{
		val newpos = motor.currentPosition + (angle * ticksPerDeg).toInt();
		targetPosition = clampi(-limit, limit, newpos);
		motor.targetPosition = targetPosition;
	}

	@OpmodeLoaderFunction
	fun turnTo(angle: Double)
	{
		val newpos = (angle * ticksPerDeg).toInt();
		targetPosition = clampi(-limit, limit, newpos);
		motor.targetPosition = targetPosition;
	}
}
