package com.github.timtebeek.day4.password;

import java.util.stream.IntStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PasswordsTest {

	@Test
	void testPart1() {
		long count = IntStream.rangeClosed(372037, 905157)
				.mapToObj(i -> String.valueOf(i))
				.filter(Passwords::onlyIncreasingDigits)
				.filter(Passwords::atLeastTwoAdjacentDigits).count();
		Assertions.assertEquals(481, count);
	}

	@Test
	void testPart2() {
		long count = IntStream.rangeClosed(372037, 905157)
				.mapToObj(i -> String.valueOf(i))
				.filter(Passwords::onlyIncreasingDigits)
				.filter(Passwords::exactlyTwoAdjacentDigits).count();
		Assertions.assertEquals(299, count);
	}

}
