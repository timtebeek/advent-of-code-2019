package com.github.timtebeek.day1.fuel;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class FuelRequirementsTest {

	@Test
	void testCalculateTotal() throws Exception {
		Integer calculateTotal = FuelRequirements.calculateTotal();
		Assertions.assertEquals(4748063, calculateTotal);
	}

}
