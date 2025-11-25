require("opmode.auto.autoBase");

---@param num number
local function genPath(num)
	profileFileName = "redBack-" .. tostring(num);
	require("modules.telemetry");

  local preload = SeqAction.newl(
		"preload",
    RobotActions.ShooterStart.new(1400),
    RobotActions.Shoot.new(3)
  );
  local line1 = SeqAction.newl(
		"line1",
    PathAction.new(
        path.chain()
        :add(path.line(88.00, 8.00, 102.00, 60.00))
        :build()
    ),
    RobotActions.Intake.new(1.0),
    PathAction.new(
        path.chain()
        :add(path.line(102.00, 60.00, 126.00, 60.00))
        :add(path.line(126.00, 60.00, 129.00, 72.00))
        :build()
    ),
    Delay.new(2.0),
    PathAction.new(
        path.chain()
        :add(path.curve3(129.00, 72.00, 84.00, 48.00, 84.00, 12.00))
        :build()
    ),
    RobotActions.IntakeStop.new(),
    RobotActions.Shoot.new(3)
  );
  local line2 = SeqAction.newl(
		"line2",
    PathAction.new(
        path.chain()
        :add(path.line(84.00, 12.00, 102.00, 36.00))
        :build()
    ),
    RobotActions.Intake.new(1.0),
    PathAction.new(
        path.chain()
        :add(path.line(102.00, 36.00, 126.00, 36.00))
        :add(path.line(126.00, 36.00, 84.00, 12.00))
        :build()
    ),
    RobotActions.IntakeStop.new(),
		RobotActions.Shoot.new(3)
  );
  local line3 = SeqAction.newl(
		"line3",
    PathAction.new(
        path.chain()
        :add(path.line(84.00, 12.00, 132.00, 12.00))
        :build()
    ),
    RobotActions.Intake.new(1.0),
    PathAction.new(
        path.chain()
        :add(path.line(132.00, 12.00, 84.00, 12.00))
        :build()
    ),
    RobotActions.IntakeStop.new(),
    RobotActions.Shoot.new(3)
  );
  local park = PathAction.new(
      path.chain()
      :add(path.line(84.00, 12.00, 84.00, 42.00))
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