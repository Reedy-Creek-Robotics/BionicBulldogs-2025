package.path = package.path .. ";./lua/?.lua";

require("modules.telemPanes");

TelemPaneManager.h = 20;
TelemPaneManager.w = 40;

require("modules.telemetry");

telemetry = {};
function telemetry.addLine(line)
	print(line);
end

function telemetry.update()
end

actionPane:addLine("error: 'path' action failed");
drivePane:addData("x", 3.2);
drivePane:addData("y", 4.8);
drivePane:addData("h", 45);

robotPane:addData("pos", -300);
robotPane:addData("target pos", 500);
robotPane:addData("slide state", "reset");
aprilTagPane:addLine("no path running");

TelemPaneManager:update();