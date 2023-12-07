package challenges.day07;

import java.util.List;

import aocutil.io.FileReader;

public class Day07 {

	/**
	 * Day 7 of the Advent of Code 2023
	 * 
	 * https://adventofcode.com/2023/day/7
	 * 
	 * @param args The command line arguments
	 * @throws Exception
	 */
	public static void main( final String[] args ) throws Exception {
		final List<String> ex_input = new FileReader( Day07.class.getResource( "example.txt" ) ).readLines( );
		final List<String> input = new FileReader( Day07.class.getResource( "input.txt" ) ).readLines( );
		
		System.out.println( "---[ Part 1 ]---" );
		System.out.println( "Example: " + playGame( ex_input, false ) );
		System.out.println( "Answer : " + playGame( input, false ) );

		System.out.println( "\n---[ Part 2 ]---" );
		System.out.println( "Example: " + playGame( ex_input, true ) );
		System.out.println( "Answer : " + playGame( input, true ) );
	}

	/**
	 * Reconstructs a game of CamelCards from the input and returns the total
	 * winnings over all hands in the games
	 * 
	 * @param input A list of hands and associated bids
	 * @param useJokers True to use jokers in the game, false otherwise
	 * @return The sum of total winnings
	 */
	private static long playGame( final List<String> input, final boolean useJokers ) {
		final CamelCards CC = CamelCards.fromStringList( input, useJokers );
		return CC.getTotalWinnings( );
	}

}