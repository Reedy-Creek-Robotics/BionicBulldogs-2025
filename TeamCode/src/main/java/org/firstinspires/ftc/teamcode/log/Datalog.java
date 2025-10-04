package org.firstinspires.ftc.teamcode.log;

import android.provider.ContactsContract;

public class Datalog {
    // The underlying datalogger object - it cares only about an array of loggable fields
    private final Datalogger datalogger;

    // These are all of the fields that we want in the datalog.
    // Note that order here is NOT important. The order is important in the setFields() call below
    public Datalogger.GenericField current = new Datalogger.GenericField("Current");
    public Datalogger.GenericField ticks = new Datalogger.GenericField("Ticks");
    public Datalogger.GenericField motorPower = new Datalogger.GenericField("Set Power");
    public Datalogger.GenericField velocity = new Datalogger.GenericField("Velocity");
    public Datalogger.GenericField running = new Datalogger.GenericField("Running");
    public Datalogger.GenericField motorType = new Datalogger.GenericField("Motor Type");
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
            .setFields(motorPower, current, batteryVoltage, running, ticks, velocity).build();
    }

    // Tell the datalogger to gather the values of the fields
    // and write a new line in the log.
    public void writeLine()
    {
        datalogger.writeLine();
    }
}