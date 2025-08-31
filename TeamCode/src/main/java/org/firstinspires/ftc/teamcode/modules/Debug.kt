package org.firstinspires.ftc.teamcode.modules

import com.bylazar.field.PanelsField
import com.bylazar.field.Style
import com.pedropathing.follower.Follower
import com.pedropathing.geometry.Pose
import com.pedropathing.paths.Path
import com.pedropathing.util.PoseHistory

object Debug
{
	private const val ROBOT_RADIUS = 9.0;
	private val field = PanelsField.field;
	private val robotLook = Style("", "#3F51B5", 0.0);
	private val historyLook = Style("", "#4CAF50", 0.0);

	init
	{
		field.setOffsets(PanelsField.presets.PEDRO_PATHING);
	}

	fun drawDebug(follower: Follower)
	{
		if(follower.currentPath != null)
		{
			drawPath(follower.currentPath, robotLook);
			val closestPoint = follower.getPointFromPath(
				follower.currentPath.closestPointTValue
			);
			drawRobot(
				Pose(
					closestPoint.x, closestPoint.y,
					follower.currentPath.getHeadingGoal(follower.currentPath.closestPointTValue)
				), robotLook
			);
		}
		drawPoseHistory(follower.poseHistory, historyLook);
		drawRobot(follower.pose, historyLook);

		field.update();
	}

	fun drawRobot(pose: Pose, style: Style)
	{
		if(pose.x.isNaN() || pose.y.isNaN() || pose.heading.isNaN())
			return;

		field.setStyle(style);
		field.moveCursor(pose.x, pose.y);
		field.circle(ROBOT_RADIUS);

		val v = pose.headingAsUnitVector;
		v.magnitude *= ROBOT_RADIUS;
		val x1 = pose.x + v.xComponent / 2;
		val y1 = pose.y + v.yComponent / 2;
		val x2 = pose.x + v.xComponent
		val y2 = pose.y + v.yComponent;

		field.setStyle(style);
		field.moveCursor(x1, y1);
		field.line(x2, y2);
	}

	fun drawPath(path: Path, style: Style)
	{
		val points = path.panelsDrawingPoints;

		for(i in 0 until points[0].size)
		{
			for(j in points.indices)
			{
				if(points[j][i].isNaN())
					points[j][i] = 0.0;
			}
		}

		field.setStyle(style);
		field.moveCursor(points[0][0], points[0][1]);
		field.line(points[1][0], points[1][1]);
	}

	fun drawPoseHistory(poseTracker: PoseHistory, style: Style)
	{
		field.setStyle(style);

		val size = poseTracker.xPositionsArray.size;
		for(i in 0 until size - 1)
		{
			field.moveCursor(poseTracker.xPositionsArray[i], poseTracker.yPositionsArray[i]);
			field.line(poseTracker.xPositionsArray[i + 1], poseTracker.yPositionsArray[i + 1]);
		}
	}
}