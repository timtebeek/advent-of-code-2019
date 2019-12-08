package com.github.timtebeek.day8.rover;

import java.util.List;

import com.google.common.base.Splitter;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

class Rover {

	public static List<List<String>> layerize(int width, int height, String image) {
		return Splitter.fixedLength(width * height).splitToList(image).stream()
				.map(layer -> Splitter.fixedLength(width).splitToList(layer))
				.collect(toList());
	}

	public static long part1(int width, int height, String image) {
		return Splitter.fixedLength(height * width).splitToList(image).stream()
				.map(str -> str.chars().map(Character::getNumericValue).sorted().boxed()
						.collect(groupingBy(identity(), counting())))
				.sorted((o1, o2) -> Long.compare(o1.get(0), o2.get(0)))
				.findFirst()
				.map(map -> map.get(1) * map.get(2)).get();
	}

	public static void part2(int width, int height, String image) {

	}

}
