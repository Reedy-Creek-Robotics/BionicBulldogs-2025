package org.firstinspires.ftc.teamcode.modules

import android.util.Log
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

	@JvmStatic
	@OpmodeLoaderFunction
	fun savei(key: String, value: Int)
	{
		Log.d("save", "int: '$value'");
		intMap[key] = value;
	}

	@JvmStatic
	@OpmodeLoaderFunction
	fun saved(key: String, value: Double)
	{
		Log.d("save", "double: '$value'");
		doubleMap[key] = value;
	}

	@JvmStatic
	@OpmodeLoaderFunction
	fun saveb(key: String, value: Boolean)
	{
		Log.d("save", "bool: '$value'");
		boolMap[key] = value;
	}

	@JvmStatic
	@OpmodeLoaderFunction
	fun saves(key: String, value: String)
	{
		Log.d("save", "string: '$value'");
		stringMap[key] = value;
	}

	@JvmStatic
	@OpmodeLoaderFunction
	fun loadi(key: String): Int
	{
		if (intMap.contains(key))
		{
			val value = intMap[key]!!;
			Log.d("load", "int: '$value'");
			return value;
		}
		error("int map does not contain key $key");
	}

	@JvmStatic
	@OpmodeLoaderFunction
	fun loadd(key: String): Double
	{
		if (doubleMap.contains(key))
		{
			val value = doubleMap[key]!!;
			Log.d("load", "double: '$value'");
			return value;
		}
		error("double map does not contain key $key");
	}

	@JvmStatic
	@OpmodeLoaderFunction
	fun loadb(key: String): Boolean
	{
		if (boolMap.contains(key))
		{
			val value = boolMap[key]!!;
			Log.d("load", "bool: '$value'");
			return value;
		}
		error("bool map does not contain key $key");
	}

	@JvmStatic
	@OpmodeLoaderFunction
	fun loads(key: String): String
	{
		if (stringMap.contains(key))
		{
			val value = stringMap[key]!!;
			Log.d("load", "string: '$value'");
			return value;
		}
		error("string map does not contain key $key");
	}

	@JvmStatic
	@OpmodeLoaderFunction
	fun containsi(key: String): Boolean
	{
		return intMap.contains(key);
	}

	@JvmStatic
	@OpmodeLoaderFunction
	fun containsd(key: String): Boolean
	{
		return doubleMap.contains(key);
	}

	@JvmStatic
	@OpmodeLoaderFunction
	fun containsb(key: String): Boolean
	{
		return boolMap.contains(key);
	}

	@JvmStatic
	@OpmodeLoaderFunction
	fun containss(key: String): Boolean
	{
		return stringMap.contains(key);
	}
}