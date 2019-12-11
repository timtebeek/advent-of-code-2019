package com.github.timtebeek.day9.boost;

import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import static com.github.timtebeek.day9.boost.IntcodeComputer.convertToIndexedMemory;

@Data
@Slf4j
public class Computer {

	private final String name;
	private final Map<Long, Long> memory;
	public BlockingDeque<Long> input = new LinkedBlockingDeque<>();
	public BlockingDeque<Long> output = new LinkedBlockingDeque<>();

	public Computer(String name, long[] program) {
		this.name = name;
		this.memory = convertToIndexedMemory(program);
	}

	public void execute() throws InterruptedException {
		log.info("{} Executing: {}", name, memory);
		log.info("{} Input:     {}", name, input);
		IntcodeComputer.execute(input, output, memory);
		log.info("{} Output:    {}", name, output);
	}
}