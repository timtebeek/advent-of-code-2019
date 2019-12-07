package com.github.timtebeek.day2.opcode;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class OpcodesTest {

	@Test
	void testCalculate() throws Exception {
		Assertions.assertEquals(9706670, new Opcodes().calculate());
	}

	@Test
	void testExecute() {
		Assertions.assertEquals(3500, Opcodes.execute(new int[] { 1, 9, 10, 3, 2, 3, 11, 0, 99, 30, 40, 50 }));
		Assertions.assertEquals(2, Opcodes.execute(new int[] { 1, 0, 0, 0, 99 }));
		Assertions.assertEquals(2, Opcodes.execute(new int[] { 2, 3, 0, 3, 99 }));
		Assertions.assertEquals(2, Opcodes.execute(new int[] { 2, 4, 4, 5, 99, 0 }));
		Assertions.assertEquals(30, Opcodes.execute(new int[] { 1, 1, 1, 4, 99, 5, 6, 0, 99 }));
	}

}
