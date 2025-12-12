package org.firstinspires.ftc.teamcode

import android.content.Context
import android.util.Log
import com.minerkid08.dynamicopmodeloader.CompileError
import com.minerkid08.dynamicopmodeloader.FileServer
import com.minerkid08.dynamicopmodeloader.LuaError
import com.minerkid08.dynamicopmodeloader.OpmodeLoader
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.OpModeManager
import com.qualcomm.robotcore.eventloop.opmode.OpModeRegistrar
import org.firstinspires.ftc.ftccommon.external.OnCreate
import org.firstinspires.ftc.robotcore.internal.opmode.OpModeMeta
import org.firstinspires.ftc.teamcode.opmode.OpmodeloaderAutoBase
import org.firstinspires.ftc.teamcode.opmode.OpmodeloaderOpmodeBase

object FileServerStarter
{
	@OnCreate
	@JvmStatic
	fun startFileServer(context: Context)
	{
		FileServer.start();
	}

	@JvmStatic
	@OpModeRegistrar
	fun register(reg: OpModeManager)
	{
		var opmodes: Array<String>? = null;
		try
		{
			val opmodeLoader = OpmodeLoader();
			opmodes = opmodeLoader.init();
			opmodeLoader.close();
		}
		catch (e: LuaError)
		{
			Log.e("lua", "runtime error: " + e.localizedMessage);
			val stackTrace = e.stackTrace;
			for(elem in stackTrace)
			{
				var elemStr = elem.toString();
				elemStr = elemStr.substring(2);
				Log.e("lua", "  $elemStr");
			}
			val arr = Char(1);
			val meta = OpModeMeta.Builder()
				.setName(arr.toString() + "lua runtime error")
				.setFlavor(OpModeMeta.Flavor.TELEOP)
				.build();
			reg.register(meta, ErrorOpmode())
		}
		catch (e: CompileError)
		{
			Log.e("lua", "compile error: " + e.localizedMessage);
			val arr = Char(1);
			val meta = OpModeMeta.Builder()
				.setName(arr.toString() + "lua compile error")
				.setFlavor(OpModeMeta.Flavor.TELEOP)
				.build();
			reg.register(meta, ErrorOpmode())
		}

		if (opmodes == null)
		{
			Log.d("lua", "no opmodes found in lua instance");
			return;
		}

		for (opmode in opmodes)
		{
			if (opmode[0] == 't' && opmode[1] == '_')
			{
				val meta = OpModeMeta.Builder()
					.setName(opmode.substring(2))
					.setFlavor(OpModeMeta.Flavor.TELEOP)
					.setGroup("lua")
					.build();
				reg.register(meta, OpmodeloaderOpmodeBase(opmode))
			}
			if (opmode[0] == 'a' && opmode[1] == '_')
			{
				val meta = OpModeMeta.Builder()
					.setName(opmode.substring(2))
					.setFlavor(OpModeMeta.Flavor.AUTONOMOUS)
					.setGroup("lua")
					.build();
				reg.register(meta, OpmodeloaderAutoBase(opmode))
			}
		}
	}
}

class ErrorOpmode(): LinearOpMode()
{
	override fun runOpMode()
	{
	}
}