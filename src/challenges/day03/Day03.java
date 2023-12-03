package challenges.day03;

import java.util.List;

import aocutil.io.FileReader;

public class Day03 {

	/**
	 * Day 3 of the Advent of Code 2023
	 * 
	 * https://adventofcode.com/2023/day/3
	 * 
	 * @param args The command line arguments
	 * @throws Exception
	 */
	public static void main( final String[] args ) throws Exception {
		final List<String> ex_input = new FileReader( Day03.class.getResource( "example.txt" ) ).readLines( );
		final List<String> input = new FileReader( Day03.class.getResource( "input.txt" ) ).readLines( );
		
		System.out.println( "---[ Part 1 ]---" );
		System.out.println( "Example: " + part1( ex_input ) );
		System.out.println( "Answer : " + part1( input ) );

		System.out.println( "\n---[ Part 2 ]---" );
		System.out.println( "Example: " + part2( ex_input ) );
		System.out.println( "Answer : " + part2( input ) );
	}

	/**
	 * Reconstructs the engine schematic from the input and computes the sum of
	 * all engine part numbers.
	 * 
	 * @param input The engine schematic as a list of strings, one per row
	 * @return The sum of part numbers
	 */
	private static long part1( final List<String> input ) {
		return EngineSchematic.fromStringList( input ).sumEngineParts( );
	}
	
	/**
	 * Reconstructs the engine schematic from the input and computes the sum of
	 * all gear ratios.
	 * 
	 * @param input The engine schematic as a list of strings, one per row
	 * @return The sum of gear ratios
	 */
	private static long part2( final List<String> input ) {
		return EngineSchematic.fromStringList( input ).sumGearRatios( );
	}
}