package org.firstinspires.ftc.teamcode.opmode

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.minerkid08.dynamicopmodeloader.OpmodeLoader
import com.qualcomm.robotcore.util.ElapsedTime
import com.qualcomm.robotcore.util.RobotLog
import org.firstinspires.ftc.teamcode.LightStripDriver
import org.firstinspires.ftc.teamcode.modules.luaHardware.LuaAprilTagProcessor
import org.firstinspires.ftc.teamcode.modules.LuaDashboard
import org.firstinspires.ftc.teamcode.modules.LuaDefines
import org.firstinspires.ftc.teamcode.modules.LuaGamepad
import org.firstinspires.ftc.teamcode.modules.luaHardware.LuaHardwaremap
import org.firstinspires.ftc.teamcode.modules.LuaLog
import org.firstinspires.ftc.teamcode.modules.LuaSave
import org.firstinspires.ftc.teamcode.modules.LuaTelemetry
import org.firstinspires.ftc.teamcode.modules.Turret

open class OpmodeloaderOpmodeBase(private val name: String) : LinearOpMode()
{
	override fun runOpMode()
	{
		RobotLog.clearGlobalErrorMsg();
		RobotLog.clearGlobalWarningMsg();

		val opmodeloader = OpmodeLoader();
		val builder = opmodeloader.getFunctionBuilder();

		LuaSave.build(builder);
		LuaGamepad.init(builder, gamepad1);
		LuaHardwaremap.init(builder, hardwareMap);
		LuaTelemetry.init(builder, telemetry);
		LuaDashboard.init(builder);
		LuaAprilTagProcessor.build(builder, hardwareMap)
		LuaLog.init(builder);
		Turret.init(builder, hardwareMap);

		LuaDefines.build(builder);
		builder.addClassAsClass(LightStripDriver::class.java);

		opmodeloader.init();
		opmodeloader.loadOpmode(name);

		telemetry.addLine("initalised");
		telemetry.update();
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
		opmodeloader.stop();
		opmodeloader.close();
	}
}