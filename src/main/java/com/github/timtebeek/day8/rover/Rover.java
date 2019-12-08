package com.github.timtebeek.day8.rover;

import java.util.Collections;
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

	public static char[][] part2(int width, int height, String image) {
		List<List<String>> layers = layerize(width, height, image);
		char[][] projectFrontToBack = projectFrontToBack(width, height, layers);
		for (char[] cs : projectFrontToBack) {
			System.out.println(cs);
		}
		System.out.println();
		char[][] projectBackToFront = projectBackToFront(width, height, layers);
		for (char[] cs : projectBackToFront) {
			System.out.println(cs);
		}
		return projectBackToFront;
	}

	private static char[][] projectFrontToBack(int width, int height, List<List<String>> layers) {
		char[][] projection = new char[height][width];
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				projection[i][j] = '2';
			}
		}

		for (List<String> layer : layers) {
			for (int y = 0; y < layer.size(); y++) {
				String row = layer.get(y);
				for (int x = 0; x < row.length(); x++) {
					if (projection[y][x] == '2') {
						switch (row.charAt(x)) {
						case '0':
							projection[y][x] = '0';
							break;
						case '1':
							projection[y][x] = ' ';
							break;
						}
					}
				}
			}
		}
		return projection;
	}

	private static char[][] projectBackToFront(int width, int height, List<List<String>> layers) {
		Collections.reverse(layers); // Back to front
		char[][] projection = new char[height][width];
		for (List<String> list : layers) {
			for (int y = 0; y < list.size(); y++) {
				String row = list.get(y);
				char[] chars = row.toCharArray();
				for (int x = 0; x < chars.length; x++) {
					char c = chars[x];
					switch (c) {
					case '0':
						projection[y][x] = '0';
						break;
					case '1':
						projection[y][x] = ' ';
						break;
					}
				}
			}
		}
		return projection;
	}

}
