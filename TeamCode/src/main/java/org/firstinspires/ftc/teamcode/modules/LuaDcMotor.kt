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
		m.direction = when(dir)
		{
			0 -> DcMotorSimple.Direction.FORWARD
			1 -> DcMotorSimple.Direction.REVERSE
			else -> DcMotorSimple.Direction.FORWARD
		}
	}

	fun setMode(mode: Int)
	{
		m.mode = when(mode)
		{
			0 -> DcMotor.RunMode.RUN_WITHOUT_ENCODER
			1 -> DcMotor.RunMode.STOP_AND_RESET_ENCODER
			2 -> DcMotor.RunMode.RUN_TO_POSITION
			else -> DcMotor.RunMode.RUN_WITHOUT_ENCODER
		}
	}

	fun setZeroPowerBehavior(mode: Int)
	{
		m.zeroPowerBehavior = when(mode)
		{
			0 -> DcMotor.ZeroPowerBehavior.FLOAT
			1 -> DcMotor.ZeroPowerBehavior.BRAKE
			else -> DcMotor.ZeroPowerBehavior.FLOAT
		}
	}

	fun getCurrentPosition(): Int = m.currentPosition;
	fun getTargetPosition(): Int = m.targetPosition;
}
