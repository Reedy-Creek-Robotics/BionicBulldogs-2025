package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.hardware.I2cDeviceSynch
import com.qualcomm.robotcore.util.TypeConversion

class Animations
{
	abstract class AnimationBase
	{
		var brightness: Int = 0;
		var startInd: Int = 0;
		var endInd: Int = 0;

		abstract fun save(driver: I2cDeviceSynch, slot: LightStripDriver.Register);
		abstract fun load(driver: I2cDeviceSynch, slot: LightStripDriver.Register);
	}

	class SolidColor : AnimationBase()
	{
		var color = Color();

		override fun save(driver: I2cDeviceSynch, slot: LightStripDriver.Register)
		{
			writei8(driver, slot, 1, intToByte(brightness));
			writei8(driver, slot, 2, intToByte(startInd));
			writei8(driver, slot, 3, intToByte(endInd));
			writeColor(driver, slot, 4, color);
		}

		override fun load(driver: I2cDeviceSynch, slot: LightStripDriver.Register)
		{
			brightness = TypeConversion.unsignedByteToInt(readi8(driver, slot, 1));
			startInd = TypeConversion.unsignedByteToInt(readi8(driver, slot, 1));
			endInd = TypeConversion.unsignedByteToInt(readi8(driver, slot, 1));
			//color = readColor(driver, slot, 4);
		}
	}

	class Blinking : AnimationBase()
	{
		var primaryColor = Color();
		var secondaryColor = Color();
		var period = 2000;
		var primaryPeriod = 1000;

		override fun save(driver: I2cDeviceSynch, slot: LightStripDriver.Register)
		{
			writei8(driver, slot, 1, intToByte(brightness));
			writei8(driver, slot, 2, intToByte(startInd));
			writei8(driver, slot, 3, intToByte(endInd));
			writeColor(driver, slot, 4, primaryColor);
			writeColor(driver, slot, 5, secondaryColor);
			writei32(driver, slot, 6, period);
		}

		override fun load(driver: I2cDeviceSynch, slot: LightStripDriver.Register)
		{
		}
	}

	class Pulsing : AnimationBase()
	{
		var primaryColor = Color();
		var secondaryColor = Color();
		var period = 1000;

		override fun save(driver: I2cDeviceSynch, slot: LightStripDriver.Register)
		{
			writei8(driver, slot, 1, intToByte(brightness));
			writei8(driver, slot, 2, intToByte(startInd));
			writei8(driver, slot, 3, intToByte(endInd));
			writeColor(driver, slot, 4, primaryColor);
			writeColor(driver, slot, 5, secondaryColor);
			writei32(driver, slot, 6, period);
		}

		override fun load(driver: I2cDeviceSynch, slot: LightStripDriver.Register)
		{
		}
	}

	class Rainbow : AnimationBase()
	{
		var startHue = 0.0f;
		var endHue = 360.0f;
		var speed = 0.5f;
		var direction = 0;
		var repeatAfter = 25;

		override fun save(driver: I2cDeviceSynch, slot: LightStripDriver.Register)
		{
			writei8(driver, slot, 1, intToByte(brightness));
			writei8(driver, slot, 2, intToByte(startInd));
			writei8(driver, slot, 3, intToByte(endInd));
			writef32(driver, slot, 4, startHue);
			writef32(driver, slot, 5, endHue);
			writef32(driver, slot, 6, speed);
			writei8(driver, slot, 7, intToByte(direction));
			writei8(driver, slot, 9, intToByte(repeatAfter));
		}

		override fun load(driver: I2cDeviceSynch, slot: LightStripDriver.Register)
		{
		}
	}
}