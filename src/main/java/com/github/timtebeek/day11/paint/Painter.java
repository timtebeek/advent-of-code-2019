package com.github.timtebeek.day11.paint;

import java.util.HashMap;
import java.util.IntSummaryStatistics;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.github.timtebeek.day9.boost.Computer;
import lombok.ToString;
import lombok.Value;
import lombok.With;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Painter {

	public static int countPaintedPanels(long[] program, long initialPanel) throws InterruptedException {

		Computer painter = new Computer("", program);

		// Start painter in background
		Thread thread = new Thread(() -> {
			try {
				painter.execute();
			} catch (InterruptedException e) {
				log.info("Interrupted painter");
				throw new IllegalStateException(e);
			}
		}, "intcode computer");
		thread.start();

		// Initialize hull & painter positon
		Point position = new Point(0, 0);
		Orientation orientation = Orientation.UP;
		Map<Point, Long> hull = new HashMap<>();
		hull.put(position, initialPanel);

		do {
			// Log progress
			log.debug("Position: {}, Orientation: {}, Painted: {}", position, orientation, hull.size());

			// Determine color
			long currentColor = hull.getOrDefault(position, 0L);
			painter.input.putLast(currentColor);

			// Stop if there's no more input
			Long newColor = painter.output.pollFirst(50, TimeUnit.MILLISECONDS);
			if (newColor == null) {
				break;
			}

			// Apply color
			hull.put(position, newColor);

			// Determine direction
			long direction = painter.output.takeFirst();
			orientation = orientation.turn(direction);

			// Take step
			position = position.move(orientation);

			// Log outcome
			log.debug("Painted: {}, Turning: {}", newColor == 0 ? "black" : "white", direction == 0 ? "left" : "right");
		} while (true);

		paint(position, hull, orientation);

		return hull.size();
	}

	private static void paint(Point position, Map<Point, Long> hull, Orientation orientation) {
		IntSummaryStatistics xstats = hull.keySet().stream().collect(Collectors.summarizingInt(Point::getX));
		IntSummaryStatistics ystats = hull.keySet().stream().collect(Collectors.summarizingInt(Point::getY));

		StringBuilder stringBuilder = new StringBuilder(
				(xstats.getMax() - xstats.getMin()) * (ystats.getMax() - ystats.getMin()));
		for (int y = ystats.getMin(); y <= ystats.getMax(); y++) {
			for (int x = xstats.getMin(); x <= xstats.getMax(); x++) {
				Point at = new Point(x, y);
				if (at.equals(position)) {
					switch (orientation) {
					case UP:
						stringBuilder.append('^');
						break;
					case DOWN:
						stringBuilder.append('v');
						break;
					case LEFT:
						stringBuilder.append('<');
						break;
					case RIGHT:
						stringBuilder.append('>');
						break;
					}
				} else {
					stringBuilder.append(hull.getOrDefault(at, 0L) == 0L ? '.' : '#');
				}
			}
			stringBuilder.append('\n');
		}
		System.out.println(stringBuilder);
	}

}

@Value
@ToString(includeFieldNames = false)
class Point {
	@With
	int x, y;

	public Point move(Orientation orientation) {
		switch (orientation) {
		case UP:
			return withY(y - 1);
		case LEFT:
			return withX(x - 1);
		case DOWN:
			return withY(y + 1);
		case RIGHT:
			return withX(x + 1);
		}
		throw new IllegalArgumentException("Orientation " + orientation);
	}
}

enum Orientation {
	UP,
	DOWN,
	LEFT,
	RIGHT;

	Orientation turn(long direction) {
		if (direction == 0) {
			// Turn left
			switch (this) {
			case UP:
				return LEFT;
			case LEFT:
				return DOWN;
			case DOWN:
				return RIGHT;
			case RIGHT:
				return UP;
			}
		} else {
			// Turn right
			switch (this) {
			case UP:
				return RIGHT;
			case RIGHT:
				return DOWN;
			case DOWN:
				return LEFT;
			case LEFT:
				return UP;
			}
		}
		throw new IllegalArgumentException("Direction " + direction);
	}

}