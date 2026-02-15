require("opmode.auto.autoBase");

---@type AutoPaths
local config = {
	start = { x = 33, y = 136, z = 180 },
	turretTarget = { x = 6, y = 138 },
	preload = function ()
		return SeqAction.newl(
			"preload",
		  RobotActions.ShooterStart.new(680),
			PathAction.new(
				path.chain()
				:add(path.line(32, 136, 54, 90))
				:constantHeading(180)
				:build()
			),
			RobotActions.Shoot.new(1)
		)
	end,
	line1 = {
		noGate = function ()
			return SeqAction.newl(
				"line1",
				PathAction.new(
					path.chain()
					:add(path.line(54, 90, 38, 84))
					:constantHeading(180)
					:add(path.line(38, 84, 18, 84))
					:constantHeading(180)
					:build()
				),
				PathAction.new(
					path.chain()
					:add(path.line(24, 84, 54, 90))
					:constantHeading(180)
					:build()
				),
				RobotActions.Shoot.new(1)
			)
		end,
		gate = function ()
			return SeqAction.newl(
				"line1",
				PathAction.new(
					path.chain()
					:add(path.line(54, 90, 38, 87))
					:constantHeading(180)
					:add(path.line(38, 87, 23.25, 87))
					:constantHeading(180)
					:add(path.line(23.25, 87, 23.25, 78.0))
					:constantHeading(180)
					:build()
				),
				WaitForFirstAction.new(
					PathAction.new(
						path.chain()
						:add(path.line(23.25, 78.0, 16.50, 78.0))
						:constantHeading(180)
						:build()
					),
					Delay.new(2.0)
				),
				Delay.new(1.0),
				PathAction.new(
					path.chain()
					:add(path.line(17.50, 77.50, 54, 90))
					:constantHeading(180)
					:build()
				),
				RobotActions.Shoot.new(1)
			)
		end
	},
	line2 = {
		noGate = function ()
			return SeqAction.newl(
				"line2",
				PathAction.new(
					path.chain()
					:add(path.line(54, 90, 38, 60))
					:constantHeading(180)
					:build()
				),
				PathAction.new(
					path.chain()
					:add(path.line(38, 60, 21.25, 60))
					:constantHeading(180)
					:add(path.line(21.25, 60, 54, 90))
					:constantHeading(180)
					:build()
				),
				RobotActions.Shoot.new(1)
			)
		end,
		gate = function ()
			return SeqAction.newl(
				"line2",
				PathAction.new(
					path.chain()
					:add(path.line(54, 90, 38, 60))
					:constantHeading(180)
					:build()
				),
				PathAction.new(
					path.chain()
					:add(path.line(38, 60, 23, 60))
					:constantHeading(180)
					:add(path.line(23, 60, 24, 71))
					:constantHeading(180)
					:build()
				),
				WaitForFirstAction.new(
					PathAction.new(
						path.chain()
						:add(path.line(24, 71, 17, 71))
						:constantHeading(180)
						:build()
					),
					Delay.new(2.0)
				),
				PathAction.new(
					path.chain()
					:add(path.line(24, 60, 54, 90))
					:constantHeading(180)
					:build()
				),
				RobotActions.Shoot.new(1)
			)
		end
	},
	line3 = function ()
		return SeqAction.newl(
			"line3",
			PathAction.new(
				path.chain()
				:add(path.line(54, 90, 40, 39))
				:constantHeading(180)
				:build()
			),
			PathAction.new(
				path.chain()
				:add(path.line(40, 38, 20, 39))
				:constantHeading(180)
				:add(path.line(20, 38, 54, 90))
				:constantHeading(180)
				:build()
			),
			RobotActions.Shoot.new(1)
		)
	end,
	line4 = function ()
		return SeqAction.newl(
			"line4",
			PathAction.new(
				path.chain()
				:add(path.curve4(54, 90, 36, 84, 8, 48, 8, 40))
				:linearHeading(180, 270)
				:add(path.line(8, 40, 8, 12))
				:constantHeading(270)
				:build()
			),
			RobotActions.IntakeStop.new(),
			PathAction.new(
				path.chain()
				:add(path.curve3(10, 8, 24, 72, 54, 90))
				:linearHeading(270, 180)
				:build()
			),
			RobotActions.Shoot.new(1)
		)
	end,
	park = function ()
		return PathAction.new(
			path.chain()
			:add(path.line(54, 90, 48, 71))
			:constantHeading(180)
			:build()
		)
	end
};

loadOpmodeConfigs("blueFront", config);