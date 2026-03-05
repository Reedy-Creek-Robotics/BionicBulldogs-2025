package com.minerkid08.telemetryui

class Renderer(private val output: Output, private val screenHeight: Int)
{
	private var indentCount = -1;
	private var sameLine = false;
	private var line = "";

	private var currentLine = 0;
	private var selectedLine = 0;
	private var startLine = 0;
	private val lines = ArrayList<String>();

	fun addLine(line: String, selected: Boolean)
	{
		addLine(line, "  ", selected);
	}

	fun addLine(line: String, indent: String, selected: Boolean)
	{
		if (indentCount == -1)
			return;
		if (sameLine)
			this.line += "  $line";
		else
		{
			if (this.line.isNotEmpty())
			{
				if (lines.size <= currentLine)
					lines.add(this.line);
				else
					lines[currentLine] = this.line;
				currentLine++;
			}
			var str = "";
			for (i in 0 until indentCount)
				str += indent;
			this.line = str + line;
		}
		sameLine = false;
		if (selected)
			selectedLine = currentLine;
	}

	fun endFrame()
	{
		indentCount = -1;
		sameLine = false;
		if (line.isNotEmpty())
		{
			if (lines.size <= currentLine)
				lines.add(this.line);
			else
				lines[currentLine] = this.line;
		}
		line = "";
		currentLine++;

		if (selectedLine < startLine)
			startLine = selectedLine;
		if (selectedLine >= startLine + screenHeight)
			startLine = selectedLine - screenHeight + 1;

		for (i in startLine until startLine + screenHeight)
		{
			if(i >= lines.size || i >= currentLine)
				break;
			output.addLine(lines[i]);
		}

		output.endFrame();
		currentLine = 0;
	}

	fun addIndent()
	{
		indentCount++;
	}

	fun removeIndent()
	{
		indentCount--;
	}

	fun sameLine()
	{
		sameLine = true;
	}
}