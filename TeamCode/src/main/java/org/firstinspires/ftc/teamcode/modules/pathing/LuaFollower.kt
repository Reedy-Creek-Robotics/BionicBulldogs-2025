package org.firstinspires.ftc.teamcode.modules.pathing

import com.minerkid08.dynamicopmodeloader.FunctionBuilder
import com.minerkid08.dynamicopmodeloader.OpmodeLoaderFunction
import com.pedropathing.follower.Follower
import com.pedropathing.localization.Pose
import com.pedropathing.pathgen.Path
import com.pedropathing.pathgen.PathChain

class LuaFollower(private val follower: Follower)
{
	companion object
	{
		fun init(builder: FunctionBuilder, follower: Follower)
		{
			builder.pushTable("follower");
			builder.addObjectAsGlobal(LuaFollower(follower));
			builder.popTable();
		}
	}

	@OpmodeLoaderFunction
	fun getPositionX() = follower.pose.x;
	@OpmodeLoaderFunction
	fun getPositionY() = follower.pose.y;
	@OpmodeLoaderFunction
	fun getPositionH() = follower.pose.heading;

	@OpmodeLoaderFunction
	fun setPosition(x: Double, y: Double, h: Double)
	{
		follower.pose = Pose(x, y, Math.toRadians(h));
	}

	@OpmodeLoaderFunction
	fun followPathc(path: PathChain)
	{
		follower.followPath(path);
	}

	@OpmodeLoaderFunction
	fun followPath(path: Path)
	{
		follower.followPath(path);
	}

	@OpmodeLoaderFunction
	fun isBusy() = follower.isBusy;

	@OpmodeLoaderFunction
	fun update()
	{
		follower.update();
	}
}