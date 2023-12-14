package challenges.day13;

import java.util.List;

import aocutil.io.FileReader;

public class Day13 {

	/**
	 * Day 13 of the Advent of Code 2023
	 * 
	 * https://adventofcode.com/2023/day/13
	 * 
	 * @param args The command line arguments
	 * @throws Exception
	 */
	public static void main( final String[] args ) throws Exception {
		final List<String> ex_input = new FileReader( Day13.class.getResource( "example.txt" ) ).readLineGroups( "\n" );
		final List<String> input = new FileReader( Day13.class.getResource( "input.txt" ) ).readLineGroups( "\n" );
		
		System.out.println( "---[ Part 1 ]---" );
		System.out.println( "Example: " + part1( ex_input ) );
		System.out.println( "Answer : " + part1( input ) );

		System.out.println( "\n---[ Part 2 ]---" );
		System.out.println( "Example: " + part2( ex_input ) );
		System.out.println( "Answer : " + part2( input ) );
	}

	/**
	 * Finds the setup of a mirror in each rock pattern in the input and sums the
	 * setup scores 
	 * 
	 * @param input TThe list of rock patterns, one per string
	 * @return The sum of mirror setups
	 */
	private static long part1( final List<String> input ) {
		return input.stream( ).mapToInt( in -> new RockPattern( in ).getMirrorScore( ) ).sum( );
	}
	
	/**
	 * Finds the setup of a mirror in each rock pattern in the input and sums the
	 * setup scores. However, now each pattern contains exactly one smudge
	 * somewhere in the pattern that needs to be fixed, resulting in a new setup
	 * 
	 * @param input TThe list of rock patterns, one per string
	 * @return The sum of mirror setups
	 */
	private static long part2( final List<String> input ) {
		return input.stream( ).mapToInt( in -> new RockPattern( in ).getMirrorScoreSmudged( ) ).sum( );
	}
	
	// 14361 too low
}