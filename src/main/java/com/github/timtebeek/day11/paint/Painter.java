package com.github.timtebeek.day11.paint;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.github.timtebeek.day9.boost.Computer;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Painter {

	public static int countPaintedPanels(long[] program) throws InterruptedException {

		Computer painter = new Computer("", program);

		// Start painter in background
		ExecutorService executorService = Executors.newSingleThreadExecutor();
		executorService.submit(() -> {
			try {
				painter.execute();
			} catch (InterruptedException e) {
				throw new IllegalStateException(e);
			}
		});
		executorService.shutdown();
		executorService.awaitTermination(1, TimeUnit.SECONDS);

		// Initialize hull & painter positon
		long[][] hull = new long[5][5];
		Point position = new Point(2, 2);
		Orientation orientation = Orientation.UP;
		Set<Point> painted = new HashSet<>();

		do {
			// Log progress
			log.info("Position: {}, Orientation: {}, Painted: {} - {}", position, orientation, painted.size(), painted);

			// Determine color
			long currentColor = hull[position.getX()][position.getY()];
			painter.input.putLast(currentColor);

			// Apply color
			long newColor = painter.output.takeLast();
			hull[position.getX()][position.getY()] = newColor;
			painted.add(position);

			// Determine direction
			long direction = painter.output.takeLast();
			orientation = orientation.turn(direction);

			// Take step
			position = position.move(orientation);

			// Log outcome
			log.info("Painted: {}, Turning: {}", newColor == 0 ? "black" : "white", direction == 0 ? "left" : "right");

			paint(position, hull, orientation);
		} while (!executorService.isTerminated());

		return painted.size();
	}

	private static void paint(Point position, long[][] hull, Orientation orientation) {
		for (int x = 0; x < 5; x++) {
			for (int y = 0; y < 5; y++) {
				Point at = new Point(x, y);
				if (at.equals(position)) {
					switch (orientation) {
					case UP:
						System.out.print('^');
						break;
					case DOWN:
						System.out.print('v');
						break;
					case LEFT:
						System.out.print('<');
						break;
					case RIGHT:
						System.out.print('>');
						break;
					}
				} else {
					System.out.print(hull[at.getX()][at.getY()] == 0L ? '.' : '#');
				}
			}
			System.out.println();
		}
	}

}

@Value
class Point {
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

	private Point withX(int newX) {
		if (0 <= newX && newX < 5) {
			return new Point(newX, y);
		}
		return this;
	}

	private Point withY(int newY) {
		if (0 <= newY && newY < 5) {
			return new Point(x, newY);
		}
		return this;
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
			case LEFT:
				return UP;
			case DOWN:
				return LEFT;
			case RIGHT:
				return DOWN;
			}
		}
		throw new IllegalArgumentException("Direction " + direction);
	}

}