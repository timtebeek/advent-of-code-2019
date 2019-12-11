package com.github.timtebeek.day9.boost;

import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.LongStream;

import com.google.common.collect.Streams;
import lombok.extern.slf4j.Slf4j;

import static java.util.stream.Collectors.toMap;

@Slf4j
public class IntcodeComputer {

	public static long execute(long input, long[] program) throws InterruptedException {
		Map<Long, Long> memory = convertToIndexedMemory(program);
		BlockingDeque<Long> inputs = new LinkedBlockingDeque<>();
		inputs.putFirst(input);
		BlockingDeque<Long> outputs = new LinkedBlockingDeque<>();
		execute(inputs, outputs, memory);
		return outputs.peekLast();
	}

	public static Map<Long, Long> convertToIndexedMemory(long[] program) {
		return Streams.zip(
				LongStream.range(0, program.length).boxed(),
				LongStream.of(program).boxed(),
				Map::entry)
				.collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	public static boolean execute(BlockingDeque<Long> inputs, BlockingDeque<Long> outputs, Map<Long, Long> memory)
			throws InterruptedException {

		long pointer = 0;
		long relativeBase = 0;
		String instruction;

		while (true) {
			// Read first instruction
			instruction = String.valueOf(memory.get(pointer));
			final int numberOfParameters;
			boolean jumped = false;

			log.info("Mem: {} -> {}", pointer, memory);

			// Execute instructions
			if (instruction.endsWith("1")) {
				long firstParam = readParameterValue(instruction, 1, pointer, relativeBase, memory);
				long secondParam = readParameterValue(instruction, 2, pointer, relativeBase, memory);
				long targetAddress = memory.get(pointer + 3);
				long value = firstParam + secondParam;
				log.info("{} -> [{}, {},+ {}, {}] ({})", pointer, instruction, memory.get(pointer + 1),
						memory.get(pointer + 2), memory.get(pointer + 3), value);
				memory.put(targetAddress, value);
				numberOfParameters = 3;
			} else if (instruction.endsWith("2")) {
				long firstParam = readParameterValue(instruction, 1, pointer, relativeBase, memory);
				long secondParam = readParameterValue(instruction, 2, pointer, relativeBase, memory);
				long targetAddress = memory.get(pointer + 3);
				long value = firstParam * secondParam;
				log.info("{} -> [{}, {},* {}, {}] ({})", pointer, instruction, memory.get(pointer + 1),
						memory.get(pointer + 2), memory.get(pointer + 3), value);
				memory.put(targetAddress, value);
				numberOfParameters = 3;
			} else if (instruction.endsWith("3")) {
				// Store input in memory
				long targetAddress = memory.get(pointer + 1);
				Long read = inputs.takeFirst();
				memory.put(targetAddress, read);
				log.info("{} -> [{}, {}] (read: {})", pointer, instruction, memory.get(pointer + 1), read);
				numberOfParameters = 1;
			} else if (instruction.endsWith("4")) {
				// Print value at position
				long value = readParameterValue(instruction, 1, pointer, relativeBase, memory);
				outputs.putLast(value);
				log.info("{} -> [{}, {}] (wrote: {})", pointer, instruction, memory.get(pointer + 1), outputs);
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
				return false;
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
		// Determine mode: Either 0 for memory position, or 1 for immediate value
		int mode = determineParameterMode(instruction, parameter);

		// Extract parameter value
		long valueAtPointerPlusParameter = memory.get(pointer + parameter);

		// Return value at referenced memory location
		if (mode == 0) {
			return memory.get(valueAtPointerPlusParameter);
		}

		// Return immediate value
		if (mode == 1) {
			return valueAtPointerPlusParameter;
		}

		// Return relative value
		if (mode == 2) {
			return memory.getOrDefault(relativeBase + valueAtPointerPlusParameter, 0L);
		}

		throw new IllegalStateException("Mode " + mode);
	}

	private static int determineParameterMode(String instruction, int parameter) {
		// Account for two digit opcode when determining character
		int index = 1 + parameter;
		if (index < instruction.length()) {
			char charAt = new StringBuilder(instruction).reverse().charAt(index);
			return Character.getNumericValue(charAt);
		}
		return 0;
	}

}
