package org.firstinspires.ftc.teamcode.modules.luaHardware

import com.minerkid08.dynamicopmodeloader.OpmodeLoaderFunction
import com.qualcomm.robotcore.hardware.TouchSensor

class LuaBeamBreak(val sensor: TouchSensor)
{
	@OpmodeLoaderFunction
	fun getState() = sensor.isPressed;
}