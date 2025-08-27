package org.firstinspires.ftc.teamcode.modules.pathing;

import com.minerkid08.dynamicopmodeloader.FunctionBuilder
import com.minerkid08.dynamicopmodeloader.LuaCallback
import com.minerkid08.dynamicopmodeloader.OpmodeLoaderBuilderFunction
import com.minerkid08.dynamicopmodeloader.OpmodeLoaderFunction
import com.pedropathing.follower.Follower
import com.pedropathing.pathgen.BezierCurve
import com.pedropathing.pathgen.BezierLine
import com.pedropathing.pathgen.PathBuilder
import com.pedropathing.pathgen.PathChain
import com.pedropathing.pathgen.Point

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
			builder.createClass(PathChain::class.simpleName!!);
		}
	}

	@OpmodeLoaderFunction
	fun line(x1: Double, y1: Double, x2: Double, y2: Double): BezierLine
	{
		return BezierLine(Point(x1, y1), Point(x2, y2));
	}

	@OpmodeLoaderFunction
	fun curve3(x1: Double, y1: Double, x2: Double, y2: Double, x3: Double, y3: Double): BezierCurve
	{
		return BezierCurve(Point(x1, y1), Point(x2, y2), Point(x3, y3));
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
	fun addLine(x1: Double, y1: Double, x2: Double, y2: Double)
	{
		builder.addBezierLine(Point(x1, y1), Point(x2, y2))
	}

	@OpmodeLoaderBuilderFunction
	fun addCurve3(x1: Double, y1: Double, x2: Double, y2: Double, x3: Double, y3: Double)
	{
		builder.addBezierCurve(Point(x1, y1), Point(x2, y2), Point(x3, y3));
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