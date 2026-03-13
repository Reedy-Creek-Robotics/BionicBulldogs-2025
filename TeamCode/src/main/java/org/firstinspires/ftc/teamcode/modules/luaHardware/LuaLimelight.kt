package org.firstinspires.ftc.teamcode.modules.luaHardware

import android.util.Log
import com.minerkid08.dynamicopmodeloader.OpmodeLoaderFunction
import com.qualcomm.hardware.limelightvision.LLResult
import com.qualcomm.hardware.limelightvision.LLResultTypes
import com.qualcomm.hardware.limelightvision.Limelight3A
import com.qualcomm.robotcore.hardware.HardwareMap

class LuaLimelight(hardwareMap: HardwareMap)
{
	private val limelight: Limelight3A = hardwareMap.get(Limelight3A::class.java, "limelight");

	lateinit var detection: LLResult;

	init
	{
		limelight.start();

		limelight.pipelineSwitch(0);
	}

	@OpmodeLoaderFunction
	fun update()
	{
		detection = limelight.latestResult;
	}

	@OpmodeLoaderFunction
	fun getTag(id: Int): LimelightTag?
	{
		for(tag in detection.fiducialResults)
		{
			Log.d("tag", tag.fiducialId.toString());
			if(tag.fiducialId == id)
				return LimelightTag(tag);
		}
		return null;
	}
}

class LimelightTag(private val tag: LLResultTypes.FiducialResult)
{
	@OpmodeLoaderFunction
	fun tx() = tag.targetXDegrees;

	@OpmodeLoaderFunction
	fun ty() = tag.targetYDegrees;
}