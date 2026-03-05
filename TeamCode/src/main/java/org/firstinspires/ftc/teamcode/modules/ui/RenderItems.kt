package com.minerkid08.telemetryui

enum class RenderItemType
{
	Text,
	Seperator,
	SameLine
}

data class RenderItem(var after: Int, var type: RenderItemType, var arg: String? = null)