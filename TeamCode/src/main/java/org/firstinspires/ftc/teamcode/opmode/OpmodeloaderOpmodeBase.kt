package org.firstinspires.ftc.teamcode.opmode

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.minerkid08.dynamicopmodeloader.OpmodeLoader
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.modules.LuaGamepad
import org.firstinspires.ftc.teamcode.modules.LuaHardwaremap
import org.firstinspires.ftc.teamcode.modules.LuaLog
import org.firstinspires.ftc.teamcode.modules.LuaTelemetry

abstract class OpmodeloaderOpmodeBase(private val name: String) : LinearOpMode()
{
	override fun runOpMode()
	{
		val opmodeloader = OpmodeLoader();
		val builder = opmodeloader.getFunctionBuilder();

		LuaGamepad.init(builder, gamepad1);
		LuaHardwaremap.init(builder, hardwareMap);
		LuaTelemetry.init(builder, telemetry);
		LuaLog.init(builder);

		opmodeloader.init();
		opmodeloader.loadOpmode(name);
		waitForStart();

		if (!opModeIsActive())
			return;

		opmodeloader.start();
		val e = ElapsedTime();
		e.reset();

		var now = e.seconds();
		var dt: Double;
		var prev = now;

		while(opModeIsActive())
		{
			now = e.seconds();
			dt = now - prev;
			prev = now;
			if(opmodeloader.update(dt, now))
				break;
		}
		opmodeloader.close();
	}
}