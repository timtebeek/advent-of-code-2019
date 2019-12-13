package com.github.timtebeek.day13.arcade;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.github.timtebeek.day9.boost.Computer;
import lombok.ToString;
import lombok.Value;
import lombok.With;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Arcade {

	public static long play(long[] program) throws InterruptedException {

		Computer arcade = new Computer("arcade", program);

		// Start in background
		Thread thread = new Thread(() -> {
			try {
				arcade.execute();
			} catch (InterruptedException e) {
				log.info("Interrupted painter");
				throw new IllegalStateException(e);
			}
		}, "intcode computer");
		thread.start();

		Map<Point, Tile> screen = new HashMap<>();
		Tile[] tilevalues = Tile.values();

		do {
			// Poll first input; Or stop
			Long xpos = arcade.output.pollFirst(50, TimeUnit.MILLISECONDS);
			Long ypos = arcade.output.pollFirst(50, TimeUnit.MILLISECONDS);
			Long tileid = arcade.output.pollFirst(50, TimeUnit.MILLISECONDS);
			if (xpos == null || ypos == null || tileid == null) {
				break;
			}

			// Add pxiel to screen
			screen.put(new Point(xpos, ypos), tilevalues[tileid.intValue()]);
		} while (true);

		paint(screen);

		return screen.values().stream().filter(Tile.BLOCK::equals).count();
	}

	private static void paint(Map<Point, Tile> screen) {
		long maxx = screen.keySet().stream().mapToLong(Point::getX).max().getAsLong();
		long maxy = screen.keySet().stream().mapToLong(Point::getY).max().getAsLong();
		StringBuilder sb = new StringBuilder((int) (maxx * maxy));
		for (int y = 0; y <= maxy; y++) {
			for (int x = 0; x <= maxx; x++) {
				sb.append(pixel(screen.getOrDefault(new Point(x, y), Tile.EMPTY)));
			}
			sb.append('\n');
		}
		System.out.println(sb);
	}

	private static char pixel(Tile tile) {
		switch (tile) {
		case EMPTY:
			return ' ';
		case WALL:
			return '|';
		case BLOCK:
			return '-';
		case PADDLE:
			return '_';
		case BALL:
			return 'O';
		}
		return '?';
	}
}

@Value
@ToString(includeFieldNames = false)
class Point {
	@With
	long x, y;
}

enum Tile {
	EMPTY,
	WALL,
	BLOCK,
	PADDLE,
	BALL
}