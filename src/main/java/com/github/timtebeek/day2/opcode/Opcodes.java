package com.github.timtebeek.day2.opcode;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

class Opcodes {
	public static void main(String[] args) throws Exception {
		int positionZero = new Opcodes().calculate();
		System.out.println(positionZero);
	}

	int calculate() throws Exception {
		// Convert first line into
		Path input = Paths.get(getClass().getResource("opcodes.txt").toURI());
		int[] opcodes = Files.lines(input).findFirst().stream()
				.flatMapToInt(str -> Stream.of(str.split(",")).mapToInt(Integer::valueOf))
				.toArray();

		// Restore 1202 program alarm state
		opcodes[1] = 12;
		opcodes[2] = 2;

		return execute(opcodes);
	}

	static int execute(int[] memory) {
		int pointer = 0;
		int instruction = memory[pointer];
		do {
			// Extract instructions, values and target
			int leftValue = memory[memory[pointer + 1]];
			int rightValue = memory[memory[pointer + 2]];
			int targetAddress = memory[pointer + 3];

			// Execute instructions
			if (instruction == 1) {
				memory[targetAddress] = leftValue + rightValue;
			} else if (instruction == 2) {
				memory[targetAddress] = leftValue * rightValue;
			} else {
				throw new IllegalStateException("Illegal instruction " + memory[pointer] + " at address " + pointer);
			}

			// Skip ahead to next instruction
			pointer += 4;
			instruction = memory[pointer];
		} while (instruction != 99);

		// Return value at position 0
		return memory[0];
	}
}
