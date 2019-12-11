package com.github.timtebeek.day7.amplify;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Stream;

import com.github.timtebeek.day9.boost.Computer;
import com.google.common.collect.Collections2;

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
		Computer A = new Computer("A", memory);
		Computer B = new Computer("B", memory);
		Computer C = new Computer("C", memory);
		Computer D = new Computer("D", memory);
		Computer E = new Computer("E", memory);

		// Wire up thrusters
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

	static long findOptiomalPermutationDay2(long[] memory) {
		Collection<List<Long>> permutations = Collections2.permutations(List.of(5l, 6l, 7l, 8l, 9l));
		return permutations.stream().mapToLong(perm -> {
			try {
				return executeInLoop(perm, memory);
			} catch (InterruptedException e) {
				throw new IllegalStateException(e);
			}
		}).max().getAsLong();
	}

	static long executeInLoop(List<Long> phases, long[] memory) throws InterruptedException {
		Computer A = new Computer("A", memory);
		Computer B = new Computer("B", memory);
		Computer C = new Computer("C", memory);
		Computer D = new Computer("D", memory);
		Computer E = new Computer("E", memory);

		// Wire up thrusters
		A.input = E.output;
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

		// "To start the process, a 0 signal is sent to amplifier A's input exactly once."
		A.input.putLast(0l);


		// Run all at the same time, since they are waiting for each others inputs
		ExecutorService executorService = Executors.newFixedThreadPool(5);
		Stream.of(A, B, C, D, E).forEach(amp -> executorService.submit(() -> {
			try {
				amp.execute();
			} catch (InterruptedException e) {
				throw new IllegalStateException(e);
			}
		}));
		executorService.shutdown();
		executorService.awaitTermination(30, TimeUnit.SECONDS);

		return E.output.peekLast();
	}

}