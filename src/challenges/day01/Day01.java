package challenges.day01;

import java.util.List;

import aocutil.io.FileReader;
import aocutil.string.StringUtil;

public class Day01 {

	/**
	 * Day 1 of the Advent of Code 2023
	 * 
	 * https://adventofcode.com/2023/day/1
	 * 
	 * @param args The command line arguments
	 * @throws Exception
	 */
	public static void main( final String[] args ) throws Exception {
		final List<String> ex_input = new FileReader( Day01.class.getResource( "example.txt" ) ).readLines( );
		final List<String> ex_input2 = new FileReader( Day01.class.getResource( "example2.txt" ) ).readLines( );
		final List<String> input = new FileReader( Day01.class.getResource( "input.txt" ) ).readLines( );
		
		System.out.println( "---[ Part 1 ]---" );
		System.out.println( "Example: " + sumDigits( ex_input, false ) );
		System.out.println( "Answer : " + sumDigits( input, false ) );

		System.out.println( "\n---[ Part 2 ]---" );
		System.out.println( "Example: " + sumDigits( ex_input2, true ) );
		System.out.println( "Answer : " + sumDigits( input, true ) );
	}
	
	/**
	 * Sums the combination of first and last digit in every string over all
	 * input strings
	 *   
	 * @param input The list of input strings
	 * @param text True to include textual digits in sum
	 * @return The sum of values from concatenating the first and last digit of
	 *   every string in the input
	 */
	private static long sumDigits( final List<String> input, final boolean text ) {
		return input.stream( ).mapToInt( x -> 10 * findDigit( x, text ) + findDigit( StringUtil.reverse( x ), text ) ).sum( );
	}

	/**
	 * Finds the first digit in the string, either only numerical or including
	 * textual values
	 *   
	 * @param input The input string to scan
	 * @param text True to include textual digits in scan
	 * @return The value of the first digit encountered in the string
	 */
	private static int findDigit( final String input, final boolean text ) {
		for( int i = 0; i < input.length( ); i++ ) {
			
			// do text based processing of digits? i.e., include textual occurrences
			if( text ) { 
				final String t = input.substring( 0, i );
				for( int d = 0; d < digits.length; d ++ )
					if( t.contains( digits[d] ) || t.contains( StringUtil.reverse( digits[d] ) ) ) return d+1;
			}

			// check if there is an actual digit at this index
			if( input.charAt( i ) > '0' && input.charAt( i ) <= '9' ) return Integer.parseInt( "" + input.charAt( i ) );
		}
		
		throw new RuntimeException( "No digit found in string" );
	}
	
	// list of digits in text form
	private static String[] digits = new String[] { "one", "two", "three", "four", "five", "six", "seven", "eight", "nine" };
}