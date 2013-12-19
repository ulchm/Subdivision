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

import java.awt.Point;

/**
 * A three-dimensional point in some space.
 * 
 * <p>
 * The space is limited to the size of a signed integer.  
 * Floating point coordinates are rounded to the nearest
 * integral point.
 * 
 * @author cjones
 */
public class Point3D extends Point {
	static final long serialVersionUID = -2964730762699616706L;
	
	/**
	 * The Z-Coordinate of the point.
	 */
	public int z;
	
	/**
	 * Construct a new Point3D.
	 */
	public Point3D() {
		super();
	}

	/**
	 * Create a new Point3D that is a copy of an existing point.
	 * 
	 * @param p the existing point.
	 */
	public Point3D(Point3D p) {
		super(p);
		this.z = p.z;
	}

	/**
	 * Create a new Point3D with the given coordinates.
	 * 
	 * @param x the X coordinate.
	 * @param y the Y coordinate.
	 * @param z the Z coordinate.
	 */
	public Point3D(int x, int y, int z) {
		super(x, y);
		this.z = z;
	}
	
	/**
	 * Create a new Point3D based on a 2D point.
	 * 
	 * @param p the 2D point.
	 */
	public Point3D(Point p) {
		super(p);
		z = 0;
	}

	/**
	 * Get the Z coordinate with double precision.
	 * 
	 * @return the Z coordinate.
	 */
	public double getZ() {
		return z;
	}
	
	/**
	 * Set the location of the point.
	 * 
	 * @param x the X coordinate.
	 * @param y the Y coordinate.
	 * @param z the Z coordinate.
	 */
	public void setLocation(int x, int y, int z) {
		move(x, y, z);
	}

    /**
     * Sets the location of this point to the specified double coordinates.
     * The double values will be rounded to integer values.
     * Any number smaller than <code>Integer.MIN_VALUE</code>
     * will be reset to <code>MIN_VALUE</code>, and any number
     * larger than <code>Integer.MAX_VALUE</code> will be
     * reset to <code>MAX_VALUE</code>.
     *
     * @param x the <i>x</i> coordinate of the new location
     * @param y the <i>y</i> coordinate of the new location
     * @param z the <i>z</i> coordinate of the new location.
     * @see #getLocation
     */
    public void setLocation(double x, double y, double z) {
		super.setLocation(x, y);
		this.z = (int) Math.floor(z+0.5);
    }

    /**
     * Moves this point to the specified location in the 
     * (<i>x</i>,&nbsp;<i>y</i>) coordinate plane. This method
     * is identical with <code>setLocation(int,&nbsp;int)</code>.
     * @param       xc  the <i>x</i> coordinate of the new location
     * @param       yc  the <i>y</i> coordinate of the new location
     * @param		zc  the <i>z</i> coordinate of the new location.
     * @see         java.awt.Component#setLocation(int, int)
     */
    public void move(int xc, int yc, int zc) {
		super.move(xc, yc);
		this.z = zc;
    }	

    /**
     * Translates this point, at location (<i>x</i>,&nbsp;<i>y</i>), 
     * by <code>dx</code> along the <i>x</i> axis and <code>dy</code> 
     * along the <i>y</i> axis so that it now represents the point 
     * (<code>x</code>&nbsp;<code>+</code>&nbsp;<code>dx</code>, 
     * <code>y</code>&nbsp;<code>+</code>&nbsp;<code>dy</code>). 
     * @param       dx   the distance to move this point 
     *                            along the <i>x</i> axis
     * @param       dy    the distance to move this point 
     *                            along the <i>y</i> axis
     * @param		dz	 the distance to move this point
     * 							  along the <i>z</i> axis.
     */
    public void translate(int dx, int dy, int dz) {
		super.translate(dx, dy);
		this.z += dz;
    }	

    /**
     * Determines whether or not two points are equal. Two instances of
     * <code>Point2D</code> are equal if the values of their 
     * <code>x</code> and <code>y</code> member fields, representing
     * their position in the coordinate space, are the same.
     * @param obj an object to be compared with this <code>Point2D</code>
     * @return <code>true</code> if the object to be compared is
     *         an instance of <code>Point2D</code> and has
     *         the same values; <code>false</code> otherwise.
     */
    public boolean equals(Object obj) {
		if (obj instanceof Point3D) {
		    Point3D pt = (Point3D)obj;
		    return (x == pt.x) && (y == pt.y) &&
		    	   (z == pt.z);
		}
		
		return super.equals(obj);
    }

    /**
     * Returns a string representation of this point and its location 
     * in the (<i>x</i>,&nbsp;<i>y</i>, <i>z</i>) coordinate space. This method is 
     * intended to be used only for debugging purposes, and the content 
     * and format of the returned string may vary between implementations. 
     * The returned string may be empty but may not be <code>null</code>.
     * 
     * @return  a string representation of this point
     */
    public String toString() {
		return getClass().getName() + "[x=" + x + ",y=" + y + ",z=" + z + "]";
    }
	
	/**
	 * Compare this point to another point.
	 * 
	 * @param p the other point.
	 * @return 0 if equal, -1 if this is smaller, 1 if this is larger.
	 */
	public int compareTo(Point3D p) {
		int c = 0;
		if (!this.equals(p)) {
			if (x < p.x && y < p.y ||
				y < p.y && z < p.z ||
				x < p.x && z < p.z) {
				c = -1;
			} else {
				c = 1;
			}
		}
		
		return c;
	}

	/**
	 * Get distance.
	 * 
	 * @param p distance to another point.
	 * @return the distance to the other point.
	 */
	public double getDistance(Point3D p) {
		double dx = Math.abs(p.x - x);
		double dy = Math.abs(p.y - y);
		double dz = Math.abs(p.z - z);
		
		return Math.sqrt(dx * dx + dy * dy + dz * dz);
	}
}
