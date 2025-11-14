package org.firstinspires.ftc.teamcode

import android.content.Context
import android.util.Log
import com.minerkid08.dynamicopmodeloader.FileServer
import com.minerkid08.dynamicopmodeloader.OpmodeLoader
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
	fun register(manager: OpModeManager)
	{
		val opmodeLoader = OpmodeLoader();
		val opmodes = opmodeLoader.init();
		opmodeLoader.close();

		if (opmodes == null)
			return;
		for (opmode in opmodes)
		{
			if (opmode[0] == 't' && opmode[1] == '_')
			{
				val metadata = OpModeMeta.Builder()
					.setName(opmode.substring(2))
					.setFlavor(OpModeMeta.Flavor.TELEOP)
					.build();
				manager.register(metadata, OpmodeloaderOpmodeBase(opmode))
				Log.d("opmodeloader", "loading telop opmode '$opmode'");
			}
			if (opmode[0] == 'a' && opmode[1] == '_')
			{
				val metadata = OpModeMeta.Builder()
					.setName(opmode.substring(2))
					.setFlavor(OpModeMeta.Flavor.AUTONOMOUS)
					.build();
				manager.register(metadata, OpmodeloaderAutoBase(opmode))
				Log.d("opmodeloader", "loading auto opmode '$opmode'");
			}
		}
	}
}