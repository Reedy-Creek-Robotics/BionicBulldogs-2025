package org.firstinspires.ftc.teamcode.log;

import android.provider.ContactsContract;

public class Datalog {
    // The underlying datalogger object - it cares only about an array of loggable fields
    private final Datalogger datalogger;

    // These are all of the fields that we want in the datalog.
    // Note that order here is NOT important. The order is important in the setFields() call below
    public Datalogger.GenericField leftCurrent = new Datalogger.GenericField("leftCurrent");
    public Datalogger.GenericField rightCurrent = new Datalogger.GenericField("rightCurrent");
    public Datalogger.GenericField leftTicks = new Datalogger.GenericField("leftTicks");
    public Datalogger.GenericField rightTicks = new Datalogger.GenericField("rightTicks");
    public Datalogger.GenericField leftPower = new Datalogger.GenericField("leftPower");
    public Datalogger.GenericField rightPower = new Datalogger.GenericField("rightPower");
    public Datalogger.GenericField leftVelocity = new Datalogger.GenericField("leftVelocity");
    public Datalogger.GenericField rightVelocity = new Datalogger.GenericField("rightVelocity");
    public Datalogger.GenericField leftPIDF = new Datalogger.GenericField("leftPIDF");
    public Datalogger.GenericField rightPIDF = new Datalogger.GenericField("rightPIDF");
    public Datalogger.GenericField batteryVoltage = new Datalogger.GenericField("Battery Voltage");

    public Datalog(String name)
    {
        // Build the underlying datalog object
        datalogger = new Datalogger.Builder()

            // Pass through the filename
            .setFilename(name)

            // Request an automatic timestamp field
            .setAutoTimestamp(Datalogger.AutoTimestamp.DECIMAL_SECONDS)

            // Tell it about the fields we care to log.
            // Note that order *IS* important here! The order in which we list
            // the fields is the order in which they will appear in the log.
            .setFields(
                leftCurrent, leftTicks, leftPower, leftVelocity, leftPIDF,
                rightCurrent, rightTicks, rightPower, rightVelocity, rightPIDF,
                batteryVoltage
            ).build();
    }

    // Tell the datalogger to gather the values of the fields
    // and write a new line in the log.
    public void writeLine()
    {
        datalogger.writeLine();
    }
}