package org.firstinspires.ftc.teamcode

import android.content.Context
import android.util.Log
import com.minerkid08.dynamicopmodeloader.FileServer
import com.minerkid08.dynamicopmodeloader.LuaCompileError
import com.minerkid08.dynamicopmodeloader.LuaRuntimeError
import com.minerkid08.dynamicopmodeloader.Opmode
import com.minerkid08.dynamicopmodeloader.OpmodeLoader
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.OpModeManager
import com.qualcomm.robotcore.eventloop.opmode.OpModeRegistrar
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareDevice
import com.qualcomm.robotcore.hardware.LightSensor
import com.qualcomm.robotcore.hardware.RobotCoreLynxUsbDevice
import com.qualcomm.robotcore.hardware.ServoController
import com.qualcomm.robotcore.util.ElapsedTime
import com.qualcomm.robotcore.util.RobotLog
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
		//reg.register(
		//	OpModeMeta.Builder()
		//		.setName("\$Stop\$Robot$")
		//		.setFlavor(OpModeMeta.Flavor.SYSTEM)
		//		.setSystemOpModeBaseDisplayName("Stop Robot")
		//		.build(),
		//	DefaultOpMode()
		//);

		var opmodes: Array<Opmode>? = null;
		try
		{
			val opmodeLoader = OpmodeLoader();
			opmodes = opmodeLoader.init();
			opmodeLoader.close();
		}
		catch (e: LuaRuntimeError)
		{
			RobotLog.setGlobalErrorMsg("lua runtime error \"${e.localizedMessage}\"");
			Log.e("lua", "runtime error: " + e.localizedMessage);
			val stackTrace = e.stackTrace;
			for (elem in stackTrace)
			{
				var elemStr = elem.toString();
				elemStr = elemStr.substring(2);
				RobotLog.addGlobalWarningMessage("  $elemStr");
				Log.e("lua", "  $elemStr");
			}
			val arr = Char(1);
			val meta = OpModeMeta.Builder()
				.setName(arr.toString() + "lua runtime error")
				.setFlavor(OpModeMeta.Flavor.TELEOP)
				.build();
			reg.register(meta, ErrorOpmode());
		}
		catch (e: LuaCompileError)
		{
			RobotLog.setGlobalErrorMsg("lua compile error \"${e.localizedMessage}\"");
			Log.e("lua", "compile error: " + e.localizedMessage);
			val arr = Char(1);
			val meta = OpModeMeta.Builder()
				.setName(arr.toString() + "lua compile error")
				.setFlavor(OpModeMeta.Flavor.TELEOP)
				.build();
			reg.register(meta, ErrorOpmode());
		}

		if (opmodes == null)
		{
			Log.d("lua", "no opmodes found in lua instance");
			return;
		}

		for (opmode in opmodes)
		{
			val group = if (opmode.group == null) "usorted" else opmode.group;
			val type =
				if (opmode.type == Opmode.Telop) OpModeMeta.Flavor.TELEOP else OpModeMeta.Flavor.AUTONOMOUS;
			val meta = OpModeMeta.Builder()
				.setName(opmode.name)
				.setFlavor(type)
				.setGroup(group!!)
				.setSystemOpModeBaseDisplayName("mainTelop")
				.build();
			if (opmode.type == Opmode.Telop)
				reg.register(meta, OpmodeloaderOpmodeBase(opmode.name));
			else
				reg.register(meta, OpmodeloaderAutoBase(opmode.name));
		}
		RobotLog.addGlobalWarningMessage("loaded ${opmodes.size} opmodes");
	}
}

class ErrorOpmode() : LinearOpMode()
{
	override fun runOpMode()
	{
	}
}

class DefaultOpMode : OpMode()
{
	private var nanoNextSafe: Long = 0;
	private var firstTimeRun = true;
	private val blinkerTimer = ElapsedTime();

	init
	{
		firstTimeRun = true;
	}

	override fun init()
	{
		startSafe();
		telemetry.addData("Status", "Robot is stopping :)");
	}

	override fun init_loop()
	{
		staySafe();
		telemetry.addData("Status", "Robot is stopped :)");
	}

	override fun loop()
	{
		staySafe();
		telemetry.addData("Status", "Robot is stopped :)");
	}

	override fun stop()
	{
	}

	private fun isLynxDevice(device: HardwareDevice): Boolean
	{
		return device.manufacturer == HardwareDevice.Manufacturer.Lynx;
	}

	private fun isLynxDevice(o: Any?): Boolean
	{
		return isLynxDevice(o as HardwareDevice?);
	}

	private fun startSafe()
	{
		for (motor in hardwareMap.getAll(DcMotorSimple::class.java))
		{
			if (motor.power != 0.0) motor.power = 0.0;
		}

		if (firstTimeRun)
		{
			firstTimeRun = false;
			nanoNextSafe = System.nanoTime();
			blinkerTimer.reset();
		}
		else nanoNextSafe = System.nanoTime() + SAFE_WAIT_NANOS;
	}

	private fun staySafe()
	{
		if (System.nanoTime() > nanoNextSafe)
		{
			for (device in hardwareMap.getAll(RobotCoreLynxUsbDevice::class.java))
			{
				device.failSafe();
			}

			for (servoController in hardwareMap.getAll(ServoController::class.java))
			{
				if (!isLynxDevice(servoController))
				{
					servoController.pwmDisable();
				}
			}

			for (dcMotor in hardwareMap.getAll(DcMotor::class.java))
			{
				if (!isLynxDevice(dcMotor))
				{
					dcMotor.power = 0.0;
					dcMotor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER;
				}
			}

			for (light in hardwareMap.getAll(LightSensor::class.java))
			{
				light.enableLed(false);
			}

			nanoNextSafe = System.nanoTime() + SAFE_WAIT_NANOS;
		}
	}

	companion object
	{
		private val SAFE_WAIT_NANOS = 100 * ElapsedTime.MILLIS_IN_NANO;
	}
}