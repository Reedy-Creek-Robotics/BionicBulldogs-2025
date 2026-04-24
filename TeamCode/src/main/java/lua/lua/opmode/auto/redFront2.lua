require("opmode.auto.autoBase");

---@type AutoPaths
local config = {
	start = { x = -33, y = 136, z = 0 },
	turretTarget = { x = -6, y = 138 },
	preload = function ()
		return SeqAction.newl(
			"preload",
			RobotActions.ShooterStart.new(680),
			PathAction.new(
				path.chain()
				:add(path.line(-32, 136, -52, 83))
				:constantHeading(0)
				:build()
			),
			SleepAction.new(999),
			RobotActions.Shoot.new(1),
			Delay.new(0.1)
		)
	end,
	line1 = {
		noGate = function ()
			return SeqAction.newl(
				"line1",
				PathAction.new(
					path.chain()
					:add(path.line(-52, 83, -15, 83))
					:constantHeading(0)
					:add(path.line(-15, 83, -52, 83))
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
					:add(path.line(-52, 83, -15, 83))
					:constantHeading(0)
					:build()
				),
				PathAction.new(
					path.chain()
					:add(path.line(-15, 83, -24, 71))
					:constantHeading(0)
					:add(path.line(-24, 71, -15, 71))
					:constantHeading(0)
					:build()
				),
				PathAction.new(
					path.chain()
					:add(path.line(-15, 71, -52, 83))
					:constantHeading(0)
					:build()
				),
				SleepAction.new(999),
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
					:add(path.line(-54, 90, -38, 62))
					:constantHeading(0)
					:add(path.line(-38, 62, -18.25, 62))
					:constantHeading(0)
					:add(path.line(-18.25, 62, -54, 90))
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
					:add(path.line(-54, 90, -38, 60))
					:constantHeading(0)
					:build()
				),
				PathAction.new(
					path.chain()
					:add(path.line(-38, 60, -23, 60))
					:constantHeading(0)
					:add(path.line(-23, 60, -24, 71))
					:constantHeading(0)
					:build()
				),
				WaitForFirstAction.new(
					PathAction.new(
						path.chain()
						:add(path.line(-24, 71, -17, 71))
						:constantHeading(0)
						:build()
					),
					Delay.new(2.0)
				),
				PathAction.new(
					path.chain()
					:add(path.line(-24, 60, -54, 90))
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
				:add(path.line(-54, 90, -40, 40))
				:constantHeading(0)
				:add(path.line(-40, 40, -13, 40))
				:constantHeading(0)
				:add(path.line(-13, 40, -54, 90))
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
				:add(path.curve4(-54, 90, -36, 84, -8, 48, -8, 40))
				:linearHeading(0, -90)
				:add(path.line(-8, 40, -8, 12))
				:constantHeading(-90)
				:build()
			),
			RobotActions.IntakeStop.new(),
			PathAction.new(
				path.chain()
				:add(path.curve3(-10, 8, -24, 72, -54, 90))
				:linearHeading(-90, 0)
				:build()
			),
			RobotActions.Shoot.new(1)
		)
	end,
	cycle = function ()
		return SeqAction.newl(
			"cycle",
			PathAction.new(
				path.chain()
				:add(path.curve3(-54, 90, -54, 63.5, -11.25, 63.5))
				:linearHeading(0, 30.5)
				:build()
			),
			Delay.new(2.0),
			RobotActions.IntakeStop.new(),
			PathAction.new(
				path.chain()
				:add(path.curve3(-11.25, 63.5, -54, 63.5, -54.00, 90.00))
				:linearHeading(30.5, 0)
				:build()
			),
			RobotActions.Shoot.new(1)
		);
	end,
	park = function ()
		return PathAction.new(
			path.chain()
			:add(path.line(-54, 90, -48, 71))
			:constantHeading(0)
			:build()
		)
	end
};

--loadOpmodeConfigs("2redFront", config);