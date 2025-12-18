require("opmode.auto.autoBase");

---@param num number
local function genPath(num)
	profileFileName = "redBack-" .. tostring(num);
	require("modules.telemetry");

	follower.setPosition(-55.5, 8, 0);

	local preload = SeqAction.newl(
		"preload",
		RobotActions.ShooterStart.new(1200),
		RobotActions.TurretTurnTo.new(70),
		RobotActions.Shoot.new(4)
	);
	local line1 = SeqAction.newl(
		"line1",
		PathAction.new(
			path.chain()
			:add(path.line(-55.50, 8.00, -42.00, 58.00))
			:constantHeading(0)
			:build()
		),
		RobotActions.Intake.new(1.0),
		PathAction.new(
			path.chain()
			:add(path.line(-42.00, 58.00, -18.00, 58.00))
			:constantHeading(0)
			:add(path.line(-18.00, 58.00, -18.00, 71.00))
			:constantHeading(0)
			:build()
		),
		WaitForFirstAction.new(
			PathAction.new(
				path.chain()
				:add(path.line(-18.00, 71.00, -15.00, 71.00))
				:constantHeading(0)
				:build()
			),
			Delay.new(2.0)
		),
		Delay.new(2.0),
		PathAction.new(
			path.chain()
			:add(path.curve3(-15.00, 71.00, -60.00, 48.00, -60.00, 12.00))
			:constantHeading(0)
			:build()
		),
		RobotActions.IntakeStop.new(),
		RobotActions.Shoot.new(3)
	);
	local line2 = SeqAction.newl(
		"line2",
		PathAction.new(
			path.chain()
			:add(path.line(-60.00, 12.00, -42.00, 36.00))
			:constantHeading(0)
			:build()
		),
		RobotActions.Intake.new(1.0),
		PathAction.new(
			path.chain()
			:add(path.line(-42.00, 36.00, -18.00, 36.00))
			:constantHeading(0)
			:add(path.line(-18.00, 36.00, -60.00, 12.00))
			:constantHeading(0)
			:build()
		),
		RobotActions.IntakeStop.new(),
		RobotActions.Shoot.new(3)
	);
	local line3 = SeqAction.newl(
		"line3",
		PathAction.new(
			path.chain()
			:add(path.line(-60.00, 12.00, -12.00, 12.00))
			:constantHeading(0)
			:build()
		),
		RobotActions.Intake.new(1.0),
		PathAction.new(
			path.chain()
			:add(path.line(-12.00, 12.00, -60.00, 12.00))
			:constantHeading(0)
			:build()
		),
		RobotActions.IntakeStop.new(),
		RobotActions.Shoot.new(3)
	);
	local park = PathAction.new(
		path.chain()
		:add(path.line(-60.00, 12.00, -60.00, 42.00))
		:constantHeading(0)
		:build()
	);

	if (num == 0) then
		action = SeqAction.new(preload, park);
	elseif (num == 1) then
		action = SeqAction.new(preload, line1, park);
	elseif (num == 2) then
		action = SeqAction.new(preload, line1, line2, park);
	elseif (num == 3) then
		action = SeqAction.new(preload, line1, line2, line3, park);
	end
end

loadOpmodes("redBack", genPath);