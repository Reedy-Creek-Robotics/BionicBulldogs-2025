package org.firstinspires.ftc.teamcode.modules

import com.minerkid08.dynamicopmodeloader.FunctionBuilder
import com.minerkid08.dynamicopmodeloader.OpmodeLoaderFunction

object LuaSave
{
	fun build(builder: FunctionBuilder)
	{
		builder.pushTable("save");
		builder.addStaticClassAsGlobal(LuaSave::class.java);
		builder.popTable();
	}

	val intMap = HashMap<String, Int>();
	val doubleMap = HashMap<String, Double>();
	val boolMap = HashMap<String, Boolean>();
	val stringMap = HashMap<String, String>();

	@OpmodeLoaderFunction
	fun savei(key: String, value: Int)
	{
		intMap[key] = value;
	}

	@OpmodeLoaderFunction
	fun saved(key: String, value: Double)
	{
		doubleMap[key] = value;
	}

	@OpmodeLoaderFunction
	fun saveb(key: String, value: Boolean)
	{
		boolMap[key] = value;
	}

	@OpmodeLoaderFunction
	fun saves(key: String, value: String)
	{
		stringMap[key] = value;
	}

	@OpmodeLoaderFunction
	fun loadi(key: String): Int
	{
		if (intMap.contains(key))
			return intMap[key]!!;
		error("int map does not contain key $key");
	}

	@OpmodeLoaderFunction
	fun loadd(key: String): Double
	{
		if(doubleMap.contains(key))
		return doubleMap[key]!!;
		error("double map does not contain key $key");
	}

	@OpmodeLoaderFunction
	fun loadb(key: String): Boolean
	{
		if(boolMap.contains(key))
			return boolMap[key]!!;
		error("bool map does not contain key $key");
	}

	@OpmodeLoaderFunction
	fun loads(key: String): String
	{
		if(stringMap.contains(key))
			return stringMap[key]!!;
		error("string map does not contain key $key");
	}

	@OpmodeLoaderFunction
	fun containsi(key: String): Boolean
	{
		return intMap.contains(key);
	}

	@OpmodeLoaderFunction
	fun containsd(key: String): Boolean
	{
		return doubleMap.contains(key);
	}

	@OpmodeLoaderFunction
	fun containsb(key: String): Boolean
	{
		return boolMap.contains(key);
	}

	@OpmodeLoaderFunction
	fun containss(key: String): Boolean
	{
		return stringMap.contains(key);
	}
}