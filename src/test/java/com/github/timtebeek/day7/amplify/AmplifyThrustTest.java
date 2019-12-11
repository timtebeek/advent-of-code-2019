package com.github.timtebeek.day7.amplify;

import java.util.List;

import org.junit.jupiter.api.Test;

import static com.github.timtebeek.day7.amplify.AmplifyThrust.executeInLoop;
import static com.github.timtebeek.day7.amplify.AmplifyThrust.executeInSequence;
import static com.github.timtebeek.day7.amplify.AmplifyThrust.findOptiomalPermutationDay1;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AmplifyThrustTest {

	@Test
	void testExecuteSample1() throws InterruptedException {
		long[] memory = {
				3, 15,
				3, 16,
				1002, 16, 10, 16,
				1, 16, 15, 15,
				4, 15,
				99,
				0, 0 };
		long maxThruster = executeInSequence(List.of(4l, 3l, 2l, 1l, 0l), memory);
		assertEquals(43210, maxThruster);
	}

	@Test
	void testExecuteSample2() throws InterruptedException {
		long[] memory = {
				3, 23, 3, 24, 1002, 24, 10, 24, 1002, 23, -1, 23,
				101, 5, 23, 23, 1, 24, 23, 23, 4, 23, 99, 0, 0 };
		long maxThruster = executeInSequence(List.of(0l, 1l, 2l, 3l, 4l), memory);
		assertEquals(54321, maxThruster);
	}

	@Test
	void testExecuteSample3() throws InterruptedException {
		long[] memory = {
				3, 31, 3, 32, 1002, 32, 10, 32, 1001, 31, -2, 31, 1007, 31, 0, 33,
				1002, 33, 7, 33, 1, 33, 31, 31, 1, 32, 31, 31, 4, 31, 99, 0, 0, 0 };
		long maxThruster = executeInSequence(List.of(1l, 0l, 4l, 3l, 2l), memory);
		assertEquals(65210, maxThruster);
	}

	@Test
	void testPart1() throws Exception {
		long[] memory = {
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

	// Part 2
	@Test
	void testExecuteLoopSample1() throws Exception {
		long[] memory = {
				3, 26,
				1001, 26, -4, 26,
				3, 27,
				1002, 27, 2, 27,
				1, 27, 26, 27,
				4, 27,
				1001, 28, -1, 28,
				1005, 28, 6,
				99,
				0, 0, 5 };
		assertEquals(139629729, executeInLoop(List.of(9l, 8l, 7l, 6l, 5l), memory));
	}

	@Test
	void testExecuteLoopSample2() throws Exception {
		long[] memory = {
				3, 52,
				1001, 52, -5, 52,
				3, 53,
				1, 52, 56, 54,
				1007, 54, 5, 55,
				1005, 55, 26,
				1001, 54, -5, 54,
				1105, 1, 12,
				1, 53, 54, 53,
				1008, 54, 0, 55,
				1001, 55, 1, 55,
				2, 53, 55, 53,
				4, 53,
				1001, 56, -1, 56,
				1005, 56, 6,
				99, 0, 0, 0, 0, 10 };
		assertEquals(18216, executeInLoop(List.of(9l, 7l, 8l, 5l, 6l), memory));
	}

}
