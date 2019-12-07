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
		Amplifier a = new Amplifier("A", phases.get(0), memory.clone(), null);
		Amplifier b = new Amplifier("B", phases.get(1), memory.clone(), a.signalOut);
		Amplifier c = new Amplifier("C", phases.get(2), memory.clone(), b.signalOut);
		Amplifier d = new Amplifier("D", phases.get(3), memory.clone(), c.signalOut);
		Amplifier e = new Amplifier("E", phases.get(4), memory.clone(), d.signalOut);
		a.signalIn = e.signalOut;

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
	final int phase;

	int pointer = 0;
	String instruction;
	int numberOfParameters = -1;
	boolean jumped = false;

	Deque<Integer> signalIn = new ArrayDeque<>();
	Deque<Integer> signalOut = new ArrayDeque<>();

	public Amplifier(String name, int phase, int[] memory, Deque<Integer> signalIn) {
		this.name = name;
		this.phase = phase;
		this.memory = memory;
		this.signalIn = signalIn;
	}

	void execute() {
		// "Provide each amplifier its phase setting at its first input instruction; all further input/output
		// instructions are for signals."
		signalIn.push(phase);

		while (true) {
			// Skip ahead to next instruction
			if (!jumped) {
				pointer += (1 + numberOfParameters);
			}

			// Read first instruction
			instruction = String.valueOf(memory[pointer]);
			System.out.println(String.format("%s: pointer = %d, instruction = %s", name, pointer, instruction));

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
				break;// Doubtful
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
