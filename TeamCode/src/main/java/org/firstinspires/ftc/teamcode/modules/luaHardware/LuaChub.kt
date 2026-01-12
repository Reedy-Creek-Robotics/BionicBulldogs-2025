package org.firstinspires.ftc.teamcode.modules.luaHardware

import com.minerkid08.dynamicopmodeloader.OpmodeLoaderFunction
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.VoltageSensor

class LuaChub(hardwareMap: HardwareMap)
{
	val voltageSensor: VoltageSensor = hardwareMap.get(VoltageSensor::class.java, "Control Hub");

	//val lynxModule: LynxModule = hardwareMap.get(LynxModule::class.java, "Control Hub");

	@OpmodeLoaderFunction
	fun getVoltage() = voltageSensor.voltage;
}