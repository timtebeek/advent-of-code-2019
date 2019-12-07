package com.github.timtebeek.day5.test;

import org.junit.jupiter.api.Test;

import static com.github.timtebeek.day5.test.ThermalDiagnostics.readParameterValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ThermalDiagnosticsTest {
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
