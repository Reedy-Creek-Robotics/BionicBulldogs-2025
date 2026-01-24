require("opmode.auto.autoBase");

---@type AutoPaths
local config = {
	start = { x = -33, y = 136, z = 0 },
	turretTarget = { x = -6, y = 138 },
	preload = function ()
		return SeqAction.newl(
			"preload",
			RobotActions.ShooterStart.new(840),
			PathAction.new(
				path.chain()
				:add(path.line(-32.00, 136.00, -54.00, 90.00))
				:constantHeading(0)
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
					:add(path.line(-54.00, 90.00, -38.00, 84.00))
					:constantHeading(0)
					:add(path.line(-38.00, 84.00, -18.00, 84.00))
					:constantHeading(0)
					:build()
				),
				PathAction.new(
					path.chain()
					:add(path.line(-24.00, 84.00, -54.00, 90.00))
					:constantHeading(0)
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
					:add(path.line(-54.00, 90.00, -38.00, 87.00))
					:constantHeading(0)
					:add(path.line(-38.00, 87.00, -23.25, 87.00))
					:constantHeading(0)
					:add(path.line(-23.25, 87.00, -23.25, 77.50))
					:constantHeading(0)
					:build()
				),
				WaitForFirstAction.new(
					PathAction.new(
						path.chain()
						:add(path.line(-23.25, 77.50, -17.50, 77.60))
						:constantHeading(0)
						:build()
					),
					Delay.new(2.0)
				),
				Delay.new(1.0),
				PathAction.new(
					path.chain()
					:add(path.line(-17.50, 77.50, -54.00, 90.00))
					:constantHeading(0)
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
					:add(path.line(-54.00, 90.00, -38.00, 60.00))
					:constantHeading(0)
					:build()
				),
				PathAction.new(
					path.chain()
					:add(path.line(-38.00, 60.00, -21.25, 60.00))
					:constantHeading(0)
					:add(path.line(-21.25, 60.00, -54.00, 90.00))
					:constantHeading(0)
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
					:add(path.line(-54.00, 90.00, -38.00, 60.00))
					:constantHeading(0)
					:build()
				),
				PathAction.new(
					path.chain()
					:add(path.line(-38.00, 60.00, -24.00, 60.00))
					:constantHeading(0)
					:add(path.line(-24.00, 60.00, -24.00, 71.00))
					:constantHeading(0)
					:build()
				),
				WaitForFirstAction.new(
					PathAction.new(
						path.chain()
						:add(path.line(-24.00, 71.00, -15.00, 71.00))
						:constantHeading(0)
						:build()
					),
					Delay.new(2.0)
				),
				PathAction.new(
					path.chain()
					:add(path.line(-24.00, 60.00, -54.00, 90.00))
					:constantHeading(0)
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
				:add(path.line(-54.00, 90.00, -42.00, 39.00))
				:constantHeading(0)
				:build()
			),
			PathAction.new(
				path.chain()
				:add(path.line(-42.00, 38.00, -20.00, 39.00))
				:constantHeading(0)
				:add(path.line(-20.00, 38.00, -54.00, 90.00))
				:constantHeading(0)
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
				:add(path.curve3(-54.00, 90.00, -54, 12, -12, 12))
				:constantHeading(0)
				:build()
			),
			PathAction.new(
				path.chain()
				:add(path.curve3(-12, 12, -54, 12, -54, 90))
				:constantHeading(0)
				:build()
			),
			RobotActions.Shoot.new(1)
		)
	end,
	park = function ()
		return PathAction.new(
			path.chain()
			:add(path.line(-54.00, 90.00, -48.00, 71.00))
			:constantHeading(0)
			:build()
		)
	end
};

loadOpmodeConfigs("redFront", config);