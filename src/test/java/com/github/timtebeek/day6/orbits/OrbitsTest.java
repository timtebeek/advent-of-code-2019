package com.github.timtebeek.day6.orbits;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrbitsTest {

	@Test
	void testSample() throws Exception {
		Path path = Paths.get(getClass().getResource("sample.txt").toURI());
		assertEquals(42, Orbits.totalNumberOfOrbits(Files.lines(path).collect(Collectors.toList())));
	}

	@Test
	void testPart1() throws Exception {
		Path path = Paths.get(getClass().getResource("map.txt").toURI());
		assertEquals(234446, Orbits.totalNumberOfOrbits(Files.lines(path).collect(Collectors.toList())));
	}

}
