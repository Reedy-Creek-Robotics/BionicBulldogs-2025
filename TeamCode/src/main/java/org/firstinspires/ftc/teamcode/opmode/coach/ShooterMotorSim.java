package org.firstinspires.ftc.teamcode.opmode.coach;

import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Class to simulate shooting so that we can tune our motors PIDF
 *
 * It's going to do the following
 *   - starting at a minimum power it will ramp to a target velo
 *   - it will "coast" at that velo for a few seconds
 *   - it will dip to 50% and then back to target
 *   - immediately repeat the dip 2 more times
 *   - go back to coast
 */
public class ShooterMotorSim {
    public static double MOTOR_TICKS_PER_REV = 28;
    public static double MOTOR_MAX_RPM = 5800; // in ticks per second
    public static double MOTOR_GEAR_RATIO = 1; // output (wheel) speed / input (motor) speed

    public static double TESTING_MAX_SPEED = 0.6 * MOTOR_MAX_RPM;
    public static double TESTING_MIN_SPEED = 0.3 * MOTOR_MAX_RPM;

    enum SimState {
        NOT_STARTED, RAMPING, COASTING, DIP_1, RAMP_1, DIP_2, RAMP_2, DIP_3, RAMP_3
    }

    private ElapsedTime stateTimer = new ElapsedTime();

    private double targetVelocity = 0.0;

    private SimState currentState = SimState.NOT_STARTED;

    public SimState getCurrentState() {
        return currentState;
    }

    public void start() {
        currentState = SimState.RAMPING;
        stateTimer.reset();
    }

    public double update() {
        switch (currentState) {
            case NOT_STARTED:
                break;
            case RAMPING:
                double elapsedTime = stateTimer.seconds();
                // ramp proportionally to the time we spend here
                targetVelocity = rpmToTicksPerSecond(TESTING_MAX_SPEED) * (elapsedTime/3);
                if( stateTimer.seconds() >= 3 ) {
                    // move to next state
                    currentState = SimState.COASTING;
                    stateTimer.reset();
                }
                break;
            case COASTING:
                if( stateTimer.seconds() >= 5) {
                    // move to next state
                    currentState = SimState.DIP_1;
                    stateTimer.reset();
                }
                break;
            case DIP_1:
                elapsedTime = stateTimer.seconds();
                // ramp proportionally to the time we spend here - 30% of max
                targetVelocity = rpmToTicksPerSecond(TESTING_MAX_SPEED - (TESTING_MAX_SPEED - TESTING_MIN_SPEED) * (elapsedTime));
                if( stateTimer.seconds() >= 1 ) {
                    // move to next state
                    currentState = SimState.RAMP_1;
                    stateTimer.reset();
                }
                break;
            case RAMP_1:
                elapsedTime = stateTimer.seconds();
                // ramp proportionally to the time we spend here - 30% of max
                targetVelocity = rpmToTicksPerSecond( TESTING_MIN_SPEED + (TESTING_MAX_SPEED - TESTING_MIN_SPEED) * (elapsedTime));
                if( stateTimer.seconds() >= 1 ) {
                    // move to next state
                    currentState = SimState.COASTING;
                    stateTimer.reset();
                }
                break;
            case DIP_2:
                break;
            case RAMP_2:
                break;
            case DIP_3:
                break;
            case RAMP_3:
                break;
        }

        return targetVelocity;
    }

    public static double rpmToTicksPerSecond(double rpm) {
        return rpm * MOTOR_TICKS_PER_REV / MOTOR_GEAR_RATIO / 60;
    }
}
