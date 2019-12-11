package com.github.timtebeek.day9.boost;

import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import org.junit.jupiter.api.Test;

import static com.github.timtebeek.day9.boost.IntcodeComputer.convertToIndexedMemory;
import static com.github.timtebeek.day9.boost.IntcodeComputer.readParameterValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class IntcodeComputerTest {
	@Test
	void testReadParameterValue() throws Exception {
		Map<Long, Long> memory = convertToIndexedMemory(new long[] { 0, 1l, 2, 4, 8, 16, 32, 64, 128, 256 });

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
	void testProduce16Digits() throws InterruptedException {
		long[] program = { 1102, 34915192, 34915192, 7, 4, 7, 99, 0 };
		Map<Long, Long> memory = IntcodeComputer.convertToIndexedMemory(program);

		BlockingDeque<Long> inputs = new LinkedBlockingDeque<>();
		BlockingDeque<Long> outputs = new LinkedBlockingDeque<>();
		IntcodeComputer.execute(inputs, outputs, memory);
		Long peekLast = outputs.peekLast();
		assertEquals(1219070632396864l, peekLast);
	}

	@Test
	void testProduceLargeNumber() throws InterruptedException {
		long[] program = { 104, 1125899906842624L, 99 };
		Map<Long, Long> memory = IntcodeComputer.convertToIndexedMemory(program);

		BlockingDeque<Long> inputs = new LinkedBlockingDeque<>();
		BlockingDeque<Long> outputs = new LinkedBlockingDeque<>();
		IntcodeComputer.execute(inputs, outputs, memory);
		Long peekLast = outputs.peekLast();
		assertEquals(1125899906842624L, peekLast);
	}

	@Test
	void testProduceCopy() throws InterruptedException {
		long[] program = { 109, 1, 204, -1, 1001, 100, 1, 100, 1008, 100, 16, 101, 1006, 101, 0, 99 };
		Map<Long, Long> memory = IntcodeComputer.convertToIndexedMemory(program);

		BlockingDeque<Long> inputs = new LinkedBlockingDeque<>();
		BlockingDeque<Long> outputs = new LinkedBlockingDeque<>();
		IntcodeComputer.execute(inputs, outputs, memory);
		Long peekLast = outputs.peekLast();
		assertEquals(99, peekLast);
	}

	@Test
	void testSanityCheck() throws InterruptedException {
		long[] program = {
				1101, 3, 4, 5,
				104, -1,
				109, 5,
				204, 0,
				99 };
		Map<Long, Long> memory = IntcodeComputer.convertToIndexedMemory(program);

		BlockingDeque<Long> inputs = new LinkedBlockingDeque<>();
		BlockingDeque<Long> outputs = new LinkedBlockingDeque<>();
		IntcodeComputer.execute(inputs, outputs, memory);
		Long peekLast = outputs.peekLast();
		assertEquals(7, peekLast);
	}

	@Test
	void testPart1() throws InterruptedException {
		long[] program = { 1102, 34463338, 34463338, 63, 1007, 63, 34463338, 63, 1005, 63, 53, 1101, 0, 3, 1000, 109,
				988, 209, 12, 9, 1000, 209, 6, 209, 3, 203, 0, 1008, 1000, 1, 63, 1005, 63, 65, 1008, 1000, 2, 63, 1005,
				63, 904, 1008, 1000, 0, 63, 1005, 63, 58, 4, 25, 104, 0, 99, 4, 0, 104, 0, 99, 4, 17, 104, 0, 99, 0, 0,
				1102, 32, 1, 1016, 1101, 38, 0, 1012, 1102, 1, 693, 1022, 1102, 1, 27, 1007, 1101, 0, 190, 1025, 1102,
				20, 1, 1019, 1102, 1, 33, 1008, 1102, 28, 1, 1013, 1101, 0, 1, 1021, 1102, 1, 851, 1026, 1102, 22, 1,
				1018, 1101, 29, 0, 1005, 1101, 0, 21, 1004, 1101, 36, 0, 1009, 1101, 0, 195, 1024, 1101, 0, 39, 1002,
				1101, 0, 848, 1027, 1102, 1, 34, 1003, 1102, 23, 1, 1015, 1102, 1, 30, 1010, 1101, 0, 26, 1017, 1102, 1,
				35, 1001, 1102, 1, 489, 1028, 1102, 1, 484, 1029, 1102, 1, 686, 1023, 1102, 0, 1, 1020, 1102, 25, 1,
				1006, 1102, 1, 31, 1014, 1101, 37, 0, 1000, 1102, 1, 24, 1011, 109, 23, 2105, 1, 1, 4, 187, 1105, 1,
				199, 1001, 64, 1, 64, 1002, 64, 2, 64, 109, -18, 2102, 1, -3, 63, 1008, 63, 41, 63, 1005, 63, 223, 1001,
				64, 1, 64, 1106, 0, 225, 4, 205, 1002, 64, 2, 64, 109, 2, 2101, 0, 1, 63, 1008, 63, 33, 63, 1005, 63,
				251, 4, 231, 1001, 64, 1, 64, 1105, 1, 251, 1002, 64, 2, 64, 109, 13, 21107, 40, 41, -8, 1005, 1012,
				269, 4, 257, 1106, 0, 273, 1001, 64, 1, 64, 1002, 64, 2, 64, 109, 9, 1205, -8, 287, 4, 279, 1105, 1,
				291, 1001, 64, 1, 64, 1002, 64, 2, 64, 109, -38, 2101, 0, 9, 63, 1008, 63, 34, 63, 1005, 63, 315, 1001,
				64, 1, 64, 1106, 0, 317, 4, 297, 1002, 64, 2, 64, 109, 18, 21108, 41, 38, 1, 1005, 1010, 337, 1001, 64,
				1, 64, 1106, 0, 339, 4, 323, 1002, 64, 2, 64, 109, -5, 2107, 30, 1, 63, 1005, 63, 359, 1001, 64, 1, 64,
				1106, 0, 361, 4, 345, 1002, 64, 2, 64, 109, 14, 21101, 42, 0, -7, 1008, 1011, 42, 63, 1005, 63, 387, 4,
				367, 1001, 64, 1, 64, 1106, 0, 387, 1002, 64, 2, 64, 109, -18, 1208, 0, 39, 63, 1005, 63, 403, 1106, 0,
				409, 4, 393, 1001, 64, 1, 64, 1002, 64, 2, 64, 109, 3, 1207, -1, 38, 63, 1005, 63, 425, 1106, 0, 431, 4,
				415, 1001, 64, 1, 64, 1002, 64, 2, 64, 109, -8, 1201, 5, 0, 63, 1008, 63, 35, 63, 1005, 63, 455, 1001,
				64, 1, 64, 1106, 0, 457, 4, 437, 1002, 64, 2, 64, 109, 30, 1206, -4, 469, 1106, 0, 475, 4, 463, 1001,
				64, 1, 64, 1002, 64, 2, 64, 109, 10, 2106, 0, -7, 4, 481, 1106, 0, 493, 1001, 64, 1, 64, 1002, 64, 2,
				64, 109, -24, 21102, 43, 1, 3, 1008, 1014, 40, 63, 1005, 63, 517, 1001, 64, 1, 64, 1105, 1, 519, 4, 499,
				1002, 64, 2, 64, 109, -4, 2108, 41, -5, 63, 1005, 63, 539, 1001, 64, 1, 64, 1106, 0, 541, 4, 525, 1002,
				64, 2, 64, 109, 18, 21101, 44, 0, -8, 1008, 1017, 47, 63, 1005, 63, 561, 1105, 1, 567, 4, 547, 1001, 64,
				1, 64, 1002, 64, 2, 64, 109, -24, 1202, 6, 1, 63, 1008, 63, 27, 63, 1005, 63, 589, 4, 573, 1106, 0, 593,
				1001, 64, 1, 64, 1002, 64, 2, 64, 109, 7, 1208, -5, 34, 63, 1005, 63, 611, 4, 599, 1106, 0, 615, 1001,
				64, 1, 64, 1002, 64, 2, 64, 109, -5, 1207, 6, 37, 63, 1005, 63, 637, 4, 621, 1001, 64, 1, 64, 1106, 0,
				637, 1002, 64, 2, 64, 109, 23, 1206, -6, 655, 4, 643, 1001, 64, 1, 64, 1105, 1, 655, 1002, 64, 2, 64,
				109, -10, 2107, 32, -8, 63, 1005, 63, 673, 4, 661, 1105, 1, 677, 1001, 64, 1, 64, 1002, 64, 2, 64, 109,
				5, 2105, 1, 2, 1001, 64, 1, 64, 1106, 0, 695, 4, 683, 1002, 64, 2, 64, 109, -17, 1202, 0, 1, 63, 1008,
				63, 20, 63, 1005, 63, 715, 1106, 0, 721, 4, 701, 1001, 64, 1, 64, 1002, 64, 2, 64, 109, -4, 1201, 4, 0,
				63, 1008, 63, 21, 63, 1005, 63, 743, 4, 727, 1106, 0, 747, 1001, 64, 1, 64, 1002, 64, 2, 64, 109, 10,
				1205, 10, 763, 1001, 64, 1, 64, 1105, 1, 765, 4, 753, 1002, 64, 2, 64, 109, 1, 21102, 45, 1, 1, 1008,
				1012, 45, 63, 1005, 63, 787, 4, 771, 1105, 1, 791, 1001, 64, 1, 64, 1002, 64, 2, 64, 109, -4, 2102, 1,
				-2, 63, 1008, 63, 29, 63, 1005, 63, 813, 4, 797, 1105, 1, 817, 1001, 64, 1, 64, 1002, 64, 2, 64, 109,
				-4, 2108, 33, 5, 63, 1005, 63, 835, 4, 823, 1105, 1, 839, 1001, 64, 1, 64, 1002, 64, 2, 64, 109, 23,
				2106, 0, 1, 1106, 0, 857, 4, 845, 1001, 64, 1, 64, 1002, 64, 2, 64, 109, -12, 21108, 46, 46, 1, 1005,
				1015, 879, 4, 863, 1001, 64, 1, 64, 1106, 0, 879, 1002, 64, 2, 64, 109, 10, 21107, 47, 46, -5, 1005,
				1019, 899, 1001, 64, 1, 64, 1105, 1, 901, 4, 885, 4, 64, 99, 21101, 27, 0, 1, 21101, 915, 0, 0, 1105, 1,
				922, 21201, 1, 52134, 1, 204, 1, 99, 109, 3, 1207, -2, 3, 63, 1005, 63, 964, 21201, -2, -1, 1, 21101, 0,
				942, 0, 1105, 1, 922, 22101, 0, 1, -1, 21201, -2, -3, 1, 21101, 0, 957, 0, 1105, 1, 922, 22201, 1, -1,
				-2, 1106, 0, 968, 21201, -2, 0, -2, 109, -3, 2106, 0, 0 };
		Map<Long, Long> memory = IntcodeComputer.convertToIndexedMemory(program);

		BlockingDeque<Long> inputs = new LinkedBlockingDeque<>();
		BlockingDeque<Long> outputs = new LinkedBlockingDeque<>();
		inputs.putFirst(1L);
		IntcodeComputer.execute(inputs, outputs, memory);
		System.out.println(outputs);
	}

}
