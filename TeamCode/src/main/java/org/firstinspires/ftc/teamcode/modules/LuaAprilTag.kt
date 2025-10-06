package org.firstinspires.ftc.teamcode.modules

import android.util.Size
import com.minerkid08.dynamicopmodeloader.FunctionBuilder
import com.minerkid08.dynamicopmodeloader.OpmodeLoaderFunction
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.GainControl
import org.firstinspires.ftc.vision.VisionPortal
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor
import java.util.concurrent.TimeUnit

object LuaAprilTagProcessor
{
	fun build(builder: FunctionBuilder, hwMap: HardwareMap)
	{
		hardwareMap = hwMap;
		builder.pushTable("aprilTagProcessor");
		builder.addStaticClassAsGlobal(LuaAprilTagProcessor::class.java)
		builder.addClassAsClass(LuaAprilTag::class.java)
		builder.popTable();
	}

	var hardwareMap: HardwareMap? = null;
	var processor: AprilTagProcessor? = null;

	@OpmodeLoaderFunction
	@JvmStatic
	fun init(width: Int, height: Int, exposureMS: Int, gain: Int)
	{
		processor = AprilTagProcessor.Builder().build();
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
	fun getTag(id: Int): LuaAprilTag
	{
		val detections = processor?.detections;
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

class LuaAprilTag(private val tag: AprilTagDetection?)
{
	@OpmodeLoaderFunction
	fun valid() = (tag != null)

	@OpmodeLoaderFunction
	fun getDist(): Double
	{
		if(tag != null)
			return tag.ftcPose.range;
		return -1.0;
	}
}