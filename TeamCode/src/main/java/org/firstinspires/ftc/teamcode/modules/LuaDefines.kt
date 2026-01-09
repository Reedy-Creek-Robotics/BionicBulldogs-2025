package org.firstinspires.ftc.teamcode.modules

import com.minerkid08.dynamicopmodeloader.FunctionBuilder
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple

object LuaDefines
{
	object Direction
	{
		const val Forward = 0;
		const val Reverse = 1;
	}

	object RunMode
	{
		const val RunWithoutEncoder = 0;
		const val StopAndResetEncoder = 1;
		const val RunToPosition = 2;
		const val RunUsingEncoder = 3;
	}

	object ZeroPowerBehavior
	{
		const val Float = 0;
		const val Brake = 1;
	}

	fun build(builder: FunctionBuilder)
	{
		builder.createClass("Direction");
		builder.createClass("RunMode");
		builder.createClass("ZeroPowerBehavior");

		builder.pushTable("direction");
		builder.pushValueo("forward", DcMotorSimple.Direction.FORWARD);
		builder.pushValueo("reverse", DcMotorSimple.Direction.REVERSE);
		builder.popTable();

		builder.pushTable("runMode");
		builder.pushValueo("runWithoutEncoder", DcMotor.RunMode.RUN_WITHOUT_ENCODER);
		builder.pushValueo("runUsingEncoder", DcMotor.RunMode.RUN_USING_ENCODER);
		builder.pushValueo("runToPosition", DcMotor.RunMode.RUN_TO_POSITION);
		builder.pushValueo("stopAndResetEncoder", DcMotor.RunMode.STOP_AND_RESET_ENCODER);
		builder.popTable();

		builder.pushTable("zeroPowerBehavior");
		builder.pushValueo("brake", DcMotor.ZeroPowerBehavior.BRAKE);
		builder.pushValueo("float", DcMotor.ZeroPowerBehavior.FLOAT);
		builder.popTable();
	}
}