package org.firstinspires.ftc.teamcode.modules.luaHardware

import com.minerkid08.dynamicopmodeloader.OpmodeLoaderFunction
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D

class LuaPinpoint(val pinpoint: GoBildaPinpointDriver)
{
	init
	{
		pinpoint.setOffsets(-6.7, 0.0, DistanceUnit.INCH);
		pinpoint.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
		pinpoint.setEncoderDirections(
			GoBildaPinpointDriver.EncoderDirection.REVERSED,
			GoBildaPinpointDriver.EncoderDirection.FORWARD
		);
		pinpoint.resetPosAndIMU();
	}

	private lateinit var pos: Pose2D;

	@OpmodeLoaderFunction
	fun update()
	{
		pinpoint.update();
		pos = pinpoint.position;
	}

	@OpmodeLoaderFunction
	fun getHeading() = pos.getHeading(AngleUnit.RADIANS);
	@OpmodeLoaderFunction
	fun getX() = pos.getX(DistanceUnit.INCH);
	@OpmodeLoaderFunction
	fun getY() = pos.getY(DistanceUnit.INCH);

	@OpmodeLoaderFunction
	fun setHeading(heading: Double)
	{
		pinpoint.setHeading(heading, AngleUnit.RADIANS)
	}

	@OpmodeLoaderFunction
	fun setPosX(x: Double)
	{
		pinpoint.setPosX(x, DistanceUnit.INCH);
	}

	@OpmodeLoaderFunction
	fun setPosY(y: Double)
	{
		pinpoint.setPosY(y, DistanceUnit.INCH);
	}
}
