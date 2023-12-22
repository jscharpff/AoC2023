package challenges.day22;

import java.util.List;

import aocutil.io.FileReader;

public class Day22 {

	/**
	 * Day 22 of the Advent of Code 2023
	 * 
	 * https://adventofcode.com/2023/day/22
	 * 
	 * @param args The command line arguments
	 * @throws Exception
	 */
	public static void main( final String[] args ) throws Exception {
		final List<String> ex_input = new FileReader( Day22.class.getResource( "example.txt" ) ).readLines( );
		final List<String> input = new FileReader( Day22.class.getResource( "input.txt" ) ).readLines( );
		
		System.out.println( "---[ Part 1 ]---" );
		System.out.println( "Example: " + part1( ex_input ) );
		System.out.println( "Answer : " + part1( input ) );

		System.out.println( "\n---[ Part 2 ]---" );
		System.out.println( "Example: " + part2( ex_input ) );
		System.out.println( "Answer : " + part2( input ) );
	}

	/**
	 * Simulates the falling of bricks of sand and counts the number of bricks
	 * that could be removed from the resulting tower without causing other
	 * bricks to tumble (Jenga much?)
	 * 
	 * @param input A snapshot of the sand bricks as they are still in the air
	 * @return The count of bricks that can be removed safely from the resulting
	 *   tower
	 */
	private static long part1( final List<String> input ) {
		return new BrickTower( input ).countRemovable( );
	}

	/**
	 * Simulates the falling of bricks of sand again but now does almost the
	 * opposite of part 1 by counting for each brick the number of other bricks
	 * that will fall if it it removed from the tower
	 * 
	 * @param input A snapshot of the sand bricks as they are still in the air
	 * @return The sum of all brick fall counts
	 */
	
	private static long part2( final List<String> input ) {
		return new BrickTower( input ).countMaxFall( );
	}
}