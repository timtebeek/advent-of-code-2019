package com.github.timtebeek.day6.orbits;

import java.util.*;
import java.util.stream.Stream;

import lombok.Data;
import lombok.EqualsAndHashCode;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

class Orbits {

	static Map<String, SpaceObject> parseMap(List<String> list) {
		// Create a map containing all space objects
		Map<String, SpaceObject> allSpaceObjects = list.stream()
				.flatMap(line -> Stream.of(line.split("\\)")))
				.distinct()
				.collect(toMap(identity(), SpaceObject::new));

		// Loop over all orbits to assign children
		list.stream()
				.map(line -> line.split("\\)"))
				.forEach(orbit -> {
					SpaceObject left = allSpaceObjects.get(orbit[0]);
					SpaceObject right = allSpaceObjects.get(orbit[1]);
					right.setOrbits(left);
					left.getChildren().add(right);
				});
		return allSpaceObjects;
	}

	public static long numberOfTransfers(Map<String, SpaceObject> allSpaceObjects) {
		SpaceObject youOrbit = allSpaceObjects.get("YOU").getOrbits();
		SpaceObject sanOrbit = allSpaceObjects.get("SAN").getOrbits();

		List<SpaceObject> yourchain = orbitChain(youOrbit);
		List<SpaceObject> sanchain = orbitChain(sanOrbit);

		List<SpaceObject> intersections = new ArrayList<>(yourchain);
		intersections.retainAll(sanchain);
		SpaceObject first = intersections.get(0);

		return yourchain.indexOf(first) + sanchain.indexOf(first);
	}

	private static List<SpaceObject> orbitChain(SpaceObject spaceObject) {
		List<SpaceObject> chain = new ArrayList<>();
		SpaceObject current = spaceObject;
		while (current != null) {
			chain.add(current);
			current = current.getOrbits();
		}
		return chain;
	}
}

@Data
@EqualsAndHashCode(of = "name")
class SpaceObject {
	final String name;
	SpaceObject orbits;
	Set<SpaceObject> children = new HashSet<>();

	public long countOrbits(int depth) {
		return depth + children.stream().mapToLong(child -> child.countOrbits(depth + 1)).sum();
	}
}
