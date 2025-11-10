package org.firstinspires.ftc.teamcode.modules

import com.minerkid08.dynamicopmodeloader.OpmodeLoaderFunction


class ApriltagDistance
{
	@OpmodeLoaderFunction
	fun apirlDis(dist: Double): Int
	{
		//velocitym is what the velocity of the motor is being set to
		var velocitym = 0
		if (dist >= 102)
		{
			velocitym = 1800
			return velocitym
		}
		else if (dist >= 45)
		{
			velocitym = 1500
			return velocitym
		}
		else
		{
			velocitym = 1200
			return velocitym
		}
	}


}