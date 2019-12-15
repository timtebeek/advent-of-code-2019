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
		}, "IntcodeComputer");
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
		Point ball = null;
		Point paddle = null;
		Point aim = null;
		Long score = 0L;

		do {
			// Poll first input; Or stop
			Long newx = arcade.output.pollFirst(50, TimeUnit.MILLISECONDS);
			Long newy = arcade.output.pollFirst(50, TimeUnit.MILLISECONDS);
			Long tileid = arcade.output.pollFirst(50, TimeUnit.MILLISECONDS);
			if (newx == null || newy == null || tileid == null) {
				break;
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
			case EMPTY:
				// No need to track empty space
				screen.remove(position);
				break;
			case PADDLE:
				// Update paddle position
				paddle = position;
				break;
			case BALL:
				// Store previous and current ball position
				Point previousball = ball;
				ball = position;

				if (paddle != null) {
					// Aim
					aim = estimateWherePaddleNeedsToBe(previousball, ball, screen, paddle);

					// Determine action
					if (ball.equals(paddle.withY(paddle.getY() - 1))) {
						joystick = Direction.NEUTRAL;
					} else if (aim.leftOf(paddle)) {
						joystick = Direction.LEFT;
					} else if (aim.rightOf(paddle)) {
						joystick = Direction.RIGHT;
					} else {
						joystick = Direction.NEUTRAL;
					}
				}
				// Paint with each new ball position
				paint(screen, score, aim, joystick);

				// Take action
				arcade.input.putLast(joystick.position());
				break;
			default:
				break;
			}
		} while (true);

		return score;
	}

	private static Point estimateWherePaddleNeedsToBe(Point previousball, Point currentball, Map<Point, Tile> screen,
			Point paddle) {
		if (previousball == null || paddle == null) {
			return currentball;
		}

		// We've reached the point where we need to be; or the ceiling and just don't care
		if (currentball.getY() == paddle.getY() || currentball.getY() == 0) {
			return currentball;
		}

		// Determine where we're going
		boolean movingRight = previousball.leftOf(currentball);
		boolean movingDown = previousball.above(currentball);

		// Estimate next position
		Point nextball = currentball
				.withX(currentball.getX() + (movingRight ? 1 : -1))
				.withY(currentball.getY() + (movingDown ? 1 : -1));

		// Correct for any bouncing off walls and blocks
		Tile tileatnext = screen.get(nextball);
		if (tileatnext == Tile.WALL || tileatnext == Tile.BLOCK) {
			nextball = nextball.withX(nextball.getX() - (movingRight ? -2 : 2));
		}

		// Recurse
		return estimateWherePaddleNeedsToBe(currentball, nextball, screen, paddle);
	}

	private static void paint(Map<Point, Tile> screen, long score, Point aim, Direction joystick) {
		long maxx = screen.keySet().stream().mapToLong(Point::getX).max().getAsLong();
		long maxy = screen.keySet().stream().mapToLong(Point::getY).max().getAsLong();
		StringBuilder sb = new StringBuilder((int) (maxx * maxy));
		for (long y = 0; y <= maxy; y++) {
			for (long x = 0; x <= maxx; x++) {
				sb.append(screen.getOrDefault(new Point(x, y), Tile.EMPTY).pixel);
			}
			sb.append('\n');
		}
		if (aim != null) {
			for (int i = 0; i < aim.getX(); i++) {
				sb.append(' ');
			}
			sb.append("* ").append(aim + "\n");
		}
		sb.append("Score: ").append(score).append('\t').append(joystick);
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