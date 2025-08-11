package org.firstinspires.ftc.teamcode.pedroPathing.constants;

import com.pedropathing.localization.*;
import com.pedropathing.localization.constants.*;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;

public class LConstants {
    static {
        TwoWheelConstants.forwardTicksToInches = 0.001977958;
        TwoWheelConstants.strafeTicksToInches = 0.001982840;
        TwoWheelConstants.forwardY = 0;
        TwoWheelConstants.strafeX = 0;
        TwoWheelConstants.forwardEncoder_HardwareMapName = "backRight";
        TwoWheelConstants.strafeEncoder_HardwareMapName = "backLeft";
        TwoWheelConstants.forwardEncoderDirection = Encoder.FORWARD;
        TwoWheelConstants.strafeEncoderDirection = Encoder.FORWARD;
        TwoWheelConstants.IMU_HardwareMapName = "imu";
        TwoWheelConstants.IMU_Orientation = new RevHubOrientationOnRobot(RevHubOrientationOnRobot.LogoFacingDirection.UP, RevHubOrientationOnRobot.UsbFacingDirection.RIGHT);
    }
}




