package challenges.day04;

import java.util.List;
import java.util.stream.IntStream;

import aocutil.io.FileReader;

public class Day04 {

	/**
	 * Day 4 of the Advent of Code 2023
	 * 
	 * https://adventofcode.com/2023/day/4
	 * 
	 * @param args The command line arguments
	 * @throws Exception
	 */
	public static void main( final String[] args ) throws Exception {
		final List<String> ex_input = new FileReader( Day04.class.getResource( "example.txt" ) ).readLines( );
		final List<String> input = new FileReader( Day04.class.getResource( "input.txt" ) ).readLines( );
		
		System.out.println( "---[ Part 1 ]---" );
		System.out.println( "Example: " + part1( ex_input ) );
		System.out.println( "Answer : " + part1( input ) );

		System.out.println( "\n---[ Part 2 ]---" );
		System.out.println( "Example: " + part2( ex_input ) );
		System.out.println( "Answer : " + part2( input ) );
	}

	/**
	 * Reads all the scratch cards from the input and determines the total
	 * score of all cards combined
	 * 
	 * @param input The list of scratch cards
	 * @return The total winning score
	 */
	private static long part1( final List<String> input ) {
		return input.stream( ).mapToLong( card -> ScratchCard.fromString( card ).getScore( ) ).sum( );
	}

	/**
	 * Again reads all the scratch cards but now counts the total number of cards
	 * that result from winning new tickets.
	 * 
	 * @param input The list of initial scratch cards
	 * @return The total number of cards that results from the original cards and
	 *   winning new cards
	 */
	private static long part2( final List<String> input ) {
		// keep track how many of each card we have/win
		final int[] cards = new int[ input.size( ) ];
		
		// go over all cards and determine the number of new cards we win
		for( final String s : input ) {
			// read the card and add one initial copy 
			final ScratchCard sc = ScratchCard.fromString( s );
			cards[ sc.ID - 1]++;
			
			// for every winning number on this card, add a new successive card to
			// the pile for every copy we have of this card 
			for( int i = 0; i < sc.getWins( ); i++ )
				cards[ sc.ID + i ] += cards[ sc.ID - 1 ];
		}
		
		// return sum of cards
		return IntStream.of( cards ).sum( );
	}
}