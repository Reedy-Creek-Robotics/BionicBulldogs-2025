package org.firstinspires.ftc.teamcode.opmode

import com.minerkid08.dynamicopmodeloader.OpmodeLoader
import com.pedropathing.follower.Follower
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.modules.LuaHardwaremap
import org.firstinspires.ftc.teamcode.modules.LuaLog
import org.firstinspires.ftc.teamcode.modules.LuaTelemetry
import org.firstinspires.ftc.teamcode.modules.pathing.LuaFollower
import org.firstinspires.ftc.teamcode.modules.pathing.LuaPath
import org.firstinspires.ftc.teamcode.pedroPathing.constants.FConstants
import org.firstinspires.ftc.teamcode.pedroPathing.constants.LConstants

abstract class OpmodeloaderAutoBase(private val name: String) : LinearOpMode()
{
	override fun runOpMode()
	{
		val opmodeloader = OpmodeLoader();
		val builder = opmodeloader.getFunctionBuilder();

		val follower = Follower(hardwareMap, FConstants::class.java, LConstants::class.java);

		LuaHardwaremap.init(builder, hardwareMap);
		LuaTelemetry.init(builder, telemetry);
		LuaFollower.init(builder, follower);
		LuaPath.init(builder, follower);
		LuaLog.init(builder);

		opmodeloader.init();
		opmodeloader.loadOpmode(name);
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
		opmodeloader.close();
	}
}