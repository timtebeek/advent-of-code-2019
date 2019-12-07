package com.github.timtebeek.day5.test;

import java.util.Scanner;
import java.util.stream.Stream;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThermalDiagnostics {

	public static void main(String[] args) {
		String input = "3,225,1,225,6,6,1100,1,238,225,104,0,1002,92,42,224,1001,224,-3444,224,4,224,102,8,223,223,101,4,224,224,1,224,223,223,1102,24,81,225,1101,89,36,224,101,-125,224,224,4,224,102,8,223,223,101,5,224,224,1,224,223,223,2,118,191,224,101,-880,224,224,4,224,1002,223,8,223,1001,224,7,224,1,224,223,223,1102,68,94,225,1101,85,91,225,1102,91,82,225,1102,85,77,224,101,-6545,224,224,4,224,1002,223,8,223,101,7,224,224,1,223,224,223,1101,84,20,225,102,41,36,224,101,-3321,224,224,4,224,1002,223,8,223,101,7,224,224,1,223,224,223,1,188,88,224,101,-183,224,224,4,224,1002,223,8,223,1001,224,7,224,1,224,223,223,1001,84,43,224,1001,224,-137,224,4,224,102,8,223,223,101,4,224,224,1,224,223,223,1102,71,92,225,1101,44,50,225,1102,29,47,225,101,7,195,224,101,-36,224,224,4,224,102,8,223,223,101,6,224,224,1,223,224,223,4,223,99,0,0,0,677,0,0,0,0,0,0,0,0,0,0,0,1105,0,99999,1105,227,247,1105,1,99999,1005,227,99999,1005,0,256,1105,1,99999,1106,227,99999,1106,0,265,1105,1,99999,1006,0,99999,1006,227,274,1105,1,99999,1105,1,280,1105,1,99999,1,225,225,225,1101,294,0,0,105,1,0,1105,1,99999,1106,0,300,1105,1,99999,1,225,225,225,1101,314,0,0,106,0,0,1105,1,99999,107,677,677,224,1002,223,2,223,1006,224,329,1001,223,1,223,1108,226,677,224,102,2,223,223,1006,224,344,101,1,223,223,1107,226,226,224,1002,223,2,223,1006,224,359,101,1,223,223,8,677,226,224,1002,223,2,223,1006,224,374,1001,223,1,223,1107,677,226,224,102,2,223,223,1005,224,389,1001,223,1,223,1008,677,677,224,1002,223,2,223,1006,224,404,1001,223,1,223,108,677,677,224,102,2,223,223,1005,224,419,1001,223,1,223,1107,226,677,224,102,2,223,223,1006,224,434,101,1,223,223,1008,226,226,224,1002,223,2,223,1006,224,449,1001,223,1,223,107,226,226,224,102,2,223,223,1006,224,464,1001,223,1,223,1007,677,226,224,1002,223,2,223,1006,224,479,1001,223,1,223,1108,226,226,224,102,2,223,223,1006,224,494,1001,223,1,223,8,226,226,224,1002,223,2,223,1005,224,509,1001,223,1,223,7,226,677,224,102,2,223,223,1005,224,524,101,1,223,223,1008,677,226,224,102,2,223,223,1005,224,539,101,1,223,223,107,226,677,224,1002,223,2,223,1006,224,554,1001,223,1,223,1108,677,226,224,102,2,223,223,1005,224,569,101,1,223,223,108,226,226,224,1002,223,2,223,1005,224,584,1001,223,1,223,7,677,226,224,1002,223,2,223,1005,224,599,1001,223,1,223,108,226,677,224,1002,223,2,223,1006,224,614,101,1,223,223,1007,677,677,224,1002,223,2,223,1006,224,629,101,1,223,223,7,677,677,224,102,2,223,223,1005,224,644,101,1,223,223,1007,226,226,224,1002,223,2,223,1006,224,659,1001,223,1,223,8,226,677,224,102,2,223,223,1005,224,674,1001,223,1,223,4,223,99,226";
		int[] memory = Stream.of(input.split(",")).mapToInt(Integer::valueOf).toArray();
		ThermalDiagnostics.execute(memory);
	}

	static int execute(int[] memory) {
		int pointer = 0;
		String instruction;
		while (true) {
			// Read first instruction
			instruction = String.valueOf(memory[pointer]);
			final int numberOfParameters;

			log.info("{}", memory);

			// Execute instructions
			if (instruction.endsWith("1")) {
				int firstParam = readParameterValue(instruction, 1, pointer, memory);
				int secondParam = readParameterValue(instruction, 2, pointer, memory);
				int targetAddress = memory[pointer + 3];
				memory[targetAddress] = firstParam + secondParam;
				numberOfParameters = 3;
				log.debug("{}: Adding {} + {} and storing at position {}", instruction, firstParam, secondParam,
						targetAddress);
			} else if (instruction.endsWith("2")) {
				int firstParam = readParameterValue(instruction, 1, pointer, memory);
				int secondParam = readParameterValue(instruction, 2, pointer, memory);
				int targetAddress = memory[pointer + 3];
				memory[targetAddress] = firstParam * secondParam;
				numberOfParameters = 3;
				log.debug("{}: Multiplying {} * {} and storing at position {}", instruction, firstParam, secondParam,
						targetAddress);
			} else if (instruction.endsWith("3")) {
				// Prompt user for input
				System.out.print("$ ");
				Scanner scanner = new Scanner(System.in);
				String input = scanner.nextLine();
				// Store input in memory
				int targetAddress = memory[pointer + 1];
				memory[targetAddress] = Integer.parseInt(input);
				numberOfParameters = 1;
			} else if (instruction.endsWith("4")) {
				int sourceAddress = memory[pointer + 1];
				log.debug("{}: Printing position {} + 1", instruction, pointer);
				System.out.println("> " + sourceAddress);
				numberOfParameters = 1;
			} else if (instruction.endsWith("99")) {
				System.out.println("Goodbye!");
				break;
			} else {
				throw new IllegalStateException("Illegal instruction " + memory[pointer] + " at address " + pointer);
			}

			// Skip ahead to next instruction
			pointer += (1 + numberOfParameters);
		}
		return memory[0];
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
