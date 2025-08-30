package org.firstinspires.ftc.teamcode.modules

import com.minerkid08.dynamicopmodeloader.FunctionBuilder
import com.minerkid08.dynamicopmodeloader.LuaType
import com.minerkid08.dynamicopmodeloader.OpmodeLoaderFunction
import org.firstinspires.ftc.robotcore.external.Telemetry

class LuaTelemetry(private val telem: Telemetry)
{
	companion object
	{
		fun init(builder: FunctionBuilder, telem: Telemetry)
		{
			builder.pushTable("telem");
			builder.addObjectAsGlobal(LuaTelemetry(telem));
			builder.popTable();
		}
	}

	@OpmodeLoaderFunction
	fun addDataf(tag: String, value: Float)
	{
		telem.addData(tag, value);
	}

	@OpmodeLoaderFunction
	fun addDatab(tag: String, value: Boolean)
	{
		telem.addData(tag, value);
	}

	@OpmodeLoaderFunction
	fun addDatas(tag: String, value: String)
	{
		telem.addData(tag, value);
	}

	@OpmodeLoaderFunction
	fun addLine(tag: String)
	{
		telem.addLine(tag);
	}

	@OpmodeLoaderFunction
	fun update()
	{
		telem.update();
	}
}