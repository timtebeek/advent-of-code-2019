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
import lombok.experimental.Delegate;
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

		// Find shortest path
		shortestPathToOxygenSystem(remoteControl, new Screen(), Point.ZERO, Direction.NORTH);
		return -2;
	}

	private static void shortestPathToOxygenSystem(
			RemoteControl remoteControl,
			Screen screen,
			Point droid,
			Direction direction) throws InterruptedException {

		// Determine where we're going
		Point newpos = droid.move(direction);

		// Short circuit if known path
		Tile atpos = screen.get(newpos);
		if (atpos != null && atpos != Tile.OXYGEN) {
			// Prevent walking in a circle
			return;
		}

		// Try to take a step in direction
		Status status = remoteControl.move(direction);

		// Interpret returned status
		switch (status) {
		case WALL:
			screen.put(newpos, Tile.WALL);
			break;
		case MOVED:
			screen.put(newpos, Tile.EMPTY);
			// Traverse and compare; Which adds droid at new location
			for (Direction newdir : Direction.values()) {
				shortestPathToOxygenSystem(remoteControl, screen, newpos, newdir);
			}
			break;
		case FOUND:
			screen.put(newpos, Tile.OXYGEN);
			log.info("Found at: {} ", newpos);
			break;
		case STOPPED:
			log.info("Stopped!");
			break;
		default:
			throw new IllegalArgumentException(status.name());
		}

		System.out.println("Found a " + status + " to my " + direction);
		System.out.println(screen);
	}
}

class RemoteControl extends Computer {

	public RemoteControl(long[] program) {
		super("remoteControl", program);
	}

	public Status move(Direction direction) throws InterruptedException {
		input.putLast(direction.command);
		Long status = output.pollFirst(1, TimeUnit.SECONDS);
		return Status.of(status);
	}
}

class Screen {
	@Delegate
	private Map<Point, Tile> screen = new HashMap<>();

	@Override
	public String toString() {
		LongSummaryStatistics xstats = screen.keySet().stream().collect(Collectors.summarizingLong(Point::getX));
		LongSummaryStatistics ystats = screen.keySet().stream().collect(Collectors.summarizingLong(Point::getY));

		StringBuilder sb = new StringBuilder(
				(int) ((xstats.getMax() - xstats.getMin()) * (ystats.getMax() - ystats.getMin())));
		for (long y = ystats.getMin(); y <= ystats.getMax(); y++) {
			for (long x = xstats.getMin(); x <= xstats.getMax(); x++) {
				sb.append(screen.getOrDefault(new Point(x, y), Tile.UNKNOWN).pixel);
			}
			sb.append('\n');
		}
		return sb.toString();
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
	OXYGEN('X'),
	UNKNOWN(' ');

	final char pixel;
}

@RequiredArgsConstructor
enum Direction {
	NORTH(1),
	EAST(4),
	SOUTH(2),
	WEST(3);

	final long command;
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