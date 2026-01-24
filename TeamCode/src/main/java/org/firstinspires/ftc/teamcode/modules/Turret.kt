package org.firstinspires.ftc.teamcode.modules;

import com.minerkid08.dynamicopmodeloader.FunctionBuilder
import com.minerkid08.dynamicopmodeloader.OpmodeLoaderFunction
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.PIDFCoefficients
import com.qualcomm.robotcore.hardware.TouchSensor
import org.firstinspires.ftc.teamcode.modules.luaHardware.LuaDcMotor
import org.firstinspires.ftc.teamcode.modules.luaHardware.LuaDcMotorEx
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

	lateinit var motor: DcMotorEx;
	lateinit var sensor: TouchSensor;
	var state = State.Tracking;

	//145.1 for 1150
	//384.5 for 435

	private val ticksPerRev = 384.5;
	private val gearRatio = 208.0 / 51.0;
	private val ticksPerDeg = ticksPerRev / 360 * gearRatio;
	private val limit = abs(ticksPerDeg * 89).toInt();
	private var offsetTicks = 0;

	private var offset = 0.0;
	private var prevPos = 0;

	@OpmodeLoaderFunction
	fun init(reset: Boolean)
	{
		motor = hardwaremap.dcMotor.get("turret") as DcMotorEx;
		sensor = hardwaremap.get(TouchSensor::class.java, "turretSensor");
		if (reset)
			motor.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER;
		motor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER;
		motor.targetPositionTolerance = 1;
		motor.setPIDFCoefficients(DcMotor.RunMode.RUN_TO_POSITION, PIDFCoefficients(15.0, 0.0, 0.0, 0.0));
	}

	@OpmodeLoaderFunction
	fun getMotor() = LuaDcMotorEx(motor);

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
		offsetTicks = (offset * ticksPerDeg).toInt();
	}

	@OpmodeLoaderFunction
	fun update(pos: Double)
	{
		if (state == State.Resetting)
		{
			if(!sensor.isPressed)
			{
				motor.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER;

				motor.power = 0.0;
				setOffset(0.0);
				turnTo(pos);
				motor.mode = DcMotor.RunMode.RUN_TO_POSITION;
				motor.power = 1.0;
				state = State.Tracking;
			}
			prevPos = motor.currentPosition;
		}
		else
			turnTo(pos);
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
		val targetPosition = clampi(-limit, limit, newpos) + offsetTicks;
		motor.targetPosition = targetPosition;
	}
}
