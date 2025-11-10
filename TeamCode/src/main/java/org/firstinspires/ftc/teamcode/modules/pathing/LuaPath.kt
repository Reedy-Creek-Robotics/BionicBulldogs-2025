package org.firstinspires.ftc.teamcode.modules.pathing;

import com.minerkid08.dynamicopmodeloader.FunctionBuilder
import com.minerkid08.dynamicopmodeloader.LuaCallback
import com.minerkid08.dynamicopmodeloader.OpmodeLoaderBuilderFunction
import com.minerkid08.dynamicopmodeloader.OpmodeLoaderFunction
import com.pedropathing.follower.Follower
import com.pedropathing.geometry.BezierCurve
import com.pedropathing.geometry.BezierLine
import com.pedropathing.geometry.BezierPoint
import com.pedropathing.geometry.Pose
import com.pedropathing.paths.Path
import com.pedropathing.paths.PathBuilder
import com.pedropathing.paths.PathChain

class LuaPath(private val follower: Follower)
{
	companion object
	{
		fun init(builder: FunctionBuilder, follower: Follower)
		{
			builder.pushTable("path");
			builder.addObjectAsGlobal(LuaPath(follower));
			builder.popTable();

			builder.addClassAsClass(LuaPathBuilder::class.java);

			builder.createClass(BezierCurve::class.simpleName!!);
			builder.createClass(BezierLine::class.simpleName!!);
			builder.createClass(BezierPoint::class.simpleName!!);
			builder.createClass(PathChain::class.simpleName!!);
		}
	}

	@OpmodeLoaderFunction
	fun line(x1: Double, y1: Double, x2: Double, y2: Double): BezierLine
	{
		return BezierLine(Pose(x1, y1), Pose(x2, y2));
	}

	@OpmodeLoaderFunction
	fun curve3(x1: Double, y1: Double, x2: Double, y2: Double, x3: Double, y3: Double): BezierCurve
	{
		return BezierCurve(Pose(x1, y1), Pose(x2, y2), Pose(x3, y3));
	}

	@OpmodeLoaderFunction
	fun curve4(
		x1: Double,
		y1: Double,
		x2: Double,
		y2: Double,
		x3: Double,
		y3: Double,
		x4: Double,
		y4: Double
	): BezierCurve
	{
		return BezierCurve(Pose(x1, y1), Pose(x2, y2), Pose(x3, y3), Pose(x4, y4));
	}

	@OpmodeLoaderFunction
	fun chain() = LuaPathBuilder(follower.pathBuilder());
}

class LuaPathBuilder(private val builder: PathBuilder)
{
	@OpmodeLoaderBuilderFunction
	fun add(curve: BezierCurve)
	{
		builder.addPath(curve);
	}

	@OpmodeLoaderBuilderFunction
	fun constantHeading(h: Double)
	{
		builder.setConstantHeadingInterpolation(Math.toRadians(h));
	}

	@OpmodeLoaderBuilderFunction
	fun linearHeading(h1: Double, h2: Double)
	{
		builder.setLinearHeadingInterpolation(Math.toRadians(h1), Math.toRadians(h2));
	}

	@OpmodeLoaderBuilderFunction
	fun distanceCallback(t: Double, callback: LuaCallback)
	{
		builder.addParametricCallback(t, { callback.call() });
	}

	@OpmodeLoaderBuilderFunction
	fun timeCallback(t: Double, callback: LuaCallback)
	{
		builder.addTemporalCallback(t, { callback.call() });
	}

	@OpmodeLoaderFunction
	fun build(): PathChain = builder.build();
}