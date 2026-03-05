package org.firstinspires.ftc.teamcode.modules.luaHardware

import com.qualcomm.hardware.limelightvision.LLResult
import com.qualcomm.hardware.limelightvision.Limelight3A
import com.qualcomm.robotcore.hardware.HardwareMap

class LuaLimelight(hardwareMap: HardwareMap)
{
	private val limelight: Limelight3A = hardwareMap.get(Limelight3A::class.java, "limelight");

	lateinit var detection: LLResult;
	fun update()
	{
		detection = limelight.latestResult;
	}

	fun tx() = detection.tx;
	fun ty() = detection.ty;
}