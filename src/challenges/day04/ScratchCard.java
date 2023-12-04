package challenges.day04;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import aocutil.string.RegexMatcher;

/**
 * Container for a single scratch card
 * 
 * @author Joris
 */
public class ScratchCard {
	/** The ID of the ScratchCard */
	public final int ID;
	
	/** The set of winning numbers */
	protected final Set<Integer> winners;
	
	/** The set of my numbers */
	protected final Set<Integer> numbers;
	
	/**
	 * Creates a new scratch card
	 * 
	 * @param ID The ID of the card
	 * @param W The collection of winning numbers
	 * @param N The collection of found numbers
	 */
	public ScratchCard( final int ID, final Collection<Integer> W, final Collection<Integer> N ) {
		this.ID = ID;
		winners = new HashSet<>( W );
		numbers = new HashSet<>( N );
	}
	
	/**
	 * @return The number of winning numbers on this ticket
	 */
	public int getWins( ) {
		return (int)numbers.stream( ).filter( x -> winners.contains( x ) ).count( );
	}
	
	/**
	 * @return The score based upon the number of winning numbers, that is, the
	 * score given by 2 ^ (#wins - 1)
	 */
	public long getScore( ) {
		final long wins = getWins( );
		if( wins <= 0 ) return 0;
		return (long)Math.pow( 2, wins - 1 );
	}

	/**
	 * Reconstructs a scratch card from a textual representation
	 * 
	 * @param input The score card as text
	 * @return The processed scratch card object
	 */
	public static ScratchCard fromString( final String input ) {
		final Set<Integer> winners = new HashSet<>( );
		final Set<Integer> numbers = new HashSet<>( );
		
		// get first part with Card ID
		final String[] str = input.split( ": " );
		final int ID = RegexMatcher.match( "Card\\s+#D", str[0] ).getInt( 1 );
		
		// then extract set of winning numbers
		final String[] s = str[1].split( " \\| " );
		for( final String n : s[0].split( " " ) )
			if( n.trim( ).length( ) > 0 )
				winners.add( Integer.parseInt( n ) );
		
		// and the set of my numbers
		for (final String n : s[1].split( " " ) )
			if( n.trim( ).length( ) > 0 )
				numbers.add( Integer.parseInt( n ) );
		
		// return the resulting scratch card
		return new ScratchCard( ID, winners, numbers );
	}
}
