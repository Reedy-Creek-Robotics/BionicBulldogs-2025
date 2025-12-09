package org.firstinspires.ftc.teamcode.opmode

import com.qualcomm.hardware.limelightvision.Limelight3A
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.robotcore.external.Telemetry
import com.qualcomm.robotcore.hardware.DcMotor
import kotlin.math.abs

@TeleOp
class AutoAimLimelight : LinearOpMode()
{
	enum class State
	{
		Tracking, Waiting
	}

	private var targetPosition = 0;
	lateinit var motor: DcMotor;

	override fun runOpMode()
	{
		val limelight = hardwareMap.get(Limelight3A::class.java, "limelight");
		limelight.setPollRateHz(100);
		limelight.start();

		limelight.pipelineSwitch(0);

		telemetry.setDisplayFormat(Telemetry.DisplayFormat.MONOSPACE);

		motor = hardwareMap.dcMotor.get("turret") as DcMotorEx
		motor.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER;
		motor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER;

		//tpr = ticks per rev
    val ticksPerRev = 537.7;

		//tpd = tick per degrees
  
    val gearRatio = 208.0 / 114;

		val ticksPerDeg = ticksPerRev / 360 * gearRatio;

		val limit = abs((ticksPerDeg * 90).toInt());

		val timer = ElapsedTime();

		var state = State.Waiting;

		waitForStart();

		while (opModeIsActive())
		{
			val result = limelight.latestResult;
			if (result != null)
			{
				for (tag in result.fiducialResults)
				{
					if(tag.fiducialId != 24)
						continue;
					val x = tag.targetXDegrees;
					telemetry.addLine("tag 24");
					telemetry.addLine("  bearing: $x");

					if (x > 10 || x < -10)
					{
						val newpos = motor.currentPosition + (x * ticksPerDeg).toInt();
						targetPosition = clampi(-limit, limit, newpos);
						/*motor.power = 0.0;
						motor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER;
						motor.targetPosition = clampi(-limit, limit, newpos);
						motor.mode = DcMotor.RunMode.RUN_TO_POSITION;
						motor.power = 1.0;*/
					}
					state = State.Tracking;
					timer.reset();
				}
			}
			if (state == State.Tracking)
			{
				if (timer.seconds() >= 0.5)
				{
					state = State.Waiting;
					targetPosition = 0;
					/*motor.power = 0.0;
					motor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER;
					motor.targetPosition = 0;
					motor.mode = DcMotor.RunMode.RUN_TO_POSITION;
					motor.power = 1.0;*/
				}
			}
			telemetry.addData("targetPos", targetPosition);
			telemetry.addData("curPos", motor.currentPosition);
			telemetry.update();
		}
	}
}