require("opmode.auto.autoBase");

---@type AutoPaths
local config = {
	start = { x = 55.25, y = 7.75, z = 180 },
	turretTarget = { x = 0, y = 144 },
	preload = function ()
		intake.speed = 0.85;
		return SeqAction.newl(
			"preload",
			RobotActions.ShooterStart.new(1120),
			RobotActions.Shoot.new(1),
			SleepAction.new(0.5)
		)
	end,
	line1 = {
		noGate = function ()
			return SeqAction.newl(
				"line1",
				PathAction.new(
					path.chain()
					:add(path.line(55.50, 8.00, 42.00, 58.00))
					:constantHeading(180)
					:build()
				),
				PathAction.new(
					path.chain()
					:add(path.line(42.00, 58.00, 16.00, 58.00))
					:constantHeading(180)
					:build()
				),
				PathAction.new(
					path.chain()
					:add(path.curve3(16.00, 58.00, 60.00, 48.00, 60.00, 12.00))
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
					:add(path.line(55.50, 8.00, 42.00, 58.00))
					:constantHeading(180)
					:build()
				),
				PathAction.new(
					path.chain()
					:add(path.line(42.00, 56.00, 23.00, 56.00))
					:constantHeading(180)
					:add(path.line(23.00, 56.00, 25.00, 65.00))
					:constantHeading(180)
					:build()
				),
				WaitForFirstAction.new(
					PathAction.new(
						path.chain()
						:add(path.line(25.00, 65.00, 17.00, 65.00))
						:constantHeading(180.00)
						:build()
					),
					Delay.new(1.0)
				),
				Delay.new(0.5),
				PathAction.new(
					path.chain()
					:add(path.curve3(17.00, 65.00, 60.00, 48.00, 60.00, 12.00))
					:constantHeading(180)
					:build()
				),
				Delay.new(0.5),
				RobotActions.Shoot.new(1)
			)
		end
	},
	line2 = {
		gate = function ()
			return SeqAction.newl("empty", SleepAction.new(10000))
		end,
		noGate = function ()
			return SeqAction.newl(
				"line2",
				PathAction.new(
					path.chain()
					:add(path.line(60.00, 12.00, 42.00, 36.00))
					:constantHeading(180)
					:build()
				),
				PathAction.new(
					path.chain()
					:add(path.line(42.00, 36.00, 17.00, 36.00))
					:constantHeading(180)
					:add(path.line(17.00, 36.00, 60.00, 12.00))
					:constantHeading(180)
					:build()
				),
				Delay.new(0.5),
				RobotActions.Shoot.new(1)
			)
		end
	},
	line3 = function ()
		return SeqAction.newl(
			"line3",
			PathAction.new(
				path.chain()
				:add(path.line(60.00, 12.00, 12.00, 8.00))
				:constantHeading(180)
				:build(),
				0.5
			),
			RobotActions.IntakeStop.new(),
			PathAction.new(
				path.chain()
				:add(path.line(12.00, 8.00, 60.00, 12.00))
				:constantHeading(180)
				:build()
			),
			Delay.new(0.5),
			RobotActions.Shoot.new(1)
		)
	end,
	park = function ()
		return PathAction.new(
			path.chain()
			:add(path.line(60.00, 12.00, 60.00, 42.00))
			:constantHeading(180)
			:build()
		)
	end
}

loadOpmodeConfigs("blueBack", config);