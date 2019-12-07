package com.github.timtebeek.day2.opcode;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

class Opcodes {
	public static void main(String[] args) throws Exception {
		System.out.println(new Opcodes().calculatePart1());
		System.out.println(new Opcodes().calculatePart2(19690720));
	}

	int calculatePart1() throws Exception {
		// Read first line of input into memory
		int[] memory = readMemory();

		// Restore 1202 program alarm state
		return calculcateWithNounAndVerb(12, 2, memory);
	}

	private int[] readMemory() throws URISyntaxException, IOException {
		Path input = Paths.get(getClass().getResource("opcodes.txt").toURI());
		return Files.lines(input).findFirst().stream()
				.flatMapToInt(str -> Stream.of(str.split(",")).mapToInt(Integer::valueOf))
				.toArray();
	}

	int calculatePart2(int targetOutput) throws Exception {
		int[] memory = readMemory();
		for (int noun = 0; noun < 100; noun++) {
			for (int verb = 0; verb < 100; verb++) {
				int output = calculcateWithNounAndVerb(noun, verb, memory.clone());
				if (output == targetOutput) {
					return 100 * noun + verb;
				}
			}
		}
		throw new IllegalStateException("Could not calculcate " + targetOutput + " from memory");
	}

	private int calculcateWithNounAndVerb(int noun, int verb, int[] memory) {
		memory[1] = noun;
		memory[2] = verb;
		return execute(memory);
	}

	static int execute(int[] memory) {
		int pointer = 0;
		int instruction = memory[pointer];
		do {
			// Extract instructions, values and target
			int leftParam = memory[memory[pointer + 1]];
			int rightParam = memory[memory[pointer + 2]];
			int targetAddress = memory[pointer + 3];

			// Execute instructions
			if (instruction == 1) {
				memory[targetAddress] = leftParam + rightParam;
			} else if (instruction == 2) {
				memory[targetAddress] = leftParam * rightParam;
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
