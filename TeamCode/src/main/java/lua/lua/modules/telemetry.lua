require("modules.telemPanes");

TelemPaneManager:reset("actions");

local aPane = TelemPaneManager.rootPane;
---@cast aPane TelemPane
actionPane = aPane;
actionPane.autoReset = false;
robotPane = actionPane:vsplit("robot", 5);
drivePane = robotPane:hsplit("drive");
aprilTagPane = drivePane:vsplit("aprilTag");