require("test.testBase");
require("opmode.blueFront");

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
	end
};

opmodes[3]:init();
opmodes[3]:stop();