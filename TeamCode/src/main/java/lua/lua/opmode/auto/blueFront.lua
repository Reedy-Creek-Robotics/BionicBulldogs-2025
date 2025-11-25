require("opmode.auto.autoBase");

---@param num number
local function genPath(num)
	profileFileName = "blueFront-" .. tostring(num);
	require("modules.telemetry");

	local preload = SeqAction.newl(
		"preload",
		RobotActions.ShooterStart.new(1400),
		PathAction.new(
			path.chain()
			:add(path.line(32.00, 136.00, 54.00, 90.00))
			:constantHeading(180.00)
			:build()
		),
		RobotActions.Shoot.new(3)
	);
	local line1 = SeqAction.newl(
		"line1",
		RobotActions.Intake.new(1.0),
		PathAction.new(
			path.chain()
			:add(path.line(54.00, 90.00, 38.00, 84.00))
			:constantHeading(180.00)
			:add(path.line(38.00, 84.00, 18.00, 84.00))
			:add(path.line(18.00, 84.00, 12.00, 72.00))
			:build()
		),
		Delay.new(2.0),
		RobotActions.IntakeStop.new(),
		PathAction.new(
			path.chain()
			:add(path.line(12.00, 72.00, 54.00, 90.00))
			:build()
		),
		RobotActions.Shoot.new(3)
	);
	local line2 = SeqAction.newl(
		"line2",
		PathAction.new(
			path.chain()
			:add(path.line(54.00, 90.00, 38.00, 60.00))
			:build()
		),
		RobotActions.Intake.new(1.0),
		PathAction.new(
			path.chain()
			:add(path.line(38.00, 60.00, 18.00, 60.00))
			:add(path.line(18.00, 60.00, 54.00, 90.00))
			:build()
		),
		RobotActions.IntakeStop.new(),
		RobotActions.Shoot.new(3)
	);
	local line3 = SeqAction.newl(
		"line3",
		PathAction.new(
			path.chain()
			:add(path.line(54.00, 90.00, 36.00, 36.00))
			:build()
		),
		RobotActions.Intake.new(1.0),
		PathAction.new(
			path.chain()
			:add(path.line(36.00, 36.00, 18.00, 36.00))
			:add(path.line(18.00, 36.00, 54.00, 90.00))
			:build()
		),
		RobotActions.IntakeStop.new(),
		RobotActions.Shoot.new(3)
	);
	local park = PathAction.new(
		path.chain()
		:add(path.line(54.00, 90.00, 60.00, 118.00))
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

loadOpmodes("blueFront", genPath);