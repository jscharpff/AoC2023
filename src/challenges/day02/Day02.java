package challenges.day02;

import java.util.List;

import aocutil.io.FileReader;
import aocutil.string.RegexMatcher;

public class Day02 {

	/**
	 * Day 2 of the Advent of Code 2023
	 * 
	 * https://adventofcode.com/2023/day/2
	 * 
	 * @param args The command line arguments
	 * @throws Exception
	 */
	public static void main( final String[] args ) throws Exception {
		final List<String> ex_input = new FileReader( Day02.class.getResource( "example.txt" ) ).readLines( );
		final List<String> input = new FileReader( Day02.class.getResource( "input.txt" ) ).readLines( );
		
		System.out.println( "---[ Part 1 ]---" );
		System.out.println( "Example: " + part1( ex_input ) );
		System.out.println( "Answer : " + part1( input ) );

		System.out.println( "\n---[ Part 2 ]---" );
		System.out.println( "Example: " + part2( ex_input ) );
		System.out.println( "Answer : " + part2( input ) );
	}
	
	/**
	 * Sums the IDs of the games in the input that could have been played with 12
	 * red, 13 green and 14 blue cubes.
	 * 
	 * @param input The list of played games
	 * @return The sum of IDs of valid games
	 */
	private static long part1( final List<String> input ) {
		final int[] cubes = new int[] { 12, 13, 14 };
		long sum = 0;
		
		// go over all games and sum the IDs of those that are valid with 12, 13
		// and 14 rgb balls
		for( final String s : input ) {
			final String[] game = s.split( ": " );
			
			// check every play of the game whether the number of drawn cubes does
			// not exceed the maximum amount available per colour. If any play does
			// not satisfy this, it is not counted
			boolean isvalid = true;
			for( final String gm : game[1].split( ";" ) ) {
				for( int i = 0; i < cubes.length; i++ ) {
					final String colour = rgb[i];
					isvalid &= RegexMatcher.match( "#D " + colour, gm + ", 0 " + colour ).getInt( 1 ) <= cubes[i];
				}
			}
			
			// sum IDs of valid games
			if( isvalid ) sum += RegexMatcher.match( "Game #D", game[0] ).getInt( 1 );
		}
		
		return sum;
	}
	
	/**
	 * Sums the game powers of all games played. The game power is computed by
	 * multiplying for every game the minimum number of cubes required of each
	 * colour, determined from the game plays.
	 * 
	 * @param input The set of played games
	 * @return The sum of all game powers
	 */
	private static long part2( final List<String> input ) {
		long powersum = 0;
		
		// for every game and every colour cube, determine the minimum number of 
		// cubes required to play it
		for( final String s : input ) {
			final int[] cubes = new int[] { 0, 0, 0 };
			for( final String gm : s.split( ": " )[1].split( ";" ) ) {
				for( int i = 0; i < cubes.length; i++ ) {
					final String colour = rgb[i];
					final int c = RegexMatcher.match( "#D " + colour, gm + ", 0 " + colour ).getInt( 1 );
					if( c > cubes[i] ) cubes[i] = c;
				}
			}
		
			// and finally add the game power to the sum
			powersum += cubes[0] * cubes[1] * cubes[2];
			
		}
		return powersum;
	}
	
	/** String array of colour names */
	private final static String[] rgb = new String[] { "red", "green", "blue" };
}