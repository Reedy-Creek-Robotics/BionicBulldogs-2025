package org.firstinspires.ftc.teamcode.opmode.coach;

import android.os.Environment;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.MotorControlAlgorithm;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.util.RobotLog;

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

    private NanoFileServer server;
    private static final int PORT = 8888;

    double velocityIncrement = 50;

    Datalog log;

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

        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        DcMotorEx flywheelLeft = hardwareMap.get(DcMotorEx.class, "flywheelLeft");
        flywheelLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        flywheelLeft.setDirection(DcMotorSimple.Direction.REVERSE);

        DcMotorEx flywheelRight = hardwareMap.get(DcMotorEx.class, "flywheelRight");
        flywheelRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        DcMotorEx transfer = hardwareMap.get(DcMotorEx.class, "transfer");
        transfer.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        ShooterMotorSim motorSim = new ShooterMotorSim();

        // current power setting
        double motorVelocity = 0.0;

        // init the file logging
        // add a timestamp on end of filename so each run of op mode gives
        // you a unique file
        String timeStamp = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new java.util.Date());
        log = new Datalog("shooterTester_" + timeStamp);

        // WAIT FOR INIT
        waitForStart();

        // start the simulator
        motorSim.start();

        boolean isRunning = false;
        boolean loggingEnabled = false;
        while(opModeIsActive()) {
            double targetVelocity = motorSim.update();

            flywheelLeft.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, SHOOTER_PIDF_VALUES);
            flywheelRight.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, SHOOTER_PIDF_VALUES);
            if( isRunning ) {
                flywheelLeft.setVelocity(targetVelocity);
                flywheelRight.setVelocity(targetVelocity);
            }

            // controls
            if( gamepad1.dpadDownWasPressed() ) {
                motorVelocity -= velocityIncrement;
            }

            if( gamepad1.dpadUpWasPressed() ) {
                motorVelocity += velocityIncrement;
            }

            // start the test
            if( gamepad1.squareWasPressed() ) {
                isRunning = true;
                //flywheelLeft.setVelocity(targetVelocity);
                //flywheelRight.setVelocity(targetVelocity);
            }

            // stop the test
            if( gamepad1.triangleWasPressed() ) {
                isRunning = false;
                flywheelLeft.setVelocity(0);
                flywheelRight.setVelocity(0);
            }

            if( gamepad1.circleWasPressed()) {
                loggingEnabled = !loggingEnabled;
            }

            if( gamepad1.crossWasPressed() ) {
                if( transfer.getPower() == 0 ) {
                    transfer.setPower(1.0);
                }
                else {
                    transfer.setPower(0);
                }
            }

            // collect data
            // double actualPower = motor.getPower();
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
            telemetry.addData("VELOCITY", motorVelocity);
            telemetry.addData("TARGET VELO", targetVelocity);
            telemetry.addData("STATE", motorSim.getCurrentState());
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

            telemetry.addData("transfer power", transfer.getPower());
            telemetry.update();
        }

        // Clean shutdown
        if (server != null) server.stop();
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
