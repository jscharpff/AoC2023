package challenges.day07;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A nice and family-friendly game of CamelCards
 * 
 * @author Joris
 */
public class CamelCards {
	/** The list of hands in this game */
	private final List<CCHand> hands;
	
	/** Ue jokers in the game */
	private final boolean useJokers;
	
	/**
	 * Creates a new game of CamelCards
	 * 
	 * @param useJokers True to use jokers
	 */
	public CamelCards( final boolean useJokers ) {
		hands = new ArrayList<>();
		this.useJokers = useJokers;
	}
	
	/**
	 * Ranks the hands in the game on their value and computes the total winnings
	 * in the game.
	 * 
	 * @return The sum of winnings 
	 */
	public long getTotalWinnings( ) {
		// sort hands based upon their value
		final List<CCHand> sorted = new ArrayList<>( hands );
		sorted.sort( CCHand::compareTo );
		
		// sum over hand bids times their rank to determine total winnings
		long winnings = 0;
		for( int i = 0; i < sorted.size( ); i++ )
			winnings += sorted.get( i ).bid * (long)(i+1);
		return winnings;
	}

	/**
	 * Reconstructs a game of CamelCards from a list of hands and bids
	 * 
	 * @param input The list of card hands and associated bid value
	 * @param useJokers True to use jokers in the game, false otherwise
	 * @return The game
	 */
	public static CamelCards fromStringList( final List<String> input, final boolean useJokers ) {
		final CamelCards CC = new CamelCards( useJokers );
		
		for( final String s : input ) {
			final String[] str = s.split( " " );
			CC.hands.add( CC.new CCHand( str[0], Integer.parseInt( str[1] ) ) );
		}
		
		return CC;		
	}
	
	/**
	 * One single hand in a game of CamelCards
	 * 
	 * @author Joris
	 */
	protected class CCHand implements Comparable<CCHand> {
		/** The hand */
		protected final String hand;
		
		/** The value of the hand */
		protected final long value;
		
		/** The bid associated with this hand */
		protected final int bid;
		
		/**
		 * Constructs a new hand of cards, immediately computes its value
		 * 
		 * @param cards The 5 cards in this hand
		 * @param bid The bid associated with the hand for scoring purposes
		 */
		protected CCHand( final String cards, final int bid ) {
			this.hand = cards;
			this.bid = bid;
			this.value = computeValue( );
		}

		/**
		 * Determines the value of this hand of cards
		 * 
		 * @return The value of this hand given by a number that starts with the 
		 *   type, followed by a value per individual card
		 */
		private long computeValue( ) {
			// determine type
			final int type = getHandType( );
			
			// add hand value
			long cval = 0;
			for( int i = 0; i < hand.length( ); i++ )
				cval += Math.pow( 100, hand.length( ) - i - 1 ) * getCardValue( hand.charAt( i ) );
			
			// set value to type followed by card values
			return cval + type * (long)Math.pow( 100, hand.length( ) );
		}
		
		/** 
		 * Determines the type of hand we have, i.e., 5 of a kind, full house, etc.
		 * 
		 * @return The type of hand, a value from 6 to 0 reflecting the type's
		 *   value
		 */
		private int getHandType( ) {
			// count occurrence of each character
			final Map<Character, Integer> occ = new HashMap<>( hand.length( ) );
			for( final char c : hand.toCharArray( ) ) occ.put( c, occ.getOrDefault( c, 0 ) + 1 );

			// if we use jokers, remove the 'J' cards from the count
			final int jokers;
			if( useJokers && occ.containsKey( 'J' ) ) {
				jokers = occ.remove( 'J' );
			} else { jokers = 0; }
			
			// get the highest count of matching cards
			final int maxcount = occ.values( ).stream( ).mapToInt( Integer::intValue ).max( ).orElse( 0 );
			
			// check what hand we have
			// five of a kind (possibly with jokers)
			if( maxcount >= 5 - jokers ) return 6;
			
			// four of a kind
			if( maxcount >= 4 - jokers ) return 5;
			
			// full house, possibly constructed with a joker. To check this there
			// must be at least 2 different pairs in the hand. Note that if we would
			// have more that one joker and a pair, we would get 4 of a kind
			if( maxcount >= 3 - jokers && occ.values( ).stream( ).filter( x -> x >= 2 ).count( ) > 1 ) return 4;
			
			// three of a kind
			if( maxcount >= 3 - jokers ) return 3;
			
			// two pairs and one pair
			final int pairs = (int)occ.values( ).stream( ).filter( x -> x == 2 ).count( );
			if( pairs > 0 ) return pairs;
			
			// if we have nothing special, we might at least make one pair if we have
			// one joker (more jokers would have given us 3 of a kind or higher)
			if( jokers > 0 ) return 1;
			
			// nothing special, simply use value of the cards
			return 0;
		}
		
		/**
		 * Converts the card face value into a numerical ordering value
		 * 
		 * @param card The card face
		 * @return Its numerical value
		 */
		private int getCardValue( final char card ) {
			switch( card ) {
				case 'A': return 14;
				case 'K': return 13;
				case 'Q': return 12;
				// if jokers are used, the Jack becomes a 'worthless' joker
				case 'J': return useJokers ? 0 : 11; 
				case 'T': return 10;
				default: return (card - '2') + 2;
			}
		}

		/**
		 * Compares two hands on their hand value, carefully constructed to
		 * enable sorting on winning value.
		 * 
		 * @param o The other hand to compare against
		 * @return The integer value that reflect the difference between o.value
		 *   and the value of this hand
		 */
		@Override
		public int compareTo( final CCHand o ) {
			return Long.compare( value, o.value );
		}
	}

}
