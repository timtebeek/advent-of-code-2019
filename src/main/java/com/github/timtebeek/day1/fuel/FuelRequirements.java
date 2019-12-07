package com.github.timtebeek.day1.fuel;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.util.stream.Collectors.summingInt;

public class FuelRequirements {

	public static void main(String[] args) throws Exception {
		System.out.println(calculateTotal());
	}

	static Integer calculateTotal() throws Exception {
		Path input = Paths.get(FuelRequirements.class.getResource("input.txt").toURI());
		return Files.lines(input).collect(summingInt(str -> calculateFuel(Integer.parseInt(str))));
	}

	private static int calculateFuel(int mass) {
		int fuel = mass / 3 - 2;
		return fuel <= 0 ? 0 : fuel + calculateFuel(fuel);
	}

}
