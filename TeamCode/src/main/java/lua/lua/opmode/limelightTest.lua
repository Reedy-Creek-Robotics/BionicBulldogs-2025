---@type Opmode
local opmode = { name = "limelightTest", type = OpmodeType.Telop };


function opmode.init()
    limelight = hardwareMap.limelightGet();
end

function opmode.update()
	limelight:update();
	local tag = limelight:getTag(20);
	if(tag ~= nil) then
		telemetry.addDataf("tx", tag:tx());
	else
		telemetry.addLine("no tag found");
	end
	telemetry.update();
	return false;
end

addOpmode(opmode);