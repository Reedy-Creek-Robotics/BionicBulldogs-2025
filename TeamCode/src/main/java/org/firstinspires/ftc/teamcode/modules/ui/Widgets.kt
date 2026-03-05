package com.minerkid08.telemetryui

class Button(id: String): UiElement(id)
{
	override fun render(target: Renderer, selected: Boolean)
	{
		addLine(target, selected, id);
	}

	override fun update(input: InputManager, selected: Boolean): Boolean
	{
		return selected && input.getRight();
	}
}

class IntInput(id: String, private val value: IntPtr, var step: Int): UiElement(id)
{
	private var v = 0;
	override fun render(target: Renderer, selected: Boolean)
	{
		addLine(target, selected, "$id: $v");
	}

	override fun update(input: InputManager, selected: Boolean): Boolean
	{
		if(!selected)
			return false;
		if(input.getRight())
		{
			value.value += step;
			v = value.value;
			return true;
		}
		if(input.getLeft())
		{
			value.value -= step;
			v = value.value;
			return true;
		}
		return false;
	}
}

class FloatInput(id: String, private val value: FloatPtr, var step: Float): UiElement(id)
{
	private var v = 0.0f;
	override fun render(target: Renderer, selected: Boolean)
	{
		addLine(target, selected, "$id: $v");
	}

	override fun update(input: InputManager, selected: Boolean): Boolean
	{
		if(!selected)
			return false;
		if(input.getRight())
		{
			value.value += step;
			v = value.value
			return true;
		}
		if(input.getLeft())
		{
			value.value -= step;
			v = value.value
			return true;
		}
		return false;
	}
}

class Checkbox(id: String, private val value: BoolPtr): UiElement(id)
{
	private var v = false;
	override fun render(target: Renderer, selected: Boolean)
	{
		addLine(target, selected, id + ": " + if(v) "[V]" else "[X]")
	}

	override fun update(input: InputManager, selected: Boolean): Boolean
	{
		if(!selected)
			return false;
		if(input.getRight())
		{
			value.value = !value.value;
			v = value.value;
			return true;
		}
		return false;
	}
}

class Dropdown(
	id: String,
	parent: UiContainer,
	private val value: IntPtr,
	private val listItems: List<String>
):
	UiContainer(id, parent)
{
	private var v = 0;
	init
	{
		for(item in listItems)
			items.add(Button(item));
	}

	override fun render(target: Renderer, selected: Boolean)
	{
		addLine(target, selected && selectedIndex == -1, "$id: ${listItems[v]}");
		if(opened)
		{
			target.addIndent();
			for((i, elem) in items.withIndex())
				elem.render(target, i == selectedIndex && selected);
			target.removeIndent();
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
			return false;
		}
		for((i, item) in items.withIndex())
		{
			if(item.update(input, selectedIndex == i))
			{
				value.value = i;
				v = value.value;
				close();
				return true;
			}
		}

		return false;
	}

	override fun scrollUp(): Boolean
	{
		if(!opened) return false;
		if(selectedIndex == -1)
			return false;
		selectedIndex--;
		return true;
	}

	override fun scrollDown(): Boolean
	{
		if(!opened) return false;
		if(selectedIndex == items.size - 1)
			return false;
		selectedIndex++;
		return true;
	}
}
