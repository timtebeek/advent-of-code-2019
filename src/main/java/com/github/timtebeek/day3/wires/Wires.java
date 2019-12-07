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

	private static List<Point> traceWire(String wire) {
		List<Point> points = new ArrayList<>();
		int x = 0;
		int y = 0;
		for (final String section : wire.split(",")) {
			char direction = section.charAt(0);
			int length = Integer.parseInt(section.substring(1));
			for (int i = 0; i < length; i++) {
				switch (direction) {
				case 'U':
					y++;
					break;
				case 'D':
					y--;
					break;
				case 'L':
					x--;
					break;
				case 'R':
					x++;
					break;
				default:
					throw new IllegalArgumentException(section);
				}
				points.add(new Point(x, y));
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
}