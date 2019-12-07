package com.github.timtebeek.day7.amplify;

import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import static com.github.timtebeek.day7.amplify.AmplifyThrust.executeInSequence;
import static com.github.timtebeek.day7.amplify.AmplifyThrust.findOptiomalPermutationDay1;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AmplifyThrustTest {

	@Test
	void testExecuteSample1() {
		int[] memory = new int[] { 3, 15, 3, 16, 1002, 16, 10, 16, 1, 16, 15, 15, 4, 15, 99, 0, 0 };
		int maxThruster = executeInSequence(0, IntStream.of(4, 3, 2, 1, 0).iterator(), memory);
		assertEquals(43210, maxThruster);
	}

	@Test
	void testExecuteSample2() {
		int[] memory = new int[] {
				3, 23, 3, 24, 1002, 24, 10, 24, 1002, 23, -1, 23,
				101, 5, 23, 23, 1, 24, 23, 23, 4, 23, 99, 0, 0 };
		int maxThruster = executeInSequence(0, IntStream.of(0, 1, 2, 3, 4).iterator(), memory);
		assertEquals(54321, maxThruster);
	}

	@Test
	void testExecuteSample3() {
		int[] memory = new int[] {
				3, 31, 3, 32, 1002, 32, 10, 32, 1001, 31, -2, 31, 1007, 31, 0, 33,
				1002, 33, 7, 33, 1, 33, 31, 31, 1, 32, 31, 31, 4, 31, 99, 0, 0, 0 };
		int maxThruster = executeInSequence(0, IntStream.of(1, 0, 4, 3, 2).iterator(), memory);
		assertEquals(65210, maxThruster);
	}

	@Test
	void testPart1() throws Exception {
		int[] memory = {
				3, 8, 1001, 8, 10, 8, 105, 1, 0, 0, 21, 46, 59, 72, 93, 110, 191, 272, 353, 434, 99999, 3, 9, 101, 4, 9,
				9, 1002, 9, 3, 9, 1001, 9, 5, 9, 102, 2, 9, 9, 1001, 9, 5, 9, 4, 9, 99, 3, 9, 1002, 9, 5, 9, 1001, 9, 5,
				9, 4, 9, 99, 3, 9, 101, 4, 9, 9, 1002, 9, 4, 9, 4, 9, 99, 3, 9, 102, 3, 9, 9, 101, 3, 9, 9, 1002, 9, 2,
				9, 1001, 9, 5, 9, 4, 9, 99, 3, 9, 1001, 9, 2, 9, 102, 4, 9, 9, 101, 2, 9, 9, 4, 9, 99, 3, 9, 1002, 9, 2,
				9, 4, 9, 3, 9, 1002, 9, 2, 9, 4, 9, 3, 9, 102, 2, 9, 9, 4, 9, 3, 9, 102, 2, 9, 9, 4, 9, 3, 9, 102, 2, 9,
				9, 4, 9, 3, 9, 1002, 9, 2, 9, 4, 9, 3, 9, 101, 2, 9, 9, 4, 9, 3, 9, 101, 2, 9, 9, 4, 9, 3, 9, 1001, 9,
				2, 9, 4, 9, 3, 9, 101, 2, 9, 9, 4, 9, 99, 3, 9, 101, 2, 9, 9, 4, 9, 3, 9, 101, 2, 9, 9, 4, 9, 3, 9, 101,
				1, 9, 9, 4, 9, 3, 9, 101, 1, 9, 9, 4, 9, 3, 9, 1002, 9, 2, 9, 4, 9, 3, 9, 101, 2, 9, 9, 4, 9, 3, 9, 102,
				2, 9, 9, 4, 9, 3, 9, 1002, 9, 2, 9, 4, 9, 3, 9, 102, 2, 9, 9, 4, 9, 3, 9, 101, 1, 9, 9, 4, 9, 99, 3, 9,
				101, 2, 9, 9, 4, 9, 3, 9, 1001, 9, 1, 9, 4, 9, 3, 9, 101, 1, 9, 9, 4, 9, 3, 9, 1002, 9, 2, 9, 4, 9, 3,
				9, 1001, 9, 2, 9, 4, 9, 3, 9, 102, 2, 9, 9, 4, 9, 3, 9, 1002, 9, 2, 9, 4, 9, 3, 9, 1002, 9, 2, 9, 4, 9,
				3, 9, 1001, 9, 1, 9, 4, 9, 3, 9, 101, 2, 9, 9, 4, 9, 99, 3, 9, 102, 2, 9, 9, 4, 9, 3, 9, 1001, 9, 2, 9,
				4, 9, 3, 9, 1001, 9, 2, 9, 4, 9, 3, 9, 1002, 9, 2, 9, 4, 9, 3, 9, 1002, 9, 2, 9, 4, 9, 3, 9, 1002, 9, 2,
				9, 4, 9, 3, 9, 1001, 9, 1, 9, 4, 9, 3, 9, 101, 2, 9, 9, 4, 9, 3, 9, 102, 2, 9, 9, 4, 9, 3, 9, 1001, 9,
				2, 9, 4, 9, 99, 3, 9, 1001, 9, 1, 9, 4, 9, 3, 9, 1001, 9, 1, 9, 4, 9, 3, 9, 1001, 9, 2, 9, 4, 9, 3, 9,
				102, 2, 9, 9, 4, 9, 3, 9, 102, 2, 9, 9, 4, 9, 3, 9, 101, 1, 9, 9, 4, 9, 3, 9, 101, 1, 9, 9, 4, 9, 3, 9,
				1002, 9, 2, 9, 4, 9, 3, 9, 1002, 9, 2, 9, 4, 9, 3, 9, 1001, 9, 1, 9, 4, 9, 99 };
		long maxThrust = findOptiomalPermutationDay1(memory);
		assertEquals(21000, maxThrust);
	}

}
