package com.github.timtebeek.day7.amplify;

import java.util.ArrayDeque;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AmplifyThrustTest {

	@Test
	void testExecuteSample1() {
		int[] memory = new int[] { 3, 15, 3, 16, 1002, 16, 10, 16, 1, 16, 15, 15, 4, 15, 99, 0, 0 };
		int signal = 0;
		signal = AmplifyThrust.execute(new ArrayDeque<>(List.of(4, signal)), memory.clone());
		signal = AmplifyThrust.execute(new ArrayDeque<>(List.of(3, signal)), memory.clone());
		signal = AmplifyThrust.execute(new ArrayDeque<>(List.of(2, signal)), memory.clone());
		signal = AmplifyThrust.execute(new ArrayDeque<>(List.of(1, signal)), memory.clone());
		signal = AmplifyThrust.execute(new ArrayDeque<>(List.of(0, signal)), memory.clone());
		int maxThruster = signal;
		assertEquals(43210, maxThruster);
	}

	@Test
	void testExecuteSample2() {
		int[] memory = new int[] {
				3, 23, 3, 24, 1002, 24, 10, 24, 1002, 23, -1, 23,
				101, 5, 23, 23, 1, 24, 23, 23, 4, 23, 99, 0, 0 };
		int signal = 0;
		signal = AmplifyThrust.execute(new ArrayDeque<>(List.of(0, signal)), memory.clone());
		signal = AmplifyThrust.execute(new ArrayDeque<>(List.of(1, signal)), memory.clone());
		signal = AmplifyThrust.execute(new ArrayDeque<>(List.of(2, signal)), memory.clone());
		signal = AmplifyThrust.execute(new ArrayDeque<>(List.of(3, signal)), memory.clone());
		signal = AmplifyThrust.execute(new ArrayDeque<>(List.of(4, signal)), memory.clone());
		int maxThruster = signal;
		assertEquals(54321, maxThruster);
	}

	@Test
	void testExecuteSample3() {
		int[] memory = new int[] {
				3, 31, 3, 32, 1002, 32, 10, 32, 1001, 31, -2, 31, 1007, 31, 0, 33,
				1002, 33, 7, 33, 1, 33, 31, 31, 1, 32, 31, 31, 4, 31, 99, 0, 0, 0 };
		int signal = 0;
		signal = AmplifyThrust.execute(new ArrayDeque<>(List.of(1, signal)), memory.clone());
		signal = AmplifyThrust.execute(new ArrayDeque<>(List.of(0, signal)), memory.clone());
		signal = AmplifyThrust.execute(new ArrayDeque<>(List.of(4, signal)), memory.clone());
		signal = AmplifyThrust.execute(new ArrayDeque<>(List.of(3, signal)), memory.clone());
		signal = AmplifyThrust.execute(new ArrayDeque<>(List.of(2, signal)), memory.clone());
		int maxThruster = signal;
		assertEquals(65210, maxThruster);
	}

}
