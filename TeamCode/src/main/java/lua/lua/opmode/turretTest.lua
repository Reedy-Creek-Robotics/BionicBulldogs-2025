---@type Opmode
local opmode = { name = "t_turretTest" };

local turretStateLabels = { "waiting", "tracking", "manual" };

function opmode.init()
	turret.init();
end


function opmode.update()
	telemetry.addLine("turret state: " .. turretStateLabels[turret.getState()]);

	if (gamepad:getCircle2()) then
		turret.startManual();
	end
	if (gamepad:getCross2()) then
		turret.startAutomatic();
	end
	if (gamepad:getSquare2()) then
		turret.resetHeading();
	end

	turret.update(gamepad:getLeftStickX());

	return false;
end

addOpmode(opmode);