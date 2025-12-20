require("opmode.auto.autoBase");

---@type AutoPaths
local config = {
	start = {x = 55.5, y = 8, z = 180},
	turretTarget = {x = 6, y = 138},
	preload = SeqAction.newl(
		"preload",
		RobotActions.ShooterStart.new(1200),
		RobotActions.Shoot.new(4)
	),
	line1 = {
		noGate = SeqAction.newl(
		"line1",
		PathAction.new(
			path.chain()
			:add(path.line(55.50, 8.00, 42.00, 58.00))
			:constantHeading(180)
			:build()
		),
		PathAction.new(
			path.chain()
			:add(path.line(42.00, 58.00, 18.00, 58.00))
			:constantHeading(180)
			:build()
		),
		PathAction.new(
			path.chain()
			:add(path.curve3(18.00, 58.00, 60.00, 48.00, 60.00, 12.00))
			:constantHeading(180)
			:build()
		),
		RobotActions.Shoot.new(3)
	),
		gate = SeqAction.newl(
		"line1",
		PathAction.new(
			path.chain()
			:add(path.line(55.50, 8.00, 42.00, 58.00))
			:constantHeading(180)
			:build()
		),
		RobotActions.Intake.new(1.0),
		PathAction.new(
			path.chain()
			:add(path.line(42.00, 58.00, 18.00, 58.00))
			:constantHeading(180)
			:add(path.line(18.00, 58.00, 18.00, 71.00))
			:constantHeading(180)
			:build()
		),
		WaitForFirstAction.new(
			PathAction.new(
				path.chain()
				:add(path.line(18.00, 71.00, 15.00, 71.00))
				:constantHeading(180.00)
				:build()
			),
			Delay.new(2.0)
		),
		Delay.new(2.0),
		PathAction.new(
			path.chain()
			:add(path.curve3(15.00, 71.00, 60.00, 48.00, 60.00, 12.00))
			:constantHeading(180)
			:build()
		),
		RobotActions.IntakeStop.new(),
		RobotActions.Shoot.new(3)
	)
},
	line2 = {
		gate = SeqAction.newl("empty", SleepAction.new(10000)),
		noGate = SeqAction.newl(
		"line2",
		PathAction.new(
			path.chain()
			:add(path.line(60.00, 12.00, 42.00, 36.00))
			:constantHeading(180)
			:build()
		),
		RobotActions.Intake.new(1.0),
		PathAction.new(
			path.chain()
			:add(path.line(42.00, 36.00, 18.00, 36.00))
			:constantHeading(180)
			:add(path.line(18.00, 36.00, 60.00, 12.00))
			:constantHeading(180)
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
			:add(path.line(60.00, 12.00, 12.00, 12.00))
			:constantHeading(180)
			:build()
		),
		RobotActions.Intake.new(1.0),
		PathAction.new(
			path.chain()
			:add(path.line(12.00, 12.00, 60.00, 12.00))
			:constantHeading(180)
			:build()
		),
		RobotActions.IntakeStop.new(),
		RobotActions.Shoot.new(3)
	),
	park = PathAction.new(
		path.chain()
		:add(path.line(60.00, 12.00, 60.00, 42.00))
		:constantHeading(180)
		:build()
	)
}

loadOpmodeConfigs("blueBack", config);