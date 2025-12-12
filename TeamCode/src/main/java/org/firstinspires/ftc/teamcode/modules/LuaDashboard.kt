package org.firstinspires.ftc.teamcode.modules

import com.acmerobotics.dashboard.FtcDashboard
import com.minerkid08.dynamicopmodeloader.FunctionBuilder
import com.minerkid08.dynamicopmodeloader.OpmodeLoaderFunction
import org.firstinspires.ftc.robotcore.external.Telemetry

object LuaDashboard
{
	val telem: Telemetry = FtcDashboard.getInstance().telemetry;

	fun init(builder: FunctionBuilder)
	{
		telem.setDisplayFormat(Telemetry.DisplayFormat.MONOSPACE);
		builder.pushTable("dashboard");
		builder.addStaticClassAsGlobal(LuaDashboard::class.java);
		builder.popTable();
	}

	@JvmStatic
	@OpmodeLoaderFunction
	fun addDataf(tag: String, value: Float)
	{
		telem.addData(tag, value);
	}

	@JvmStatic
	@OpmodeLoaderFunction
	fun addDatab(tag: String, value: Boolean)
	{
		telem.addData(tag, value);
	}

	@JvmStatic
	@OpmodeLoaderFunction
	fun addDatas(tag: String, value: String)
	{
		telem.addData(tag, value);
	}

	@JvmStatic
	@OpmodeLoaderFunction
	fun addLine(tag: String)
	{
		telem.addLine(tag);
	}

	@JvmStatic
	@OpmodeLoaderFunction
	fun update()
	{
		telem.update();
	}
}