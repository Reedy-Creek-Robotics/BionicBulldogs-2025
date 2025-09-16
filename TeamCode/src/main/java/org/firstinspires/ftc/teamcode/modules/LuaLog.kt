package org.firstinspires.ftc.teamcode.modules

import android.util.Log
import com.minerkid08.dynamicopmodeloader.FunctionBuilder
import com.minerkid08.dynamicopmodeloader.OpmodeLoaderFunction

object LuaLog
{
	fun init(builder: FunctionBuilder)
	{
		builder.pushTable("log");
		builder.addStaticClassAsGlobal(LuaLog::class.java);
		builder.popTable();
	}

	@JvmStatic
	@OpmodeLoaderFunction
	fun d(tag: String, value: String)
	{
		Log.d(tag, value);
	}

	@JvmStatic
	@OpmodeLoaderFunction
	fun i(tag: String, value: String)
	{
		Log.i(tag, value);
	}

	@JvmStatic
	@OpmodeLoaderFunction
	fun w(tag: String, value: String)
	{
		Log.w(tag, value);
	}

	@JvmStatic
	@OpmodeLoaderFunction
	fun e(tag: String, value: String)
	{
		Log.e(tag, value);
	}
}