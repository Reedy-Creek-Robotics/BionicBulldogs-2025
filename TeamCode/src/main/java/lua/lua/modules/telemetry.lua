require("modules.telemPanes");

TelemPaneManager:reset("actions");

---@type TelemPane
actionPane = TelemPaneManager.rootPane;
actionPane.autoReset = false;
robotPane = actionPane:vsplit("robot", 5);
drivePane = robotPane:hsplit("drive");
aprilTagPane = drivePane:vsplit("aprilTag");