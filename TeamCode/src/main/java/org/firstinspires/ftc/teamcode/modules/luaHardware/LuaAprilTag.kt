package org.firstinspires.ftc.teamcode.modules.luaHardware

import android.util.Size
import com.minerkid08.dynamicopmodeloader.FunctionBuilder
import com.minerkid08.dynamicopmodeloader.LuaError
import com.minerkid08.dynamicopmodeloader.OpmodeLoaderFunction
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.GainControl
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D
import org.firstinspires.ftc.robotcore.external.navigation.Position
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles
import org.firstinspires.ftc.vision.VisionPortal
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection
import org.firstinspires.ftc.vision.apriltag.AprilTagPoseFtc
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor
import java.util.concurrent.TimeUnit

val cameraPosition = Position(
	DistanceUnit.INCH,
	-0.25, 5.5, 8.0, 0
);
val cameraOrientation = YawPitchRollAngles(
	AngleUnit.DEGREES,
	0.0, -90.0 + 5, 0.0, 0
);

object LuaAprilTagProcessor
{
	fun build(builder: FunctionBuilder, hwMap: HardwareMap)
	{
		hardwareMap = hwMap;
		builder.pushTable("aprilTagProcessor");
		builder.addStaticClassAsGlobal(LuaAprilTagProcessor::class.java)
		builder.popTable();
		builder.addClassAsClass(LuaAprilTag::class.java)
		builder.addClassAsClass(LuaFtcPos::class.java)
		builder.addClassAsClass(LuaPose3D::class.java)
	}

	var hardwareMap: HardwareMap? = null;
	var processor: AprilTagProcessor? = null;
	var detections: List<AprilTagDetection>? = null;

	@OpmodeLoaderFunction
	@JvmStatic
	fun init(width: Int, height: Int, exposureMS: Int, gain: Int, decimation: Float)
	{
		processor = AprilTagProcessor.Builder()
			//.setLensIntrinsics(596.507, 596.507, 960.585, 536.89)
			.setCameraPose(cameraPosition, cameraOrientation)
			.setDrawAxes(true)
			.build();

		processor?.setDecimation(decimation)

		val visionPortal = VisionPortal.Builder()
			.setCamera(hardwareMap?.get(WebcamName::class.java, "Webcam 1"))
			.addProcessor(processor)
			.setStreamFormat(VisionPortal.StreamFormat.MJPEG)
			.setCameraResolution(Size(width, height))
			.setAutoStartStreamOnBuild(true)
			.build();

		setManualExposure(exposureMS, gain, visionPortal);
	}

	@OpmodeLoaderFunction
	@JvmStatic
	fun update()
	{
		detections = processor?.detections;
	}

	@OpmodeLoaderFunction
	@JvmStatic
	fun getTag(id: Int): LuaAprilTag
	{
		if (detections != null)
		{
			for (detection in detections)
			{
				if (detection.metadata == null)
					continue;
				if (detection.id == id)
				{
					return LuaAprilTag(detection);
				}
			}
		}
		return LuaAprilTag(null);
	}

	fun setManualExposure(exposureMS: Int, gain: Int, visionPortal: VisionPortal)
	{
		if (visionPortal.cameraState != VisionPortal.CameraState.STREAMING)
		{
			while ((visionPortal.cameraState != VisionPortal.CameraState.STREAMING));
		}

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

	fun delay(ms: Float)
	{
		val elapsedTime = ElapsedTime();
		elapsedTime.reset();
		while (elapsedTime.milliseconds() < ms);
	}
}

class LuaFtcPos(private val pos: AprilTagPoseFtc)
{
	@OpmodeLoaderFunction
	fun x(): Double = pos.x;
	@OpmodeLoaderFunction
	fun y(): Double = pos.y;
	@OpmodeLoaderFunction
	fun bearing(): Double = pos.bearing;
	@OpmodeLoaderFunction
	fun range(): Double = pos.range;
}

class LuaPose3D(private val pos: Pose3D)
{
	@OpmodeLoaderFunction
	fun x(): Double = pos.position.x;
	@OpmodeLoaderFunction
	fun y(): Double = pos.position.y;
	@OpmodeLoaderFunction
	fun z(): Double = pos.position.z;
	@OpmodeLoaderFunction
	fun pitch(): Double = pos.orientation.pitch;
	@OpmodeLoaderFunction
	fun yaw(): Double = pos.orientation.yaw;
	@OpmodeLoaderFunction
	fun roll(): Double = pos.orientation.roll;
}

class LuaAprilTag(private val tag: AprilTagDetection?)
{
	@OpmodeLoaderFunction
	fun valid() = (tag != null)

	@OpmodeLoaderFunction
	fun ftcPos(): LuaFtcPos
	{
		if (tag != null)
			return LuaFtcPos(tag.ftcPose);
		throw LuaError("attempted to call 'ftcPos' on a nil tag");
	}

	@OpmodeLoaderFunction
	fun robotPos(): LuaPose3D
	{
		if (tag != null)
			return LuaPose3D(tag.robotPose);
		throw LuaError("attempted to call 'ftcPos' on a nil tag");
	}
}