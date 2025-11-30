package.path = package.path .. ";./lua/?.lua";

---@type Opmode[]
opmodes = {};

---@param opmode Opmode
function addOpmode(opmode)
	table.insert(opmodes, opmode);
end

DATADIR = "./out/"