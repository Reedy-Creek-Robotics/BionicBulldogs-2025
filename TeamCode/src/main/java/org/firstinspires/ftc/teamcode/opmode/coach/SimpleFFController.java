package org.firstinspires.ftc.teamcode.opmode.coach;

import com.acmerobotics.dashboard.config.Config;

/**
 * Simple Feedforward controller
 * Reference to learn about Feedforward: https://www.ctrlaltftc.com/feedforward-control
 *
 * Output = P*error + I*integral + D*derivative + F*targetVelocity
 * Output is going to be the power we give the motor
 *
 * ticks_per_second = (RPM / 60.0) * ticks_per_rev
 */
@Config
public class SimpleFFController {
    //public static double MOTOR_TICKS_PER_REV = 28;
    //public static double MOTOR_MAX_RPM = 5800;
    //public static double MOTOR_GEAR_RATIO = 1; // output (wheel) speed / input (motor) speed

    private double kV; // Velocity gain
    private double kA; // Acceleration gain
    private double kS; // Static friction gain (optional)

    /**
     * @param kV Velocity gain (proportional to desired velocity).
     * @param kA Acceleration gain (proportional to desired acceleration).
     * @param kS Static friction gain (optional, for overcoming static friction).
     */
    public SimpleFFController(double kV, double kA, double kS) {
        this.kV = kV;
        this.kA = kA;
        this.kS = kS;
    }

    /**
     * Calculates the feedforward output based on desired velocity and acceleration.
     * @param targetVelocity The desired velocity.
     * @param targetAcceleration The desired acceleration.
     * @return The calculated feedforward output (e.g., motor voltage).
     */
    public double calculate(double targetVelocity, double targetAcceleration) {
        double output = (kV * targetVelocity) + (kA * targetAcceleration);

        // (optional) add kS here

        return output;
    }

    /**
     * Calculates the feedforward output for velocity control, assuming zero acceleration.
     * @param targetVelocity The desired velocity.
     * @return The calculated feedforward output.
     */
    public double calculate(double targetVelocity) {
        return calculate(targetVelocity, 0.0);
    }

    //public static double rpmToTicksPerSecond(double rpm) {
    //    return rpm * MOTOR_TICKS_PER_REV / MOTOR_GEAR_RATIO / 60;
    //}
}
