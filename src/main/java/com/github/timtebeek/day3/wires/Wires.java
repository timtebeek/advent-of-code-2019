package com.github.timtebeek.day3.wires;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class Wires {
	static int distanceToClosestIntersection(String wireA, String wireB) {
		List<Point> pointsA = traceWire(wireA);
		List<Point> pointsB = traceWire(wireB);

		// Diagnostics
		// plot(pointsA, pointsB);

		// Retain only intersections
		pointsA.retainAll(pointsB);
		log.info("Intersections: {}", pointsA);

		// Return shortest distance to intersection
		return pointsA.stream()
				.mapToInt(p -> Math.abs(p.getX()) + Math.abs(p.getY()))
				.min().getAsInt();
	}

	static int fewestStepToIntersection(String wireA, String wireB) {
		List<Point> pointsA = traceWire(wireA);
		List<Point> pointsB = traceWire(wireB);

		// Diagnostics
		// plot(pointsA, pointsB);

		// Retain only intersections
		List<Point> intersections = new ArrayList<>(pointsA);
		intersections.retainAll(pointsB);
		log.info("Intersections: {}", intersections);

		// Return shortest distance to intersection
		return intersections.stream()
				.mapToInt(p -> pointsA.indexOf(p) + 1 + pointsB.indexOf(p) + 1)
				.min().getAsInt();
	}

	private static List<Point> traceWire(String wireA) {
		List<Point> points = new ArrayList<>();
		Point current = new Point(0, 0);
		for (String section : wireA.split(",")) {
			char direction = section.charAt(0);
			int length = Integer.parseInt(section.substring(1));
			for (int i = 0; i < length; i++) {
				current = current.step(direction);
				points.add(current);
			}
		}
		return points;
	}

	private static void plot(Collection<Point> pointsA, Collection<Point> pointsB) {
		int maxX = Stream.concat(pointsA.stream(), pointsB.stream())
				.mapToInt(Point::getX)
				.max().getAsInt();
		int maxY = Stream.concat(pointsA.stream(), pointsB.stream())
				.mapToInt(Point::getY)
				.max().getAsInt();
		for (int y = maxY; 0 <= y; y--) {
			for (int x = 0; x <= maxX; x++) {
				Point xy = new Point(x, y);
				boolean hasA = pointsA.contains(xy);
				boolean hasB = pointsB.contains(xy);
				if (hasA != hasB) {
					System.out.print(hasA ? 'A' : 'B');
				} else {
					System.out.print(hasA ? 'X' : '.');
				}
			}
			System.out.println(" " + y);
		}
		System.out.println("01234567890");
	}
}

@Value
class Point {
	int x;
	int y;

	Point step(char direction) {
		switch (direction) {
		case 'U':
			return new Point(x, y + 1);
		case 'D':
			return new Point(x, y - 1);
		case 'L':
			return new Point(x - 1, y);
		case 'R':
			return new Point(x + 1, y);
		default:
			throw new IllegalArgumentException("Illegal direction " + direction);
		}
	}

	@Override
	public String toString() {
		return String.format("(%d,%d)", x, y);
	}

}