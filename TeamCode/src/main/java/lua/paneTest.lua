package.path = package.path .. ";./lua/?.lua";

require("lua.modules.telemPanes");

TelemPaneManager:reset("actions");

local pane = TelemPaneManager.panes[1];

pane:addLine("e: path failed");

local p3 = pane:vsplit("robot", 10);

local p2 = pane:vsplit("drive", 5);

p2:addData("x", 4);
p2:addData("y", 6.2);
p2:addData("h", 45);


TelemPaneManager:update();