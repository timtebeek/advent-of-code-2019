package com.github.timtebeek.day6.orbits;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrbitsTest {

	@Test
	void testNumberOfOrbitsSample() throws Exception {
		Path path = Paths.get(getClass().getResource("sample.txt").toURI());
		List<String> lines = Files.lines(path).collect(Collectors.toList());
		Map<String, SpaceObject> allSpaceObjects = Orbits.parseMap(lines);

		long totalNumberOfOrbits = allSpaceObjects.get("COM").countOrbits(0);
		assertEquals(42, totalNumberOfOrbits);
	}

	@Test
	void testNumberOfOrbitsPart1() throws Exception {
		Path path = Paths.get(getClass().getResource("map.txt").toURI());
		List<String> lines = Files.lines(path).collect(Collectors.toList());
		Map<String, SpaceObject> allSpaceObjects = Orbits.parseMap(lines);

		long totalNumberOfOrbits = allSpaceObjects.get("COM").countOrbits(0);
		assertEquals(234446, totalNumberOfOrbits);
	}

	@Test
	void testNumberOfTransfersSample() throws Exception {
		Path path = Paths.get(getClass().getResource("sample2.txt").toURI());
		List<String> lines = Files.lines(path).collect(Collectors.toList());
		Map<String, SpaceObject> allSpaceObjects = Orbits.parseMap(lines);

		assertEquals(4, Orbits.numberOfTransfers(allSpaceObjects));
	}

	@Test
	void testNumberOfTransfersPart2() throws Exception {
		Path path = Paths.get(getClass().getResource("map.txt").toURI());
		List<String> lines = Files.lines(path).collect(Collectors.toList());
		Map<String, SpaceObject> allSpaceObjects = Orbits.parseMap(lines);

		assertEquals(385, Orbits.numberOfTransfers(allSpaceObjects));
	}

}
