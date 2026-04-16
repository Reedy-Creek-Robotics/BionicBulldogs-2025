package org.firstinspires.ftc.teamcode.pedroPathing

import android.annotation.SuppressLint
import com.bylazar.configurables.PanelsConfigurables.refreshClass
import com.bylazar.configurables.annotations.Configurable
import com.bylazar.configurables.annotations.IgnoreConfigurable
import com.bylazar.field.FieldManager.update
import com.bylazar.field.PanelsField.field
import com.bylazar.field.PanelsField.presets
import com.bylazar.field.Style
import com.bylazar.telemetry.PanelsTelemetry
import com.bylazar.telemetry.TelemetryManager
import com.pedropathing.follower.Follower
import com.pedropathing.geometry.BezierCurve
import com.pedropathing.geometry.BezierLine
import com.pedropathing.geometry.Pose
import com.pedropathing.math.Vector
import com.pedropathing.paths.HeadingInterpolator
import com.pedropathing.paths.Path
import com.pedropathing.paths.PathChain
import com.pedropathing.telemetry.SelectScope
import com.pedropathing.telemetry.SelectableOpMode
import com.pedropathing.util.PoseHistory
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.pedroPathing.Constants.createFollower
import java.lang.Double
import java.util.function.Consumer
import java.util.function.Supplier
import kotlin.DoubleArray
import kotlin.Exception
import kotlin.Long
import kotlin.RuntimeException
import kotlin.String
import kotlin.collections.ArrayList
import kotlin.collections.MutableList
import kotlin.collections.get
import kotlin.collections.indices
import kotlin.collections.minus
import kotlin.collections.plus
import kotlin.compareTo
import kotlin.div
import kotlin.doubleArrayOf
import kotlin.math.abs
import kotlin.math.pow
import kotlin.plus
import kotlin.sequences.minus
import kotlin.sequences.plus
import kotlin.text.compareTo
import kotlin.text.format

/**
 * This is the Tuning class. It contains a selection menu for various tuning OpModes.
 *
 * @author Baron Henderson - 20077 The Indubitables
 * @version 1.0, 6/26/2025
 */
@Configurable
@TeleOp(name = "Tuning", group = "Pedro Pathing")
class Tuning :
	SelectableOpMode("Select a Tuning OpMode", Consumer { s: SelectScope<Supplier<OpMode?>?>? ->
		s!!.folder("Localization", Consumer { l: SelectScope<Supplier<OpMode?>?>? ->
			l!!.add("Localization Test", Supplier { LocalizationTest() })
			l.add("Offsets Tuner", Supplier { OffsetsTuner() })
			l.add("Forward Tuner", Supplier { ForwardTuner() })
			l.add("Lateral Tuner", Supplier { LateralTuner() })
			l.add("Turn Tuner", Supplier { TurnTuner() })
		})
		s.folder("Automatic", Consumer { a: SelectScope<Supplier<OpMode?>?>? ->
			a!!.add("Forward Velocity Tuner", Supplier { ForwardVelocityTuner() })
			a.add("Lateral Velocity Tuner", Supplier { LateralVelocityTuner() })
			a.add(
				"Forward Zero Power Acceleration Tuner",
				Supplier { ForwardZeroPowerAccelerationTuner() })
			a.add(
				"Lateral Zero Power Acceleration Tuner",
				Supplier { LateralZeroPowerAccelerationTuner() })
			a.add("Predictive Braking", Supplier { PredictiveBrakingTuner() })
		})
		s.folder("Manual", Consumer { p: SelectScope<Supplier<OpMode?>?>? ->
			p!!.add("Translational Tuner", Supplier { TranslationalTuner() })
			p.add("Heading Tuner", Supplier { HeadingTuner() })
			p.add("Drive Tuner", Supplier { DriveTuner() })
			p.add("Line Tuner", Supplier { Line() })
			p.add("Centripetal Tuner", Supplier { CentripetalTuner() })
		})
		s.folder("Tests", Consumer { p: SelectScope<Supplier<OpMode?>?>? ->
			p!!.add("Line", Supplier { Line() })
			p.add("Triangle", Supplier { Triangle() })
			p.add("Circle", Supplier { Circle() })
		})
	})
{
	public override fun onSelect()
	{
		if (follower == null)
		{
			follower = createFollower(hardwareMap)
			refreshClass(this)
		}
		else
		{
			follower = createFollower(hardwareMap)
		}

		follower!!.setStartingPose(Pose())

		poseHistory = follower!!.getPoseHistory()

		telemetryM = PanelsTelemetry.telemetry

		Drawing.init()
	}

	public override fun onLog(lines: MutableList<String?>?)
	{
	}

	companion object
	{
		var follower: Follower? = null

		@IgnoreConfigurable
		var poseHistory: PoseHistory? = null

		@IgnoreConfigurable
		var telemetryM: TelemetryManager? = null

		@IgnoreConfigurable
		var changes: ArrayList<String?> = ArrayList<String?>()

		fun drawOnlyCurrent()
		{
			try
			{
				drawRobot(follower!!.getPose())
				Drawing.sendPacket()
			}
			catch (e: Exception)
			{
				throw RuntimeException("Drawing failed " + e)
			}
		}

		fun draw()
		{
			Drawing.drawDebug(follower!!)
		}

		/** This creates a full stop of the robot by setting the drive motors to run at 0 power.  */
		fun stopRobot()
		{
			follower!!.startTeleopDrive(true)
			follower!!.setTeleOpDrive(0.0, 0.0, 0.0, true)
		}
	}
}

/**
 * This is the LocalizationTest OpMode. This is basically just a simple mecanum drive attached to a
 * PoseUpdater. The OpMode will print out the robot's pose to telemetry as well as draw the robot.
 * You should use this to check the robot's localization.
 *
 * @author Anyi Lin - 10158 Scott's Bots
 * @author Baron Henderson - 20077 The Indubitables
 * @version 1.0, 5/6/2024
 */
internal class LocalizationTest : OpMode()
{
	override fun init()
	{
		follower.setStartingPose(Pose(72.0, 72.0))
	}

	/** This initializes the PoseUpdater, the mecanum drive motors, and the Panels telemetry.  */
	override fun init_loop()
	{
		telemetryM.debug(
			"This will print your robot's position to telemetry while "
							+ "allowing robot control through a basic mecanum drive on gamepad 1."
		)
		telemetryM.update(telemetry)
		follower.update()
		drawOnlyCurrent()
	}

	override fun start()
	{
		follower.startTeleopDrive()
		follower.update()
	}

	/**
	 * This updates the robot's pose estimate, the simple mecanum drive, and updates the
	 * Panels telemetry with the robot's position as well as draws the robot's position.
	 */
	override fun loop()
	{
		follower.setTeleOpDrive(
			-gamepad1.left_stick_y,
			-gamepad1.left_stick_x,
			-gamepad1.right_stick_x,
			true
		)
		follower.update()

		telemetryM.debug("x:" + follower.getPose().getX())
		telemetryM.debug("y:" + follower.getPose().getY())
		telemetryM.debug("heading:" + follower.getPose().getHeading())
		telemetryM.debug("total heading:" + follower.getTotalHeading())
		telemetryM.update(telemetry)

		draw()
	}
}

/**
 * This is the ForwardTuner OpMode. This tracks the forward movement of the robot and displays the
 * necessary ticks to inches multiplier. This displayed multiplier is what's necessary to scale the
 * robot's current distance in ticks to the specified distance in inches. So, to use this, run the
 * tuner, then pull/push the robot to the specified distance using a ruler on the ground. When you're
 * at the end of the distance, record the ticks to inches multiplier. Feel free to run multiple trials
 * and average the results. Then, input the multiplier into the forward ticks to inches in your
 * localizer of choice.
 *
 * @author Anyi Lin - 10158 Scott's Bots
 * @author Baron Henderson - 20077 The Indubitables
 * @version 1.0, 5/6/2024
 */
internal class ForwardTuner : OpMode()
{
	override fun init()
	{
		follower.setStartingPose(Pose(72.0, 72.0))
		follower.update()
		drawOnlyCurrent()
	}

	/** This initializes the PoseUpdater as well as the Panels telemetry.  */
	override fun init_loop()
	{
		telemetryM.debug("Pull your robot forward " + DISTANCE + " inches. Your forward ticks to inches will be shown on the telemetry.")
		telemetryM.update(telemetry)
		drawOnlyCurrent()
	}

	/**
	 * This updates the robot's pose estimate, and updates the Panels telemetry with the
	 * calculated multiplier and draws the robot.
	 */
	override fun loop()
	{
		follower.update()

		telemetryM.debug("Distance Moved: " + (follower.getPose().getX() - 72))
		telemetryM.debug("The multiplier will display what your forward ticks to inches should be to scale your current distance to " + DISTANCE + " inches.")
		telemetryM.debug(
			"Multiplier: " + (DISTANCE / ((follower.getPose().getX() - 72) / follower.getPoseTracker()
				.getLocalizer().getForwardMultiplier()))
		)
		telemetryM.update(telemetry)

		draw()
	}

	companion object
	{
		var DISTANCE: Double = 48.0
	}
}

/**
 * This is the LateralTuner OpMode. This tracks the strafe movement of the robot and displays the
 * necessary ticks to inches multiplier. This displayed multiplier is what's necessary to scale the
 * robot's current distance in ticks to the specified distance in inches. So, to use this, run the
 * tuner, then pull/push the robot to the specified distance using a ruler on the ground. When you're
 * at the end of the distance, record the ticks to inches multiplier. Feel free to run multiple trials
 * and average the results. Then, input the multiplier into the strafe ticks to inches in your
 * localizer of choice.
 *
 * @author Anyi Lin - 10158 Scott's Bots
 * @author Baron Henderson - 20077 The Indubitables
 * @version 2.0, 6/26/2025
 */
internal class LateralTuner : OpMode()
{
	override fun init()
	{
		follower.setStartingPose(Pose(72.0, 72.0))
		follower.update()
		drawOnlyCurrent()
	}

	/** This initializes the PoseUpdater as well as the Panels telemetry.  */
	override fun init_loop()
	{
		telemetryM.debug("Pull your robot to the right " + DISTANCE + " inches. Your strafe ticks to inches will be shown on the telemetry.")
		telemetryM.update(telemetry)
		drawOnlyCurrent()
	}

	/**
	 * This updates the robot's pose estimate, and updates the Panels telemetry with the
	 * calculated multiplier and draws the robot.
	 */
	override fun loop()
	{
		follower.update()

		telemetryM.debug("Distance Moved: " + (follower.getPose().getY() - 72))
		telemetryM.debug("The multiplier will display what your strafe ticks to inches should be to scale your current distance to " + DISTANCE + " inches.")
		telemetryM.debug(
			"Multiplier: " + (DISTANCE / ((follower.getPose().getY() - 72) / follower.getPoseTracker()
				.getLocalizer().getLateralMultiplier()))
		)
		telemetryM.update(telemetry)

		draw()
	}

	companion object
	{
		var DISTANCE: Double = 48.0
	}
}

/**
 * This is the TurnTuner OpMode. This tracks the turning movement of the robot and displays the
 * necessary ticks to inches multiplier. This displayed multiplier is what's necessary to scale the
 * robot's current angle in ticks to the specified angle in radians. So, to use this, run the
 * tuner, then pull/push the robot to the specified angle using a protractor or lines on the ground.
 * When you're at the end of the angle, record the ticks to inches multiplier. Feel free to run
 * multiple trials and average the results. Then, input the multiplier into the turning ticks to
 * radians in your localizer of choice.
 *
 * @author Anyi Lin - 10158 Scott's Bots
 * @author Baron Henderson - 20077 The Indubitables
 * @version 1.0, 5/6/2024
 */
internal class TurnTuner : OpMode()
{
	override fun init()
	{
		follower.setStartingPose(Pose(72.0, 72.0))
		follower.update()
		drawOnlyCurrent()
	}

	/** This initializes the PoseUpdater as well as the Panels telemetry.  */
	override fun init_loop()
	{
		telemetryM.debug("Turn your robot " + ANGLE + " radians. Your turn ticks to inches will be shown on the telemetry.")
		telemetryM.update(telemetry)

		drawOnlyCurrent()
	}

	/**
	 * This updates the robot's pose estimate, and updates the Panels telemetry with the
	 * calculated multiplier and draws the robot.
	 */
	override fun loop()
	{
		follower.update()

		telemetryM.debug("Total Angle: " + follower.getTotalHeading())
		telemetryM.debug("The multiplier will display what your turn ticks to inches should be to scale your current angle to " + ANGLE + " radians.")
		telemetryM.debug(
			"Multiplier: " + (ANGLE / (follower.getTotalHeading() / follower.getPoseTracker()
				.getLocalizer().getTurningMultiplier()))
		)
		telemetryM.update(telemetry)

		draw()
	}

	companion object
	{
		var ANGLE: Double = 2 * Math.PI
	}
}

/**
 * This is the ForwardVelocityTuner autonomous follower OpMode. This runs the robot forwards at max
 * power until it reaches some specified distance. It records the most recent velocities, and on
 * reaching the end of the distance, it averages them and prints out the velocity obtained. It is
 * recommended to run this multiple times on a full battery to get the best results. What this does
 * is, when paired with StrafeVelocityTuner, allows FollowerConstants to create a Vector that
 * empirically represents the direction your mecanum wheels actually prefer to go in, allowing for
 * more accurate following.
 *
 * @author Anyi Lin - 10158 Scott's Bots
 * @author Aaron Yang - 10158 Scott's Bots
 * @author Harrison Womack - 10158 Scott's Bots
 * @author Baron Henderson - 20077 The Indubitables
 * @version 1.0, 3/13/2024
 */
internal class ForwardVelocityTuner : OpMode()
{
	private val velocities = ArrayList<Double?>()
	private var end = false

	override fun init()
	{
		follower.setStartingPose(Pose(72.0, 72.0))
	}

	/** This initializes the drive motors as well as the cache of velocities and the Panels telemetry.  */
	override fun init_loop()
	{
		telemetryM.debug("The robot will run at 1 power until it reaches " + DISTANCE + " inches forward.")
		telemetryM.debug("Make sure you have enough room, since the robot has inertia after cutting power.")
		telemetryM.debug("After running the distance, the robot will cut power from the drivetrain and display the forward velocity.")
		telemetryM.debug("Press B on game pad 1 to stop.")
		telemetryM.debug("pose", follower.getPose())
		telemetryM.update(telemetry)
		follower.update()
		drawOnlyCurrent()
	}

	/** This starts the OpMode by setting the drive motors to run forward at full power.  */
	override fun start()
	{
		var i = 0
		while (i < RECORD_NUMBER)
		{
			velocities.add(0.0)
			i++
		}
		follower.startTeleopDrive(true)
		follower.update()
		end = false
	}

	/**
	 * This runs the OpMode. At any point during the running of the OpMode, pressing B on
	 * game pad 1 will stop the OpMode. This continuously records the RECORD_NUMBER most recent
	 * velocities, and when the robot has run forward enough, these last velocities recorded are
	 * averaged and printed.
	 */
	override fun loop()
	{
		if (gamepad1.bWasPressed())
		{
			stopRobot()
			requestOpModeStop()
		}

		follower.update()
		draw()


		if (!end)
		{
			if (Math.abs(follower.getPose().getX()) > (DISTANCE + 72))
			{
				end = true
				stopRobot()
			}
			else
			{
				follower.setTeleOpDrive(1, 0, 0, true)
				//double currentVelocity = Math.abs(follower.getVelocity().getXComponent());
				val currentVelocity: Double =
					Math.abs(follower.poseTracker.getLocalizer().getVelocity().getX())
				velocities.add(currentVelocity)
				velocities.removeAt(0)
			}
		}
		else
		{
			stopRobot()
			var average = 0.0
			for (velocity in velocities)
			{
				average += velocity!!
			}
			average /= velocities.size.toDouble()
			telemetryM.debug("Forward Velocity: " + average)
			telemetryM.debug("\n")
			telemetryM.debug("Press A to set the Forward Velocity temporarily (while robot remains on).")

			for (i in velocities.indices)
			{
				telemetry.addData(i.toString(), velocities.get(i))
			}

			telemetryM.update(telemetry)
			telemetry.update()

			if (gamepad1.aWasPressed())
			{
				follower.setXVelocity(average)
				val message = "XMovement: " + average
				changes.add(message)
			}
		}
	}

	companion object
	{
		var DISTANCE: Double = 48.0
		var RECORD_NUMBER: Double = 10.0
	}
}

/**
 * This is the StrafeVelocityTuner autonomous follower OpMode. This runs the robot left at max
 * power until it reaches some specified distance. It records the most recent velocities, and on
 * reaching the end of the distance, it averages them and prints out the velocity obtained. It is
 * recommended to run this multiple times on a full battery to get the best results. What this does
 * is, when paired with ForwardVelocityTuner, allows FollowerConstants to create a Vector that
 * empirically represents the direction your mecanum wheels actually prefer to go in, allowing for
 * more accurate following.
 *
 * @author Anyi Lin - 10158 Scott's Bots
 * @author Aaron Yang - 10158 Scott's Bots
 * @author Harrison Womack - 10158 Scott's Bots
 * @author Baron Henderson - 20077 The Indubitables
 * @version 1.0, 3/13/2024
 */
internal class LateralVelocityTuner : OpMode()
{
	private val velocities = ArrayList<Double?>()

	private var end = false

	override fun init()
	{
		follower.setStartingPose(Pose(72.0, 72.0))
	}

	/**
	 * This initializes the drive motors as well as the cache of velocities and the Panels
	 * telemetryM.
	 */
	override fun init_loop()
	{
		telemetryM.debug("The robot will run at 1 power until it reaches " + DISTANCE + " inches to the left.")
		telemetryM.debug("Make sure you have enough room, since the robot has inertia after cutting power.")
		telemetryM.debug("After running the distance, the robot will cut power from the drivetrain and display the strafe velocity.")
		telemetryM.debug("Press B on Gamepad 1 to stop.")
		telemetryM.update(telemetry)
		follower.update()
		drawOnlyCurrent()
	}

	/** This starts the OpMode by setting the drive motors to run left at full power.  */
	override fun start()
	{
		var i = 0
		while (i < RECORD_NUMBER)
		{
			velocities.add(0.0)
			i++
		}
		follower.startTeleopDrive(true)
		follower.update()
	}

	/**
	 * This runs the OpMode. At any point during the running of the OpMode, pressing B on
	 * game pad1 will stop the OpMode. This continuously records the RECORD_NUMBER most recent
	 * velocities, and when the robot has run sideways enough, these last velocities recorded are
	 * averaged and printed.
	 */
	override fun loop()
	{
		if (gamepad1.bWasPressed())
		{
			stopRobot()
			requestOpModeStop()
		}

		follower.update()
		draw()

		if (!end)
		{
			if (Math.abs(follower.getPose().getY()) > (DISTANCE + 72))
			{
				end = true
				stopRobot()
			}
			else
			{
				follower.setTeleOpDrive(0, 1, 0, true)
				val currentVelocity: Double = Math.abs(follower.getVelocity().dot(Vector(1.0, Math.PI / 2)))
				velocities.add(currentVelocity)
				velocities.removeAt(0)
			}
		}
		else
		{
			stopRobot()
			var average = 0.0
			for (velocity in velocities)
			{
				average += velocity!!
			}
			average /= velocities.size.toDouble()

			telemetryM.debug("Strafe Velocity: " + average)
			telemetryM.debug("\n")
			telemetryM.debug("Press A to set the Lateral Velocity temporarily (while robot remains on).")
			telemetryM.update(telemetry)

			if (gamepad1.aWasPressed())
			{
				follower.setYVelocity(average)
				val message = "YMovement: " + average
				changes.add(message)
			}
		}
	}

	companion object
	{
		var DISTANCE: Double = 48.0
		var RECORD_NUMBER: Double = 10.0
	}
}

/**
 * This is the ForwardZeroPowerAccelerationTuner autonomous follower OpMode. This runs the robot
 * forward until a specified velocity is achieved. Then, the robot cuts power to the motors, setting
 * them to zero power. The deceleration, or negative acceleration, is then measured until the robot
 * stops. The accelerations across the entire time the robot is slowing down is then averaged and
 * that number is then printed. This is used to determine how the robot will decelerate in the
 * forward direction when power is cut, making the estimations used in the calculations for the
 * drive Vector more accurate and giving better braking at the end of Paths.
 *
 * @author Anyi Lin - 10158 Scott's Bots
 * @author Baron Henderson - 20077 The Indubitables
 * @author Aaron Yang - 10158 Scott's Bots
 * @author Harrison Womack - 10158 Scott's Bots
 * @version 1.0, 3/13/2024
 */
internal class ForwardZeroPowerAccelerationTuner : OpMode()
{
	private val accelerations = ArrayList<Double?>()
	private var previousVelocity = 0.0
	private var previousTimeNano: Long = 0

	private var stopping = false
	private var end = false

	override fun init()
	{
		follower.setStartingPose(Pose(72.0, 72.0))
	}

	/** This initializes the drive motors as well as the Panels telemetryM.  */
	override fun init_loop()
	{
		telemetryM.debug("The robot will run forward until it reaches " + VELOCITY + " inches per second.")
		telemetryM.debug("Then, it will cut power from the drivetrain and roll to a stop.")
		telemetryM.debug("Make sure you have enough room.")
		telemetryM.debug("After stopping, the forward zero power acceleration (natural deceleration) will be displayed.")
		telemetryM.debug("Press B on Gamepad 1 to stop.")
		telemetryM.update(telemetry)
		follower.update()
		drawOnlyCurrent()
	}

	/** This starts the OpMode by setting the drive motors to run forward at full power.  */
	override fun start()
	{
		follower.startTeleopDrive(false)
		follower.update()
		follower.setTeleOpDrive(1, 0, 0, true)
	}

	/**
	 * This runs the OpMode. At any point during the running of the OpMode, pressing B on
	 * game pad 1 will stop the OpMode. When the robot hits the specified velocity, the robot will
	 * record its deceleration / negative acceleration until it stops. Then, it will average all the
	 * recorded deceleration / negative acceleration and print that value.
	 */
	override fun loop()
	{
		if (gamepad1.bWasPressed())
		{
			stopRobot()
			requestOpModeStop()
		}

		follower.update()
		draw()

		val heading = Vector(1.0, follower.getPose().getHeading())
		if (!end)
		{
			if (!stopping)
			{
				if (follower.getVelocity().dot(heading) > VELOCITY)
				{
					previousVelocity = follower.getVelocity().dot(heading)
					previousTimeNano = System.nanoTime()
					stopping = true
					follower.setTeleOpDrive(0, 0, 0, true)
				}
			}
			else
			{
				val currentVelocity: Double = follower.getVelocity().dot(heading)
				accelerations.add(
					(currentVelocity - previousVelocity) / ((System.nanoTime() - previousTimeNano) / 10.0.pow(
						9.0
					))
				)
				previousVelocity = currentVelocity
				previousTimeNano = System.nanoTime()
				if (currentVelocity < follower.getConstraints().getVelocityConstraint())
				{
					end = true
				}
			}
		}
		else
		{
			var average = 0.0
			for (acceleration in accelerations)
			{
				average += acceleration!!
			}
			average /= accelerations.size.toDouble()

			telemetryM.debug("Forward Zero Power Acceleration (Deceleration): " + average)
			telemetryM.debug("\n")
			telemetryM.debug("Press A to set the Forward Zero Power Acceleration temporarily (while robot remains on).")
			telemetryM.update(telemetry)

			if (gamepad1.aWasPressed())
			{
				follower.getConstants().setForwardZeroPowerAcceleration(average)
				val message = "Forward Zero Power Acceleration: " + average
				changes.add(message)
			}
		}
	}

	companion object
	{
		var VELOCITY: Double = 30.0
	}
}

/**
 * This is the LateralZeroPowerAccelerationTuner autonomous follower OpMode. This runs the robot
 * to the left until a specified velocity is achieved. Then, the robot cuts power to the motors, setting
 * them to zero power. The deceleration, or negative acceleration, is then measured until the robot
 * stops. The accelerations across the entire time the robot is slowing down is then averaged and
 * that number is then printed. This is used to determine how the robot will decelerate in the
 * forward direction when power is cut, making the estimations used in the calculations for the
 * drive Vector more accurate and giving better braking at the end of Paths.
 *
 * @author Anyi Lin - 10158 Scott's Bots
 * @author Aaron Yang - 10158 Scott's Bots
 * @author Harrison Womack - 10158 Scott's Bots
 * @author Baron Henderson - 20077 The Indubitables
 * @version 1.0, 3/13/2024
 */
internal class LateralZeroPowerAccelerationTuner : OpMode()
{
	private val accelerations = ArrayList<Double?>()
	private var previousVelocity = 0.0
	private var previousTimeNano: Long = 0
	private var stopping = false
	private var end = false

	override fun init()
	{
		follower.setStartingPose(Pose(72.0, 72.0))
	}

	/** This initializes the drive motors as well as the Panels telemetry.  */
	override fun init_loop()
	{
		telemetryM.debug("The robot will run to the left until it reaches " + VELOCITY + " inches per second.")
		telemetryM.debug("Then, it will cut power from the drivetrain and roll to a stop.")
		telemetryM.debug("Make sure you have enough room.")
		telemetryM.debug("After stopping, the lateral zero power acceleration (natural deceleration) will be displayed.")
		telemetryM.debug("Press B on game pad 1 to stop.")
		telemetryM.update(telemetry)
		follower.update()
		drawOnlyCurrent()
	}

	/** This starts the OpMode by setting the drive motors to run forward at full power.  */
	override fun start()
	{
		follower.startTeleopDrive(false)
		follower.update()
		follower.setTeleOpDrive(0, 1, 0, true)
	}

	/**
	 * This runs the OpMode. At any point during the running of the OpMode, pressing B on
	 * game pad 1 will stop the OpMode. When the robot hits the specified velocity, the robot will
	 * record its deceleration / negative acceleration until it stops. Then, it will average all the
	 * recorded deceleration / negative acceleration and print that value.
	 */
	override fun loop()
	{
		if (gamepad1.bWasPressed())
		{
			stopRobot()
			requestOpModeStop()
		}

		follower.update()
		draw()

		val heading = Vector(1.0, follower.getPose().getHeading() - Math.PI / 2)
		if (!end)
		{
			if (!stopping)
			{
				if (Math.abs(follower.getVelocity().dot(heading)) > VELOCITY)
				{
					previousVelocity = Math.abs(follower.getVelocity().dot(heading))
					previousTimeNano = System.nanoTime()
					stopping = true
					follower.setTeleOpDrive(0, 0, 0, true)
				}
			}
			else
			{
				val currentVelocity: Double = Math.abs(follower.getVelocity().dot(heading))
				accelerations.add(
					(currentVelocity - previousVelocity) / ((System.nanoTime() - previousTimeNano) / 10.0.pow(
						9.0
					))
				)
				previousVelocity = currentVelocity
				previousTimeNano = System.nanoTime()
				if (currentVelocity < follower.getConstraints().getVelocityConstraint())
				{
					end = true
				}
			}
		}
		else
		{
			var average = 0.0
			for (acceleration in accelerations)
			{
				average += acceleration!!
			}
			average /= accelerations.size.toDouble()

			telemetryM.debug("Lateral Zero Power Acceleration (Deceleration): " + average)
			telemetryM.debug("\n")
			telemetryM.debug("Press A to set the Lateral Zero Power Acceleration temporarily (while robot remains on).")
			telemetryM.update(telemetry)

			if (gamepad1.aWasPressed())
			{
				follower.getConstants().setLateralZeroPowerAcceleration(average)
				val message = "Lateral Zero Power Acceleration: " + average
				changes.add(message)
			}
		}
	}

	companion object
	{
		var VELOCITY: Double = 30.0
	}
}

/**
 * This is the Translational PIDF Tuner OpMode. It will keep the robot in place.
 * The user should push the robot laterally to test the PIDF and adjust the PIDF values accordingly.
 *
 * @author Baron Henderson - 20077 The Indubitables
 * @author Anyi Lin - 10158 Scott's Bots
 * @author Aaron Yang - 10158 Scott's Bots
 * @author Harrison Womack - 10158 Scott's Bots
 * @version 1.0, 3/12/2024
 */
internal class TranslationalTuner : OpMode()
{
	private var forward = true

	private var forwards: Path? = null
	private var backwards: Path? = null

	override fun init()
	{
		follower.setStartingPose(Pose(72.0, 72.0))
	}

	/** This initializes the Follower and creates the forward and backward Paths.  */
	override fun init_loop()
	{
		telemetryM.debug("This will activate the translational PIDF(s)")
		telemetryM.debug("The robot will try to stay in place while you push it laterally.")
		telemetryM.debug("You can adjust the PIDF values to tune the robot's translational PIDF(s).")
		telemetryM.update(telemetry)
		follower.update()
		drawOnlyCurrent()
	}

	override fun start()
	{
		follower.deactivateAllPIDFs()
		follower.activateTranslational()
		forwards = Path(BezierLine(Pose(72.0, 72.0), Pose(DISTANCE + 72, 72.0)))
		forwards!!.setConstantHeadingInterpolation(0.0)
		backwards = Path(BezierLine(Pose(DISTANCE + 72, 72.0), Pose(72.0, 72.0)))
		backwards!!.setConstantHeadingInterpolation(0.0)
		follower.followPath(forwards)
	}

	/** This runs the OpMode, updating the Follower as well as printing out the debug statements to the Telemetry  */
	override fun loop()
	{
		follower.update()
		draw()

		if (!follower.isBusy())
		{
			if (forward)
			{
				forward = false
				follower.followPath(backwards)
			}
			else
			{
				forward = true
				follower.followPath(forwards)
			}
		}

		telemetryM.debug("Push the robot laterally to test the Translational PIDF(s).")
		telemetryM.addData("Zero Line", 0)
		telemetryM.addData("Error X", follower.errorCalculator.getTranslationalError().getXComponent())
		telemetryM.addData("Error Y", follower.errorCalculator.getTranslationalError().getYComponent())
		telemetryM.update(telemetry)
	}

	companion object
	{
		var DISTANCE: Double = 40.0
	}
}

/**
 * This is the Heading PIDF Tuner OpMode. It will keep the robot in place.
 * The user should try to turn the robot to test the PIDF and adjust the PIDF values accordingly.
 * It will try to keep the robot at a constant heading while the user tries to turn it.
 *
 * @author Baron Henderson - 20077 The Indubitables
 * @author Anyi Lin - 10158 Scott's Bots
 * @author Aaron Yang - 10158 Scott's Bots
 * @author Harrison Womack - 10158 Scott's Bots
 * @version 1.0, 3/12/2024
 */
internal class HeadingTuner : OpMode()
{
	private var forward = true

	private var forwards: Path? = null
	private var backwards: Path? = null

	override fun init()
	{
		follower.setStartingPose(Pose(72.0, 72.0))
	}

	/**
	 * This initializes the Follower and creates the forward and backward Paths. Additionally, this
	 * initializes the Panels telemetry.
	 */
	override fun init_loop()
	{
		telemetryM.debug("This will activate the heading PIDF(s).")
		telemetryM.debug("The robot will try to stay at a constant heading while you try to turn it.")
		telemetryM.debug("You can adjust the PIDF values to tune the robot's heading PIDF(s).")
		telemetryM.update(telemetry)
		follower.update()
		drawOnlyCurrent()
	}

	override fun start()
	{
		follower.deactivateAllPIDFs()
		follower.activateHeading()
		forwards = Path(BezierLine(Pose(72.0, 72.0), Pose(DISTANCE + 72, 72.0)))
		forwards!!.setConstantHeadingInterpolation(0.0)
		backwards = Path(BezierLine(Pose(DISTANCE + 72, 72.0), Pose(72.0, 72.0)))
		backwards!!.setConstantHeadingInterpolation(0.0)
		follower.followPath(forwards)
	}

	/**
	 * This runs the OpMode, updating the Follower as well as printing out the debug statements to
	 * the Telemetry, as well as the Panels.
	 */
	override fun loop()
	{
		follower.update()
		draw()

		if (!follower.isBusy())
		{
			if (forward)
			{
				forward = false
				follower.followPath(backwards)
			}
			else
			{
				forward = true
				follower.followPath(forwards)
			}
		}

		telemetryM.debug("Turn the robot manually to test the Heading PIDF(s).")
		telemetryM.addData("Zero Line", 0)
		telemetryM.addData("Error", follower.errorCalculator.getHeadingError())
		telemetryM.update(telemetry)
	}

	companion object
	{
		var DISTANCE: Double = 40.0
	}
}

/**
 * This is the Drive PIDF Tuner OpMode. It will run the robot in a straight line going forward and back.
 *
 * @author Baron Henderson - 20077 The Indubitables
 * @author Anyi Lin - 10158 Scott's Bots
 * @author Aaron Yang - 10158 Scott's Bots
 * @author Harrison Womack - 10158 Scott's Bots
 * @version 1.0, 3/12/2024
 */
internal class DriveTuner : OpMode()
{
	private var forward = true

	private var forwards: PathChain? = null
	private var backwards: PathChain? = null

	override fun init()
	{
		follower.setStartingPose(Pose(72.0, 72.0))
	}

	/**
	 * This initializes the Follower and creates the forward and backward Paths. Additionally, this
	 * initializes the Panels telemetry.
	 */
	override fun init_loop()
	{
		telemetryM.debug("This will run the robot in a straight line going " + DISTANCE + "inches forward.")
		telemetryM.debug("The robot will go forward and backward continuously along the path.")
		telemetryM.debug("Make sure you have enough room.")
		telemetryM.update(telemetry)
		follower.update()
		drawOnlyCurrent()
	}

	override fun start()
	{
		follower.deactivateAllPIDFs()
		follower.activateDrive()

		forwards = follower.pathBuilder()
			.setGlobalDeceleration()
			.addPath(BezierLine(Pose(72.0, 72.0), Pose(DISTANCE + 72, 72.0)))
			.setConstantHeadingInterpolation(0)
			.build()

		backwards = follower.pathBuilder()
			.setGlobalDeceleration()
			.addPath(BezierLine(Pose(DISTANCE + 72, 72.0), Pose(72.0, 72.0)))
			.setConstantHeadingInterpolation(0)
			.build()

		follower.followPath(forwards)
	}

	/**
	 * This runs the OpMode, updating the Follower as well as printing out the debug statements to
	 * the Telemetry, as well as the Panels.
	 */
	override fun loop()
	{
		follower.update()
		draw()

		if (!follower.isBusy())
		{
			if (forward)
			{
				forward = false
				follower.followPath(backwards)
			}
			else
			{
				forward = true
				follower.followPath(forwards)
			}
		}

		telemetryM.debug("Driving forward?: " + forward)
		telemetryM.addData("Zero Line", 0)
		telemetryM.addData("Error", follower.errorCalculator.getDriveErrors()[1])
		telemetryM.update(telemetry)
	}

	companion object
	{
		var DISTANCE: Double = 40.0
	}
}

/**
 * This is the Line Test Tuner OpMode. It will drive the robot forward and back
 * The user should push the robot laterally and angular to test out the drive, heading, and translational PIDFs.
 *
 * @author Baron Henderson - 20077 The Indubitables
 * @author Anyi Lin - 10158 Scott's Bots
 * @author Aaron Yang - 10158 Scott's Bots
 * @author Harrison Womack - 10158 Scott's Bots
 * @version 1.0, 3/12/2024
 */
internal class Line : OpMode()
{
	private var forward = true

	private var forwards: Path? = null
	private var backwards: Path? = null

	override fun init()
	{
		follower.setStartingPose(Pose(72.0, 72.0))
	}

	/** This initializes the Follower and creates the forward and backward Paths.  */
	override fun init_loop()
	{
		telemetryM.debug("This will activate all the PIDF(s)")
		telemetryM.debug("The robot will go forward and backward continuously along the path while correcting.")
		telemetryM.debug("You can adjust the PIDF values to tune the robot's drive PIDF(s).")
		telemetryM.update(telemetry)
		follower.update()
		drawOnlyCurrent()
	}

	override fun start()
	{
		follower.activateAllPIDFs()
		forwards = Path(BezierLine(Pose(72.0, 72.0), Pose(DISTANCE + 72, 72.0)))
		forwards!!.setConstantHeadingInterpolation(0.0)
		backwards = Path(BezierLine(Pose(DISTANCE + 72, 72.0), Pose(72.0, 72.0)))
		backwards!!.setConstantHeadingInterpolation(0.0)
		follower.followPath(forwards)
	}

	/** This runs the OpMode, updating the Follower as well as printing out the debug statements to the Telemetry  */
	override fun loop()
	{
		follower.update()
		draw()

		if (!follower.isBusy())
		{
			if (forward)
			{
				forward = false
				follower.followPath(backwards)
			}
			else
			{
				forward = true
				follower.followPath(forwards)
			}
		}

		telemetryM.debug("Driving Forward?: " + forward)
		telemetryM.update(telemetry)
	}

	companion object
	{
		var DISTANCE: Double = 40.0
	}
}

/**
 * This is the Centripetal Tuner OpMode. It runs the robot in a specified distance
 * forward and to the left. On reaching the end of the forward Path, the robot runs the backward
 * Path the same distance back to the start. Rinse and repeat! This is good for testing a variety
 * of Vectors, like the drive Vector, the translational Vector, the heading Vector, and the
 * centripetal Vector.
 *
 * @author Baron Henderson - 20077 The Indubitables
 * @author Anyi Lin - 10158 Scott's Bots
 * @author Aaron Yang - 10158 Scott's Bots
 * @author Harrison Womack - 10158 Scott's Bots
 * @version 1.0, 3/13/2024
 */
internal class CentripetalTuner : OpMode()
{
	private var forward = true

	private var forwards: Path? = null
	private var backwards: Path? = null

	override fun init()
	{
		follower.setStartingPose(Pose(72.0, 72.0))
	}

	/**
	 * This initializes the Follower and creates the forward and backward Paths.
	 * Additionally, this initializes the Panels telemetry.
	 */
	override fun init_loop()
	{
		telemetryM.debug("This will run the robot in a curve going " + DISTANCE + " inches to the left and the same number of inches forward.")
		telemetryM.debug("The robot will go continuously along the path.")
		telemetryM.debug("Make sure you have enough room.")
		telemetryM.update(telemetry)
		follower.update()
		drawOnlyCurrent()
	}

	override fun start()
	{
		follower.activateAllPIDFs()
		forwards = Path(
			BezierCurve(
				Pose(72.0, 72.0), Pose(abs(DISTANCE) + 72, 72.0), Pose(
					abs(
						DISTANCE
					) + 72, DISTANCE + 72
				)
			)
		)
		backwards = Path(
			BezierCurve(
				Pose(abs(DISTANCE) + 72, DISTANCE + 72),
				Pose(abs(DISTANCE) + 72, 72.0),
				Pose(72.0, 72.0)
			)
		)

		backwards!!.setTangentHeadingInterpolation()
		backwards!!.reverseHeadingInterpolation()

		follower.followPath(forwards)
	}

	/**
	 * This runs the OpMode, updating the Follower as well as printing out the debug statements to
	 * the Telemetry, as well as the Panels.
	 */
	override fun loop()
	{
		follower.update()
		draw()
		if (!follower.isBusy())
		{
			if (forward)
			{
				forward = false
				follower.followPath(backwards)
			}
			else
			{
				forward = true
				follower.followPath(forwards)
			}
		}

		telemetryM.debug("Driving away from the origin along the curve?: " + forward)
		telemetryM.update(telemetry)
	}

	companion object
	{
		var DISTANCE: Double = 20.0
	}
}

/**
 * This is the Triangle autonomous OpMode.
 * It runs the robot in a triangle, with the starting point being the bottom-middle point.
 *
 * @author Baron Henderson - 20077 The Indubitables
 * @author Samarth Mahapatra - 1002 CircuitRunners Robotics Surge
 * @version 1.0, 12/30/2024
 */
internal class Triangle : OpMode()
{
	private val startPose = Pose(72.0, 72.0, Math.toRadians(0.0))
	private val interPose = Pose((24 + 72).toDouble(), (-24 + 72).toDouble(), Math.toRadians(90.0))
	private val endPose = Pose((24 + 72).toDouble(), (24 + 72).toDouble(), Math.toRadians(45.0))

	private var triangle: PathChain? = null

	/**
	 * This runs the OpMode, updating the Follower as well as printing out the debug statements to
	 * the Telemetry, as well as the Panels.
	 */
	override fun loop()
	{
		follower.update()
		draw()

		if (follower.atParametricEnd())
		{
			follower.followPath(triangle, true)
		}
	}

	override fun init()
	{
		follower.setStartingPose(Pose(72.0, 72.0))
	}

	override fun init_loop()
	{
		telemetryM.debug("This will run in a roughly triangular shape, starting on the bottom-middle point.")
		telemetryM.debug("So, make sure you have enough space to the left, front, and right to run the OpMode.")
		telemetryM.update(telemetry)
		follower.update()
		drawOnlyCurrent()
	}

	/** Creates the PathChain for the "triangle". */
	override fun start()
	{
		follower.setStartingPose(startPose)

		triangle = follower.pathBuilder()
			.addPath(BezierLine(startPose, interPose))
			.setLinearHeadingInterpolation(startPose.getHeading(), interPose.getHeading())
			.addPath(BezierLine(interPose, endPose))
			.setLinearHeadingInterpolation(interPose.getHeading(), endPose.getHeading())
			.addPath(BezierLine(endPose, startPose))
			.setLinearHeadingInterpolation(endPose.getHeading(), startPose.getHeading())
			.build()

		follower.followPath(triangle)
	}
}

/**
 * This is the Circle autonomous OpMode. It runs the robot in a PathChain that's actually not quite
 * a circle, but some Bezier curves that have control points set essentially in a square. However,
 * it turns enough to tune your centripetal force correction and some of your heading. Some lag in
 * heading is to be expected.
 *
 * @author Anyi Lin - 10158 Scott's Bots
 * @author Aaron Yang - 10158 Scott's Bots
 * @author Harrison Womack - 10158 Scott's Bots
 * @version 1.0, 3/12/2024
 */
internal class Circle : OpMode()
{
	private var circle: PathChain? = null

	override fun start()
	{
		circle = follower.pathBuilder()
			.addPath(
				BezierCurve(
					Pose(72.0, 72.0),
					Pose(RADIUS + 72, 72.0),
					Pose(RADIUS + 72, RADIUS + 72)
				)
			)
			.setHeadingInterpolation(HeadingInterpolator.facingPoint(72.0, RADIUS + 72))
			.addPath(
				BezierCurve(
					Pose(RADIUS + 72, RADIUS + 72),
					Pose(RADIUS + 72, (2 * RADIUS) + 72),
					Pose(72.0, (2 * RADIUS) + 72)
				)
			)
			.setHeadingInterpolation(HeadingInterpolator.facingPoint(72.0, RADIUS + 72))
			.addPath(
				BezierCurve(
					Pose(72.0, (2 * RADIUS) + 72),
					Pose(-RADIUS + 72, (2 * RADIUS) + 72),
					Pose(-RADIUS + 72, RADIUS + 72)
				)
			)
			.setHeadingInterpolation(HeadingInterpolator.facingPoint(72.0, RADIUS + 72))
			.addPath(
				BezierCurve(
					Pose(-RADIUS + 72, RADIUS + 72),
					Pose(-RADIUS + 72, 72.0),
					Pose(72.0, 72.0)
				)
			)
			.setHeadingInterpolation(HeadingInterpolator.facingPoint(72.0, RADIUS + 72))
			.build()
		follower.followPath(circle)
	}

	override fun init_loop()
	{
		telemetryM.debug("This will run in a roughly circular shape of radius " + RADIUS + ", starting on the right-most edge. ")
		telemetryM.debug("So, make sure you have enough space to the left, front, and back to run the OpMode.")
		telemetryM.debug("It will also continuously face the center of the circle to test your heading and centripetal correction.")
		telemetryM.update(telemetry)
		follower.update()
		drawOnlyCurrent()
	}

	override fun init()
	{
		follower.setStartingPose(Pose(72.0, 72.0))
	}

	/**
	 * This runs the OpMode, updating the Follower as well as printing out the debug statements to
	 * the Telemetry, as well as the FTC Dashboard.
	 */
	override fun loop()
	{
		follower.update()
		draw()

		if (follower.atParametricEnd())
		{
			follower.followPath(circle)
		}
	}

	companion object
	{
		var RADIUS: Double = 10.0
	}
}

/**
 * This is the OffsetsTuner OpMode. This tracks the movement of the robot as it turns 180 degrees,
 * and calculates what the robot's strafeX and forwardY offsets should be. Ensure that your strafeX and forwardY offsets
 * are set to 0 before running this OpMode. After running, input the displayed offsets into your localizer constants.
 *
 * @author Havish Sripada - 12808 RevAmped Robotics
 * @author Baron Henderson
 */
internal class OffsetsTuner : OpMode()
{
	override fun init()
	{
		follower.setStartingPose(Pose(72.0, 72.0))
		follower.update()
		drawOnlyCurrent()
	}

	/** This initializes the PoseUpdater as well as the Panels telemetry.  */
	override fun init_loop()
	{
		telemetryM.debug("Prerequisite: Make sure both your offsets are set to 0 in your localizer constants.")
		telemetryM.debug("Turn your robot " + Math.PI + " radians. Your offsets in inches will be shown on the telemetry.")
		telemetryM.update(telemetry)

		drawOnlyCurrent()
	}

	/**
	 * This updates the robot's pose estimate, and updates the Panels telemetry with the
	 * calculated offsets and draws the robot.
	 */
	override fun loop()
	{
		follower.update()

		telemetryM.debug("Total Angle: " + follower.getTotalHeading())

		telemetryM.debug("The following values are the offsets in inches that should be applied to your localizer.")
		telemetryM.debug("strafeX: " + ((72.0 - follower.getPose().getX()) / 2.0))
		telemetryM.debug("forwardY: " + ((72.0 - follower.getPose().getY()) / 2.0))
		telemetryM.update(telemetry)

		draw()
	}
}

/**
 * This is the Predictive Braking Tuner. It runs the robot forward and backward at various power
 * levels, recording the robot’s velocity and position immediately before braking. The motors are
 * then set to zero-power brake mode, which represents the fastest theoretical braking the robot
 * can achieve. Once the robot comes to a complete stop, the tuner measures the stopping distance.
 * Using the collected data, it generates a velocity-vs-stopping-distance graph and fits a
 * quadratic curve to model the braking behavior.
 *
 * @author Ashay Sarda - 19745 Turtle Walkers
 * @author Jacob Ophoven - 18535 Frozen Code
 * @version 1.0, 12/26/2025
 */
internal class PredictiveBrakingTuner : OpMode()
{
	private enum class State
	{
		START_MOVE,
		WAIT_DRIVE_TIME,
		APPLY_BRAKE,
		WAIT_BRAKE_TIME,
		RECORD,
		DONE
	}

	private class BrakeRecord(var timeMs: Double, var pose: Pose, var velocity: Double)

	private var state = State.START_MOVE

	private val timer = ElapsedTime()

	private var iteration = 0

	private var startPosition: Vector? = null
	private var measuredVelocity = 0.0

	private val velocityToBrakingDistance: MutableList<DoubleArray?> = ArrayList<DoubleArray?>()
	private val brakeData: MutableList<BrakeRecord> = ArrayList<BrakeRecord>()

	override fun init()
	{
	}

	override fun init_loop()
	{
		telemetryM.debug("The robot will move forwards and backwards starting at max speed and slowing down.")
		telemetryM.debug("Make sure you have enough room. Leave at least 4-5 feet.")
		telemetryM.debug("After stopping, kFriction and kBraking will be displayed.")
		telemetryM.debug("Make sure to turn the timer off.")
		telemetryM.debug("Press B on game pad 1 to stop.")
		telemetryM.update(telemetry)
		follower.update()
		drawOnlyCurrent()
	}

	override fun start()
	{
		timer.reset()
		follower.update()
		follower.startTeleOpDrive(true)
	}

	@SuppressLint("DefaultLocale")
	override fun loop()
	{
		follower.update()

		if (gamepad1.b)
		{
			stopRobot()
			requestOpModeStop()
			return
		}

		when (state)
		{
			State.START_MOVE ->
			{
				if (iteration >= TEST_POWERS.size)
				{
					state = State.DONE
					break
				}

				val currentPower = TEST_POWERS[iteration]
				follower.setMaxPower(currentPower)
				if (iteration % 2 != 0)
				{
					follower.setTeleOpDrive(-1, 0, 0, true)
				}
				else
				{
					follower.setTeleOpDrive(1, 0, 0, true)
				}

				timer.reset()
				state = State.WAIT_DRIVE_TIME
			}

			State.WAIT_DRIVE_TIME ->
			{
				if (timer.milliseconds() >= DRIVE_TIME_MS)
				{
					measuredVelocity = follower.getVelocity().getMagnitude()
					startPosition = follower.getPose().getAsVector()
					state = State.APPLY_BRAKE
				}
			}

			State.APPLY_BRAKE ->
			{
				stopRobot()

				timer.reset()
				state = State.WAIT_BRAKE_TIME
			}

			State.WAIT_BRAKE_TIME ->
			{
				val t = timer.milliseconds()
				val currentPose: Pose = follower.getPose()
				val currentVelocity: Double = follower.getVelocity().getMagnitude()

				brakeData.add(BrakeRecord(t, currentPose, currentVelocity))

				if (timer.milliseconds() >= BRAKE_WAIT_MS || follower.getVelocity().getMagnitude() <= .05)
				{
					state = State.RECORD
				}
			}

			State.RECORD ->
			{
				val endPosition: Vector = follower.getPose().getAsVector()
				val brakingDistance = endPosition.minus(startPosition).getMagnitude()

				velocityToBrakingDistance.add(doubleArrayOf(measuredVelocity, brakingDistance))

				telemetryM.debug(
					"Test " + iteration,
					String.format(
						"v=%.3f  d=%.3f", measuredVelocity,
						brakingDistance
					)
				)
				telemetryM.update(telemetry)

				iteration++
				state = State.START_MOVE
			}

			State.DONE ->
			{
				stopRobot()

				val coefficients: DoubleArray = quadraticFit(velocityToBrakingDistance)

				telemetryM.debug("Tuning Complete")
				telemetryM.debug("Braking Profile:")
				telemetryM.debug("kQuadratic", coefficients[1])
				telemetryM.debug("kLinear", coefficients[0])
				telemetryM.update(telemetry)
				telemetryM.debug("Tuning Complete")
				telemetryM.debug("Braking Profile:")
				telemetryM.debug("kQuadraticFriction", coefficients[1])
				telemetryM.debug("kLinearBraking", coefficients[0])
				for (record in brakeData)
				{
					val p = record.pose
					telemetryM.debug(
						String.format(
							"t=%.0f ms, x=%.2f, y=%.2f, θ=%.2f, v=%.2f",
							record.timeMs, p.getX(), p.getY(),
							p.getHeading(),
							record.velocity
						)
					)
				}
				telemetryM.update()
			}
		}

		telemetry.update()
	}

	companion object
	{
		private val TEST_POWERS =
			doubleArrayOf(1.0, 1.0, 1.0, 0.9, 0.9, 0.8, 0.7, 0.6, 0.5, 0.4, 0.3, 0.2)

		private const val DRIVE_TIME_MS = 1000
		private const val BRAKE_WAIT_MS = 500
	}
}

/**
 * This is the Drawing class. It handles the drawing of stuff on Panels Dashboard, like the robot.
 *
 * @author Lazar - 19234
 * @version 1.1, 5/19/2025
 */
internal object Drawing
{
	const val ROBOT_RADIUS: Double = 9.0 // woah
	private val panelsField = field

	private val robotLook = Style(
		"", "#3F51B5", 0.75
	)
	private val historyLook = Style(
		"", "#4CAF50", 0.75
	)

	/**
	 * This prepares Panels Field for using Pedro Offsets
	 */
	fun init()
	{
		panelsField.setOffsets(presets.getPEDRO_PATHING())
	}

	/**
	 * This draws everything that will be used in the Follower's telemetryDebug() method. This takes
	 * a Follower as an input, so an instance of the DashbaordDrawingHandler class is not needed.
	 *
	 * @param follower Pedro Follower instance.
	 */
	fun drawDebug(follower: Follower)
	{
		if (follower.getCurrentPath() != null)
		{
			drawPath(follower.getCurrentPath(), robotLook)
			val closestPoint =
				follower.getPointFromPath(follower.getCurrentPath().getClosestPointTValue())
			drawRobot(
				Pose(
					closestPoint.getX(),
					closestPoint.getY(),
					follower.getCurrentPath()
						.getHeadingGoal(follower.getCurrentPath().getClosestPointTValue())
				), robotLook
			)
		}
		drawPoseHistory(follower.getPoseHistory(), historyLook)
		drawRobot(follower.getPose(), historyLook)

		sendPacket()
	}

	/**
	 * This draws a robot at a specified Pose with a specified
	 * look. The heading is represented as a line.
	 *
	 * @param pose  the Pose to draw the robot at
	 * @param style the parameters used to draw the robot with
	 */
	/**
	 * This draws a robot at a specified Pose. The heading is represented as a line.
	 *
	 * @param pose the Pose to draw the robot at
	 */
	@JvmOverloads
	fun drawRobot(pose: Pose?, style: Style = robotLook)
	{
		if (pose == null || Double.isNaN(pose.getX()) || Double.isNaN(pose.getY()) || Double.isNaN(pose.getHeading()))
		{
			return
		}

		panelsField.setStyle(style)
		panelsField.moveCursor(pose.getX(), pose.getY())
		panelsField.circle(ROBOT_RADIUS)

		val v = pose.getHeadingAsUnitVector()
		v.setMagnitude(v.getMagnitude() * ROBOT_RADIUS)
		val x1 = pose.getX() + v.getXComponent() / 2
		val y1 = pose.getY() + v.getYComponent() / 2
		val x2 = pose.getX() + v.getXComponent()
		val y2 = pose.getY() + v.getYComponent()

		panelsField.setStyle(style)
		panelsField.moveCursor(x1, y1)
		panelsField.line(x2, y2)
	}

	/**
	 * This draws a Path with a specified look.
	 *
	 * @param path  the Path to draw
	 * @param style the parameters used to draw the Path with
	 */
	fun drawPath(path: Path, style: Style)
	{
		val points = path.getPanelsDrawingPoints()

		for (i in points[0]!!.indices)
		{
			for (j in points.indices)
			{
				if (Double.isNaN(points[j]!![i]))
				{
					points[j]!![i] = 0.0
				}
			}
		}

		panelsField.setStyle(style)
		panelsField.moveCursor(points[0]!![0], points[0]!![1])
		panelsField.line(points[1]!![0], points[1]!![1])
	}

	/**
	 * This draws all the Paths in a PathChain with a
	 * specified look.
	 *
	 * @param pathChain the PathChain to draw
	 * @param style     the parameters used to draw the PathChain with
	 */
	fun drawPath(pathChain: PathChain, style: Style)
	{
		for (i in 0..<pathChain.size())
		{
			drawPath(pathChain.getPath(i), style)
		}
	}

	/**
	 * This draws the pose history of the robot.
	 *
	 * @param poseTracker the PoseHistory to get the pose history from
	 * @param style       the parameters used to draw the pose history with
	 */
	/**
	 * This draws the pose history of the robot.
	 *
	 * @param poseTracker the PoseHistory to get the pose history from
	 */
	@JvmOverloads
	fun drawPoseHistory(poseTracker: PoseHistory, style: Style = historyLook)
	{
		panelsField.setStyle(style)

		val size = poseTracker.getXPositionsArray().size
		for (i in 0..<size - 1)
		{
			panelsField.moveCursor(
				poseTracker.getXPositionsArray()[i],
				poseTracker.getYPositionsArray()[i]
			)
			panelsField.line(
				poseTracker.getXPositionsArray()[i + 1],
				poseTracker.getYPositionsArray()[i + 1]
			)
		}
	}

	/**
	 * This tries to send the current packet to FTControl Panels.
	 */
	fun sendPacket()
	{
		panelsField.update()
	}
}