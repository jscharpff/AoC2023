package challenges.day19;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import aocutil.object.LabeledObject;

public class PartSorter2 {
	/** The rules to use while sorting */
	protected final Map<String, SortFlow> flows;

	public PartSorter2( final List<String> ruleset ) {
		flows = new HashMap<>( );
		for( final String r : ruleset ) {
			final SortFlow sr = SortFlow.fromString( r );
			flows.put( sr.getLabel( ), sr );
		}
	}
	
	public long sort( final List<String> parts ) {
		long sum = 0;
		for( final String part : parts ) {
			final Part p = Part.fromString( part );
			if( isAccepted( p ) ) sum += p.sum( );			
		}
		return sum;
	}
	
	
	public long countAccepted( ) {
		return countAccepted( "in", new PartRange( ) );
	}

	private long countAccepted( final String nextflow, final PartRange range ) {
		if( nextflow.equals( "A" ) )  {
			return range.prod( );
		} else if( nextflow.equals( "R" ) ) {
			return 0; //range.invprod( );
		}
		
		final SortFlow f = flows.get( nextflow );
		long sum = 0;
		PartRange r = range.copy( ); 
		for( final SortRule srule : f.rules ) {
			final PartRange newrange = r.update( srule );
			
			// rule does not apply to the current range 
			if( newrange == null ) continue;
			sum += countAccepted( srule.target, newrange );			
			
			// next rule processing with inverted range of rule
						
			r = r.invert( srule );
			if( r == null ) continue;
		}
		
		return sum;
	}
	
	protected boolean isAccepted( final Part part ) {
		String flow = "in";
		
		while( !flow.equals( "A" ) ) {
			flow = flows.get( flow ).apply( part );
			if( flow.equals( "R" ) ) return false;
		}
		
		return true;
	}
	
	private static class PartRange {
		/** The map of part attributes */
		protected final Map<String, Integer> attranges;
		
		protected PartRange( ) {
			attranges = new HashMap<>( );
			for( final char c : new char[]{ 'x', 'm', 'a', 's' } ) {
				attranges.put( c + "min", 1 );
				attranges.put( c + "max", 4000 );
			}
		}
		
		public PartRange update( final SortRule rule ) {
			final PartRange newr = copy( );
			if( !rule.hasCondition( ) ) return newr;
			
			// update ranges based upon rule
			switch( rule.op ) {
				case '<':
					if( newr.attranges.get( rule.attr + "min" ) >= rule.value ) return null;
					newr.attranges.put( rule.attr + "max", rule.value - 1);					
					break;

				case '>':
					if( attranges.get( rule.attr + "max" ) <= rule.value ) return null;
					newr.attranges.put( rule.attr + "min", rule.value + 1);					
					break;
				
				default: throw new RuntimeException( "Invalid operation in rule: " + rule.op );
			}
			
			return newr;
		}
		
		
		public PartRange invert( final SortRule rule ) {
			final PartRange newr = copy( );
			if( !rule.hasCondition( ) ) return null;
			
			// update ranges based upon rule
			switch( rule.op ) {
				case '<':
					newr.attranges.put( rule.attr + "min", rule.value );					
					break;

				case '>':
					newr.attranges.put( rule.attr + "max", rule.value );					
					break;
				
				default: throw new RuntimeException( "Invalid operation in rule: " + rule.op );
			}
			
			return newr;
		}		
		
		protected PartRange copy( ) {
			final PartRange r = new PartRange( );
			for( final String s : attranges.keySet( ) ) {
				r.attranges.put( s, attranges.get( s ) );
			}
			return r;
		}
		
		protected long prod( ) {
			long p = 1;
			for( final char c : new char[]{ 'x', 'm', 'a', 's' } )
				p *= (long)(attranges.get( c + "max" ) - attranges.get( c + "min" ) + 1);
						
			return p;
		}
	}
	
	/**
	 * Simple data structure to hold a part with attribues
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
		 * @return The sum of all attribute values
		 */
		public long sum( ) {
			return attributes.values( ).stream( ).mapToInt( i -> i ).sum( );
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
