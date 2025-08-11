package org.firstinspires.ftc.teamcode.modules

import com.minerkid08.dynamicopmodeloader.FunctionBuilder
import com.minerkid08.dynamicopmodeloader.LuaType
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple

class LuaDcMotor(private val m: DcMotor)
{
	companion object
	{
		fun init(builder: FunctionBuilder)
		{
			builder.addClassFunction(
				LuaDcMotor::class.java, "setPower", argTypes = listOf(LuaType.Double)
			);
			builder.addClassFunction(
				LuaDcMotor::class.java, "setTargetPosition", argTypes = listOf(LuaType.Int)
			);
			builder.addClassFunction(
				LuaDcMotor::class.java, "setDirection", argTypes = listOf(LuaType.Int)
			);
			builder.addClassFunction(
				LuaDcMotor::class.java, "setMode", argTypes = listOf(LuaType.Int)
			);
			builder.addClassFunction(
				LuaDcMotor::class.java, "setZeroPowerBehavior", argTypes = listOf(LuaType.Int)
			);
			builder.addClassFunction(LuaDcMotor::class.java, "getCurrentPosition", LuaType.Int);
			builder.addClassFunction(LuaDcMotor::class.java, "getTargetPosition", LuaType.Int);
		}
	}

	fun setPower(power: Double)
	{
		m.power = power;
	}

	fun setTargetPosition(pos: Int)
	{
		m.targetPosition = pos;
	}

	fun setDirection(dir: Int)
	{
		m.direction = when (dir)
		{
			LuaDefines.Direction.Forward -> DcMotorSimple.Direction.FORWARD
			LuaDefines.Direction.Reverse -> DcMotorSimple.Direction.REVERSE
			else                         -> DcMotorSimple.Direction.FORWARD
		}
	}

	fun setMode(mode: Int)
	{
		m.mode = when (mode)
		{
			LuaDefines.RunMode.RunWithoutEncoder   -> DcMotor.RunMode.RUN_WITHOUT_ENCODER
			LuaDefines.RunMode.StopAndResetEncoder -> DcMotor.RunMode.STOP_AND_RESET_ENCODER
			LuaDefines.RunMode.RunToPosition       -> DcMotor.RunMode.RUN_TO_POSITION
			else                                   -> DcMotor.RunMode.RUN_WITHOUT_ENCODER
		}
	}

	fun setZeroPowerBehavior(mode: Int)
	{
		m.zeroPowerBehavior = when (mode)
		{
			LuaDefines.ZeroPowerBehavior.Float -> DcMotor.ZeroPowerBehavior.FLOAT
			LuaDefines.ZeroPowerBehavior.Brake -> DcMotor.ZeroPowerBehavior.BRAKE
			else                               -> DcMotor.ZeroPowerBehavior.FLOAT
		}
	}

	fun getCurrentPosition(): Int = m.currentPosition;
	fun getTargetPosition(): Int = m.targetPosition;
}
