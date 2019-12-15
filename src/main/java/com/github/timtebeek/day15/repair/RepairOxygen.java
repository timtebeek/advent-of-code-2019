package com.github.timtebeek.day15.repair;

import java.util.HashMap;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.github.timtebeek.day9.boost.Computer;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.Value;
import lombok.With;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RepairOxygen {

	public static long run(long[] program) throws InterruptedException {
		RemoteControl remoteControl = new RemoteControl(program);

		// Start in background
		Thread thread = new Thread(() -> {
			try {
				remoteControl.execute();
			} catch (InterruptedException e) {
				throw new IllegalStateException(e);
			}
		}, "IntcodeComputer");
		thread.start();

		// Initialize screen
		return shortestPathToOxygenSystem(remoteControl, new HashMap<>(), Point.ZERO, Long.MAX_VALUE - 1);
	}

	private static long shortestPathToOxygenSystem(RemoteControl remoteControl, Map<Point, Tile> screen, Point droid,
			long shortestSoFar) throws InterruptedException {
		// Add droid to screen
		screen.put(droid, Tile.DROID);
		paint(screen);

		long shortestFromHere = shortestSoFar;
		for (Direction direction : Direction.values()) {
			// Determine where we're going
			Point newpos = droid.move(direction);
			if (screen.containsKey(newpos)) {
				// Prevent walking in a circle
				return Long.MAX_VALUE - 1;
			}

			// Take a step in direction
			Status status = remoteControl.move(direction);

			// Interpret returned status
			switch (status) {
			case WALL:
				screen.put(newpos, Tile.WALL);
				break;
			case MOVED:
				// Update screen
				screen.put(droid, Tile.EMPTY);

				// Traverse and compare
				long shortestFromNext = 1 + shortestPathToOxygenSystem(remoteControl, screen, newpos, shortestSoFar);
				if (shortestFromNext < shortestFromHere) {
					shortestFromHere = shortestFromNext;
				}

				// Restore screen
				if (screen.get(newpos) == Tile.DROID) {
					screen.put(newpos, Tile.EMPTY);
				}
				screen.put(droid, Tile.DROID);
				break;
			case FOUND:
				log.info("Found at: {}", newpos);
				screen.put(newpos, Tile.OXYGEN);
				shortestFromHere = 0;
				paint(screen);
				break;
			case STOPPED:
				log.info("Stopped!");
				break;
			default:
				throw new IllegalArgumentException(status.name());
			}
		}

		return shortestFromHere;
	}

	private static void paint(Map<Point, Tile> screen) {
		LongSummaryStatistics xstats = screen.keySet().stream().collect(Collectors.summarizingLong(Point::getX));
		LongSummaryStatistics ystats = screen.keySet().stream().collect(Collectors.summarizingLong(Point::getY));

		StringBuilder sb = new StringBuilder(
				(int) ((xstats.getMax() - xstats.getMin()) * (ystats.getMax() - ystats.getMin())));
		for (long y = ystats.getMin(); y <= ystats.getMax(); y++) {
			for (long x = xstats.getMin(); x <= xstats.getMax(); x++) {
				sb.append(screen.getOrDefault(new Point(x, y), Tile.EMPTY).pixel);
			}
			sb.append('\n');
		}
		System.out.println(sb);
	}
}

class RemoteControl extends Computer {

	public RemoteControl(long[] program) {
		super("remoteControl", program);
	}

	public Status move(Direction direction) throws InterruptedException {
		input.putLast((long) direction.ordinal() + 1);
		Long status = output.pollFirst(1, TimeUnit.SECONDS);
		return Status.of(status);
	}

}

@Value
@ToString(includeFieldNames = false)
class Point {
	static final Point ZERO = new Point(0l, 0l);

	@With
	Long x, y;

	Point move(Direction direction) {
		switch (direction) {
		case NORTH:
			return withY(y - 1);
		case SOUTH:
			return withY(y + 1);
		case WEST:
			return withX(x - 1);
		case EAST:
			return withX(x + 1);
		default:
			throw new IllegalArgumentException(direction.name());
		}
	}
}

@RequiredArgsConstructor
enum Tile {
	EMPTY('.'),
	WALL('#'),
	DROID('D'),
	OXYGEN('X');

	final char pixel;

	private static final Tile[] vals = values();

	static Tile of(Long val) {
		return vals[val.intValue()];
	}
}

enum Direction {
	NORTH,
	SOUTH,
	WEST,
	EAST;
}

enum Status {
	WALL,
	MOVED,
	FOUND,
	STOPPED;

	private static final Status[] vals = values();

	static Status of(Long val) {
		if (val == null) {
			return Status.STOPPED;
		}
		return vals[val.intValue()];
	}
}