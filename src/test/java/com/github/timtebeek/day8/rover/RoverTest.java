package com.github.timtebeek.day8.rover;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RoverTest {

	@Test
	void testSample() {
		List<List<String>> layers = Rover.layerize(3, 2, "123456789012");
		assertEquals(2, layers.size());
		assertEquals("123", layers.get(0).get(0));
		assertEquals("456", layers.get(0).get(1));
		assertEquals("789", layers.get(1).get(0));
		assertEquals("012", layers.get(1).get(1));
	}

	@Test
	void testPart1() throws Exception {
		String image = Files.lines(Paths.get(getClass().getResource("image.txt").toURI())).findFirst().get();
		long onesByTwos = Rover.part1(25, 6, image);
		assertEquals(1703, onesByTwos);
	}

	@Test
	void testPart2() throws Exception {
		String image = Files.lines(Paths.get(getClass().getResource("image.txt").toURI())).findFirst().get();
		Rover.part2(25, 6, image);
	}

}
