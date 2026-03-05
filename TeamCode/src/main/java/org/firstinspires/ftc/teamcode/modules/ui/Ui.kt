package com.minerkid08.telemetryui

interface InputManager
{
	fun getUp(): Boolean;
	fun getDown(): Boolean;
	fun getLeft(): Boolean;
	fun getRight(): Boolean;
}

interface Output
{
	fun addLine(line: String);
	fun endFrame();
}

abstract class UiElement(val id: String)
{
	abstract fun render(target: Renderer, selected: Boolean);
	abstract fun update(input: InputManager, selected: Boolean): Boolean;
}

fun addLine(target: Renderer, selected: Boolean, text: String)
{
	if(selected)
		target.addLine("--$text", true);
	else
		target.addLine("  $text", false);
}


class Ui(private val input: InputManager, output: Output, screenHeight: Int)
{
	private val renderer = Renderer(output, screenHeight);
	private val mainContainer = UiContainer("", null);
	private var curContainer: UiContainer = mainContainer;

	init
	{
		mainContainer.open();
	}

	fun update()
	{
		if(curContainer != mainContainer)
			error("did not call pop enough");
		if(input.getUp())
			mainContainer.scrollUp();
		if(input.getDown())
			mainContainer.scrollDown();
		mainContainer.clampSelected();
		mainContainer.render(renderer, true);
		renderer.endFrame();
	}

	fun button(label: String): Boolean
	{
		addItem<Button>(curContainer, label, {Button(label)});
		val i = curContainer.itemCount - 1;
		val items = curContainer.items;
		return items[i].update(input, curContainer.selectedIndex == i);
	}

	fun intInput(label: String, value: IntPtr, step: Int): Boolean
	{
		addItem<IntInput>(curContainer, label, {IntInput(label, value, step)});
		val i = curContainer.itemCount - 1;
		val items = curContainer.items;
		(items[i] as IntInput).step = step;
		return items[i].update(input, curContainer.selectedIndex == i);
	}

	fun floatInput(label: String, value: FloatPtr, step: Float): Boolean
	{
		addItem<FloatInput>(curContainer, label, {FloatInput(label, value, step)});
		val i = curContainer.itemCount - 1;
		val items = curContainer.items;
		(items[i] as FloatInput).step = step;
		return items[i].update(input, curContainer.selectedIndex == i);
	}

	fun checkbox(label: String, value: BoolPtr): Boolean
	{
		addItem<Checkbox>(curContainer, label, {Checkbox(label, value)});
		val i = curContainer.itemCount - 1;
		val items = curContainer.items;
		return items[i].update(input, curContainer.selectedIndex == i);
	}

	fun dropdown(label: String, value: IntPtr, listItems: List<String>): Boolean
	{
		addItem<Dropdown>(curContainer, label, {Dropdown(label, curContainer, value, listItems)});
		val i = curContainer.itemCount - 1;
		val items = curContainer.items;
		return items[i].update(input, curContainer.selectedIndex == i);
	}

	fun treeNode(label: String, startOpen: Boolean = false): Boolean
	{
		val i = curContainer.itemCount;
		val items = curContainer.items;
		if(items.size <= i)
		{
			val item = UiContainer(label, curContainer)
			items.add(item);
			if(startOpen)
				item.open();
		}
		else if(items[i] !is UiContainer)
		{
			val item = UiContainer(label, curContainer)
			items[i] = item;
			if(startOpen)
				item.open();
		}
		else if(items[i].id != label)
		{
			val item = UiContainer(label, curContainer)
			items[i] = item;
			if(startOpen)
				item.open();
		}
		curContainer.itemCount++;
		val open = items[i].update(input, curContainer.selectedIndex == i);
		if(open)
			curContainer = items[i] as UiContainer;
		return open;
	}

	fun treePop()
	{
		if(curContainer.parent == null)
			error("called pop too many times");
		curContainer = curContainer.parent!!;
	}

	fun text(label: String)
	{
		val count = curContainer.renderItemCount;
		val i = curContainer.itemCount - 1;
		val items = curContainer.renderItems;

		if(items.size <= count)
			items.add(RenderItem(i, RenderItemType.Text));
		items[count].after = i;
		items[count].type = RenderItemType.Text
		items[count].arg = "  $label";
		curContainer.renderItemCount++;
	}

	fun seperator()
	{
		val count = curContainer.renderItemCount;
		val i = curContainer.itemCount - 1;
		val items = curContainer.renderItems;

		if(items.size <= count)
			items.add(RenderItem(i, RenderItemType.SameLine));
		items[count].after = i;
		items[count].type = RenderItemType.Seperator;
		curContainer.renderItemCount++;
	}

	fun sameLine()
	{
		val count = curContainer.renderItemCount;
		val i = curContainer.itemCount - 1;
		val items = curContainer.renderItems;

		if(items.size <= count)
			items.add(RenderItem(i, RenderItemType.SameLine));
		items[count].after = i;
		items[count].type = RenderItemType.SameLine;
		curContainer.renderItemCount++;
	}

	private inline fun <reified T: UiElement> addItem(
		container: UiContainer,
		id: String,
		constructor: () -> T
	)
	{
		val i = container.itemCount;
		val items = container.items;
		if(items.size <= i)
			items.add(constructor());
		else if(items[i] !is T)
			items[i] = constructor();
		else if(items[i].id != id)
			items[i] = constructor();
		container.itemCount++;
	}
}
