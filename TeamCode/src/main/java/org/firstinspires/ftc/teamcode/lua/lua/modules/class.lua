---@generic T
---@param t T
---@return T
function new(t)
	local n = {};
	for k, v in pairs(t) do
		n[k] = v;
	end
	n.new = nil;
	return n;
end