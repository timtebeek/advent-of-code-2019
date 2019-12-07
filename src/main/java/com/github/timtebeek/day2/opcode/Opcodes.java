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

	static int execute(int[] opcodes) {
		int position = 0;
		// Run calculations
		while (opcodes[position] != 99) {
			if (opcodes[position] == 1) {
				opcodes[opcodes[position + 3]] = opcodes[opcodes[position + 1]] + opcodes[opcodes[position + 2]];
			} else if (opcodes[position] == 2) {
				opcodes[opcodes[position + 3]] = opcodes[opcodes[position + 1]] * opcodes[opcodes[position + 2]];
			} else {
				throw new IllegalStateException("Illegal operation " + opcodes[position] + " at position " + position);
			}

			// Skip ahead to next instruction
			position += 4;
		}

		// Return value at position 0
		return opcodes[0];
	}
}
