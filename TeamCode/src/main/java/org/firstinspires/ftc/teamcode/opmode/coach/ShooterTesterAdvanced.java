package org.firstinspires.ftc.teamcode.opmode.coach;

import android.os.Environment;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.MotorControlAlgorithm;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.log.Datalog;
import org.firstinspires.ftc.teamcode.utils.NanoFileServer;

import java.io.File;
import java.net.Inet4Address;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.Collections;

@Config
@TeleOp
public class ShooterTesterAdvanced extends LinearOpMode {
    public static PIDFCoefficients SHOOTER_PIDF_VALUES = new PIDFCoefficients(10, 3, 0, 0, MotorControlAlgorithm.LegacyPID);
    public static double intakePowerWhenShooting = 0.5;

    private NanoFileServer server;
    private static final int PORT = 8888;

    double velocityIncrement = 25;

    Datalog log;

    // SHOOTER
    DcMotorEx flywheelLeft;
    DcMotorEx flywheelRight;
    double shooterVelocity = 1000.0;
    boolean isShooterRunning = false;

    // INTAKE
    DcMotorEx intake;
    enum IntakeState { STOPPED, FORWARD, REVERSE};
    IntakeState intakeState = IntakeState.STOPPED;

    // SHOOTER GATE
    Servo shooterGate;
    static class GatePosition {
        public static double open = 0.3;
        public static double closed = 0.0;
    }

    // DRIVE
    DcMotorEx frontLeftDrive;
    DcMotorEx frontRightDrive;
    DcMotorEx backLeftDrive;
    DcMotorEx backRightDrive;
    IMU imu;

    /***
     * dpad = up and down to increment/decrement motor power by 0.2
     * dpad = side to side cycle through motors
     *   - create a static map of motors: 117, 312, 435, 1150
     *   - map also includes number of ticks to use for encoder movements
     * X = start the program
     * Y = stop
     * log file = every loop, comma separated
     *  - timestamp
     *  - current motor current
     *  - encoder ticks since start
     *  - log start and stop button pressed
     *  - should use a unique filename that includes timestamp
     *  - create the file if it does not exist during init
     *  - close the file on stop
     *  output to the screen current motor current, encoder ticks since start
     */
    @Override
    public void runOpMode() throws InterruptedException {
        serveFiles();

        initHardware();

        // init the file logging
        // add a timestamp on end of filename so each run of op mode gives
        // you a unique file
        String timeStamp = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new java.util.Date());
        log = new Datalog("shooterTester_" + timeStamp);

        // WAIT FOR INIT
        waitForStart();

        boolean loggingEnabled = false;
        while(opModeIsActive()) {
            //flywheelLeft.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, SHOOTER_PIDF_VALUES);
            //flywheelRight.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, SHOOTER_PIDF_VALUES);
            //if( isRunning ) {
            //    flywheelLeft.setVelocity(targetVelocity);
            //    flywheelRight.setVelocity(targetVelocity);
            //}

            // module updates
            shooter();
            shooterGate();
            intake();
            driveFieldRelative(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);

            // controls
            if( gamepad1.dpadDownWasPressed() ) {
                shooterVelocity -= velocityIncrement;
            }

            if( gamepad1.dpadUpWasPressed() ) {
                shooterVelocity += velocityIncrement;
            }

            if( gamepad1.squareWasPressed()) {
                loggingEnabled = !loggingEnabled;
            }

            // collect data
            double leftCurrent = flywheelLeft.getCurrent(CurrentUnit.AMPS);
            double rightCurrent = flywheelRight.getCurrent(CurrentUnit.AMPS);
            double leftVelocity = flywheelLeft.getVelocity();
            double rightVelocity = flywheelRight.getVelocity();
            double leftTicks = flywheelLeft.getCurrentPosition();
            double rightTicks = flywheelRight.getCurrentPosition();
            PIDFCoefficients leftPIDF = flywheelLeft.getPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER);
            PIDFCoefficients rightPIDF = flywheelRight.getPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER);

            // log - only when logging is enabled
            if( loggingEnabled ) {
                log.leftCurrent.set(leftCurrent);
                log.leftVelocity.set(leftVelocity);
                log.leftTicks.set(leftTicks);
                log.leftPIDF.set("P="+leftPIDF.p+" I="+leftPIDF.i + " D="+ leftPIDF.d + " F="+ leftPIDF.f);
                log.leftPower.set(flywheelLeft.getPower());

                log.rightCurrent.set(rightCurrent);
                log.rightVelocity.set(rightVelocity);
                log.rightTicks.set(rightTicks);
                log.rightPIDF.set("P="+rightPIDF.p+" I="+rightPIDF.i + " D="+ rightPIDF.d + " F="+ rightPIDF.f);
                log.rightPower.set(flywheelRight.getPower());

                log.batteryVoltage.set(getBatteryVoltage());

                log.writeLine();
            }

            // telemetry
            telemetry.addData("IS_LOGGING", loggingEnabled + ", " + timeStamp);
            telemetry.addData("VELOCITY", shooterVelocity);
            telemetry.addData("TARGET VELO", shooterVelocity);
            telemetry.addLine("");
            telemetry.addData("leftVelocity", leftVelocity);
            telemetry.addData("leftTicks", leftTicks);
            telemetry.addData("leftPower", flywheelLeft.getPower());
            telemetry.addData("leftCurrent", leftCurrent);
            telemetry.addData("leftPIDF", leftPIDF.toString());

            telemetry.addData("rightVelocity", rightVelocity);
            telemetry.addData("rightTicks", rightTicks);
            telemetry.addData("rightPower", flywheelRight.getPower());
            telemetry.addData("rightCurrent", rightCurrent);
            telemetry.addData("rightPIDF", rightPIDF.toString());

            telemetry.addData("transfer power", intake.getPower());
            telemetry.update();
        }

        // Clean shutdown
        if (server != null) server.stop();
    }

    public void shooter() {
        // start the test
        if( gamepad1.circleWasPressed() ) {
            isShooterRunning = true;
            flywheelLeft.setVelocity(shooterVelocity);
            flywheelRight.setVelocity(shooterVelocity);
        }

        // stop the test
        if( gamepad1.triangleWasPressed() ) {
            isShooterRunning = false;
            flywheelLeft.setVelocity(0);
            flywheelRight.setVelocity(0);
        }
    }

    public void shooterGate() {
        if( gamepad1.crossWasPressed() ) {
            if( shooterGate.getPosition() == GatePosition.open ) {
                shooterGate.setPosition(GatePosition.closed);
            }
            else {
                shooterGate.setPosition(GatePosition.open);
            }
            intake.setPower(intakePowerWhenShooting);
        }
/*
        // delayed open / close
        int delay = 150;
        if( gamepad1.crossWasPressed() ) {
            // SHOT 1
            // open --
            shooterGate.setPosition(GatePosition.open);
            sleep(200);
            // close --
            shooterGate.setPosition(GatePosition.closed);
            sleep(200);

            // SHOT 2
            // open --
            shooterGate.setPosition(GatePosition.open);
            sleep(200);
            // close --
            shooterGate.setPosition(GatePosition.closed);
            sleep(200);

            // SHOT 3
            // open --
            shooterGate.setPosition(GatePosition.open);
            sleep(200);
            // close --
            shooterGate.setPosition(GatePosition.closed);
            sleep(200);
        }
 */
    }

    public void intake() {
        if( gamepad1.rightBumperWasPressed() ) {
            if( intakeState == IntakeState.FORWARD ) {
                intakeState = IntakeState.STOPPED;
                intake.setPower(0);
            }
            else {
                intakeState = IntakeState.FORWARD;
                intake.setPower(1);
            }
        }

        if( gamepad1.leftBumperWasPressed() ) {
            if( intakeState == IntakeState.REVERSE ) {
                intakeState = IntakeState.STOPPED;
                intake.setPower(0);
            }
            else {
                intakeState = IntakeState.REVERSE;
                intake.setPower(-1);
            }
        }
    }

    double getBatteryVoltage() {
        double result = Double.POSITIVE_INFINITY;
        for (VoltageSensor sensor : hardwareMap.voltageSensor) {
            double voltage = sensor.getVoltage();
            if (voltage > 0) {
                result = Math.min(result, voltage);
            }
        }
        return result;
    }

    public void initHardware() {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        frontLeftDrive = hardwareMap.get(DcMotorEx.class, "frontLeft");
        frontRightDrive = hardwareMap.get(DcMotorEx.class, "frontRight");
        backLeftDrive = hardwareMap.get(DcMotorEx.class, "backLeft");
        backRightDrive = hardwareMap.get(DcMotorEx.class, "backRight");

        imu = hardwareMap.get(IMU.class, "imu");
        // This needs to be changed to match the orientation on your robot
        RevHubOrientationOnRobot.LogoFacingDirection logoDirection = RevHubOrientationOnRobot.LogoFacingDirection.LEFT;
        RevHubOrientationOnRobot.UsbFacingDirection usbDirection = RevHubOrientationOnRobot.UsbFacingDirection.UP;
        RevHubOrientationOnRobot orientationOnRobot = new RevHubOrientationOnRobot(logoDirection, usbDirection);
        imu.initialize(new IMU.Parameters(orientationOnRobot));
        imu.resetYaw();

        flywheelLeft = hardwareMap.get(DcMotorEx.class, "flywheelLeft");
        flywheelLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        flywheelLeft.setDirection(DcMotorSimple.Direction.REVERSE);

        flywheelRight = hardwareMap.get(DcMotorEx.class, "flywheelRight");
        flywheelRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        intake = hardwareMap.get(DcMotorEx.class, "intake");
        intake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // shooter gate
        shooterGate = hardwareMap.get(Servo.class, "transfer");
    }

    // This routine drives the robot field relative
    private void driveFieldRelative(double forward, double right, double rotate) {
        // First, convert direction being asked to drive to polar coordinates
        double theta = Math.atan2(forward, right);
        double r = Math.hypot(right, forward);

        // Second, rotate angle by the angle the robot is pointing
        theta = AngleUnit.normalizeRadians(theta -
            imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS));

        // Third, convert back to cartesian
        double newForward = r * Math.sin(theta);
        double newRight = r * Math.cos(theta);

        // Finally, call the drive method with robot relative forward and right amounts
        drive(newForward, newRight, rotate);
    }

    // Thanks to FTC16072 for sharing this code!!
    public void drive(double forward, double right, double rotate) {
        // This calculates the power needed for each wheel based on the amount of forward,
        // strafe right, and rotate
        /*
        double frontLeftPower = forward + right + rotate;
        double frontRightPower = forward - right - rotate;
        double backRightPower = forward + right - rotate;
        double backLeftPower = forward - right + rotate;
        */

        double frontLeftPower = forward + right + rotate;
        double frontRightPower = forward - right - rotate;
        double backRightPower = forward + right - rotate;
        double backLeftPower = forward - right + rotate;

        double maxPower = 1.0;
        double maxSpeed = 1.0;  // make this slower for outreaches

        // This is needed to make sure we don't pass > 1.0 to any wheel
        // It allows us to keep all of the motors in proportion to what they should
        // be and not get clipped
        maxPower = Math.max(maxPower, Math.abs(frontLeftPower));
        maxPower = Math.max(maxPower, Math.abs(frontRightPower));
        maxPower = Math.max(maxPower, Math.abs(backRightPower));
        maxPower = Math.max(maxPower, Math.abs(backLeftPower));

        // We multiply by maxSpeed so that it can be set lower for outreaches
        // When a young child is driving the robot, we may not want to allow full
        // speed.
        frontLeftDrive.setPower(maxSpeed * (frontLeftPower / maxPower));
        frontRightDrive.setPower(maxSpeed * (frontRightPower / maxPower));
        backLeftDrive.setPower(maxSpeed * (backLeftPower / maxPower));
        backRightDrive.setPower(maxSpeed * (backRightPower / maxPower));
    }

    private void serveFiles() {
        // Common pick: /sdcard/FIRST (exists on both phones & Control Hub)
        File firstDir = new File(Environment.getExternalStorageDirectory(), "FIRST/java/src/Datalogs/");
        if (!firstDir.exists()) firstDir.mkdirs();

        // Start the server (NanoHTTPD spawns its own thread)
        server = new NanoFileServer(PORT, firstDir);

        try {
            server.start(NanoFileServer.SOCKET_READ_TIMEOUT, false);
        } catch (Exception e) {
            telemetry.addLine("Failed to start server: " + e.getMessage());
            telemetry.update();
            // Let the op run anyway; or return if you want strict behavior
        }

        String ip = getLocalIpOrFallback();
        RobotLog.d("Serving directory", firstDir.getAbsolutePath());
        RobotLog.d("URL", "http://" + ip + ":" + PORT + "/");
    }

    /** Tries to find the Wi-Fi/lan IPv4, falls back to common FTC default. */
    private String getLocalIpOrFallback() {
        try {
            for (NetworkInterface nif : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                for (java.net.InetAddress addr : Collections.list(nif.getInetAddresses())) {
                    if (!addr.isLoopbackAddress() && addr instanceof Inet4Address) {
                        return addr.getHostAddress();
                    }
                }
            }
        } catch (Exception ignored) {}
        // Common FTC hotspot IP for RC/Control Hub
        return "192.168.43.1";
    }
}
