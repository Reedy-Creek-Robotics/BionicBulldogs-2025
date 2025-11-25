require("test.testBase");

path = {
	chain = function ()
		return {
			add = function (self) return self end,
			constantHeading = function (self) return self end,
			linearHeading = function (self) return self end,
			build = function (self) return {} end
		};
	end,
	line = function (x1, y1, x2, y2)
		print(tostring(x1) .. ", " .. tostring(y1) .. ", " .. tostring(x2) .. ", " .. tostring(y2));
		return {}
	end,
	curve3 = function (x1, y1, x2, y2)
		print(tostring(x1) .. ", " .. tostring(y1) .. ", " .. tostring(x2) .. ", " .. tostring(y2));
		return {}
	end
};

require("opmode.auto.blueBack");
require("opmode.auto.blueFront");
require("opmode.auto.redBack");
require("opmode.auto.redFront");

for k, v in ipairs(opmodes) do
	print("opmode " .. tostring(k) .. ": " .. v.name);
end