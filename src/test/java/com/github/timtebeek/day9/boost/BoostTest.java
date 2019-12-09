package com.github.timtebeek.day9.boost;

import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.LongStream;

import com.google.common.collect.Streams;
import org.junit.jupiter.api.Test;

import static java.util.stream.Collectors.toMap;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BoostTest {

	@Test
	void testProduce16Digits() throws InterruptedException {
		long[] program = { 1102, 34915192, 34915192, 7, 4, 7, 99, 0 };
		Map<Long, Long> memory = Streams.zip(
				LongStream.range(0, program.length).boxed(),
				LongStream.of(program).boxed(),
				Map::entry)
				.collect(toMap(Map.Entry::getKey, Map.Entry::getValue));

		BlockingDeque<Long> inputs = new LinkedBlockingDeque<>();
		BlockingDeque<Long> outputs = new LinkedBlockingDeque<>();
		Boost.execute(inputs, outputs, memory);
		Long peekLast = outputs.peekLast();
		assertEquals(1219070632396864l, peekLast);
	}

}
