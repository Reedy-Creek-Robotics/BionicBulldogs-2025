package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.I2cDeviceSynch
import com.qualcomm.robotcore.util.TypeConversion
import com.qualcomm.robotcore.util.TypeConversion.byteArrayToInt
import java.nio.ByteBuffer
import java.nio.ByteOrder

fun intToByte(i: Int): Byte
{
	val buffer = ByteBuffer.allocate(4).putInt(i);

	return buffer.array()[3];
}

fun readInt(driver: I2cDeviceSynch, register: LightStripDriver.Register): Int
{
	return byteArrayToInt(
		driver.read(register.id, 4),
		ByteOrder.LITTLE_ENDIAN
	)
}

fun writei8(driver: I2cDeviceSynch, slot: LightStripDriver.Register, layer: Byte, value: Byte)
{
	val data = byteArrayOf(layer, value);
	driver.write(slot.id, data);
}

fun writei32(driver: I2cDeviceSynch, slot: LightStripDriver.Register, layer: Byte, value: Int)
{
	val data = TypeConversion.intToByteArray(value, ByteOrder.LITTLE_ENDIAN);

	val byteArr = byteArrayOf(
		layer,
		data[0],
		data[1],
		data[2],
		data[3]
	);
	driver.write(slot.id, byteArr);
}

fun writef32(driver: I2cDeviceSynch, slot: LightStripDriver.Register, layer: Byte, value: Float)
{
	val data = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putFloat(value).array();

	val byteArr = byteArrayOf(
		layer,
		data[0],
		data[1],
		data[2],
		data[3]
	);
	driver.write(slot.id, byteArr);
}