package com.github.timtebeek.day7.amplify;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;

import com.google.common.collect.Collections2;

public class AmplifyThrust {
	static long findOptiomalPermutationDay1(int[] memory) {
		Collection<List<Integer>> permutations = Collections2.permutations(List.of(0, 1, 2, 3, 4));
		return permutations.stream().mapToLong(perm -> executeInSequence(0, perm.iterator(), memory)).max().getAsLong();
	}

	static int executeInSequence(int signal, Iterator<Integer> phases, int[] memory) {
		signal = AmplifyThrust.execute(IntStream.of(phases.next(), signal).iterator(), memory.clone());
		signal = AmplifyThrust.execute(IntStream.of(phases.next(), signal).iterator(), memory.clone());
		signal = AmplifyThrust.execute(IntStream.of(phases.next(), signal).iterator(), memory.clone());
		signal = AmplifyThrust.execute(IntStream.of(phases.next(), signal).iterator(), memory.clone());
		signal = AmplifyThrust.execute(IntStream.of(phases.next(), signal).iterator(), memory.clone());
		return signal;
	}

	static int execute(Iterator<Integer> inputs, int[] memory) {
		int pointer = 0;
		String instruction;
		while (true) {
			// Read first instruction
			instruction = String.valueOf(memory[pointer]);
			final int numberOfParameters;
			boolean jumped = false;

			// Execute instructions
			if (instruction.endsWith("1")) {
				int firstParam = readParameterValue(instruction, 1, pointer, memory);
				int secondParam = readParameterValue(instruction, 2, pointer, memory);
				int targetAddress = memory[pointer + 3];
				memory[targetAddress] = firstParam + secondParam;
				numberOfParameters = 3;
			} else if (instruction.endsWith("2")) {
				int firstParam = readParameterValue(instruction, 1, pointer, memory);
				int secondParam = readParameterValue(instruction, 2, pointer, memory);
				int targetAddress = memory[pointer + 3];
				memory[targetAddress] = firstParam * secondParam;
				numberOfParameters = 3;
			} else if (instruction.endsWith("3")) {
				// Store input in memory
				int targetAddress = memory[pointer + 1];
				memory[targetAddress] = inputs.next();
				numberOfParameters = 1;
			} else if (instruction.endsWith("4")) {
				// Print value at position
				int value = readParameterValue(instruction, 1, pointer, memory);
				return value;
			} else if (instruction.endsWith("5")) {
				// jump-if-true
				int firstParam = readParameterValue(instruction, 1, pointer, memory);
				int secondParam = readParameterValue(instruction, 2, pointer, memory);
				if (firstParam != 0) {
					pointer = secondParam;
					jumped = true;
				}
				numberOfParameters = 2;
			} else if (instruction.endsWith("6")) {
				// jump-if-false
				int firstParam = readParameterValue(instruction, 1, pointer, memory);
				int secondParam = readParameterValue(instruction, 2, pointer, memory);
				if (firstParam == 0) {
					pointer = secondParam;
					jumped = true;
				}
				numberOfParameters = 2;
			} else if (instruction.endsWith("7")) {
				// less than
				int firstParam = readParameterValue(instruction, 1, pointer, memory);
				int secondParam = readParameterValue(instruction, 2, pointer, memory);
				int targetAddress = memory[pointer + 3];
				memory[targetAddress] = firstParam < secondParam ? 1 : 0;
				numberOfParameters = 3;
			} else if (instruction.endsWith("8")) {
				// equals
				int firstParam = readParameterValue(instruction, 1, pointer, memory);
				int secondParam = readParameterValue(instruction, 2, pointer, memory);
				int targetAddress = memory[pointer + 3];
				memory[targetAddress] = firstParam == secondParam ? 1 : 0;
				numberOfParameters = 3;
			} else {
				throw new IllegalStateException("Illegal instruction " + memory[pointer] + " at address " + pointer);
			}

			// Skip ahead to next instruction
			if (!jumped) {
				pointer += (1 + numberOfParameters);
			}
		}
	}

	static int readParameterValue(String instruction, int parameter, int pointer, int[] memory) {
		// Account for two digit opcode when determining character
		int index = 1 + parameter;

		// Determine mode: Either 0 for memory position, or 1 for immediate value
		int mode = 0;
		if (index < instruction.length()) {
			char charAt = new StringBuilder(instruction).reverse().charAt(index);
			mode = Character.getNumericValue(charAt);
		}

		// Extract parameter value
		int parameterValue = memory[pointer + parameter];

		// Return immediate value
		if (mode == 1) {
			return parameterValue;
		}

		// Return value at referenced memory location
		if (mode == 0) {
			return memory[parameterValue];
		}

		throw new IllegalStateException("Mode " + mode);
	}
}
