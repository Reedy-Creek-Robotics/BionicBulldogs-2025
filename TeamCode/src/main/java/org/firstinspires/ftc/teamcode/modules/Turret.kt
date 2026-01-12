package org.firstinspires.ftc.teamcode.modules;

import com.minerkid08.dynamicopmodeloader.FunctionBuilder
import com.minerkid08.dynamicopmodeloader.OpmodeLoaderFunction
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.modules.luaHardware.LuaDcMotor
import org.firstinspires.ftc.teamcode.opmode.clampi
import kotlin.math.abs

class Turret(val hardwaremap: HardwareMap)
{
	companion object
	{
		fun init(builder: FunctionBuilder, hardwareMap: HardwareMap)
		{
			builder.pushTable("turret");
			builder.addObjectAsGlobal(Turret(hardwareMap))
			builder.popTable();
		}
	}

	enum class State
	{
		Tracking, Resetting
	}

	lateinit var motor: DcMotor;
	var state = State.Tracking;

	//145.1 for 1150
	//384.5 for 435

	private val ticksPerRev = 384.5;
	private val gearRatio = 208.0 / 50.0;
	private val ticksPerDeg = ticksPerRev / 360 * gearRatio;
	private val limit = abs(ticksPerDeg * 90).toInt();

	private var offset = 0.0;
	private var prevPos = 0;

	@OpmodeLoaderFunction
	fun init(reset: Boolean)
	{
		motor = hardwaremap.dcMotor.get("turret");
		if (reset)
			motor.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER;
		motor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER;
	}

	@OpmodeLoaderFunction
	fun getMotor() = LuaDcMotor(motor);

	@OpmodeLoaderFunction
	fun reset()
	{
		state = State.Resetting;
		motor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER;
		motor.power = -0.2;
		prevPos = 999999999;
	}

	@OpmodeLoaderFunction
	fun start()
	{
		motor.power = 0.0;
		motor.targetPosition = 0;
		motor.mode = DcMotor.RunMode.RUN_TO_POSITION;
		motor.power = 1.0;
	}

	@OpmodeLoaderFunction
	fun getOffset() = offset;

	@OpmodeLoaderFunction
	fun setOffset(o: Double)
	{
		offset = o;
	}

	@OpmodeLoaderFunction
	fun update(pos: Double)
	{
		if (state == State.Resetting)
		{
			if(prevPos == motor.currentPosition)
			{
				motor.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER;
				offset = 92.0;

				motor.power = 0.0;
				turnTo(offset);
				motor.mode = DcMotor.RunMode.RUN_TO_POSITION;
				motor.power = 1.0;
			}
			prevPos = motor.currentPosition;
		}
		else
			turnTo(pos + offset);
	}

	@OpmodeLoaderFunction
	fun turnAngle(angle: Double)
	{
		if(state == State.Resetting) return;
		val newpos = motor.currentPosition + (angle * ticksPerDeg).toInt();
		val targetPosition = clampi(-limit, limit, newpos);
		motor.targetPosition = targetPosition;
	}

	@OpmodeLoaderFunction
	fun turnTo(angle: Double)
	{
		if(state == State.Resetting) return;
		val newpos = (angle * ticksPerDeg).toInt();
		val targetPosition = clampi(-limit, limit, newpos);
		motor.targetPosition = targetPosition;
	}
}
