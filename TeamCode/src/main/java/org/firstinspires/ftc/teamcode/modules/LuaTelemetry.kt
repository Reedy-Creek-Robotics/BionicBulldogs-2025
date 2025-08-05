package org.firstinspires.ftc.teamcode.modules

import com.minerkid08.dynamicopmodeloader.FunctionBuilder
import com.minerkid08.dynamicopmodeloader.LuaType
import org.firstinspires.ftc.robotcore.external.Telemetry

class LuaTelemetry(private val telem: Telemetry)
{
	companion object
	{
		fun init(builder: FunctionBuilder, telem: Telemetry)
		{
			builder.pushTable("telem");

			builder.setCurrentObject(LuaTelemetry(telem));

			builder.addObjectFunction("addDataf", argTypes = listOf(LuaType.String, LuaType.Float));
			builder.addObjectFunction("addDatab", argTypes = listOf(LuaType.String, LuaType.Bool));
			builder.addObjectFunction("addDatas", argTypes = listOf(LuaType.String, LuaType.String));
			builder.addObjectFunction("addLine", argTypes = listOf(LuaType.String));
			builder.addObjectFunction("update");

			builder.popTable();
		}
	}

	fun addDataf(tag: String, value: Float)
	{
		telem.addData(tag, value);
	}

	fun addDatab(tag: String, value: Boolean)
	{
		telem.addData(tag, value);
	}

	fun addDatas(tag: String, value: String)
	{
		telem.addData(tag, value);
	}

	fun addLine(tag: String)
	{
		telem.addLine(tag);
	}

	fun update()
	{
		telem.update();
	}
}