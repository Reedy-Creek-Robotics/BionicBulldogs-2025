package org.firstinspires.ftc.teamcode.modules

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
}