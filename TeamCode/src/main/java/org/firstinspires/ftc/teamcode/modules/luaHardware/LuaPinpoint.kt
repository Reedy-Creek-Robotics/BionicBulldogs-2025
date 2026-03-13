package org.firstinspires.ftc.teamcode.modules.luaHardware

import com.minerkid08.dynamicopmodeloader.LuaError
import com.minerkid08.dynamicopmodeloader.OpmodeLoaderFunction
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D
import org.firstinspires.ftc.robotcore.external.navigation.UnnormalizedAngleUnit

class LuaPinpoint(val pinpoint: GoBildaPinpointDriver)
{
	init
	{
		pinpoint.setOffsets(-6.5, 0.0, DistanceUnit.INCH);
		pinpoint.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
		pinpoint.setEncoderDirections(
			GoBildaPinpointDriver.EncoderDirection.REVERSED,
			GoBildaPinpointDriver.EncoderDirection.FORWARD
		);
		pinpoint.resetPosAndIMU();
	}

	private var pos: Pose2D? = null;

	@OpmodeLoaderFunction
	fun update()
	{
		pinpoint.update();
		pos = pinpoint.position;
	}

	@OpmodeLoaderFunction
	fun getHeading(): Double
	{
		if(pos == null)
			throw LuaError("position is null");
		return pos!!.getHeading(AngleUnit.RADIANS);
	}

	@OpmodeLoaderFunction
	fun getX(): Double
	{
		if(pos == null)
			throw LuaError("position is null");
		return pos!!.getX(DistanceUnit.INCH);
	}

	@OpmodeLoaderFunction
	fun getY(): Double
	{
		if(pos == null)
			throw LuaError("position is null");
		return pos!!.getY(DistanceUnit.INCH);
	}

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

	@OpmodeLoaderFunction
	fun getVelX() = pinpoint.getVelX(DistanceUnit.INCH);

	@OpmodeLoaderFunction
	fun getVelY() = pinpoint.getVelY(DistanceUnit.INCH);

	@OpmodeLoaderFunction
	fun getVelH() = pinpoint.getHeadingVelocity(UnnormalizedAngleUnit.RADIANS);
}
