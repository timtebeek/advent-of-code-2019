package com.github.timtebeek.day7.amplify;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import com.github.timtebeek.day9.boost.IntcodeComputer;
import com.google.common.collect.Collections2;
import lombok.Data;

import static com.github.timtebeek.day9.boost.IntcodeComputer.convertToIndexedMemory;

public class AmplifyThrust {
	static long findOptiomalPermutationDay1(long[] memory) {
		Collection<List<Long>> permutations = Collections2.permutations(List.of(0L, 1L, 2L, 3L, 4L));
		return permutations.stream()
				.mapToLong(perm -> {
					try {
						return executeInSequence(perm, memory);
					} catch (InterruptedException e) {
						throw new IllegalStateException(e);
					}
				})
				.max()
				.getAsLong();
	}

	static long executeInSequence(List<Long> phases, long[] memory) throws InterruptedException {
		Amplifier2 A = new Amplifier2(memory);
		Amplifier2 B = new Amplifier2(memory);
		Amplifier2 C = new Amplifier2(memory);
		Amplifier2 D = new Amplifier2(memory);
		Amplifier2 E = new Amplifier2(memory);

		// Wire up signals
		B.input = A.output;
		C.input = B.output;
		D.input = C.output;
		E.input = D.output;

		// Provide phases as first input
		A.input.putFirst(phases.get(0));
		B.input.putFirst(phases.get(1));
		C.input.putFirst(phases.get(2));
		D.input.putFirst(phases.get(3));
		E.input.putFirst(phases.get(4));

		// Provide zero as first signal to A
		A.input.putLast(0L);

		// Execute all once in sequence
		A.execute();
		B.execute();
		C.execute();
		D.execute();
		E.execute();

		// Return last signal from E
		return E.output.peekLast();
	}

}

@Data
class Amplifier2 {

	final Map<Long, Long> memory;
	BlockingDeque<Long> input = new LinkedBlockingDeque<>();
	BlockingDeque<Long> output = new LinkedBlockingDeque<>();

	public Amplifier2(long[] program) {
		memory = convertToIndexedMemory(program);
	}

	void execute() throws InterruptedException {
		IntcodeComputer.execute(input, output, memory);
	}
}