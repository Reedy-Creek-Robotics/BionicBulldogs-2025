package org.firstinspires.ftc.teamcode.modules

import com.minerkid08.dynamicopmodeloader.OpmodeLoaderFunction
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit

class LuaDcMotor(private val m: DcMotor)
{
	@OpmodeLoaderFunction
	fun setPower(power: Double)
	{
		m.power = power;
	}

	@OpmodeLoaderFunction
	fun setTargetPosition(pos: Int)
	{
		m.targetPosition = pos;
	}

	@OpmodeLoaderFunction
	fun setDirection(dir: Int)
	{
		m.direction = when (dir)
		{
			LuaDefines.Direction.Forward -> DcMotorSimple.Direction.FORWARD
			LuaDefines.Direction.Reverse -> DcMotorSimple.Direction.REVERSE
			else                         -> DcMotorSimple.Direction.FORWARD
		}
	}

	@OpmodeLoaderFunction
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

	@OpmodeLoaderFunction
	fun setZeroPowerBehavior(mode: Int)
	{
		m.zeroPowerBehavior = when (mode)
		{
			LuaDefines.ZeroPowerBehavior.Float -> DcMotor.ZeroPowerBehavior.FLOAT
			LuaDefines.ZeroPowerBehavior.Brake -> DcMotor.ZeroPowerBehavior.BRAKE
			else                               -> DcMotor.ZeroPowerBehavior.FLOAT
		}
	}

	@OpmodeLoaderFunction
	fun getCurrentPosition(): Int = m.currentPosition;

	@OpmodeLoaderFunction
	fun getTargetPosition(): Int = m.targetPosition;
}

class LuaDcMotorEx(private val m: DcMotorEx)
{
	@OpmodeLoaderFunction
	fun getCurrent() = m.getCurrent(CurrentUnit.AMPS);

	@OpmodeLoaderFunction
	fun getVelocity() = m.velocity;

	@OpmodeLoaderFunction
	fun setPower(power: Double)
	{
		m.power = power;
	}

	@OpmodeLoaderFunction
	fun setTargetPosition(pos: Int)
	{
		m.targetPosition = pos;
	}

	@OpmodeLoaderFunction
	fun setDirection(dir: Int)
	{
		m.direction = when (dir)
		{
			LuaDefines.Direction.Forward -> DcMotorSimple.Direction.FORWARD
			LuaDefines.Direction.Reverse -> DcMotorSimple.Direction.REVERSE
			else                         -> DcMotorSimple.Direction.FORWARD
		}
	}

	@OpmodeLoaderFunction
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

	@OpmodeLoaderFunction
	fun setZeroPowerBehavior(mode: Int)
	{
		m.zeroPowerBehavior = when (mode)
		{
			LuaDefines.ZeroPowerBehavior.Float -> DcMotor.ZeroPowerBehavior.FLOAT
			LuaDefines.ZeroPowerBehavior.Brake -> DcMotor.ZeroPowerBehavior.BRAKE
			else                               -> DcMotor.ZeroPowerBehavior.FLOAT
		}
	}

	@OpmodeLoaderFunction
	fun getCurrentPosition(): Int = m.currentPosition;

	@OpmodeLoaderFunction
	fun getTargetPosition(): Int = m.targetPosition;
}
