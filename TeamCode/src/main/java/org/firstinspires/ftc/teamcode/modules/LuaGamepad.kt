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

			builder.addObjectFunction("getLeftStickX", LuaType.Float);
			builder.addObjectFunction("getLeftStickY", LuaType.Float);
			builder.addObjectFunction("getRightStickX", LuaType.Float);
			builder.addObjectFunction("getRightStickY", LuaType.Float);

			builder.addObjectFunction("getDpadUp", LuaType.Bool);
			builder.addObjectFunction("getDpadDown", LuaType.Bool);
			builder.addObjectFunction("getDpadLeft", LuaType.Bool);
			builder.addObjectFunction("getDpadRight", LuaType.Bool);

			builder.addObjectFunction("getTriangle", LuaType.Bool);
			builder.addObjectFunction("getCircle", LuaType.Bool);
			builder.addObjectFunction("getCross", LuaType.Bool);
			builder.addObjectFunction("getSquare", LuaType.Bool);

			builder.addObjectFunction("getLeftTrigger", LuaType.Float);
			builder.addObjectFunction("getRightTrigger", LuaType.Float);
			builder.addObjectFunction("getLeftBumper", LuaType.Bool);
			builder.addObjectFunction("getRightBumper", LuaType.Bool);

			builder.addObjectFunction("getStart", LuaType.Bool);
			builder.addObjectFunction("getShare", LuaType.Bool);
			builder.addObjectFunction("getTouchpad", LuaType.Bool);
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
}