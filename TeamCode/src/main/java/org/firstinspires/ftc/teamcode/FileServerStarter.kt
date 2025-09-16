package org.firstinspires.ftc.teamcode

import android.content.Context
import android.util.Log
import com.minerkid08.dynamicopmodeloader.FileServer
import org.firstinspires.ftc.ftccommon.external.OnCreate

object FileServerStarter
{
	@OnCreate
	@JvmStatic
	fun startFileServer(context: Context)
	{
		Log.d("fserver", "calling start");
		FileServer.start();
	}
}