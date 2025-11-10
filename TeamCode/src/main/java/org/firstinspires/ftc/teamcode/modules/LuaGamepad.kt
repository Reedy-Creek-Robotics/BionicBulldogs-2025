package org.firstinspires.ftc.teamcode.modules

import com.minerkid08.dynamicopmodeloader.FunctionBuilder
import com.minerkid08.dynamicopmodeloader.LuaType
import com.qualcomm.robotcore.hardware.Gamepad

class LuaGamepad(private val gamepad: Gamepad)
{
	companion object
	{
		fun init(builder: FunctionBuilder, gamepad: Gamepad)
		{
			builder.pushTable("gamepad");

			builder.setCurrentObject(LuaGamepad(gamepad));

			builder.addGlobalFunction("getLeftStickX", LuaType.Float);
			builder.addGlobalFunction("getLeftStickY", LuaType.Float);
			builder.addGlobalFunction("getRightStickX", LuaType.Float);
			builder.addGlobalFunction("getRightStickY", LuaType.Float);

			builder.addGlobalFunction("getDpadUp", LuaType.Bool);
			builder.addGlobalFunction("getDpadDown", LuaType.Bool);
			builder.addGlobalFunction("getDpadLeft", LuaType.Bool);
			builder.addGlobalFunction("getDpadRight", LuaType.Bool);

			builder.addGlobalFunction("getTriangle", LuaType.Bool);
			builder.addGlobalFunction("getCircle", LuaType.Bool);
			builder.addGlobalFunction("getCross", LuaType.Bool);
			builder.addGlobalFunction("getSquare", LuaType.Bool);

			builder.addGlobalFunction("getLeftTrigger", LuaType.Float);
			builder.addGlobalFunction("getRightTrigger", LuaType.Float);
			builder.addGlobalFunction("getLeftBumper", LuaType.Bool);
			builder.addGlobalFunction("getRightBumper", LuaType.Bool);

			builder.addGlobalFunction("getStart", LuaType.Bool);
			builder.addGlobalFunction("getShare", LuaType.Bool);
			builder.addGlobalFunction("getTouchpad", LuaType.Bool);

			builder.addGlobalFunction("getDpadUp2", LuaType.Bool);
			builder.addGlobalFunction("getDpadDown2", LuaType.Bool);
			builder.addGlobalFunction("getDpadLeft2", LuaType.Bool);
			builder.addGlobalFunction("getDpadRight2", LuaType.Bool);

			builder.addGlobalFunction("getTriangle2", LuaType.Bool);
			builder.addGlobalFunction("getCircle2", LuaType.Bool);
			builder.addGlobalFunction("getCross2", LuaType.Bool);
			builder.addGlobalFunction("getSquare2", LuaType.Bool);

			builder.addGlobalFunction("getStart2", LuaType.Bool);
			builder.addGlobalFunction("getShare2", LuaType.Bool);
			builder.addGlobalFunction("getTouchpad2", LuaType.Bool);

			builder.addGlobalFunction("getLeftBumper2", LuaType.Bool);
			builder.addGlobalFunction("getRightBumper2", LuaType.Bool);

			builder.addGlobalFunction("getStart2", LuaType.Bool);
			builder.addGlobalFunction("getShare2", LuaType.Bool);
			builder.addGlobalFunction("getTouchpad2", LuaType.Bool);
			builder.popTable();
		}
	}

	fun getLeftStickX() = gamepad.left_stick_x;
	fun getLeftStickY() = gamepad.left_stick_y;

	fun getRightStickX() = gamepad.right_stick_x;
	fun getRightStickY() = gamepad.right_stick_y;

	fun getDpadUp() = gamepad.dpad_up;
	fun getDpadDown() = gamepad.dpad_down;
	fun getDpadLeft() = gamepad.dpad_left;
	fun getDpadRight() = gamepad.dpad_right;

	fun getCross() = gamepad.cross;
	fun getSquare() = gamepad.square;
	fun getTriangle() = gamepad.triangle;
	fun getCircle() = gamepad.circle;

	fun getLeftTrigger() = gamepad.left_trigger;
	fun getRightTrigger() = gamepad.right_trigger;
	fun getLeftBumper() = gamepad.left_bumper;
	fun getRightBumper() = gamepad.right_bumper;

	fun getStart() = gamepad.start;
	fun getShare() = gamepad.share;
	fun getTouchpad() = gamepad.touchpad;


	fun getDpadUp2() = gamepad.dpadUpWasPressed();
	fun getDpadDown2() = gamepad.dpadDownWasPressed();
	fun getDpadLeft2() = gamepad.dpadLeftWasPressed();
	fun getDpadRight2() = gamepad.dpadRightWasReleased();

	fun getCross2() = gamepad.crossWasPressed();
	fun getSquare2() = gamepad.squareWasPressed();
	fun getTriangle2() = gamepad.triangleWasPressed();
	fun getCircle2() = gamepad.circleWasPressed();

	fun getLeftBumper2() = gamepad.leftBumperWasPressed();
	fun getRightBumper2() = gamepad.rightBumperWasPressed();

	fun getStart2() = gamepad.startWasPressed();
	fun getShare2() = gamepad.shareWasPressed();
	fun getTouchpad2() = gamepad.touchpadWasPressed();
}