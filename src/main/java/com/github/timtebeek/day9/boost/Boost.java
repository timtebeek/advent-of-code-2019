package com.github.timtebeek.day9.boost;

import java.util.Map;
import java.util.concurrent.BlockingDeque;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Boost {

	static void execute(BlockingDeque<Long> inputs, BlockingDeque<Long> outputs, Map<Long, Long> memory)
			throws InterruptedException {

		long pointer = 0;
		long relativeBase = 0;
		String instruction;

		while (true) {
			// Read first instruction
			instruction = String.valueOf(memory.get(pointer));
			final int numberOfParameters;
			boolean jumped = false;


			// Execute instructions
			if (instruction.endsWith("1")) {
				log.info("{} -> [{}, {},+ {}, {}]", pointer, instruction, memory.get(pointer + 1),
						memory.get(pointer + 2), memory.get(pointer + 3));
				long firstParam = readParameterValue(instruction, 1, pointer, relativeBase, memory);
				long secondParam = readParameterValue(instruction, 2, pointer, relativeBase, memory);
				long targetAddress = memory.get(pointer + 3);
				memory.put(targetAddress, firstParam + secondParam);
				numberOfParameters = 3;
			} else if (instruction.endsWith("2")) {
				log.info("{} -> [{}, {},* {}, {}]", pointer, instruction, memory.get(pointer + 1),
						memory.get(pointer + 2), memory.get(pointer + 3));
				long firstParam = readParameterValue(instruction, 1, pointer, relativeBase, memory);
				long secondParam = readParameterValue(instruction, 2, pointer, relativeBase, memory);
				long targetAddress = memory.get(pointer + 3);
				memory.put(targetAddress, firstParam * secondParam);
				numberOfParameters = 3;
			} else if (instruction.endsWith("3")) {
				log.info("{} -> [{}, {}]", pointer, instruction, memory.get(pointer + 1));
				// Store input in memory
				long targetAddress = memory.get(pointer + 1);
				memory.put(targetAddress, inputs.takeFirst());
				numberOfParameters = 1;
			} else if (instruction.endsWith("4")) {
				log.info("{} -> [{}, {}]", pointer, instruction, memory.get(pointer + 1));
				// Print value at position
				long value = readParameterValue(instruction, 1, pointer, relativeBase, memory);
				outputs.putLast(value);
				numberOfParameters = 1;
			} else if (instruction.endsWith("5")) {
				log.info("{} -> [{}, {}, {}]", pointer, instruction, memory.get(pointer + 1), memory.get(pointer + 2));
				// jump-if-true
				long firstParam = readParameterValue(instruction, 1, pointer, relativeBase, memory);
				long secondParam = readParameterValue(instruction, 2, pointer, relativeBase, memory);
				if (firstParam != 0) {
					pointer = secondParam;
					jumped = true;
				}
				numberOfParameters = 2;
			} else if (instruction.endsWith("6")) {
				log.info("{} -> [{}, {}, {}]", pointer, instruction, memory.get(pointer + 1), memory.get(pointer + 2));
				// jump-if-false
				long firstParam = readParameterValue(instruction, 1, pointer, relativeBase, memory);
				long secondParam = readParameterValue(instruction, 2, pointer, relativeBase, memory);
				if (firstParam == 0) {
					pointer = secondParam;
					jumped = true;
				}
				numberOfParameters = 2;
			} else if (instruction.endsWith("7")) {
				log.info("{} -> [{}, {},< {}, {}]", pointer, instruction, memory.get(pointer + 1),
						memory.get(pointer + 2), memory.get(pointer + 3));
				// less than
				long firstParam = readParameterValue(instruction, 1, pointer, relativeBase, memory);
				long secondParam = readParameterValue(instruction, 2, pointer, relativeBase, memory);
				long targetAddress = memory.get(pointer + 3);
				memory.put(targetAddress, firstParam < secondParam ? 1 : 0l);
				numberOfParameters = 3;
			} else if (instruction.endsWith("8")) {
				log.info("{} -> [{}, {},== {}, {}]", pointer, instruction, memory.get(pointer + 1),
						memory.get(pointer + 2), memory.get(pointer + 3));
				// equals
				long firstParam = readParameterValue(instruction, 1, pointer, relativeBase, memory);
				long secondParam = readParameterValue(instruction, 2, pointer, relativeBase, memory);
				long targetAddress = memory.get(pointer + 3);
				memory.put(targetAddress, firstParam == secondParam ? 1 : 0l);
				numberOfParameters = 3;
			} else if (instruction.endsWith("99")) {
				// halt
				System.out.println("Goodbye! " + outputs.peekLast());
				return;
			} else if (instruction.endsWith("9")) {
				log.info("{} -> [{}, {}]", pointer, instruction, memory.get(pointer + 1));
				// adjust relative base
				long firstParam = readParameterValue(instruction, 1, pointer, relativeBase, memory);
				relativeBase += firstParam;
				numberOfParameters = 1;
			} else {
				throw new IllegalStateException(
						"Illegal instruction " + memory.get(pointer) + " at address " + pointer);
			}

			// Skip ahead to next instruction
			if (!jumped) {
				pointer += (1 + numberOfParameters);
			}
		}
	}

	static long readParameterValue(String instruction, int parameter, long pointer, long relativeBase,
			Map<Long, Long> memory) {
		// Account for two digit opcode when determining character
		int index = 1 + parameter;

		// Determine mode: Either 0 for memory position, or 1 for immediate value
		int mode = 0;
		if (index < instruction.length()) {
			char charAt = new StringBuilder(instruction).reverse().charAt(index);
			mode = Character.getNumericValue(charAt);
		}

		// Extract parameter value
		long parameterValue = memory.get(pointer + parameter);

		// Return value at referenced memory location
		if (mode == 0) {
			return memory.getOrDefault(parameterValue, 0L);
		}

		// Return immediate value
		if (mode == 1) {
			return parameterValue;
		}

		// Return relative value
		if (mode == 2) {
			return memory.getOrDefault(relativeBase + parameterValue, 0L);
		}

		throw new IllegalStateException("Mode " + mode);
	}

}
