package org.firstinspires.ftc.teamcode.opmode

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.robotcore.external.Telemetry
import kotlin.math.abs

@TeleOp
class TurretEncoderTest : LinearOpMode()
{
  lateinit var motor: DcMotor;

  override fun runOpMode()
  {
		motor = hardwareMap.dcMotor.get("turetM");
		motor.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER;
		motor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER;
    telemetry.setDisplayFormat(Telemetry.DisplayFormat.MONOSPACE);

    val ticksPerRev = 145.1;

    val gearRatio = 208.0 / 114;

    val ticksPerDeg = -ticksPerRev / 360 * gearRatio;

    val limit = abs((ticksPerDeg * 90).toInt());

    waitForStart();

    var angle = 0;

    while (opModeIsActive())
    {
      if (gamepad1.crossWasPressed())
      {
        val newpos = (angle * ticksPerDeg).toInt();
        val targetPosition = clampi(-limit, limit, newpos);
        motor.power = 0.0;
        motor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER;
        motor.targetPosition = targetPosition;
        motor.mode = DcMotor.RunMode.RUN_TO_POSITION;
        motor.power = 0.5;
      }

      if (gamepad1.dpadUpWasPressed())
        angle += 10;
      if (gamepad1.dpadDownWasPressed())
        angle -= 10;

      telemetry.addData("angle", angle);
      telemetry.addData("targetPos", motor.targetPosition);
      telemetry.addData("curPos", motor.currentPosition);
      telemetry.update();
    }
  }
}
