package challenges.day19;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import aocutil.object.LabeledObject;

/**
 * A machine that uses flows to sort machine parts into categories accept and
 * reject based upon part attributes.
 * @author Joris
 */
public class PartSorter {
	/** The rules to use while sorting */
	protected final Map<String, SortFlow> flows;

	/**
	 * Creates a new part sorting machine with the given set of sorting flows
	 * 
	 * @param flowset The list of strings that describe the sorting flows of
	 *   this machine
	 */
	public PartSorter( final List<String> flowset ) {
		flows = new HashMap<>( );
		for( final String f : flowset ) {
			final SortFlow sf = SortFlow.fromString( f );
			flows.put( sf.getLabel( ), sf );
		}
	}
	
	/**
	 * Sorts the parts and counts the number of accepted parts 
	 * 
	 * @param parts The parts to run through the sorting machine
	 * @return The sum of part attribute values of every part that is accepted
	 *   by the sorting machine
	 */
	public long sort( final List<String> parts ) {
		long sum = 0;
		for( final String part : parts ) {
			final Part p = Part.fromString( part );
			if( isAccepted( p ) ) sum += p.sum( );			
		}
		return sum;
	}
	
	/**
	 * Tests whether the specified part configuration is accepted by the sorting
	 * machine
	 * 
	 * @param part The part configuration
	 * @return True iff following all sorting rules results in the "A" state
	 */
	protected boolean isAccepted( final Part part ) {
		// start at the "in" flow and keep processing flows until we end in either
		// an accept ("A") or reject ("R") state
		String flow = "in";		
		while( !flow.equals( "A" ) ) {
			flow = flows.get( flow ).apply( part );
			if( flow.equals( "R" ) ) return false;
		}
		
		return true;
	}	
	
	/**
	 * Counts the number of unique attribute configurations that will result in
	 * a part being accepted by the sorting machine
	 *  
	 * @return The total count of accepted parts
	 */
	public long countAccepted( ) {
		return countAccepted( "in", new PartRange( new char[] { 'x', 'm', 'a', 's' }, 1, 4000 ) );
	}

	/**
	 * Counts the number of accepted part configurations from the given flow and
	 * (remaining) ranges of attribute values
	 * 
	 * @param nextflow The next flow to consider
	 * @param range The (remaining) attribute value ranges
	 * @return The total number of unique part attribute configurations that will
	 *   be accepted by the sorting machine
	 */
	private long countAccepted( final String nextflow, final PartRange range ) {
		// check if we are in a terminal state
		if( nextflow.equals( "A" ) ) return range.prod( );
		else if( nextflow.equals( "R" ) ) return 0;
		
		// keep processing flows and updating attribute ranges until we eventually
		// reach an accept or reject state 
		final SortFlow f = flows.get( nextflow );
		long sum = 0;
		PartRange r = new PartRange( range ); 
		for( final SortRule srule : f.rules ) {
			final PartRange newrange = r.update( srule );
			
			// rule does not apply to the current range (does not actually happen in
			// the example and input, but might with different flow configurations)
			if( newrange == null ) continue;
			sum += countAccepted( srule.target, newrange );			
			
			// process next rule but without the attribute values that would have
			// triggered the previous rule 
			r = r.remaining( srule );
			
			// if this is a default rule, the inverted range is null and we can stop
			if( r == null ) continue;
		}
		
		return sum;
	}
	
	/**
	 * Container for a range of part attribute values 
	 * 
	 * @author Joris
	 */
	private static class PartRange {
		/** The part with as its attributes the lower bounds on the range */
		protected final Part attmin;
		
		/** The part with as its attributes the upper bounds on the range */
		protected final Part attmax;
		
		/**
		 * Creates a new part range for the given attributes with specified initial
		 * values for the lower and upper bounds as range for every attribute
		 * 
		 * @param attributes The list of attributes in this range
		 * @param lower The lower bound of the range
		 * @param upper The upper bound of the range
		 */
		protected PartRange( final char[] attributes, final int lower, final int upper ) {
			attmin = new Part( );
			attmax = new Part( );
			
			for( final char c : attributes ) {
				attmin.set( c, lower );
				attmax.set( c, upper );
			}
		}
		
		/**
		 * Creates a copy of the given part range object
		 * 
		 * @param range The range to copy
		 */
		protected PartRange( final PartRange range ) {
			attmin = range.attmin.copy( );
			attmax = range.attmax.copy( );
		}
		
		/**
		 * Returns the attribute ranges to meet the condition of the specified rule
		 * on the current attribute range values
		 * 
		 * @param rule The rule to apply condition of
		 * @return A new range that meets the condition of the given rule 
		 */		
		public PartRange update( final SortRule rule ) {
			final PartRange newr = new PartRange( this );
			if( !rule.hasCondition( ) ) return newr;
			
			// update ranges based upon rule
			switch( rule.op ) {
				case '<':
					if( newr.attmin.get( rule.attr ) >= rule.value ) return null;
					newr.attmax.set( rule.attr, rule.value - 1);					
					break;

				case '>':
					if( attmax.get( rule.attr ) <= rule.value ) return null;
					newr.attmin.set( rule.attr, rule.value + 1);					
					break;
				
				default: throw new RuntimeException( "Invalid operation in rule: " + rule.op );
			}
			
			return newr;
		}
		
		/**
		 * Finds the range that corresponds to applying the inversion of the rule
		 * to the current range, i.e., the part of the range that does not meet the
		 * condition of the rule.
		 *  
		 * @param rule The rule to consider
		 * @return A copy of the range without the range specified by the condition
		 *   of the given rule
		 */
		public PartRange remaining( final SortRule rule ) {
			final PartRange newr = new PartRange( this );
			if( !rule.hasCondition( ) ) return null;
			
			// update ranges based upon rule
			switch( rule.op ) {
				case '<':	newr.attmin.set( rule.attr , rule.value ); break;
				case '>': newr.attmax.set( rule.attr, rule.value ); break;
				
				default: throw new RuntimeException( "Invalid operation in rule: " + rule.op );
			}
			
			return newr;
		}		
		
		/**
		 * @return The product of all attribute range sizes
		 */
		protected long prod( ) {
			long p = 1;
			for( final char c : new char[]{ 'x', 'm', 'a', 's' } )
				p *= (long)(attmax.get( c ) - attmin.get( c ) + 1);
						
			return p;
		}
	}
	
	/**
	 * Simple data structure to hold a part with attributes
	 * 
	 * @author Joris
	 *
	 */
	private static class Part {
		/** The map of part attributes */
		protected final Map<Character, Integer> attributes;
		
		/**
		 * Creates a new part with no attributes yet
		 */
		private Part( ) {
			attributes = new HashMap<>( );
		}
		
		/**
		 * Reconstructs a part from a string configuration
		 * @param attr The attribute values of the part as a string {attr=value,...}
		 * @return The part object
		 */
		protected static Part fromString( final String attr ) {
			final Part p = new Part( );
			final Matcher m = Pattern.compile( "([xmas]{1})=(\\d+)" ).matcher( attr );
			while( m.find( ) ) p.attributes.put( m.group( 1 ).charAt( 0 ), Integer.parseInt( m.group( 2 ) ) );
			return p;
		}
		
		/**
		 * Sets the value for the given attribute
		 *  
		 * @param attr The attribute to set
		 * @param value The value to assign
		 */
		protected void set( final char attr, final int value ) {
			attributes.put( attr, value );
		}
		
		/**
		 * Retrieves the value for the given attribute
		 * 
		 * @param attr The attribute name
		 * @return The value of the attribute 
		 */
		protected int get( final char attr ) {
			return attributes.get( attr );
		}
		
		/**
		 * @return The sum of all attribute values
		 */
		public long sum( ) {
			return attributes.values( ).stream( ).mapToInt( i -> i ).sum( );
		}
		
		/** @return A copy of this part */
		protected Part copy( ) {
			final Part p = new Part( );
			for( final char ch : attributes.keySet( ) ) {
				p.attributes.put( ch, attributes.get( ch ) );
			}
			return p;
		}
	}
	
	/**
	 * Simple container for a rule-based flow system
	 * 
	 * @author Joris
	 */
	private static class SortFlow extends LabeledObject {
		/** The list of rules to apply in their order */
		private final List<SortRule> rules;
		
		/** 
		 * Creates a new sorting flow container
		 * 
		 * @param name The name of this flow
		 */
		public SortFlow( final String name ) {
			super( name );
			rules = new ArrayList<>( );
		}
		
		/**
		 * Applies this flow to a part, returning the target flow name of the first
		 * rule that evaluates to true for the given part.
		 * 
		 * @param p The part to apply the flow to
		 * @return The name of the target flow that is specified by the first
		 *   matching sorting rule 
		 */
		public String apply( final Part p ) {
			for( final SortRule r : rules )
				if( r.matches( p ) ) return r.target;
			
			throw new RuntimeException( "Falied to match any rule" );
		}
		
		/**
		 * Reconstructs a sorting flow from its string description
		 * 
		 * @param flow The flow as a string name{rules} where rules is a comma
		 *   separated string of sorting rules
		 * @return The sorting flow object
		 */
		public static SortFlow fromString( final String flow ) {
			// create an empty flow and add all rules to it
			final SortFlow f = new SortFlow( flow.substring( 0, flow.indexOf( '{' ) ) );			
			for( final String rule : flow.substring( flow.indexOf( '{' ) + 1, flow.indexOf( '}' ) ).split( "," ) )
				f.rules.add( SortRule.fromString( rule ) );			
			return f;
		}
	}
	
	/**
	 * Describes a single rule of a sorting flow
	 * 
	 * @author Joris
	 */
	private static class SortRule {
		/** The attribute to condition on */
		private final char attr;
		
		/** The testing operation to perform*/
		private final char op;
		
		/** The attribute value to match */
		private final int value;
		
		/** The target flow to move to if the rule condition is met */
		private final String target;
		
		/**
		 * Creates a new sorting rule
		 * 
		 * @param attribute The attribute to test
		 * @param op The operation to use in testing
		 * @param value The value to test against
		 * @param target The name of the target flow if the rule test evaluates
		 *   successfully 
		 */
		private SortRule( final char attribute, final char op, final int value, final String target ) {
			this.attr = attribute;
			this.op =  op;
			this.value = value;
			this.target = target;
		}
		
		/**
		 * Creates a new rule with no condition that will always evaluate to true
		 * 
		 * @param target The target flow for this rule
		 */
		private SortRule( final String target ) {
			this( '-', '-', 0, target );
		}
		
		/**
		 * Tests if a given part attribute configuration matches the condition of
		 * this rule
		 * 
		 * @param p The part to test
		 * @return True iff the part attribute value we test is lower or higher
		 *   than the test value, depending on the operator
		 */
		public boolean matches( final Part p ) {
			if( !hasCondition( ) ) return true;

			final int matchvalue = p.attributes.get( attr );
			switch( op ) {
				case '<': return matchvalue < value;
				case '>': return matchvalue > value;
				default: throw new RuntimeException( "Unknown match operation: " + op );
			}
		}
		
		/**
		 * Reconstructs the rule from a string description
		 * 
		 * @param rule The rule description string
		 * @return The sorting rule object
		 */
		public static SortRule fromString( final String rule ) {
			// unconditional rule?
			if( !rule.contains( ":" ) ) return new SortRule( rule );
			
			// nope, parse the rule conditions
			return new SortRule( rule.charAt( 0 ), rule.charAt( 1 ), Integer.parseInt( rule.substring( 2, rule.indexOf( ':' ) ) ), rule.substring( rule.indexOf( ':' ) + 1 ) );
		}
		
		/** @return True if this rule has conditions, false if rule is always true */
		private boolean hasCondition( ) { return attr != '-'; }
	}
}
