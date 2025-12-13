require("test.testBase");
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
	currentPane:addData("fl", 100);
	currentPane:addData("fr", 100);
	currentPane:addData("bl", 100);
	currentPane:addData("br", 100);
	currentPane:addData("sl", 100);
	currentPane:addData("sr", 100);
	currentPane:addData("in", 100);

robotPane:addData("pos", -300);
robotPane:addData("target pos", 500);
robotPane:addData("slide state", "reset");
aprilTagPane:addLine("no path running");

TelemPaneManager:update();