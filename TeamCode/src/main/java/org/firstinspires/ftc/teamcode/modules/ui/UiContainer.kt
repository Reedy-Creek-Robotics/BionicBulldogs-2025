package com.minerkid08.telemetryui

open class UiContainer(id: String, val parent: UiContainer?): UiElement(id)
{
	var selectedIndex = -1;
	protected var selectedId: String? = null;
	var itemCount = 0;
	val items = ArrayList<UiElement>();
	protected var opened = false;

	val renderItems = ArrayList<RenderItem>();
	var renderItemCount = 0;

	override fun render(target: Renderer, selected: Boolean)
	{
		addLine(target, selected && selectedIndex == -1, id);
		if(opened)
		{
			if(selectedIndex > -1 && items[selectedIndex].id != selectedId)
			{
				for(i in 0 until itemCount)
				{
					val item = items[i];
					if(item.id == selectedId)
					{
						selectedIndex = i;
						break;
					}
				}
				clampSelected();
			}
			var renderItemInd = 0;
			target.addIndent();
			for(i in 0 until itemCount)
			{
				while(renderItemInd < renderItemCount && renderItems[renderItemInd].after < i)
					renderItemInd++;
				val elem = items[i];
				elem.render(target, i == selectedIndex && selected);
				while(renderItemInd < renderItemCount && renderItems[renderItemInd].after == i)
				{
					val item = renderItems[renderItemInd];
					when(item.type)
					{
						RenderItemType.Text      ->
							target.addLine(item.arg!!, false);
						RenderItemType.Seperator ->
							target.addLine("------------", "--", false);
						RenderItemType.SameLine  ->
							target.sameLine();
					}
					renderItemInd++;
				}
			}
			target.removeIndent();
			itemCount = 0;
			renderItemCount = 0;
		}
	}

	override fun update(input: InputManager, selected: Boolean): Boolean
	{
		if(selectedIndex == -1 && selected)
		{
			if(input.getRight())
			{
				selectedIndex = -1;
				opened = !opened;
			}
		}

		return opened;
	}

	open fun scrollUp(): Boolean
	{
		if(!opened) return false;
		if(selectedIndex >= 0)
		{
			val item = items[selectedIndex];
			if(item is UiContainer)
			{
				if(item.scrollUp())
					return true;
			}
		}
		if(selectedIndex == -1)
			return false;
		selectedIndex--;
		if(selectedIndex != -1)
			selectedId = items[selectedIndex].id;
		return true;
	}

	open fun scrollDown(): Boolean
	{
		if(!opened) return false;
		if(selectedIndex >= 0)
		{
			val item = items[selectedIndex];
			if(item is UiContainer)
			{
				if(item.scrollDown())
					return true;
			}
		}
		if(selectedIndex == itemCount - 1)
			return false;
		selectedIndex++;
		selectedId = items[selectedIndex].id;
		return true;
	}

	fun clampSelected()
	{
		if(id == "" && selectedIndex < 0)
			selectedIndex = 0;
		if(selectedIndex < -1)
			selectedIndex = -1;
		if(selectedIndex >= itemCount)
			selectedIndex = itemCount - 1;
	}

	fun open()
	{
		selectedIndex = -1;
		opened = true;
	}

	fun close()
	{
		selectedIndex = -1;
		opened = false;
	}
}
