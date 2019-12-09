package com.github.timtebeek.day9.boost;

import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.LongStream;

import com.google.common.collect.Streams;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static java.util.stream.Collectors.toMap;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
class BoostTest {

	@Test
	void testProduce16Digits() throws InterruptedException {
		long[] program = { 1102, 34915192, 34915192, 7, 4, 7, 99, 0 };
		Map<Long, Long> memory = convertToIndexedMemory(program);

		BlockingDeque<Long> inputs = new LinkedBlockingDeque<>();
		BlockingDeque<Long> outputs = new LinkedBlockingDeque<>();
		Boost.execute(inputs, outputs, memory);
		Long peekLast = outputs.peekLast();
		assertEquals(1219070632396864l, peekLast);
	}

	@Test
	void testProduceLargeNumber() throws InterruptedException {
		long[] program = { 104, 1125899906842624L, 99 };
		Map<Long, Long> memory = convertToIndexedMemory(program);

		BlockingDeque<Long> inputs = new LinkedBlockingDeque<>();
		BlockingDeque<Long> outputs = new LinkedBlockingDeque<>();
		Boost.execute(inputs, outputs, memory);
		Long peekLast = outputs.peekLast();
		assertEquals(1125899906842624L, peekLast);
	}

	@Test
	void testProduceCopy() throws InterruptedException {
		long[] program = { 109, 1, 204, -1, 1001, 100, 1, 100, 1008, 100, 16, 101, 1006, 101, 0, 99 };
		Map<Long, Long> memory = convertToIndexedMemory(program);

		BlockingDeque<Long> inputs = new LinkedBlockingDeque<>();
		BlockingDeque<Long> outputs = new LinkedBlockingDeque<>();
		Boost.execute(inputs, outputs, memory);
		Long peekLast = outputs.peekLast();
		assertEquals(99, peekLast);
	}

	private static Map<Long, Long> convertToIndexedMemory(long[] program) {
		log.info("{}", program);
		Map<Long, Long> memory = Streams.zip(
				LongStream.range(0, program.length).boxed(),
				LongStream.of(program).boxed(),
				Map::entry)
				.collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
		return memory;
	}

}
