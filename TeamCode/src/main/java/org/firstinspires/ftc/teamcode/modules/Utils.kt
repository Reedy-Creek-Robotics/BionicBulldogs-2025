package org.firstinspires.ftc.teamcode.modules

import org.firstinspires.ftc.robotcore.external.Telemetry

fun Telemetry.fmt(fmt: String, vararg args: Any)
{
	addLine(fmt.format(args));
}