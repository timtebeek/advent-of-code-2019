package com.github.timtebeek.day5.test;

import org.junit.jupiter.api.Test;

import static com.github.timtebeek.day5.test.ThermalDiagnostics.execute;
import static com.github.timtebeek.day5.test.ThermalDiagnostics.readParameterValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ThermalDiagnosticsTest {

	@Test
	void testExecuteDay2() {
		assertEquals(3500, execute(new int[] { 1, 9, 10, 3, 2, 3, 11, 0, 99, 30, 40, 50 }));
		assertEquals(2, execute(new int[] { 1, 0, 0, 0, 99 }));
		assertEquals(2, execute(new int[] { 2, 3, 0, 3, 99 }));
		assertEquals(2, execute(new int[] { 2, 4, 4, 5, 99, 0 }));
		assertEquals(30, execute(new int[] { 1, 1, 1, 4, 99, 5, 6, 0, 99 }));
		assertEquals(9706670, execute(new int[] { 1, 12, 2, 3, 1, 1, 2, 3, 1, 3, 4, 3, 1, 5, 0, 3, 2, 10, 1, 19, 1, 5,
				19, 23, 1, 23, 5, 27, 2, 27, 10, 31, 1, 5, 31, 35, 2, 35, 6, 39, 1, 6, 39, 43, 2, 13, 43, 47, 2, 9, 47,
				51, 1, 6, 51, 55, 1, 55, 9, 59, 2, 6, 59, 63, 1, 5, 63, 67, 2, 67, 13, 71, 1, 9, 71, 75, 1, 75, 9, 79,
				2, 79, 10, 83, 1, 6, 83, 87, 1, 5, 87, 91, 1, 6, 91, 95, 1, 95, 13, 99, 1, 10, 99, 103, 2, 6, 103, 107,
				1, 107, 5, 111, 1, 111, 13, 115, 1, 115, 13, 119, 1, 13, 119, 123, 2, 123, 13, 127, 1, 127, 6, 131, 1,
				131, 9, 135, 1, 5, 135, 139, 2, 139, 6, 143, 2, 6, 143, 147, 1, 5, 147, 151, 1, 151, 2, 155, 1, 9, 155,
				0, 99, 2, 14, 0, 0 }));
	}

	@Test
	void testReadParameterValue() throws Exception {
		int[] memory = new int[] { 0, 1, 2, 4, 8, 16, 32, 64, 128, 256 };

		// Verify reference position mode
		int pointer = 0;
		assertEquals(1, readParameterValue("1", 1, pointer, memory));
		assertEquals(1, readParameterValue("01", 1, pointer, memory));
		assertEquals(1, readParameterValue("001", 1, pointer, memory));
		assertEquals(1, readParameterValue("0001", 1, pointer, memory));
		assertEquals(2, readParameterValue("1", 2, pointer, memory));
		assertEquals(2, readParameterValue("01", 2, pointer, memory));
		assertEquals(2, readParameterValue("001", 2, pointer, memory));
		assertEquals(2, readParameterValue("0001", 2, pointer, memory));

		// Increase pointer for bigger offset
		pointer = 2;
		assertEquals(8, readParameterValue("1", 1, pointer, memory));
		assertEquals(8, readParameterValue("01", 1, pointer, memory));
		assertEquals(8, readParameterValue("001", 1, pointer, memory));
		assertEquals(8, readParameterValue("0001", 1, pointer, memory));
		assertEquals(128, readParameterValue("1", 2, pointer, memory));
		assertEquals(128, readParameterValue("01", 2, pointer, memory));
		assertEquals(128, readParameterValue("001", 2, pointer, memory));
		assertEquals(128, readParameterValue("0001", 2, pointer, memory));

		// Switch to immediate mode
		assertEquals(4, readParameterValue("101", 1, pointer, memory));
		assertEquals(4, readParameterValue("0101", 1, pointer, memory));
		assertEquals(4, readParameterValue("1101", 1, pointer, memory));
		assertEquals(8, readParameterValue("1001", 2, pointer, memory));
		assertEquals(8, readParameterValue("1101", 2, pointer, memory));

		// Increase pointer yet again
		pointer = 4;
		assertEquals(16, readParameterValue("0101", 1, pointer, memory));
		assertEquals(16, readParameterValue("1101", 1, pointer, memory));
		assertEquals(32, readParameterValue("1001", 2, pointer, memory));
		assertEquals(32, readParameterValue("1101", 2, pointer, memory));
	}
}
