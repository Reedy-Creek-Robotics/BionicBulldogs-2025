package org.firstinspires.ftc.teamcode

import com.minerkid08.dynamicopmodeloader.OpmodeLoaderFunction
import com.qualcomm.robotcore.hardware.HardwareDevice.Manufacturer
import com.qualcomm.robotcore.hardware.I2cAddr
import com.qualcomm.robotcore.hardware.I2cDeviceSynch
import com.qualcomm.robotcore.hardware.I2cDeviceSynchDevice
import com.qualcomm.robotcore.hardware.configuration.annotations.DeviceProperties
import com.qualcomm.robotcore.hardware.configuration.annotations.I2cDeviceType
import com.qualcomm.robotcore.util.TypeConversion
import java.nio.ByteOrder

class Color
{
	var r: Int = 0;
	var g: Int = 0;
	var b: Int = 0;

	constructor()
	{
	}

	constructor(r2: Int, g2: Int, b2: Int)
	{
		r = r2;
		g = g2;
		b = b2;
	}

	fun toByteArray(): ByteArray
	{
		val arr = ByteArray(3);
		arr[0] = intToByte(r);
		arr[1] = intToByte(g);
		arr[2] = intToByte(b);
		return arr;
	}
}

fun writeColor(driver: I2cDeviceSynch, slot: LightStripDriver.Register, layer: Byte, value: Color)
{
	val c = value.toByteArray();
	val data = byteArrayOf(
		layer,
		c[0],
		c[1],
		c[2]
	);
	driver.write(slot.id, data);
}

/*fun readColor(driver: I2cDeviceSynch, slot: LightStripDriver.Register, layer: Byte): Color
{
	driver.write(byteArrayOf(intToByte(slot.id), layer));
	return driver.read(1)[0];
	val data = byteArrayOf(
		layer,
		c[0],
		c[1],
		c[2]
	);
	driver.write(slot.id, data);
}*/

//class Pulsing
//class Sine
//class DroidScan
//class Rainbow
//class Snakes
//class Random
//class Sparkle
//class SingleFill
//class RainbowSnakes
//class PoliceLights

@I2cDeviceType
@DeviceProperties(name = "lightStripDriver", xmlTag = "lightstrip")
class LightStripDriver(deviceClient: I2cDeviceSynch, isOwned: Boolean) :
	I2cDeviceSynchDevice<I2cDeviceSynch>(deviceClient, isOwned)
{
	enum class Register
		(var id: Int)
	{
		DeviceID(0),
		FirmwareVersion(0x01),
		HardwareVersion(0x02),
		PowerCycleCount(0x03),
		Runtime(0x04),
		Status(0x05),
		Control(0x06),
		SaveLoadAnimation(0x07),
		L0(0x08),
		L1(0x09),
		L2(0x0a),
		L3(0x0b),
		L4(0x0c),
		L5(0x0d),
		L6(0x0e),
		L7(0x0f),
		L8(0x10),
		L9(0x11),
		First(DeviceID.id),
		Last(L9.id)
	}

	enum class AnimationEnum
		(var id: Int)
	{
		SolidColor(0x01),
		Blinking(0x02),
		Pulsing(0x03),
		Sine(0x04),
		DroidScan(0x05),
		Rainbow(0x06),
		Snakes(0x07),
		Random(0x08),
		Sparkle(0x09),
		SingleFill(0x0a),
		RainbowSnakes(0x0b),
		PoliceLights(0x0c)
	}

	init
	{
		val readWindow = I2cDeviceSynch.ReadWindow(
			Register.First.id,
			Register.Last.id,
			I2cDeviceSynch.ReadMode.REPEAT
		);

		deviceClient.readWindow = readWindow;

		registerArmingStateCallback(false);
		engage();

		deviceClient.i2cAddress = I2cAddr(0x38);
	}

	override fun getManufacturer(): Manufacturer
	{
		return Manufacturer.GoBilda;
	}

	@Synchronized
	override fun doInitialize(): Boolean
	{
		return true;
	}

	override fun getDeviceName(): String
	{
		return "light driver";
	}

	fun saveAnimation(anim: Animations.AnimationBase, slot: Int)
	{
		val slot = slotIdToEnum(slot);
		val animId = classToEnum(anim).id;

		writei8(deviceClient, slot, 0, intToByte(animId));
		anim.save(deviceClient, slot);
	}

	fun loadAnimation(slot: Int): Animations.AnimationBase
	{
		val slot = slotIdToEnum(slot);

		val animId = readi8(deviceClient, slot, 0);
		val anim = when (animId)
		{
			intToByte(AnimationEnum.SolidColor.id) -> Animations.SolidColor();
			intToByte(AnimationEnum.Blinking.id) -> Animations.Blinking();
			intToByte(AnimationEnum.Pulsing.id) -> Animations.Pulsing();
			else -> error("invalid anim id");
		}
		anim.load(deviceClient, slot);
		return anim;
	}

	fun clearAnimations()
	{
		val data = 1 shl 25;
		val packet = TypeConversion.intToByteArray(data, ByteOrder.LITTLE_ENDIAN);
		deviceClient.write(Register.Control.id, packet);
	}

	fun getRuntime(): Int
	{
		return readInt(deviceClient, Register.Runtime);
	}

	fun setLedCount(count: Int)
	{
		var data = 1 shl 24;
		data = data or (count shl 16);
		val packet = TypeConversion.intToByteArray(data, ByteOrder.LITTLE_ENDIAN);
		deviceClient.write(Register.Control.id, packet);
	}

	fun getLedCount(): Int
	{
		val data = deviceClient.read(Register.Status.id, 4);
		return TypeConversion.unsignedByteToInt(data[0]);
	}

	fun saveArtBoard(slot: Int)
	{
		val data = 1 shl slot;
		deviceClient.write(
			Register.SaveLoadAnimation.id,
			TypeConversion.intToByteArray(data, ByteOrder.LITTLE_ENDIAN)
		);
	}

	@OpmodeLoaderFunction
	fun displayArtBoard(slot: Int)
	{
		val addr = 1 shl slot;
		val data = addr shl 8;
		deviceClient.write(
			Register.SaveLoadAnimation.id,
			TypeConversion.intToByteArray(data, ByteOrder.LITTLE_ENDIAN)
		);
	}

	fun enableBootAnimation(slot: Int)
	{
		var data = 1 shl slot;
		data = data shl 16;
		data = data or (1 shl 24);
		deviceClient.write(
			Register.SaveLoadAnimation.id,
			TypeConversion.intToByteArray(data, ByteOrder.LITTLE_ENDIAN)
		);
	}

	fun disableBootAnimation()
	{
		val data = 1 shl 25;
		val packet = TypeConversion.intToByteArray(data, ByteOrder.LITTLE_ENDIAN);
		deviceClient.write(Register.SaveLoadAnimation.id, packet);
	}

	fun classToEnum(animation: Animations.AnimationBase): AnimationEnum
	{
		return when (animation)
		{
			is Animations.SolidColor -> AnimationEnum.SolidColor;
			is Animations.Blinking   -> AnimationEnum.Blinking;
			is Animations.Pulsing    -> AnimationEnum.Pulsing;
			is Animations.Rainbow    -> AnimationEnum.Rainbow;
			else                     -> AnimationEnum.SolidColor;
		}
	}

	fun slotIdToEnum(i: Int): Register
	{
		return when (i)
		{
			0    -> Register.L0;
			1    -> Register.L1;
			2    -> Register.L2;
			3    -> Register.L3;
			4    -> Register.L4;
			5    -> Register.L5;
			6    -> Register.L6;
			7    -> Register.L7;
			8    -> Register.L8;
			9    -> Register.L9;
			else -> error("invalid reg");
		}
	}
}