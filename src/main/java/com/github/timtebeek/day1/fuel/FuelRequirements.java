package com.github.timtebeek.day1.fuel;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.util.stream.Collectors.summingInt;

public class FuelRequirements {

	public static void main(String[] args) throws Exception {
		Path input = Paths.get(FuelRequirements.class.getResource("input.txt").toURI());
		Integer total = Files.lines(input).collect(summingInt(str -> Integer.parseInt(str) / 3 - 2));
		System.out.println(total);
	}

}
