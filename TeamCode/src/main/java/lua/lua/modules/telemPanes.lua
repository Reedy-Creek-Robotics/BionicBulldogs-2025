require("modules.class")

---@class TelemPaneManager
---@field w integer
---@field h integer
---@field panes TelemPane[]
TelemPaneManager = {
	w = 15,
	h = 15,
	panes = {}
};

---@param label string?
function TelemPaneManager:reset(label)
	self.panes = {};
	table.insert(self.panes, TelemPane.new(0, 0, self.w, self.h, label or "main"));
end

function TelemPaneManager:sort()
	table.sort(self.panes, function (a, b)
	    return a.y < b.y
	end)
end

function TelemPaneManager:update()
	local filler = "|" .. string.rep(" ", self.w - 1) .. "|";
	for id, pane in ipairs(self.panes) do
		print(pane.header);
		for i = 1, pane.h - 1 do
			if (i > #pane.data) then
				print(filler);
			else
				print("|" .. pane.data[i] .. string.rep(" ", self.w - pane.data[i]:len() - 1) .. "|");
				if (not pane.autoReset) then
					pane.data[i] = nil;
				end
			end
		end
	end
end

---@class TelemPane
---@field x integer
---@field y integer
---@field w integer
---@field h integer
---@field header string
---@field data string[]
---@field autoReset boolean
TelemPane = {};

---@param x integer
---@param y integer
---@param w integer
---@param h integer
---@param label string
---@return TelemPane
function TelemPane.new(x, y, w, h, label)
	local p = new(TelemPane);
	p.x = x;
	p.y = y;
	p.w = w;
	p.h = h;
	local len = label:len();
	p.header = "-" .. label;
	for i = 1, w - len do
		p.header = p.header .. "-";
	end
	p.data = {};
	p.autoReset = true;
	return p;
end

---@param label string
---@param newHeight integer?
---@return TelemPane
function TelemPane:vsplit(label, newHeight)
	local y = self.y + newHeight;
	local h = self.h - newHeight;
	self.h = newHeight;

	local newPane = TelemPane.new(0, y, self.w, h, label);
	table.insert(TelemPaneManager.panes, newPane);
	TelemPaneManager:sort();
	return newPane;
end

---@param line string
function TelemPane:addLine(line)
	print("adding data " .. line);
	table.insert(self.data, line);
end

---@param label string
---@param value any
function TelemPane:addData(label, value)
	print("adding data " .. label .. ": " .. tostring(value));
	table.insert(self.data, label .. ": " .. tostring(value));
end