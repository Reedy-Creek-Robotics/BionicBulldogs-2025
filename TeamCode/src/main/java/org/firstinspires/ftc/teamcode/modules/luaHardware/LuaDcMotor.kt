package org.firstinspires.ftc.teamcode.modules.luaHardware

import com.minerkid08.dynamicopmodeloader.FunctionBuilder
import com.minerkid08.dynamicopmodeloader.LuaError
import com.minerkid08.dynamicopmodeloader.LuaType
import com.minerkid08.dynamicopmodeloader.OpmodeLoaderFunction
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.MotorControlAlgorithm
import com.qualcomm.robotcore.hardware.PIDFCoefficients
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit
import org.firstinspires.ftc.teamcode.modules.LuaDefines

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
			LuaDefines.RunMode.RunUsingEncoder     -> DcMotor.RunMode.RUN_USING_ENCODER
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

	@OpmodeLoaderFunction
	fun getPower(): Double = m.power;
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
	fun setVelocity(ticks: Double)
	{
		m.velocity = ticks;
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
			else                         -> throw LuaError("invalid direction $dir");
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
			LuaDefines.RunMode.RunUsingEncoder     -> DcMotor.RunMode.RUN_USING_ENCODER
			else                                   -> throw LuaError("invalid run mode $mode");
		}
	}

	@OpmodeLoaderFunction
	fun setZeroPowerBehavior(mode: Int)
	{
		m.zeroPowerBehavior = when (mode)
		{
			LuaDefines.ZeroPowerBehavior.Float -> DcMotor.ZeroPowerBehavior.FLOAT
			LuaDefines.ZeroPowerBehavior.Brake -> DcMotor.ZeroPowerBehavior.BRAKE
			else                               -> throw LuaError("invalid zero power behavior $mode");
		}
	}

	@OpmodeLoaderFunction
	fun getCurrentPosition(): Int = m.currentPosition;

	@OpmodeLoaderFunction
	fun getTargetPosition(): Int = m.targetPosition;

	@OpmodeLoaderFunction
	fun getTargetPositionTolerance(): Int = m.targetPositionTolerance;

	@OpmodeLoaderFunction
	fun setTargetPositionTolorance(i: Int)
	{
		m.targetPositionTolerance = i;
	}

	@OpmodeLoaderFunction
	fun getPower(): Double = m.power;

	@OpmodeLoaderFunction
	fun setPidf(p: Double, i: Double, d: Double, f: Double)
	{
		m.setPIDFCoefficients(m.mode, PIDFCoefficients(p, i, d, f, MotorControlAlgorithm.PIDF));
	}
}

fun buildDcMotor(builder: FunctionBuilder)
{
	builder.createClass("DcMotor");
	builder.addClassFunction(
		DcMotor::class.java,
		"setDirection",
		LuaType.Void,
		listOf(LuaType.Object(DcMotorSimple.Direction::class.java))
	);
	builder.addClassFunction(
		DcMotor::class.java,
		"getDirection",
		LuaType.Object(DcMotorSimple.Direction::class.java)
	);
	builder.addClassFunction(DcMotor::class.java, "setPower", LuaType.Void, listOf(LuaType.Double));
	builder.addClassFunction(DcMotor::class.java, "getPower", LuaType.Double);
	builder.addClassFunction(
		DcMotor::class.java,
		"setZeroPowerBehavior",
		LuaType.Void,
		listOf(LuaType.Object(DcMotor.ZeroPowerBehavior::class.java))
	);
	builder.addClassFunction(
		DcMotor::class.java,
		"getZeroPowerBehavior",
		LuaType.Object(DcMotor.ZeroPowerBehavior::class.java)
	);
	builder.addClassFunction(
		DcMotor::class.java,
		"setTargetPosition",
		LuaType.Void,
		listOf(LuaType.Int)
	);
	builder.addClassFunction(DcMotor::class.java, "getTargetPosition", LuaType.Int);
	builder.addClassFunction(DcMotor::class.java, "getCurrentPosition", LuaType.Int);
	builder.addClassFunction(
		DcMotor::class.java,
		"setMode",
		LuaType.Void,
		listOf(LuaType.Object(DcMotor.RunMode::class.java))
	);
	builder.addClassFunction(
		DcMotor::class.java,
		"getMode",
		LuaType.Object(DcMotor.RunMode::class.java)
	);

	builder.createClass("DcMotorEx");
	builder.addClassFunction(
		DcMotorEx::class.java,
		"setDirection",
		LuaType.Void,
		listOf(LuaType.Object(DcMotorSimple.Direction::class.java))
	);
	builder.addClassFunction(
		DcMotorEx::class.java,
		"getDirection",
		LuaType.Object(DcMotorSimple.Direction::class.java)
	);
	builder.addClassFunction(DcMotorEx::class.java, "setPower", LuaType.Void, listOf(LuaType.Double));
	builder.addClassFunction(DcMotorEx::class.java, "getPower", LuaType.Double);
	builder.addClassFunction(
		DcMotorEx::class.java,
		"setZeroPowerBehavior",
		LuaType.Void,
		listOf(LuaType.Object(DcMotor.ZeroPowerBehavior::class.java))
	);
	builder.addClassFunction(
		DcMotorEx::class.java,
		"getZeroPowerBehavior",
		LuaType.Object(DcMotor.ZeroPowerBehavior::class.java)
	);
	builder.addClassFunction(
		DcMotorEx::class.java,
		"setTargetPosition",
		LuaType.Void,
		listOf(LuaType.Int)
	);
	builder.addClassFunction(DcMotorEx::class.java, "getTargetPosition", LuaType.Int);
	builder.addClassFunction(DcMotorEx::class.java, "getCurrentPosition", LuaType.Int);
	builder.addClassFunction(
		DcMotorEx::class.java,
		"setMode",
		LuaType.Void,
		listOf(LuaType.Object(DcMotor.RunMode::class.java))
	);
	builder.addClassFunction(
		DcMotorEx::class.java,
		"getMode",
		LuaType.Object(DcMotor.RunMode::class.java)
	);
	builder.addClassFunction(
		DcMotorEx::class.java,
		"setVelocity",
		LuaType.Void,
		listOf(LuaType.Double)
	);
	builder.addClassFunction(DcMotorEx::class.java, "getVelocity", LuaType.Double);
	builder.addClassFunction(
		DcMotorEx::class.java,
		"setPIDFCoefficients",
		LuaType.Void,
		listOf(
			LuaType.Object(DcMotor.RunMode::class.java),
			LuaType.Object(PIDFCoefficients::class.java)
		)
	);
	builder.addClassFunction(
		DcMotorEx::class.java,
		"getCurrent",
		LuaType.Double,
		listOf(LuaType.Object(CurrentUnit::class.java))
	);
}
