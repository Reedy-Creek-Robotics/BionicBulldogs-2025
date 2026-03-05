package org.firstinspires.ftc.teamcode.opmode

import com.minerkid08.dynamicopmodeloader.OpmodeLoader
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.util.ElapsedTime
import com.qualcomm.robotcore.util.RobotLog
import org.firstinspires.ftc.teamcode.LightStripDriver
import org.firstinspires.ftc.teamcode.modules.ApriltagDistance
import org.firstinspires.ftc.teamcode.modules.luaHardware.LuaAprilTagProcessor
import org.firstinspires.ftc.teamcode.modules.luaHardware.LuaHardwaremap
import org.firstinspires.ftc.teamcode.modules.LuaLog
import org.firstinspires.ftc.teamcode.modules.LuaDashboard
import org.firstinspires.ftc.teamcode.modules.LuaSave
import org.firstinspires.ftc.teamcode.modules.LuaTelemetry
import org.firstinspires.ftc.teamcode.modules.Turret
import org.firstinspires.ftc.teamcode.modules.pathing.LuaFollower
import org.firstinspires.ftc.teamcode.modules.pathing.LuaPath
import org.firstinspires.ftc.teamcode.pedroPathing.Constants

open class OpmodeloaderAutoBase(private val name: String) : LinearOpMode()
{
	override fun runOpMode()
	{
		RobotLog.clearGlobalErrorMsg();
		RobotLog.clearGlobalWarningMsg();
		val opmodeloader = OpmodeLoader();
		val builder = opmodeloader.getFunctionBuilder();

		val follower = Constants.createFollower(hardwareMap);

		LuaSave.build(builder);
		LuaHardwaremap.init(builder, hardwareMap);
		LuaTelemetry.init(builder, telemetry);
		LuaDashboard.init(builder);
		LuaPath.init(builder, follower);
		LuaLog.init(builder);
		Turret.init(builder, hardwareMap);
		builder.addClassAsGlobal(ApriltagDistance::class.java)
		LuaAprilTagProcessor.build(builder, hardwareMap)
		builder.addClassAsClass(LightStripDriver::class.java);

		opmodeloader.init();

		LuaFollower.init(builder, follower);

		opmodeloader.loadOpmode(name);

		sleep(1000);

		telemetry.addLine("initalised");
		telemetry.update();
		waitForStart();
		if (!opModeIsActive())
			return;

		opmodeloader.start();

		if (!opModeIsActive())
			return;

		val e = ElapsedTime();
		e.reset();

		var now = e.seconds();
		var dt: Double;
		var prev = now;

		while (opModeIsActive())
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
