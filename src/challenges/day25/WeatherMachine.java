package challenges.day25;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import aocutil.graph.Edge;
import aocutil.graph.Graph;
import aocutil.graph.Node;

/**
 * A weather machine built haphazardly of a (too) large number of components
 * and must be split into two independent machines to restart snow production
 * 
 * @author Joris
 */
public class WeatherMachine {
	/** The graph of components in the machine */
	protected final Graph comp;

	/**
	 * Reconstructs the internals of the weather machine from the list of strings
	 * that describe the machinery
	 * 
	 * @param input List of strings that describe the components and connections
	 *   between the components
	 */
	public WeatherMachine( final List<String> input ) {
		// parse components and connections into a graph
		comp = new Graph( );
		for( final String in : input ) {
			final String[] i = in.split( ": " );
			final Node n = comp.addNode( i[0] );
			
			for( final String i2 : i[1].split( " " ) ) {
				final Node n2 = comp.addNode( i2 );
				comp.addEdge( new Edge( n, n2 ) );
			}
		}
	}
	
	/**
	 * Finds the three connections within the weather machine that, if cut, will
	 * split the machine into two independently operating machines.
	 * 
	 * @return The product of the number of components in each of the two sets
	 */
	public long split( ) {
		// we find the three edges to cut by running a exhaustive search over all
		// combinations of three edges, albeit guided by the number of times an
		// edge occurs in the All Pair Shortest Path matrix. The rationale is that
		// the more a certain edge is visited, the more likely it is on the
		// critical path between the two sets and hence a likely candidate for the
		// cut set
		
		// first build APSP from every component to every other component, storing
		// the actual paths
		final Map<Node, Map<Node, Path>> M = buildAPSP( );

		// then count edge occurrence in each of the paths
		final Map<Edge, EdgeCounter> count = new HashMap<>( );
		for( final Map<Node, Path> m : M.values( ) ) {
			for( final Path p : m.values( ) ) {
				for( final Edge e : p.edges ) {
					final EdgeCounter ec = count.getOrDefault( e, new EdgeCounter( e ) );
					ec.add( );
					count.put( e, ec );
				}
			}
		}
		
		// sort on most used edges, they are the most likely candidates to cut
		final List<EdgeCounter> C = new ArrayList<>( count.values( ) );
		C.sort( EdgeCounter::compareTo );

		// use a brute-force algorithm that eventually will have tried all
		// combinations of three edges, but the selection of edges to cut first is
		// based upon their edge count. This will likely result in finding a cut
		// set fast but is guaranteed to find this set eventually
		for( int i = 0; i < C.size( ) - 2; i++ ) {
			comp.removeEdge( C.get( i ).edge );
			for( int j = i + 1; j < C.size( ) - 1; j++ ) {
				comp.removeEdge( C.get( j ).edge );
				for( int k = j + 1; k < C.size( ); k++ ) {
					comp.removeEdge( C.get( k ).edge );
		
					// now find the two disjoint sets, i.e., cliques, in the original graph and
					// multiply their sizes to produce the required answer
					final List<Graph> cliques = comp.getCliques( );
					if( cliques.size( ) == 2 ) return cliques.get( 0 ).size( ) * cliques.get( 1 ).size( );
					
					comp.addEdge( C.get( k ).edge );
				}
				comp.addEdge( C.get( j ).edge );
			}
			comp.addEdge( C.get( i ).edge );
		}
		
		throw new RuntimeException( "There is no set of three edges that split the graph into two" );
	}
	
	/**
	 * Builds the APSP matrix that contains the shortest path for every pair of
	 * nodes in the graph
	 * 
	 * @return The APSP matrix
	 */
	private Map<Node, Map<Node, Path>> buildAPSP( ) {
		final Map<Node, Map<Node, Path>> APSP = new HashMap<>( );
		for( final Node n : comp.getNodes( ) ) APSP.put( n, buildAPSP( n ) );
		return APSP;
	}

	/**
	 * Finds the APSP matrix from a specified starting point to all other nodes
	 * in the graph
	 * 
	 * @param start The starting node
	 * @return The APSP map that contains the shortest path from the starting
	 *   node to all other nodes in the graph
	 */
	private Map<Node, Path> buildAPSP( final Node start ) {
		// perform a BFS to find the shortest paths to all reachable nodes
		final Map<Node, Path> P = new HashMap<>( );
		Stack<Path> S = new Stack<>( );
		final Path initpath = new Path( start );
		S.push( initpath );
		
		// continue exploring until no more path is possible
		while( !S.isEmpty( ) ) {
			final Stack<Path> Snew = new Stack<>( );
			
			// expand current paths
			while( !S.isEmpty( ) ) {
				final Path p = S.pop( );
				
				// already seen the node at the end of the path? skip it
				if( P.containsKey( p.tail ) ) continue;
				P.put( p.tail, p );
				
				// add all paths that find new nodes from this one
				for( final Edge e : p.tail.getEdges( ) ) {
					if( P.containsKey( e.getOther( p.tail ) ) ) continue;
					Snew.push( p.extend( e ) );
				}
			}
			
			// swap sets
			S = Snew;
		}
		
		// return the APSP from the starting node
		return P;
	}
	
	/**
	 * Container for a path of edges
	 */
	private static class Path {
		/** The node at which this path ends */
		final Node tail;
		
		/** The list of edges that make up the path */
		final List<Edge> edges;
		
		/**
		 * Creates a new, empty path from the given starting node
		 * 
		 * @param start The node at which the path starts
		 */
		public Path( final Node start ) {
			this.tail = start;
			this.edges = new ArrayList<>( );
		}
		
		/**
		 * Creates a new path that extends the given one with the specified edge
		 * 
		 * @param p The path to extend
		 * @param e The edge to add to the path
		 */
		private Path( final Path p, final Edge e ) {
			this.tail = e.getOther( p.tail );
			this.edges = new ArrayList<>( p.edges );
			edges.add( e );
		}
		
		/**
		 * Extends the current path with the given edge
		 * 
		 * @param e The edge to add to the path
		 * @return A new path that is one edge longer
		 */
		public Path extend( final Edge e ) {
			return new Path( this, e );
		}

		/**
		 * @return The string description of the path
		 */
		@Override
		public String toString( ) {
			return edges.toString( );
		}
	}
	
	/**
	 * Simple structure to hold edge count and sort on this value
	 *
	 * @author Joris
	 */
	private class EdgeCounter implements Comparable<EdgeCounter> {
		/** The edge we are counting */
		protected final Edge edge;
		
		/** The actual count */
		private long count;
		
		/**
		 * Creates a new edge counter, initialised add zero
		 * 
		 * @param e The edge to count
		 */
		public EdgeCounter( final Edge e ) {
			this.edge = e;
			this.count = 0;
		}
		
		/**
		 * Adds another one to the count
		 */
		protected void add( ) {
			count++;
		}
		
		/**
		 * Compares against another edge counter and returns a value that will
		 * result in an ordering from largest to smallest
		 * 
		 * @param ec The edge counter to compare against
		 * @return ec.cunt - count
		 */
		@Override
		public int compareTo( final EdgeCounter ec ) {
			return Long.compare( ec.count,  count );
		}
		
	}
}
