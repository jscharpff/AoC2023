package challenges.day24;

import java.util.ArrayList;
import java.util.List;

import aocutil.algebra.Linear;
import aocutil.number.NumberUtil;
import aocutil.string.RegexMatcher;

/**
 * A storm of hail stones
 * 
 * @author Joris
 */
public class HailStorm {
	/** The hail stones in the storm */
	protected final List<HailStone> hail;
	
	/**
	 * Creates a hail storm container from a desription of hail stones
	 * 
	 * @param input The list of hail stone positions and trajectories
	 */
	public HailStorm( final List<String> input ) {
		hail = new ArrayList<>( );
		for( final String in : input ) hail.add( HailStone.fromString( in ) );
	}

	/**
	 * Determines the intersections between all paths of each pair of hail stones
	 * in the storm on the XY plane and returns the number of intersections
	 * occurring within the specified lower and upper bounds on the XY
	 * coordinates.
	 * 
	 * @param lb The lower bound x and y values
	 * @param ub The upper bound x and y values
	 * @return The number of XY plane intersections within the bounds
	 */
	public long intersectTrails2D( final long lb, final long ub ) {
		// for every pair of hail stones, find the intersection of their trails, if
		// any
		final List<Vec3d> I = new ArrayList<>( );
		for( int i = 0; i < hail.size( ) - 1; i++ )
			for( int j = i + 1; j < hail.size( ); j++ ) {
				// get the trail intersection coordinates (XY, Z will be always 0)
				final Vec3d isec = hail.get( i ).intersectTrail2D( hail.get( j ) );
				
				// do we have an intersection? this is not the case for parallel trails
				if( isec == null ) continue;

				// yes, keep it in a set
				I.add( isec );
			}

		// then count only intersections within bounds
		return I.stream( ).filter( c -> c.x >= lb && c.y >= lb && c.x <= ub && c.y <= ub ).count( );
	}
	
	/**
	 * Finds the starting position and velocity of a trajectory that will collide
	 * with all hail stones at some time in the future.
	 * 
	 * @return The sum of x, y and z coordinates of the starting position of the
	 *   trajectory
	 */
	public long findCollisionStone( ) {
		return intersectTrails3D( ).pos.sum( );
	}
	
	/**
	 * Finds the 3D vector that intersects all the hail stones in the storm at
	 * any point in time.
	 * 
	 * @return The 'hail stone' that represents the vector that collides with all
	 *   hail stones in the storm
	 */
	private HailStone intersectTrails3D( ) {
		/*
		 the vector Vt + P that intersects all hail stones will satisfy the
		 equation Vt + P = At + B such that A and B are the velocity and position
		 vectors of each hail stone. Rewriting this for the x-axis gives:
		 Vx * t + Px = Ax * t + Bx for any hail stone At + B -> 
		 (Ax-Vx)*t = (Px-Bx) -> t = (Px-Bx)/(Ax-Vx) and similarly for y and z
		
		 the intersection must happen at a time t equal in the x, y and z terms,
		 hence: (Px-Bx)/(Ax-Vx) = (Py-By)/(Ay-Vy) = (Pz-Bz)/(Az-Vz)
		
		 equating only x and y we get (Px-Bx)/(Ax-Vx) = (Py-By)/(Ay-Vy) which can
		 be rewritten into (Px-Bx)(Ay-Vy) = (Py-By)(Ax-Vx) ->
		 PxAy - BxAy - PxVy + BxVy = PyAx - ByAx - PyVx + ByVx ->
		 PyVx - PxVy = -PxAy + BxAy - BxVy + PyAx - ByAx + ByVx
		 
		 In this, PyVX - PxVy are the position and velocity variables of the
		 intersection we are looking for they are the same for each hail stone
		 Thus if we take another hail stone A't + B', we get -PxAy + BxAy - BxVy
		 + PyAx - ByAx + ByVx = -PxA'y + B'xA'y - B'xVy + PyA'x - ByA'x + B'yVx
		
		 these can be reordered to get a function over the 4 variables we are
		 interested in, namely:
		 
		 (A'y-Ay)Px + (Ax-A'x)Py + (By-B'y)Vx + (B'x-Bx)Vy = 
		   -BxAy + ByAx + B'xA'y - B'yA'x
		  
		 THis can be solved by Guassian elimination. The Z position and velocity
		 can be derived from the result
		 */
		
		// build matrix of coefficients for Guassian elimination using the first
		// few velocity functions, using the first five hail stones as values for
		// the matrix coefficients
		final double[][] M = new double[4][5];
		for( int i = 0; i < M.length; i++ ) {
			final Vec3d A = hail.get( i ).vel;
			final Vec3d B = hail.get( i ).pos;
			final Vec3d A2 = hail.get( i + 1 ).vel;
			final Vec3d B2 = hail.get( i + 1 ).pos;
			
			// the variable coefficients matrix
			M[i][0] = A2.y - A.y; // (A'y-Ay)Px
			M[i][1] = A.x - A2.x; // (Ax - A'x)Py
			M[i][2] = B.y - B2.y; // (By - B'y)Vx
			M[i][3] = B2.x - B.x; // (B'x - Bx)Vy
			
			// and the value to equal: -AxBy + ByAx + B'xA'y - B'yA'x
			M[i][4] = -B.x*A.y + B.y * A.x + B2.x * A2.y - B2.y * A2.x;
		}

		// then perform Guassian elimination
		final double[][] result = NumberUtil.gaussianElimination( M );
		
		// get Px, Py, Vx and Vy from result. They are integer values but the
		// Guassian elimination introduces rounding errors in its computation,
		// hence we round them to the nearest integer
		final long Px = Math.round( result[0][4] );
		final long Py = Math.round( result[1][4] );
		final long Vx = Math.round( result[2][4] );
		final long Vy = Math.round( result[3][4] );
		
		// now for the z position and velocity we do the same thing: we solve
		// (Px-Bx)/(Ax-Vx) = (Pz-Bz)/(Az-Vz) by Guassian elimination, however now
		// we already now Px and Vx thus we solve:
		// (Ax-A'x)Z + (B'x-Bx)VZ = -BxAz + BzAx + B'xA'z - B'zA'x - (A'z-Az)X - (Bz-B'z)VX

		// build new matrix for the X/Z equation, now only requiring 3 hail stones
		final double[][] M2 = new double[2][3];
		for( int i = 0; i < M2.length; i++ ) {
			final Vec3d A = hail.get( i ).vel;
			final Vec3d B = hail.get( i ).pos;
			final Vec3d A2 = hail.get( i + 1 ).vel;
			final Vec3d B2 = hail.get( i + 1 ).pos;

			// fill in variable coefficients
			M2[i][0] = A.x - A2.x;
			M2[i][1] = B2.x - B.x;
			M2[i][2] = -B.x * A.z + B.z * A.x + B2.x * A2.z - B2.z * A2.x - (A2.z - A.z) * Px - (B.z - B2.z) * Vx;
		}
		
		// solve it by elimination
		final double[][] result2 = NumberUtil.gaussianElimination( M2 );
		
		// get values for Pz and Vz
		final long Pz = Math.round( result2[0][2] );
		final long Vz = Math.round( result2[1][2] );
		
		// and return the 'hail stone' that collides with all others at some point
		// in time
		return new HailStone( new Vec3d( Px, Py, Pz ), new Vec3d( Vx, Vy, Vz ) );
	}
	
	
	/**
	 * Object that describes a single hailstone in the storm
	 * 
	 * @author Joris
	 */
	private static class HailStone {
		/** The position of the hail stone */
		protected final Vec3d pos;
		
		/** The velocity of the stone, linear pace per axis */
		protected final Vec3d vel;
		
		/**
		 * Creates a new hailstone
		 * 
		 * @param position The initial position of the hailstone
		 * @param velocity The velocity of the hailstone
		 */
		public HailStone( final Vec3d position, final Vec3d velocity ) {
			this.pos = position;
			this.vel = velocity;
		}
		
		/**
		 * Determines the intersection of trails of this and the other hail stone
		 * on the XY plane, if any
		 * 
		 * @param s The hail stone to test intersection with
		 * @return The intersection of this and the other hail stone on the XY
		 *   plane, null if they do not intersect (parallel lines)
		 */
		public Vec3d intersectTrail2D( final HailStone s ) {
			// get linear equation of when the paths that the hail stones travel
			// along intersect on each axis in time, e.g. for the x axis of stone 1: 
			// x1 = v1.x * t1.x + p1.x -> t1.x = (x1 - p1.x) / v1.x
			final Linear x1 = new Linear( (double)vel.x, (double)pos.x );
			final Linear x2 = new Linear( (double)s.vel.x, (double)s.pos.x );			
			final Linear y1 = new Linear( (double)vel.y, (double)pos.y );
			final Linear y2 = new Linear( (double)s.vel.y, (double)s.pos.y );		

			// then, we find x1 = x2, i.e., the moment both hail stone paths cross on
			// the x axis, by substituting them with their functions based upon t1
			// and t2 to get their relationship. And we do the same for y. That is:
			// x1 = x2 -> xa1 * t1.x + xb1 = xa2 * t2.x + xb2 -> t1.x = xa2/xa1 * t2.x + (xb2 - xb1)/xa1
			final Linear t1x = x2.equalsTo( x1 );
			final Linear t1y = y2.equalsTo( y1 );
			final Linear t2x = x1.equalsTo( x2 );
			final Linear t2y = y1.equalsTo( y2 );
			
			// Then we solve for t1.x (and t2) equals t1.y to find the time at which x
			// and y values intersect. That is, when the hail stone trails intersect
			// on both axes. Thus: t1.x = t1.y -> tx1a * t1.x + tx1b = ty1a * t1.y + ty1b ->
			// t1.x = ty1a/tx1a * t1.y + (ty1b - tx1b)/tx1a
			// we solve both to test validity of time (t >= 0 and not infinite)
			final double t1 = t1x.solveForX( t1y );
			final double t2 = t2x.solveForX( t2y );
			if( !Double.isFinite( t1 ) || t1 < 0 || !Double.isFinite( t2 ) || t2 < 0 ) return null;

			// and finally we feed the value back into one of the original x and y
			// functions to get the coordinates at which the trails intersected
			return new Vec3d( x1.get( t1 ), y1.get( t1 ), 0 );
		}
		
		/**
		 * @return The string description of the hailstone
		 */
		@Override
		public String toString( ) {
			return pos + " @ " + vel;
		}
		
		/**
		 * Recreates a hail stone from a string description
		 * 
		 * @param input The string that describes the position and velocity of the
		 *   hailstone
		 * @return The HailStone object
		 */
		public static HailStone fromString( final String input ) {
			final String[] in = input.split( " @ " );
			return new HailStone( Vec3d.fromString( in[0] ), Vec3d.fromString( in[1] ) );
		}
	}
	
	/**
	 * A simple, double-valued 3D vector
	 * 
	 * @author Joris
	 */
	private static class Vec3d {
		/** X, Y and Z values of the vector */
		final double x;
		final double y;
		final double z;
		
		/**
		 * Constructs a new vector
		 * 
		 * @param x
		 * @param y
		 * @param z
		 */
		public Vec3d( final double x, final double y, final double z ) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
		
		/** @return The sum of vector values */
		public long sum( ) {
			return (long)(x + y + z);
		}
		
		/** @return The vector as a string (x,y,z) */
		@Override
		public String toString( ) {
			return "(" + x + "," + y + "," + z + ")";
		}
		
		/**
		 * Parses a vector from a string of comma-separated values
		 * 
		 * @param input The string describing the vector
		 * @return The vector
		 */
		public static Vec3d fromString( final String input ) {
			final RegexMatcher rm = RegexMatcher.match( "#D,\s+#D,\s+#D", input );
			return new Vec3d( rm.getLong( 1 ), rm.getLong( 2 ), rm.getLong( 3 ) );
		}
	}
}
