package challenges.day15;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aocutil.object.LabeledObject;

/**
 * A setup of boxes that hold lenses to produce a powerful light beam
 * 
 * @author Joris
 */
public class LensBoxes {
	/** The boxes containing the lenses, mapped by their index (Java does not
	 *  allow arrays of lists) */
	protected final Map<Integer, List<Lens>> boxes;

	/**
	 * Creates a new boxed lens setup
	 */
	public LensBoxes( ) {
		boxes = new HashMap<>( 256 );
		for( int i = 0; i < 256; i++ )
			boxes.put( i, new ArrayList<>( ) );
	}
	
	/**
	 * Execute a box modify operation that will add, update or remove a lens from
	 * its corresponding box
	 * 
	 * @param lcmd The command to execute
	 */
	public void execute( final String lcmd ) {
		// get the lens label and operation from the string
		if( lcmd.contains( "-" ) ) {
			// remove a lens
			remove( lcmd.substring( 0, lcmd.indexOf( "-" ) ) );
		} else {
			// add or update a lens
			final int idx = lcmd.indexOf( "=" );
			add( new Lens( lcmd.substring( 0, idx ), Integer.parseInt( lcmd.substring( idx + 1 ) ) ) );
		}		
	}
	
	/**
	 * Adds/sets the given lens in its corresponding box. If a lens with the same
	 * label is present, that one will be replaced. Otherwise a new lens will be
	 * added at the end of the box
	 * 
	 * @param lens The lens to add/update
	 */
	private void add( final Lens lens ) {
		final List<Lens> box = getBoxFromLabel( lens.getLabel( ) );
		
		// if the label is already present, replace the lens
		for( int i = box.size( ) - 1; i  >= 0; i-- )
			if( box.get( i ).getLabel( ).equals( lens.getLabel( ) ) ) {
				box.set( i, lens );
				return;
			}
			
		// the lens was not present, add it
		box.add( lens );
	}
	
	/**
	 * Removes any lens with the given label from the box corresponding to the
	 * label. If no such lens is found, nothing happens.
	 * 
	 * @param label The label of the lenses to remove
	 */
	private void remove( final String label ) {
		final List<Lens> box = getBoxFromLabel( label );
		for( int i = box.size( ) - 1; i  >= 0; i-- )
			if( box.get( i ).getLabel( ).equals( label ) ) box.remove( i );		
	}
	
	/**
	 * Determines the box for the given lens label
	 * 
	 * @param label The label of the lens
	 * @return The box it should be in
	 */
	private List<Lens> getBoxFromLabel( final String label ) {
		return boxes.get( hash( label ) );
	}
	
	/**
	 * Computes the focus power of the box array, based upon the lenses in the
	 * boxes and their order.
	 * 
	 * @return The total lens power over all boxes
	 */
	public long getFocusPower( ) {
		long power = 0;
		for( final int i : boxes.keySet( ) ) {
			final List<Lens> box = boxes.get( i );
			for( int j = 0; j < box.size( ); j++ )
				power += (i + 1) * (j + 1) * box.get( j ).strength;
		}
		return power;
	}
	
	/**
	 * Computes the hash value for a given string
	 * 
	 * @param input The input string
	 * @return Its hash value [0,255]
	 */
	public static int hash( final String input ) {
		int current = 0;
		for( int i = 0; i < input.length( ); i++ ) {
			current += (int)input.charAt( i );
			current = (current * 17) % 256;
		}
		return current;
	}
	
	/**
	 * @return The string that describes the current contents of all boxes
	 */
	@Override
	public String toString( ) {
		final StringBuilder sb = new StringBuilder( );
		for( final int boxidx : boxes.keySet( ) ) {
			final List<Lens> box = boxes.get( boxidx );
			if( !box.isEmpty( ) ) sb.append( "[Box " + boxidx + "] " + box + "\n" );
		}
		return sb.toString( );
	}

	
	/**
	 * Holds a single lens
	 * 
	 * @author Joris
	 */	
	private static class Lens extends LabeledObject {
		/** The focal strength */
		protected final int strength;
		
		/**
		 * Creates a new lens
		 * 
		 * @param label The lens label
		 * @param strength The focal strength
		 */
		public Lens( final String label, final int strength ) {
			super( label );
			this.strength = strength;
		}
		
		/** @return The lens label and strength as a single string */
		@Override
		public String toString( ) {
			return super.toString( ) + " " + strength;
		}
	}
}
