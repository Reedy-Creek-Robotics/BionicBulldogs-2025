package org.firstinspires.ftc.teamcode;

import android.os.Environment;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.log.Datalog;
import org.firstinspires.ftc.teamcode.utils.NanoFileServer;

import java.io.File;
import java.net.Inet4Address;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.Collections;

@TeleOp
public class MotorTester extends LinearOpMode {
    private NanoFileServer server;
    private static final int PORT = 8888;

    // arrays to match up motors and their ticks (for encoder calcs)
    String[] motorType = new String[]{"117","312", "435", "1150"};
    double[] motorTicks = new double[] {1425.1, 537.7, 384.5, 145.1};

    double powerIncrement = 0.2;

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

        DcMotorEx motor = hardwareMap.get(DcMotorEx.class, "motor");
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // index for currently selected motor
        int motorIndex = 0;

        // current power setting
        double motorPower = 0.0;

        // init the file logging
        // add a timestamp on end of filename so each run of op mode gives
        // you a unique file
        String timeStamp = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new java.util.Date());
        log = new Datalog("motorTester_" + timeStamp);

        // WAIT FOR INIT
        waitForStart();

        boolean isRunning = false;
        while(opModeIsActive()) {
            // controls
            if( gamepad1.dpadDownWasPressed() ) {
                motorPower -= powerIncrement;
            }

            if( gamepad1.dpadUpWasPressed() ) {
                motorPower += powerIncrement;
            }

            // cycles through index when dpad right is pressed
            if( gamepad1.dpadRightWasPressed() ) {
                motorIndex = (motorIndex % 3) + 1;
            }

            // start the test
            if( gamepad1.xWasPressed() ) {
                isRunning = true;
                motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                motor.setPower(motorPower);
            }

            // stop the test
            if( gamepad1.yWasPressed() ) {
                isRunning = false;
                motor.setTargetPosition(0);
                motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                while( motor.isBusy() ) {
                    // do nothing
                }
                motor.setPower(0);
            }

            // collect data
            double actualPower = motor.getPower();
            double current = motor.getCurrent(CurrentUnit.AMPS);
            double velocity = motor.getVelocity();
            double ticks = motor.getCurrentPosition();

            // log
            log.current.set(current);
            log.running.set(isRunning);
            log.ticks.set(ticks);
            log.motorPower.set(motorPower);
            log.motorType.set(motorType[motorIndex]);
            log.velocity.set(velocity);
            log.writeLine();

            // telemetry
            telemetry.addData("set power", motorPower);
            telemetry.addData("actual power", motor.getPower());
            telemetry.addData("current", current);
            telemetry.addData("ticks", ticks );
            telemetry.addData("velocity",velocity);
            telemetry.addData("motor type",motorType[motorIndex]);
            telemetry.update();
        }

        // Clean shutdown
        if (server != null) server.stop();
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
