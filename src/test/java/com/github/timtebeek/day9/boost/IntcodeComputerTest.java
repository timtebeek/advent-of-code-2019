package com.github.timtebeek.day9.boost;

import java.util.Map;

import org.junit.jupiter.api.Test;

import static com.github.timtebeek.day9.boost.IntcodeComputer.convertToIndexedMemory;
import static com.github.timtebeek.day9.boost.IntcodeComputer.readParameterValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class IntcodeComputerTest {

	@Test
	void testReadParameterValuePositionMode() throws Exception {
		Map<Long, Long> memory = convertToIndexedMemory(new long[] { 0, 1, 2, 4, 8, 16, 32, 64, 128, 256 });

		// Verify reference position mode
		long pointer = 0;
		assertEquals(1, readParameterValue("1", 1, pointer, 0, memory));
		assertEquals(1, readParameterValue("01", 1, pointer, 0, memory));
		assertEquals(1, readParameterValue("001", 1, pointer, 0, memory));
		assertEquals(1, readParameterValue("0001", 1, pointer, 0, memory));
		assertEquals(2, readParameterValue("1", 2, pointer, 0, memory));
		assertEquals(2, readParameterValue("01", 2, pointer, 0, memory));
		assertEquals(2, readParameterValue("001", 2, pointer, 0, memory));
		assertEquals(2, readParameterValue("0001", 2, pointer, 0, memory));

		// Increase pointer for bigger offset
		pointer = 2;
		assertEquals(8, readParameterValue("1", 1, pointer, 0, memory));
		assertEquals(8, readParameterValue("01", 1, pointer, 0, memory));
		assertEquals(8, readParameterValue("001", 1, pointer, 0, memory));
		assertEquals(8, readParameterValue("0001", 1, pointer, 0, memory));
		assertEquals(128, readParameterValue("1", 2, pointer, 0, memory));
		assertEquals(128, readParameterValue("01", 2, pointer, 0, memory));
		assertEquals(128, readParameterValue("001", 2, pointer, 0, memory));
		assertEquals(128, readParameterValue("0001", 2, pointer, 0, memory));

		// Switch to immediate mode
		assertEquals(4, readParameterValue("101", 1, pointer, 0, memory));
		assertEquals(4, readParameterValue("0101", 1, pointer, 0, memory));
		assertEquals(4, readParameterValue("1101", 1, pointer, 0, memory));
		assertEquals(8, readParameterValue("1001", 2, pointer, 0, memory));
		assertEquals(8, readParameterValue("1101", 2, pointer, 0, memory));

		// Increase pointer yet again
		pointer = 4;
		assertEquals(16, readParameterValue("0101", 1, pointer, 0, memory));
		assertEquals(16, readParameterValue("1101", 1, pointer, 0, memory));
		assertEquals(32, readParameterValue("1001", 2, pointer, 0, memory));
		assertEquals(32, readParameterValue("1101", 2, pointer, 0, memory));
	}

	@Test
	void testReadParameterValueImmediateMode() throws Exception {
		Map<Long, Long> memory = convertToIndexedMemory(new long[] { 0, 1, 2, 4, 8, 16, 32, 64, 128, 256 });

		// Values should only depend on pointer
		long pointer = 2;
		assertEquals(4, readParameterValue("101", 1, pointer, 0, memory));
		assertEquals(4, readParameterValue("0101", 1, pointer, 0, memory));
		assertEquals(4, readParameterValue("1101", 1, pointer, 0, memory));
		assertEquals(8, readParameterValue("1001", 2, pointer, 0, memory));
		assertEquals(8, readParameterValue("1101", 2, pointer, 0, memory));

		// And shift to the right when pointer moves
		pointer = 4;
		assertEquals(16, readParameterValue("0101", 1, pointer, 0, memory));
		assertEquals(16, readParameterValue("1101", 1, pointer, 0, memory));
		assertEquals(32, readParameterValue("1001", 2, pointer, 0, memory));
		assertEquals(32, readParameterValue("1101", 2, pointer, 0, memory));
	}

	@Test
	void testReadParameterValueRelativeMode() throws Exception {
		Map<Long, Long> memory = convertToIndexedMemory(new long[] { 0, 1, 2, 4, 8, 16, 32, 64, 128, 256 });

		long relativeBase = 0;

		// Verify initial relative mode
		assertEquals(1, readParameterValue("201", 1, 0, relativeBase, memory));
		assertEquals(1, readParameterValue("0201", 1, 0, relativeBase, memory));
		assertEquals(2, readParameterValue("2001", 2, 0, relativeBase, memory));
		assertEquals(2, readParameterValue("2201", 2, 0, relativeBase, memory));

		// Increase relative base
		relativeBase = 2;
		assertEquals(4, readParameterValue("201", 1, 0, relativeBase, memory));
		assertEquals(4, readParameterValue("0201", 1, 0, relativeBase, memory));
		assertEquals(8, readParameterValue("2001", 2, 0, relativeBase, memory));
		assertEquals(8, readParameterValue("2201", 2, 0, relativeBase, memory));

		// Increase pointer yet again
		relativeBase = 4;
		assertEquals(16, readParameterValue("201", 1, 0, relativeBase, memory));
		assertEquals(16, readParameterValue("0201", 1, 0, relativeBase, memory));
		assertEquals(32, readParameterValue("2001", 2, 0, relativeBase, memory));
		assertEquals(32, readParameterValue("2201", 2, 0, relativeBase, memory));
	}

}
