package org.firstinspires.ftc.teamcode.modules;

import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.GainControl
import org.firstinspires.ftc.vision.VisionPortal
import java.util.concurrent.TimeUnit

fun cameraSetExposure(exposureMS: Int, gain: Int, visionPortal: VisionPortal, telem: Telemetry? = null)
{
	if (visionPortal.cameraState != VisionPortal.CameraState.STREAMING)
	{
		telem?.addData("Camera", "Waiting");
		telem?.update();
		while (visionPortal.cameraState != VisionPortal.CameraState.STREAMING);

		telem?.addData("Camera", "Ready");
		telem?.update();
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
