---@alias Vec2 number[]

vec2 = {};

---@param v Vec2
function vec2.normalize(v)
	if (v[1] == 0 and v[2] == 0) then
		return;
	end
	local len = math.sqrt(v[1] * v[1] + v[2] * v[2]);

	local f = 1 / len;

	v[1] = v[1] * f;
	v[2] = v[2] * f;
end

---@param a vec2
---@param b vec2
---@return number
function vec2.dot(a, b)
	return a[1] * b[1] + a[2] * b[2];
end