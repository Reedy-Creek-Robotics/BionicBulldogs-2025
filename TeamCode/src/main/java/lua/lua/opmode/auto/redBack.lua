require("opmode.auto.autoBase");

---@type AutoPaths
local config = {
	start = { x = -55.25, y = 7.75, z = 0 },
	turretTarget = { x = -1, y = 144 },
	preload = function ()
		return SeqAction.newl(
			"preload",
			CallbackAction.new(function ()
				shooter:setInBack(true);
			end
			),
			RobotActions.BeamBreakStopIntake.new(false),
			RobotActions.ShooterStart.new(860),
			RobotActions.Shoot.new(1),
			RobotActions.BeamBreakStopIntake.new(true),
			SleepAction.new(0.5)
		)
	end,
	line1 = {
		noGate = function ()
			return SeqAction.newl(
				"line1",
				PathAction.new(
					path.chain()
					:add(path.line(-55.50, 8, -42, 58))
					:constantHeading(0)
					:build()
				),
				PathAction.new(
					path.chain()
					:add(path.line(-42, 58, -14, 58))
					:constantHeading(0)
					:build()
				),
				PathAction.new(
					path.chain()
					:add(path.curve3(-16, 58, -60, 48, -60, 12))
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
					:add(path.line(-55.50, 8, -42, 58))
					:constantHeading(0)
					:add(path.line(-42, 56, -23, 56))
					:constantHeading(0)
					:add(path.line(-23, 56, -25, 65))
					:constantHeading(0)
					:build()
				),
				WaitForFirstAction.new(
					PathAction.new(
						path.chain()
						:add(path.line(-25, 65, -17, 65))
						:constantHeading(0)
						:build()
					),
					Delay.new(1.0)
				),
				Delay.new(0.5),
				PathAction.new(
					path.chain()
					:add(path.curve3(-17, 65, -60, 48, -60, 12))
					:constantHeading(0)
					:build()
				),
				Delay.new(0.5),
				RobotActions.Shoot.new(1)
			)
		end
	},
	line2 = {
		gate = function ()
			return SeqAction.newl("empty", SleepAction.new(00))
		end,
		noGate = function ()
			return SeqAction.newl(
				"line2",
				PathAction.new(
					path.chain()
					:add(path.line(-60, 12, -42, 36))
					:constantHeading(0)
					:add(path.line(-42, 36, -17, 36))
					:constantHeading(0)
					:add(path.line(-17, 36, -60, 12))
					:constantHeading(0)
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
				:add(path.line(-60, 12, -10, 8))
				:constantHeading(0)
				:build()
			),
			PathAction.new(
				path.chain()
				:add(path.line(-10, 8, -60, 12))
				:constantHeading(0)
				:build()
			),
			Delay.new(0.5),
			RobotActions.Shoot.new(1)
		)
	end,
	park = function ()
		return PathAction.new(
			path.chain()
			:add(path.line(-60, 12, -36, 12))
			:constantHeading(0)
			:build()
		)
	end
}

loadOpmodeConfigs("redBack", config);