require("modules.action.seqAction");
require("modules.action.parallelAction");

DATADIR = DATADIR or "/sdcard/";

profiler = {};

---@param filename string
---@param action Action
function profiler.genString(filename, action)
	if (action.genProfileStr == nil) then
		error("'getProfileStr' is nil on root action");
	end
	local file = io.open(DATADIR .. filename, "wb");
	if (file == nil) then
		error("cant open file");
	end
	action:genProfileStr(file);
	file:close();
end