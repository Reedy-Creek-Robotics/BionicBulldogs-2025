genPathFuncs = {
	---@param config AutoPaths
	---@return Action
	[3] = function (config)
		return SeqAction.new(config.preload(), config.park(), SleepAction.new(2));
	end,
	---@param config AutoPaths
	---@return Action
	[6] = function (config)
		return SeqAction.new(config.preload(), config.line1.gate(), config.park(), SleepAction.new(2));
	end,
	---@param config AutoPaths
	---@return Action
	[9] = function (config)
		return SeqAction.new(config.preload(), config.line1.gate(), config.line2.noGate(), config.park(), SleepAction.new(2));
	end,
	---@param config AutoPaths
	---@return Action
	[12] = function (config)
		return SeqAction.new(config.preload(), config.line1.gate(), config.line2.noGate(), config.line3(), config.park(),
			SleepAction.new(2));
	end,
	---@param config AutoPaths
	---@return Action
	[15] = function (config)
		return SeqAction.new(config.preload(), config.line1.noGate(), config.line2.gate(), config.line3(), config.line4(),
			config.park(), SleepAction.new(2));
	end,
	cycle = function (config)
		return SeqAction.new(config.preload(), config.line2.noGate(), config.cycle(), config.cycle(), IfAction.new(function(et) return et < 30 - (6.6 + 4 + 1.07) end, config.cycle()), config.line1.noGate(),
			config.park(), SleepAction.new(2));
	end,
	partner = function (config)
		if (config.start.y >= 24) then
			return SeqAction.new(config.preload(), config.line1.gate(), config.line2.gate(), config.park(), SleepAction.new(2));
		end
		return SeqAction.new(config.preload(), config.line2.noGate(), config.line3(), config.line3(), config.line3(), config.line3(),
			config.park(), SleepAction.new(2));
	end
};