/*package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.hardware.HardwareDevice.Manufacturer
import com.qualcomm.robotcore.hardware.I2cAddr
import com.qualcomm.robotcore.hardware.I2cDeviceSynch
import com.qualcomm.robotcore.hardware.I2cDeviceSynchDevice
import com.qualcomm.robotcore.hardware.configuration.annotations.DeviceProperties
import com.qualcomm.robotcore.hardware.configuration.annotations.I2cDeviceType

fun write8(driver: I2cDeviceSynch, reg: LightStripDriver.Register, value: UByte)
{
	driver.write(reg.id, ByteArray(1) { value.toByte() });
}

fun write32(driver: I2cDeviceSynch, reg: LightStripDriver.Register, value: UInt)
{
	val byteArr = ByteArray(4);
	driver.write(reg.id, ByteArray(1, ));
}

class Color
{
	var r: Byte = 0;
	var g: Byte= 0;
	var b: Byte= 0;
	fun toByteArray(): ByteArray
	{
		val arr = ByteArray(3);
		arr[0] = r;
		arr[1] = g;
		arr[2] = b;
		return arr;
	}
}

abstract class Animation
{
	var brightness: Byte = 0;
	var startInd: Byte = 0;
	var endInd: Byte = 0;

	abstract fun save(driver: I2cDeviceSynch);

}

class SolidColor : Animation()
{
	var color = Color();

	override fun save(driver: I2cDeviceSynch)
	{
		write8(driver, LightStripDriver.Register.L1, brightness);
		write8(driver, LightStripDriver.Register.L2, startInd);
		write8(driver, LightStripDriver.Register.L3, endInd);
		driver.write(LightStripDriver.Register.L4.id, color.toByteArray());
	}
}

class Blinking : Animation()
{
	var primaryColor = Color();
	var secondaryColor = Color();
	var period = 2000;
	var primaryPeriod = 1000;

	override fun save(driver: I2cDeviceSynch)
	{
		write8(driver, LightStripDriver.Register.L1, brightness);
		write8(driver, LightStripDriver.Register.L2, startInd);
		write8(driver, LightStripDriver.Register.L3, endInd);
		driver.write(LightStripDriver.Register.L4.id, color.toByteArray());
		driver.write(LightStripDriver.Register.L4.id, color.toByteArray());
		driver.write32
	}
}

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

	enum class Animation
		(var id: Int)
	{
		SolidColor(0x00),
		Blinking(0x01),
		Pulsing(0x02),
		Sine(0x03),
		DroidScan(0x04),
		Rainbow(0x05),
		Snakes(0x06),
		Random(0x07),
		Sparkle(0x08),
		SingleFill(0x09),
		RainbowSnakes(0x0a),
		PoliceLights(0x0b)
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

	fun saveAnimation(anim: Animation, slot: Int)
	{

	}


}*/