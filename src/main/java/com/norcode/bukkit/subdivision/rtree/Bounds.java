/*
	R-Tree Library (rtree.jar)
	Copyright (C) 2005 by Christopher R. Jones
	
	This library is free software; you can redistribute it and/or
	modify it under the terms of the GNU Lesser General Public
	License as published by the Free Software Foundation; either
	version 2.1 of the License, or (at your option) any later version.
	
	This library is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
	Lesser General Public License for more details.
	
	You should have received a copy of the GNU Lesser General Public
	License along with this library; if not, write to the Free Software
	Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/

package com.norcode.bukkit.subdivision.rtree;

/**
 * An three-dimensional bounding box.
 * 
 * @author cjones
 */
public class Bounds implements Cloneable {
	private int x1, y1, z1, x2, y2, z2;

	/**
	 * Create the bounds.
	 */
	public Bounds() {
		super();
	}
	
	/**
	 * Create the bounds.
	 * 
	 * @param x1 the upper-left x-coordinate.
	 * @param y1 the upper-left y-coordinate.
	 * @param z1 the upper-left z-coordinate.
	 * @param x2 the bottom-right x-coordinate.
	 * @param y2 the bottom-right y-coordinate.
	 * @param z2 the bottom-right z-coordinate.
	 */
	public Bounds(int x1, int y1, int z1, int x2, int y2, int z2) {
		this.x1 = x1;
		this.y1 = y1;
		this.z1 = z1;
		this.x2 = x2;
		this.y2 = y2;
		this.z2 = z2;
	}
	
	/**
	 * Create bounds based on origin and size.
	 * 
	 * @param origin the origin of the bounds.
	 * @param width the width.
	 * @param height the height.
	 * @param depth the depth.
	 */
	public Bounds(Point3D origin, int width, int height, int depth) {
		this(origin.x, origin.y, origin.z, origin.x + width, origin.y + height, origin.z + depth);
	}

	/**
	 * Merge bounds, creating the minimum bounding box with 
	 * both bounds elements.
	 * 
	 * @return a new, merged bounds object.
	 */
	public static Bounds merge(final Bounds a, final Bounds b) {
		Bounds bounds = new Bounds();
		
		if (a != null && b != null) {
			// dumb merge
			if (a.x1 < b.x1) {
				bounds.x1 = a.x1;
			} else {
				bounds.x1 = b.x1;
			}
			
			if (a.y1 < b.y1) {
				bounds.y1 = a.y1;
			} else {
				bounds.y1 = b.y1;
			}
			
			if (a.z1 < b.z1) {
				bounds.z1 = a.z1;
			} else {
				bounds.z1 = b.z1;
			}
			
			if (a.x2 > b.x2) {
				bounds.x2 = a.x2;
			} else {
				bounds.x2 = b.x2;
			}
			
			if (a.y2 > b.y2) {
				bounds.y2 = a.y2;
			} else {
				bounds.y2 = b.y2;
			}
			
			if (a.z2 > b.z2) {
				bounds.z2 = a.z2;
			} else {
				bounds.z2 = b.z2;
			}
		} else if (a != null && b == null) {
			bounds = a;
		} else {
			bounds = b;
		}
		
		return bounds;
	}
	
	/**
	 * Indicates if the 3D point is contained within
	 * these bounds.
	 * 
	 * @param p the 3D point.
	 * @return true if this point is contained.
	 */
	public boolean contains(Point3D p) {
		return p.x >= x1 && p.x <= x2 &&
			   p.y >= y1 && p.y <= y2 &&
			   p.z >= z1 && p.z <= z2;
	}
	
	/**
	 * Indicates if the other bounds overlaps this.
	 * 
	 * @param b the other bounds.
	 */
	public boolean isOverlappedBy(Bounds b) {
		//		Cond1.  If A's left face is to the right of the B's right face,
		//				-  then A is Totally to right Of B
		if (this.x1 > b.getMax().getX()) return false;
		//		Cond2.  If A's right face is to the left of the B's left face,
		//				-  then A is Totally to left Of B
		if (this.x2 < b.getMin().getX()) return false;
		//		Cond3.  If A's top face is below B's bottom face,
		//				-  then A is Totally below B
		if (this.y1 > b.getMax().getY()) return false;
		//		Cond4.  If A's bottom face is above B's top face,
		//				-  then A is Totally above B
		if (this.y2 < b.getMin().getY()) return false;
		//		Cond5.  If A's front face is behind B's back face,
		//				-  then A is Totally behind B
		if (this.z1 > b.getMax().getZ()) return false;
		//		Cond6.  If A's back face is in front of B's front face,
		//				-  then A is Totally in front of B
		if (this.z2 < b.getMin().getZ()) return false;
		return true;
	}
	
	/**
	 * Indicates if the other bounds is completely contained
	 * by this.
	 * 
	 * @param b the other bounds.
	 */
	public boolean contains(Bounds b) {
		// all points are within the bounding box
		return 
			contains(new Point3D(b.x1, b.y1, b.z1)) &&
			contains(new Point3D(b.x1, b.y1, b.z2)) &&
			contains(new Point3D(b.x1, b.y2, b.z1)) &&
			contains(new Point3D(b.x1, b.y2, b.z2)) &&
			contains(new Point3D(b.x2, b.y1, b.z1)) &&
			contains(new Point3D(b.x2, b.y1, b.z2)) &&
			contains(new Point3D(b.x2, b.y2, b.z1)) &&
			contains(new Point3D(b.x2, b.y2, b.z2));
	}
	
	/**
	 * Indicates if the other bounds is intersected by this.
	 * 
	 * @param b the other bounds.
	 */
	public boolean insersects(Bounds b) {
		// any points or lines within the bounding box
		return contains(new Point3D(b.x1, b.y1, b.z1)) ||
			   contains(new Point3D(b.x1, b.y1, b.z2)) ||
			   contains(new Point3D(b.x1, b.y2, b.z1)) ||
			   contains(new Point3D(b.x1, b.y2, b.z2)) ||
			   contains(new Point3D(b.x2, b.y1, b.z1)) ||
			   contains(new Point3D(b.x2, b.y1, b.z2)) ||
			   contains(new Point3D(b.x2, b.y2, b.z1)) ||
			   contains(new Point3D(b.x2, b.y2, b.z2)) ||
			   b.contains(new Point3D(x1, y1, z1)) || 
			   b.contains(new Point3D(x1, y1, z2)) || 
			   b.contains(new Point3D(x1, y2, z1)) || 
			   b.contains(new Point3D(x1, y2, z2)) || 
			   b.contains(new Point3D(x2, y1, z1)) || 
			   b.contains(new Point3D(x2, y1, z2)) || 
			   b.contains(new Point3D(x2, y2, z1)) || 
			   b.contains(new Point3D(x2, y2, z2));
	}
	
	/**
	 * Get the height of the bounds.
	 * 
	 * @return the height.
	 */
	public int getHeight() {
		return y2 - y1;
	}
	
	/**
	 * Get the width of the bounds.
	 * 
	 * @return the width.
	 */
	public int getWidth() {
		return x2 - x1;
	}
	
	/**
	 * Get the depth of the bounds.
	 * 
	 * @return the depth.
	 */
	public int getDepth() {
		return z2 - z1;
	}
	
	/**
	 * Get the volume of the bounds.
	 * 
	 * @return the volume.
	 */
	public int getVolume() {
		return getHeight() * getWidth() * getDepth();
	}

	public int getX1() {
		return x1;
	}

	public void setX1(int x1) {
		this.x1 = x1;
	}

	public int getX2() {
		return x2;
	}

	public void setX2(int x2) {
		this.x2 = x2;
	}

	public int getY1() {
		return y1;
	}

	public void setY1(int y1) {
		this.y1 = y1;
	}

	public int getY2() {
		return y2;
	}

	public void setY2(int y2) {
		this.y2 = y2;
	}

	public int getZ1() {
		return z1;
	}

	public void setZ1(int z1) {
		this.z1 = z1;
	}

	public int getZ2() {
		return z2;
	}

	public void setZ2(int z2) {
		this.z2 = z2;
	}
	
	/**
	 * Duplicate the Bounds.
	 */
	public Object clone() {
		Bounds b = new Bounds();
		b.x1 = x1;
		b.x2 = x2;
		b.y1 = y1;
		b.y2 = y2;
		b.z1 = z1;
		b.z2 = z2;
		return b;
	}
	
	/**
	 * Compare this object for equality.
	 */
	public boolean equals(Object o) {
		boolean b = false;
		
		if (o instanceof Bounds) {
			Bounds z = (Bounds)o;
			
			b = z.x1 == x1 &&
				z.x2 == x2 &&
				z.y1 == y1 &&
				z.y2 == y2 &&
				z.z1 == z1 &&
				z.z2 == z2;
		}
		
		return b;
	}
	
	/**
	 * Get a String representation of the Bounds.
	 */
	public String toString() {
		return "(X:" + x1 + ",Y:" + y1 + ",Z:" + z1 + ") -> (X:" + x2 + "Y:" + y2 + ",Z:" + z2 + ")";
	}
	
	/**
	 * Get the origin of the Bounds (the upper-left corner of the bounds).
	 * 
	 * @return the origin of the Bounds.
	 */
	public Point3D getOrigin() {
		return new Point3D(x1, y1, z1);
	}

	public Point3D getMax() {
		return new Point3D(
				x1 > x2 ? x1 : x2,
				y1 > y2 ? y1 : y2,
				z1 > z2 ? z1 : z2
		);
	}

	public Point3D getMin() {
		return new Point3D(
				x1 < x2 ? x1 : x2,
				y1 < y2 ? y1 : y2,
				z1 < z2 ? z1 : z2
		);
	}
}
