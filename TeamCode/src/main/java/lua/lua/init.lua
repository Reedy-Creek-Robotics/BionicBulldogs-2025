SCRIPTDIR =	SCRIPTDIR or "/sdcard/lua";

local files = io.list(SCRIPTDIR .. "/opmode");
for i, file in ipairs(files) do
	if(file.file) then
		local ind = file.name:find('%.') - 1;
		local name = file.name:sub(1, ind);
		local ext = file.name:sub(ind + 1);
		if(ext == "lua") then
			print("requiring file");
		end
		--require("opmode." .. name);
	end
end

--[[require("opmode.pedroTest");
require("opmode.frHdriveTest");
require("opmode.intakeTest");
require("opmode.redBack");
require("opmode.blueBack");
require("opmode.blueFront");
require("opmode.mainTelop");
require("opmode.aprilTelop");
require("opmode.transferTest");
]]