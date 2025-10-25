package.path = package.path .. ";./lua/?.lua";

require("modules.table");

---@type number[]
list = { 1000, 1200, 1300, 1600 }

local input = math.tointeger(io.read());
if(input == nil) then error("not an integer") end

local i, i2 = table.closestInt(list, input)

print(i)
for k, v in ipairs(i2) do
	print(v);
end