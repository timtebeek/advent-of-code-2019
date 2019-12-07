package com.github.timtebeek.day7.amplify;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.List;

import com.google.common.collect.Collections2;

public class AmplifyThrustLoop {
	static long findOptiomalPermutationDay2(int[] memory) {
		Collection<List<Integer>> permutations = Collections2.permutations(List.of(5, 6, 7, 8, 9));
		return permutations.stream().mapToLong(perm -> executeInLoop(perm, memory)).max().getAsLong();
	}

	static int executeInLoop(List<Integer> phases, int[] memory) {
		Amplifier a = new Amplifier("A", memory.clone(), null);
		Amplifier b = new Amplifier("B", memory.clone(), a.signalOut);
		Amplifier c = new Amplifier("C", memory.clone(), b.signalOut);
		Amplifier d = new Amplifier("D", memory.clone(), c.signalOut);
		Amplifier e = new Amplifier("E", memory.clone(), d.signalOut);
		a.signalIn = e.signalOut;

		// "Provide each amplifier its phase setting at its first input instruction; all further input/output
		// instructions are for signals."
		a.signalIn.push(phases.get(0));
		b.signalIn.push(phases.get(1));
		c.signalIn.push(phases.get(2));
		d.signalIn.push(phases.get(3));
		e.signalIn.push(phases.get(4));

		// "To start the process, a 0 signal is sent to amplifier A's input exactly once."
		a.signalIn.addLast(0);

		while (true) {
			a.execute();
			b.execute();
			c.execute();
			d.execute();
			e.execute();
			System.out.println(e.signalOut);
		}
	}
}

class Amplifier {
	final String name;
	final int[] memory;

	Deque<Integer> signalIn = new ArrayDeque<>();
	Deque<Integer> signalOut = new ArrayDeque<>();

	public Amplifier(String name, int[] memory, Deque<Integer> signalIn) {
		this.name = name;
		this.memory = memory;
		this.signalIn = signalIn;
	}

	void execute() {
		int pointer = 0;
		String instruction;
		while (true) {
			// Read first instruction
			instruction = String.valueOf(memory[pointer]);
			System.out.println(name + ": pointer = " + pointer + ", instruction = " + instruction);
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
				memory[targetAddress] = signalIn.pop();
				numberOfParameters = 1;
			} else if (instruction.endsWith("4")) {
				// Produce output
				int firstParam = readParameterValue(instruction, 1, pointer, memory);
				signalOut.addLast(firstParam);
				numberOfParameters = 1;
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
			} else if (instruction.endsWith("99")) {
				System.out.println(name + ": Goodbye!");
				break;
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
