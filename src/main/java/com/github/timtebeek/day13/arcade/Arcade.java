package com.github.timtebeek.day13.arcade;

import java.util.Comparator;
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

	public static long render(long[] program) throws InterruptedException {
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

		return screen.values().stream().filter(Tile.BLOCK::equals).count();
	}

	public static long play(long[] program) throws InterruptedException {
		// Play for free
		program[0] = 2;
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

		// Initialize screen & joystick
		Map<Point, Tile> screen = new HashMap<>();
		Direction joystick = Direction.NEUTRAL;
		Point ball = Point.ZERO;
		Point paddle = Point.ZERO;
		Point aim = Point.ZERO;
		Long score = 0L;
		long maxx = -1;

		do {
			// Poll first input; Or stop
			Long newx = arcade.output.pollFirst(50, TimeUnit.MILLISECONDS);
			Long newy = arcade.output.pollFirst(50, TimeUnit.MILLISECONDS);
			Long tileid = arcade.output.pollFirst(50, TimeUnit.MILLISECONDS);
			if (newx == null || newy == null || tileid == null) {
				break;
			}

			// Determine maxx if not already set, so we can find the screen sides
			if (newx > maxx) {
				maxx = newx;
			}

			if (newx == -1 && newy == 0) {
				// Interpret as score
				score = tileid;
				continue;
			}

			// Add pixel to screen
			Tile tile = Tile.of(tileid);
			Point position = new Point(newx, newy);
			screen.put(position, tile);

			// Act for special tiles
			switch (tile) {
			case PADDLE:
				paddle = position;
				break;
			case BALL:
				// Aim & store new ball position
				aim = estimateWhereBallMeetsPaddle(ball, position, paddle, maxx);
				ball = position;

				// Determine action
				if (aim.leftOf(paddle)) {
					joystick = Direction.LEFT;
				} else if (aim.rightOf(paddle)) {
					joystick = Direction.RIGHT;
				} else {
					joystick = Direction.NEUTRAL;
				}

				// Take action
				log.info("Moving: {}", joystick);
				arcade.input.putLast(joystick.position());
				break;
			default:
				break;
			}

			// Paint each iteration
			if (!paddle.equals(Point.ZERO)) {
				paint(screen, maxx, score, aim);
			}
		} while (true);

		return score;
	}

	private static Point estimateWhereBallMeetsPaddle(Point oldball, Point newball, Point paddle, long maxx) {
		long yestimate = paddle.getY();
		long newx = newball.getX();
		long heightDifference = yestimate - newball.getY();

		// Rough estimate of x
		long xestimate;
		if (oldball.leftOf(newball)) {
			xestimate = newx - heightDifference;
		} else {
			xestimate = newx + heightDifference;
		}

		// Bounds check
		if (xestimate < 1) {
			xestimate = 1 - xestimate;
		} else if (maxx < xestimate) {
			xestimate = maxx - (xestimate - maxx);
		}

		return new Point(xestimate, yestimate);
	}

	private static void paint(Map<Point, Tile> screen, long maxx, long score, Point aim) {
		long maxy = screen.keySet().stream().mapToLong(Point::getY).max().getAsLong();
		StringBuilder sb = new StringBuilder((int) (maxx * maxy));
		for (long y = 0; y <= maxy; y++) {
			for (long x = 0; x <= maxx; x++) {
				Point pixel = new Point(x, y);
				if (pixel.equals(aim)) {
					sb.append('*');
				} else {
					sb.append(screen.getOrDefault(pixel, Tile.EMPTY).pixel);
				}
			}
			sb.append('\n');
		}
		sb.append("Score: ").append(score);
		System.out.println(sb);
	}
}

@Value
@ToString(includeFieldNames = false)
class Point implements Comparable<Point> {
	static final Point ZERO = new Point(0l, 0l);
	private static final Comparator<Point> COMPARATOR = Comparator
			.comparingLong(Point::getX)
			.thenComparingLong(Point::getY);

	@With
	Long x, y;

	@Override
	public int compareTo(Point o) {
		return COMPARATOR.compare(this, o);
	}

	boolean leftOf(Point o) {
		return this.getX() < o.getX();
	}

	boolean rightOf(Point o) {
		return this.getX() > o.getX();
	}

	boolean below(Point o) {
		return this.getY() > o.getY();
	}

	boolean above(Point o) {
		return this.getY() < o.getY();
	}
}

enum Tile {
	EMPTY(' '),
	WALL('|'),
	BLOCK('-'),
	PADDLE('_'),
	BALL('O');

	char pixel;

	Tile(char pixel) {
		this.pixel = pixel;
	}

	private static final Tile[] values = Tile.values();

	static Tile of(Long val) {
		return values[val.intValue()];
	}
}

enum Direction {
	LEFT,
	NEUTRAL,
	RIGHT;

	long position() {
		return ordinal() - 1;
	}

	private static final Direction[] values = Direction.values();

	static Direction of(int val) {
		return values[val];
	}
}