require("opmode.auto.autoBase");

---@param num number
local function genPath(num)
	profileFileName = "redFront-" .. tostring(num);
	require("modules.telemetry");

  local preload = SeqAction.newl(
		"preload",
    RobotActions.ShooterStart.new(1400),
    PathAction.new(
        path.chain()
        :add(path.line(112.00, 136.00, 90.00, 90.00))
        :constantHeading(0.00)
        :build()
    ),
    RobotActions.Shoot.new(3)
  );
  local line1 = SeqAction.new(
    RobotActions.Intake.new(1.0),
    PathAction.new(
        path.chain()
        :add(path.line(90.00, 90.00, 106.00, 84.00))
        :constantHeading(0.00)
        :add(path.line(106.00, 84.00, 126.00, 84.00))
        :add(path.line(126.00, 84.00, 132.00, 72.00))
        :build()
    ),
    Delay.new(2.0),
    RobotActions.IntakeStop.new(),
    PathAction.new(
        path.chain()
        :add(path.line(132.00, 72.00, 90.00, 90.00))
        :build()
    ),
    RobotActions.Shoot.new(3)
  );
  local line2 = SeqAction.new(
    PathAction.new(
        path.chain()
        :add(path.line(90.00, 90.00, 106.00, 60.00))
        :build()
    ),
    RobotActions.Intake.new(1.0),
    PathAction.new(
        path.chain()
        :add(path.line(106.00, 60.00, 126.00, 60.00))
        :add(path.line(126.00, 60.00, 90.00, 90.00))
        :build()
    ),
    RobotActions.IntakeStop.new(),
    RobotActions.Shoot.new(3)
  );
  local line3 = SeqAction.new(
    PathAction.new(
        path.chain()
        :add(path.line(90.00, 90.00, 108.00, 36.00))
        :build()
    ),
    RobotActions.Intake.new(1.0),
    PathAction.new(
        path.chain()
        :add(path.line(108.00, 36.00, 126.00, 36.00))
        :add(path.line(126.00, 36.00, 90.00, 90.00))
        :build()
    ),
    RobotActions.IntakeStop.new(),
    RobotActions.Shoot.new(3)
  );
  local park = PathAction.new(
      path.chain()
      :add(path.line(90.00, 90.00, 84.00, 118.00))
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

loadOpmodes("redFront", genPath);