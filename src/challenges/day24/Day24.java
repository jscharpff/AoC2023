package challenges.day24;

import java.util.List;

import aocutil.io.FileReader;

public class Day24 {

	/**
	 * Day 24 of the Advent of Code 2023
	 * 
	 * https://adventofcode.com/2023/day/24
	 * 
	 * @param args The command line arguments
	 * @throws Exception
	 */
	public static void main( final String[] args ) throws Exception {
		final List<String> ex_input = new FileReader( Day24.class.getResource( "example.txt" ) ).readLines( );
		final List<String> input = new FileReader( Day24.class.getResource( "input.txt" ) ).readLines( );
		
		System.out.println( "---[ Part 1 ]---" );
		System.out.println( "Example: " + part1( ex_input, 7, 27 ) );
		System.out.println( "Answer : " + part1( input, 200000000000000l, 400000000000000l ) );

		System.out.println( "\n---[ Part 2 ]---" );
		System.out.println( "Example: " + part2( ex_input ) );
		System.out.println( "Answer : " + part2( input ) );
	}

	/**
	 * Finds the count of pairs of hail stones of which their trail intersects
	 * somewhere on the XY plane, from a given start time t = 0.
	 * 
	 * @param input The list that describes the hail stone positions and
	 *   velocities
	 * @return The number of pairs of hail stones that at have crossed paths at
	 *   some time (not necessarily being at the same spot)
	 */
	private static long part1( final List<String> input, final long lb, final long ub ) {
		return new HailStorm( input ).intersectTrails2D( lb, ub );
	}

	/**
	 * Finds a single starting position and velocity such that the trajectory
	 * will collide with every hail stone in the storm cloud
	 * 
	 * @param input The list that describes the hail stone positions and
	 *   velocities
	 * @return The sum of X, Y and Z coordinates of the starting position of
	 *   the trajectory
	 */
	private static long part2( final List<String> input ) {
		return new HailStorm( input ).findCollisionStone( );
	}
}