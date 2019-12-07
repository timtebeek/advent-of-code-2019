package com.github.timtebeek.day6.orbits;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import lombok.Data;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

class Orbits {

	static long totalNumberOfOrbits(List<String> list) {
		// Create a map containing all space objects
		Map<String, SpaceObject> allSpaceObjects = list.stream()
				.flatMap(line -> Stream.of(line.split("\\)")))
				.distinct()
				.collect(toMap(identity(), SpaceObject::new));

		// Loop over all orbits to assign children
		list.stream()
				.map(line -> line.split("\\)"))
				.forEach(orbit -> allSpaceObjects.get(orbit[0]).getChildren().add(allSpaceObjects.get(orbit[1])));

		SpaceObject centerOfMass = allSpaceObjects.get("COM");
		System.out.println(centerOfMass);
		return centerOfMass.countOrbits(0);
	}
}

@Data
class SpaceObject {
	final String name;
	Set<SpaceObject> children = new HashSet<>();

	public long countOrbits(int depth) {
		return depth + children.stream().mapToLong(child -> child.countOrbits(depth + 1)).sum();
	}
}
