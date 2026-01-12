package org.firstinspires.ftc.teamcode.opmode

import com.minerkid08.dynamicopmodeloader.OpmodeLoader
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.modules.ApriltagDistance
import org.firstinspires.ftc.teamcode.modules.LuaDashboard
import org.firstinspires.ftc.teamcode.modules.LuaGamepad
import org.firstinspires.ftc.teamcode.modules.LuaLog
import org.firstinspires.ftc.teamcode.modules.LuaSave
import org.firstinspires.ftc.teamcode.modules.LuaTelemetry
import org.firstinspires.ftc.teamcode.modules.Turret
import org.firstinspires.ftc.teamcode.modules.luaHardware.LuaAprilTagProcessor
import org.firstinspires.ftc.teamcode.modules.luaHardware.LuaHardwaremap
import org.firstinspires.ftc.teamcode.modules.pathing.LuaFollower
import org.firstinspires.ftc.teamcode.modules.pathing.LuaPath
import org.firstinspires.ftc.teamcode.pedroPathing.Constants

@TeleOp
class GenDefFile : LinearOpMode()
{
	override fun runOpMode()
	{
		val opmodeloader = OpmodeLoader();

		opmodeloader.genDefinitionFile();

		val builder = opmodeloader.getFunctionBuilder();

		LuaSave.build(builder);
		LuaGamepad.init(builder, gamepad1);
		LuaHardwaremap.init(builder, hardwareMap);
		LuaTelemetry.init(builder, telemetry);
		LuaDashboard.init(builder);
		LuaAprilTagProcessor.build(builder, hardwareMap)
		LuaLog.init(builder);
		Turret.init(builder, hardwareMap);

		val follower = Constants.createFollower(hardwareMap);
		LuaPath.init(builder, follower);
		builder.addClassAsGlobal(ApriltagDistance::class.java)
		LuaFollower.init(builder, follower);

		opmodeloader.init();
	}
}