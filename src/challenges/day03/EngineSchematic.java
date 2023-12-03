package challenges.day03;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import aocutil.geometry.Coord2D;

/**
 * Container for an engine schematic that describes parts and part numbers in
 * a textual 2D grid format
 * 
 * @author Joris
 */
public class EngineSchematic {
	/** Map of numbers in the schematic */
	protected final Map<Coord2D, Integer> numbers;
	
	/** Map of symbols found in the schematic */
	protected final Map<Coord2D, Character> symbols;

	/**
	 * Creates a new schematic
	 */
	public EngineSchematic( ) {
		numbers = new HashMap<>( );
		symbols = new HashMap<>( );
	}
	
	/**
	 * Sums the numbers of all the engine parts in the schematic. A number is an
	 * engine part iff it is adjacent to any of the engine parts given by symbols
	 * 
	 * @return The total sum of engine parts in the schematic
	 */
	public long sumEngineParts( ) {
		// go over all numbers and check if there is a symbol adjacent. If so, sum
		// the number
		long sum = 0;
		for( final Coord2D c : numbers.keySet( ) ) {
			final int value = numbers.get( c );
			
			// if any symbol is adjacent, add the part number
			for( final Coord2D s : symbols.keySet( ) )
				if( isNumberAdjacentTo( c, value, s ) ) {
					sum += value;
					
					// no need to count part multiple times if they neighbour multiple
					// symbols
					break;
				}
		}
		
		return sum;
	}
	
	/**
	 * Sums the ratios of all gears in the schematic. A part is considered a
	 * gear if denoted by the '*' symbol and neighboured by exactly two part
	 * numbers.
	 * 
	 * @return The sum of all gear ratios, computed as the product of its two
	 *   neighbouring part numbers.
	 */
	public long sumGearRatios( ) {
		long sum = 0;
		for( final Coord2D c : symbols.keySet( ) ) {
			// only count gear symbols
			final char s = symbols.get( c );
			if( s != '*' ) continue;
			
			// find all adjacent part numbers
			final List<Integer> N = new ArrayList<>( );
			for( final Coord2D c2 : numbers.keySet( ) ) {
				final int value = numbers.get( c2 );
			
				if( isNumberAdjacentTo( c2, value, c ) ) N.add( value );
			}
			
			// if there are exactly two then this is a gear and we add its ratio
			if( N.size( ) == 2 ) sum += N.get( 0 ) * N.get( 1 );
		}
		
		return sum;
	}
	
	/**
	 * Checks if the number at c1, with specified length, is adjacent to the
	 * specified other coordinate
	 * 
	 * @param c1 The starting coordinate of the number
	 * @param value The value of the number
	 * @param c2 The coordinate to test whether it is adjacent to the number
	 * @return True iff c2 is adjacent (including diagonally) to the number
	 */
	private boolean isNumberAdjacentTo( final Coord2D c1, final int value, final Coord2D c2 ) {
		final int digits = (int)Math.ceil( Math.log10( value ) );
		return c1.x - 1 <= c2.x && c2.x <= c1.x + digits &&
				   c1.y - 1 <= c2.y && c2.y <= c1.y + 1;
	}
	
	/**
	 * Reconstructs an engine schematic from a list of strings that textually
	 * describe the parts and part numbers as a 2D grid.
	 * 
	 * @param input The list of strings, one per row, that describe the engine
	 *   schematic
	 * @return The corresponding engine schematic object parsed from the text
	 */
	public static EngineSchematic fromStringList( final List<String> input ) {
		final EngineSchematic e = new EngineSchematic( );
		
		// parse all lines in the input and process numbers/symbols, storing their
		// locations in the schematic
		int y = -1;
		for( final String line : input ) {
			y++;

			// find all number strings
			final Matcher m = Pattern.compile( "(\\d+)" ).matcher( line );
			while( m.find( ) ) e.numbers.put( new Coord2D( m.start( ), y ), Integer.parseInt( m.group( ) ) );
			
			// and all symbols
			final Matcher m2 = Pattern.compile( "([^\\d\\.])" ).matcher( line );
			while( m2.find( ) ) e.symbols.put( new Coord2D( m2.start( ), y ), m2.group( ).charAt( 0 ) );
		}
		
		return e;
	}
}
