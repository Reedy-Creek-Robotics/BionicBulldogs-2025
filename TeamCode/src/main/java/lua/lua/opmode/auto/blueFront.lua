require("opmode.auto.autoBase");

---@type AutoPaths
local config = {
	start = { x = 32, y = 136, z = 180 },
	preload = SeqAction.newl(
		"preload",
		RobotActions.ShooterStart.new(1400),
		PathAction.new(
			path.chain()
			:add(path.line(32.00, 136.00, 54.00, 90.00))
			:constantHeading(180.00)
			:build()
		),
		RobotActions.Shoot.new(3)
	),
	line1 = {
		noGate = SeqAction.newl(
			"line1",
			RobotActions.Intake.new(1.0),
			PathAction.new(
				path.chain()
				:add(path.line(54.00, 90.00, 38.00, 84.00))
				:constantHeading(180.00)
				:add(path.line(38.00, 84.00, 24.00, 84.00))
				:constantHeading(180.00)
				:build()
			),
			RobotActions.IntakeStop.new(),
			PathAction.new(
				path.chain()
				:add(path.line(24.00, 84.00, 54.00, 90.00))
				:constantHeading(180.00)
				:build()
			),
			RobotActions.Shoot.new(3)
		),
		gate = SeqAction.newl(
			"line1",
			RobotActions.Intake.new(1.0),
			PathAction.new(
				path.chain()
				:add(path.line(54.00, 90.00, 38.00, 84.00))
				:constantHeading(180.00)
				:add(path.line(38.00, 84.00, 24.00, 84.00))
				:constantHeading(180.00)
				:add(path.line(24.00, 84.00, 24.00, 72.00))
				:constantHeading(180.00)
				:build()
			),
			WaitForFirstAction.new(
				PathAction.new(
					path.chain()
					:add(path.line(24.00, 72.00, 17.00, 72.00))
					:constantHeading(180.00)
					:build()
				),
				Delay.new(2.0)
			),
			Delay.new(2.0),
			RobotActions.IntakeStop.new(),
			PathAction.new(
				path.chain()
				:add(path.line(17.00, 72.00, 54.00, 90.00))
				:constantHeading(180.00)
				:build()
			),
			RobotActions.Shoot.new(3)
		)
	},
	line2 = {
		noGate = SeqAction.newl(
			"line2",
			PathAction.new(
				path.chain()
				:add(path.line(54.00, 90.00, 38.00, 60.00))
				:constantHeading(180.00)
				:build()
			),
			RobotActions.Intake.new(1.0),
			PathAction.new(
				path.chain()
				:add(path.line(38.00, 60.00, 24.00, 60.00))
				:constantHeading(180.00)
				:add(path.line(24.00, 60.00, 54.00, 90.00))
				:constantHeading(180.00)
				:build()
			),
			RobotActions.IntakeStop.new(),
			RobotActions.Shoot.new(3)
		),
		gate = SeqAction.newl(
			"line2",
			PathAction.new(
				path.chain()
				:add(path.line(54.00, 90.00, 38.00, 60.00))
				:constantHeading(180.00)
				:build()
			),
			RobotActions.Intake.new(1.0),
			PathAction.new(
				path.chain()
				:add(path.line(38.00, 60.00, 24.00, 60.00))
				:constantHeading(180.00)
				:add(path.line(24.00, 60.00, 24.00, 71.00))
				:constantHeading(180)
				:build()
			),
			WaitForFirstAction.new(
				PathAction.new(
					path.chain()
					:add(path.line(24.00, 71.00, 15.00, 71.00))
					:constantHeading(180.00)
					:build()
				),
				Delay.new(2.0)
			),
			PathAction.new(
				path.chain()
				:add(path.line(24.00, 60.00, 54.00, 90.00))
				:constantHeading(180.00)
				:build()
			),
			RobotActions.IntakeStop.new(),
			RobotActions.Shoot.new(3)
		)
	},
	line3 = SeqAction.newl(
		"line3",
		PathAction.new(
			path.chain()
			:add(path.line(54.00, 90.00, 36.00, 36.00))
			:constantHeading(180.00)
			:build()
		),
		RobotActions.Intake.new(1.0),
		PathAction.new(
			path.chain()
			:add(path.line(36.00, 36.00, 24.00, 36.00))
			:constantHeading(180.00)
			:add(path.line(24.00, 36.00, 54.00, 90.00))
			:constantHeading(180.00)
			:build()
		),
		RobotActions.IntakeStop.new(),
		RobotActions.Shoot.new(3)
	),
	line4 = SeqAction.newl(
		"line4",
		PathAction.new(
			path.chain()
			:add(path.curve3(54.00, 90.00, 54, 12, 12, 12))
			:constantHeading(180.00)
			:build()
		),
		RobotActions.Intake.new(1.0),
		PathAction.new(
			path.chain()
			:add(path.curve3(12, 12, 54, 12, 54, 90))
			:constantHeading(180.00)
			:build()
		),
		RobotActions.IntakeStop.new(),
		RobotActions.Shoot.new(3)
	),
	park = PathAction.new(
		path.chain()
		:add(path.line(54.00, 90.00, 60.00, 118.00))
		:constantHeading(180.00)
		:build()
	)
};

loadOpmodeConfigs("blueFront", config);