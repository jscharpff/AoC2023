package challenges.day19;

import java.util.ArrayList;
import java.util.List;

import aocutil.io.FileReader;

public class Day19 {

	/**
	 * Day 19 of the Advent of Code 2023
	 * 
	 * https://adventofcode.com/2023/day/19
	 * 
	 * @param args The command line arguments
	 * @throws Exception
	 */
	public static void main( final String[] args ) throws Exception {
		final List<String> ex_input = new FileReader( Day19.class.getResource( "example.txt" ) ).readLines( );
		final List<String> input = new FileReader( Day19.class.getResource( "input.txt" ) ).readLines( );
		
		System.out.println( "---[ Part 1 ]---" );
		System.out.println( "Example: " + part1( ex_input ) );
		System.out.println( "Answer : " + part1( input ) );

		System.out.println( "\n---[ Part 2 ]---" );
		System.out.println( "Example: " + part2( ex_input ) );
		System.out.println( "Answer : " + part2( input ) );
	}

	/**
	 * Runs the part sorter machine on the set of parts to determine how many of
	 * the parts will be accepted
	 * 
	 * @param input A list of sorting flows and parts, separated by a single
	 *   blank line
	 * @return The sum of part attributes of the accepted parts
	 */
	private static long part1( final List<String> input ) {
		// process input to get flowset and parts
		final List<String> flowset = new ArrayList<>( );
		int i = 0;
		for( ; i < input.size( ); i++ ) {
			final String flow = input.get( i );
			if( flow.equals( "" ) ) break;
			flowset.add( flow );
		}
		final List<String> parts = new ArrayList<>( );
		for( i++ ; i < input.size( ); i++ ) parts.add( input.get( i ) );
		
		// now create the sorting machine and run the algorithm on the set of parts
		final PartSorter ps = new PartSorter( flowset );
		return ps.sort( parts );
	}
	

	/**
	 * Determines the total number of unique part configurations that will be
	 * accepted by the sorting machine
	 * 
	 * @param input A list of sorting flows and parts, separated by a single
	 *   blank line
	 * @return The count of unique part attribute configurations that will result
	 *   in the sorting machine accepting it
	 */
	private static long part2( final List<String> input ) {		
		// process input to get flowset and parts
		final List<String> flowset = new ArrayList<>( );
		int i = 0;
		for( ; i < input.size( ); i++ ) {
			final String flow = input.get( i );
			if( flow.equals( "" ) ) break;
			flowset.add( flow );
		}
		
		// create the sorting machine and determine all possible part
		// configurations that will be accepted by the machine
		final PartSorter ps = new PartSorter( flowset );
		return ps.countAccepted( );
	}
}