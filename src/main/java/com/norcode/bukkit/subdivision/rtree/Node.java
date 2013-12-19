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

import com.norcode.bukkit.subdivision.region.Region;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

/**
 * An R-Tree Node.
 * 
 * @author cjones
 */
public class Node implements Bounded {
	/**
	 * Create a R-Tree.
	 *
	 * @return the root of the R-Tree.
	 */
	public static Node create() {
		Node rtree = new Node();

		return rtree;
	}

	/**
	 * The minimum bounds of all child node Region entries.
	 */
	private Bounds bounds;
	
	/**
	 * Region entries, a LinkedList of Node objects.
	 */
	private LinkedList<Node> entries;
	
	/**
	 * Region entries.
	 * 
	 * <p>
	 * Only leaf nodes contain Region entries.
	 */
	private LinkedList<com.norcode.bukkit.subdivision.region.Region> regions;
	
	/**
	 * Minimum region per node count.
	 */
	private final int minimumRegionCount = 2;
	
	/**
	 * The Node's parent.
	 */
	private Node parent;
	
	/**
	 * Maximum region per node count.
	 */
	private final int regionCount = 50;

	/**
	 * Create a node.
	 */
	protected Node() {
		super();
		
		// create the region entries array
		entries = new LinkedList<Node>();
		
		// create the index list
		regions = new LinkedList<com.norcode.bukkit.subdivision.region.Region>();
	}
	
	/**
	 * Add a bounded element to the node or leaf.
	 */
	protected void add(Bounded b) {
		if (b instanceof Region) {
			add((Region)b);
		} else {
			add((Node)b);
		}
	}

	/**
	 * Add a new Region element to the leaf node.
	 * 
	 * @param r the Region element.
	 */
	protected void add(com.norcode.bukkit.subdivision.region.Region r) {
		Node partner = null;
		
		if (regions.size() < regionCount) {
			regions.add(r);
			
			// merge boundaries
			bounds = Bounds.merge(bounds, r.getBounds());
		} else {
			partner = splitNode(r);
		}
		
		// adjust the tree
		adjustTree(partner);
	}
	
	/**
	 * Add a new Node element to the region node.
	 * 
	 * @param node the Node element.
	 */
	protected void add(Node node) {
		Node partner = null;
		
		if (entries.size() < regionCount) {
			entries.add(node);
			
			// merge the boundaries
			bounds = Bounds.merge(bounds, node.getBounds());
			
			node.parent = this;
		} else {
			partner = splitNode(node);
		}
		
		// adjust the tree
		adjustTree(partner);
	}
	
	/**
	 * Adjust the tree, adjusting covering rectangles and 
	 * propagating node splits as necessary.
	 * 
	 * @param node the (optional) partner, split leaf node.
	 */
	protected void adjustTree(Node node) {
		// if this isn't the root
		if (!isRoot()) {
			// adjust the node's covering rectangle
			// get the bounds from the first entry
			if (isLeaf()) {
				if (regions.size() > 0) {
					bounds = regions.get(0).getBounds();
				}
				
				// merge the bounds with each child
				for (int i = 1; i < regions.size(); i++) {
					bounds = Bounds.merge(bounds, regions.get(i).getBounds());
				}
			} else {
				if (entries.size() > 0) {
					bounds = entries.get(0).getBounds();
				}
				
				// merge the bounds with each child
				for (int i = 1; i < regions.size(); i++) {
					bounds = Bounds.merge(bounds, entries.get(i).getBounds());
				}
			}
			
			// propagate a node split upward
			if (node != null) {
				parent.add(node);
			}
		} else {
			if (node != null) {
				if (parent == null) {
					// move old root
					Node first = new Node();
					first.regions.addAll(regions);
					first.entries.addAll(entries);
					first.bounds = bounds;
					first.parent = this;
					node.parent = this;
					
					regions.clear();
					entries.clear();
					add(first);
					add(node);
				}
			}
		}
	}
	
	/**
	 * Get all indices.
	 * 
	 * @return a List of all Region objects under this node.
	 */
	public LinkedList<Region> allIndices() {
		LinkedList<Region> ll = new LinkedList<Region>();
		
		allIndices(ll);
		
		return ll;
	}
	
	/**
	 * Get all indices.
	 * 
	 * @param ll the linked list of Region objects.
	 */
	protected void allIndices(LinkedList<Region> ll) {
		if (isLeaf()) {
			ll.addAll(regions);
		} else {
			for (Iterator<Node> it = entries.iterator(); 
				 it.hasNext(); ) {
				it.next().allIndices(ll);
			}
		}
	}
	
	/**
	 * Assign an Region entry to an appropriate split node.
	 * 
	 * <p>
	 * The Region is added to the group whose covering bounds
	 * will have to be enlarged least, the group with the
	 * smaller volume, or the one with fewer entries.
	 * 
	 * @param node1 the first node.
	 * @param node2 the second node.
	 * @param b the Bounded entry to add.
	 */
	protected void assignToNode(Node node1, Node node2, Bounded b) {
		int vol1 = Bounds.merge(node1.bounds, b.getBounds()).getVolume();
		int vol2 = Bounds.merge(node2.bounds, b.getBounds()).getVolume();
		
		if (vol1 > vol2) {
			// assign it to node 2
			node2.add(b);
		} else if (vol1 < vol2) {
			// assign it to node 1
			node1.add(b);
		} else {
			if (vol1 > vol2) {
				// assign it to node 2
				node2.add(b);
			} else if (vol1 < vol2){
				// assign it to node 1
				node1.add(b);
			} else {
				if (node1.regions.size() < node2.regions.size()) {
					// add it to node 1
					node1.add(b);
				} else {
					// default it to node 2
					node2.add(b);
				}
			}
		}
	}
	
	/**
	 * Choose a leaf node in which to place a new Region entry.
	 * 
	 * @param node the root node.
	 * @param idx the Region entry.
	 * @return the leaf Node.
	 */
	protected Node chooseLeaf(Node node, Region idx) {
		// if node isn't a leaf
		if (!node.isLeaf()) {
			// an entry that contain the bounds of the Region
			Node smallestArea = null;
			
			for (Iterator<Node> it = node.entries.iterator();
				 it.hasNext(); ) {
				node = it.next();

				if (node.bounds.contains(idx.getBounds())) {
					// this node contains the index bounds
					// in case of a tie, use the smaller area
					if (smallestArea == null ||
						smallestArea.bounds.getVolume() >
						node.bounds.getVolume()) {
						smallestArea = node;
					}
				} else {
					// just grab a node, any node
					smallestArea = node;
				}
				
				// setting this to null so we can flag
				// this as an invalid, non-leaf
				node = null;
			}
			
			if (smallestArea != null) {
				// descend until a leaf node is reached
				node = chooseLeaf(smallestArea, idx);
				
				if (node == null) {
					node = smallestArea;
				}
			}
		}
		
		return node;
	}
	
	/**
	 * Condense the tree after entry deletion.  Adjust
	 * convering rectangles on the path to the root,
	 * making them smaller if possible.
	 */
	protected void condenseTree() {
		// orphaned node set
		HashSet<Node> set = new HashSet<Node>();
		
		condenseTree(set);
		
		// re-insert orphaned entries
		Node entry;
		
		for (Iterator<Node> it = set.iterator();
			 it.hasNext(); ) {
			entry = it.next();
			
			for (Iterator<Region> iti = entry.allIndices().iterator();
				 iti.hasNext(); ) {
				insert(iti.next());
			}
		}
	}
	
	/**
	 * Condense the tree after entry deletion.
	 * 
	 * @see #condenseTree()
	 * @param set the set of removed nodes.
	 */
	public void condenseTree(Set<Node> set) {
		if (!isRoot()) {
			if (entries.size() < minimumRegionCount) {
				// eliminate an under-full node
				// remove this node in its parent
				parent.entries.remove(this);
				
				// add this to the orphaned set
				set.add(this);
			}

			// move up to the next level of the tree
			parent.condenseTree(set);
		}


		// adjust the covering rectangle
		bounds = null;
		if (entries.size() > 0) {
			bounds = entries.get(0).bounds;

			for (int i = 0; i < entries.size(); i++) {
				bounds = Bounds.merge(bounds, entries.get(i).bounds);
			}
		}
		if (regions.size() > 0) {
			bounds = (bounds == null ? regions.get(0).getBounds() : bounds);
			for (int i=1; i<regions.size(); i++) {
				bounds = Bounds.merge(bounds, regions.get(i).getBounds());
			}
		}

	}
	
	/**
	 * Find a leaf node.
	 * 
	 * @param region the Region entry to find.
	 * @return the leaf node, or null if not found.
	 */
	protected Node findLeaf(Region region) {
		Node leaf = null;
		
		if (!isLeaf()) {
			for (Iterator<Node> itn = entries.iterator(); 
				 itn.hasNext(); ) {
				// check each entry to determine if
				// this overlaps the Region
				leaf = itn.next();
				
				if (leaf.bounds.isOverlappedBy(region.getBounds())) {
					// recursively find the leaf
					leaf = leaf.findLeaf(region);
					break;
				}
			}
		} else {
			for (Iterator<Region> iti = regions.iterator();
				 iti.hasNext(); ) {
				if (iti.next().equals(region)) {
					// match -- return this leaf node
					leaf = this;
					break;
				}
			}
		}
		return leaf;
	}
	
	public Bounds getBounds() {
		return bounds;
	}
	
	public LinkedList<Node> getEntries() {
		return entries;
	}
	
	public LinkedList<Region> getIndex() {
		return regions;
	}
	
	/**
	 * Insert a new Region entry.
	 * 
	 * @param idx the new Region object.
	 */
	public void insert(Region idx) {
		// select a leaf node in which to place idx
		Node leaf = chooseLeaf(this, idx);
		
		// add the idx to the leaf node
		leaf.add(idx);
	}
	
	/**
	 * Indicates if this is a leaf node.
	 * 
	 * @return true if this is a leaf node.
	 */
	public boolean isLeaf() {
		return entries.size() == 0;
	}
	
	/**
	 * Returns true if this is a root node.
	 * @return
	 */
	public boolean isRoot() {
		return parent == null;
	}
	
	/**
	 * Pick two Region seed nodes with a linear algorithm.
	 * 
	 * @return an array of two empty Region objects.
	 */
	protected Region[] pickIndexSeeds() {
		Region idx = null, low = null, high = null;
		double normX, normY, normZ;
		double separation, lowSep = Double.MAX_VALUE, highSep = Double.MIN_VALUE;
		Point3D origin, originHLS = null, originLHS = null;
		Bounds b = null;
		Iterator<Region> iti;
		
		// find extreme rectangles along all dimensions
		for (iti = regions.iterator(); iti.hasNext(); ) {
			idx = iti.next();
			
			if (b == null) {
				b = idx.getBounds();
				originHLS = new Point3D(b.getX1(), b.getY1(), b.getZ1());

				originLHS = new Point3D(b.getX2(), b.getY2(), b.getZ2());
				continue;
			}
			
			b = idx.getBounds();
			
			// find the entry whose rectangle has the highest low side
			origin = new Point3D(b.getX1(), b.getY1(), b.getZ1());
			if (origin.compareTo(originHLS) < 0) {
				originHLS = origin;
				continue;
			}
			
			// find the entry whose rectangle has the lowest high side
			origin = new Point3D(b.getX2(), b.getY2(), b.getZ2());
			if (origin.compareTo(originLHS) > 0) {
				originLHS = origin;
				continue;
			}
		}
		
		// record the separation
		separation = originHLS.getDistance(originLHS);

		// adjust for the shape of the rectangle cluster
		// normalize the separations by dividing by the width
		// of the entire set along the corresponding dimension
		normX = separation / bounds.getWidth();
		normY = separation / bounds.getHeight();
		normZ = separation / bounds.getDepth();
		
		// select the most extreme pair
		// choose the pair with the greatest normalized
		// separation along any dimension
		for (iti = regions.iterator(); iti.hasNext(); ) {
			b = iti.next().getBounds();
			separation = Math.abs(b.getX1() - normX) +
						 Math.abs(b.getY1() - normY) + 
						 Math.abs(b.getZ1() - normZ);
			
			if (separation < lowSep) {
				lowSep = separation;
				low = idx;
			} else if (separation > highSep) {
				highSep = separation;
				high = idx;
			}
		}
		
		return new Region[] {low, high};
	}
	
	/**
	 * Delete an Region element from the R-Tree.
	 * 
	 * @param idx the Region element to remove.
	 * @return the Region element removed, or null if not found.
	 */
	public Region remove(Region idx) {
		// find the leaf containing the index element
		Node leaf = findLeaf(idx);
		
		if (leaf != null) {
			// remove the index element from the leaf
			leaf.regions.remove(idx);
			
			// propagate changes
			leaf.condenseTree();
			
			// shorten the tree, if needed
			if (!leaf.isRoot()) {
				while (!leaf.parent.isRoot()) {
					leaf = leaf.parent;
				}

				// the leaf parent is the root
				if (leaf.parent.entries.size() < 2) {
					// the root node has only one child
					// make the root a leaf
					leaf.parent.entries.clear();
					leaf.parent.entries.addAll(leaf.entries);
					// shouldn't need to clear the Region entries
					leaf.parent.regions.addAll(leaf.regions);
				}
			}
		} else {
			idx = null;
		}
		
		return idx;
	}

	/**
	 * Search the node.
	 * 
	 * @param s the boundaries of the search rectangle.
	 * @return the List of Region records.
	 * @return true if the search rectangle overlaps the entries' regions.
	 */
	public LinkedList<Region> search(Bounds s) {
		LinkedList<Region> found = new LinkedList<Region>();
		search(found, s);
		return found;
	}

	/**
	 * Recursive tree search.
	 */
	protected void search(LinkedList<Region> found, Bounds s) {
		Region idx;
		Node node;
		
		if (isLeaf()) {
			for (Iterator<Region> iti = regions.iterator();
				 iti.hasNext(); ) {
				idx = iti.next();
				if (idx.getBounds().isOverlappedBy(s)) {
					found.add(idx);
				}
			}
		} else {
			for (Iterator<Node> itn = entries.iterator(); 
				 itn.hasNext(); ) {
				node = itn.next();
				if (node.getBounds().insersects(s)) {
					node.search(found, s);
				}
			}
		}
	}

	public void setBounds(Bounds bounds) {
		this.bounds = bounds;
	}

	public void setEntries(LinkedList<Node> entries) {
		this.entries = entries;
	}

	public void setIndex(LinkedList<Region> index) {
		this.regions = index;
	}
	
	/**
	 * Split this node.
	 * 
	 * @param idx the Region entry to add.
	 * @return the new partner Node.
	 */
	protected Node splitNode(Region idx) {
		Iterator<Region> iti;
		
		// pick the first entry for each group
		Node node1 = new Node(), node2 = new Node();
		Region[] seedIndices = pickIndexSeeds();
		node1.add(seedIndices[0]);
		node2.add(seedIndices[1]);
		regions.remove(seedIndices[0]);
		regions.remove(seedIndices[1]);
		
		// check if done
		while (regions.size() > 0) {
			// check to see if one of the nodes needs more elements
			if (regions.size() < minimumRegionCount + 1 &&
				node1.regions.size() < minimumRegionCount) {
				for (iti = regions.iterator(); iti.hasNext(); ) {
					node1.add(iti.next());
				}
				
				break;
			} else if (regions.size() < minimumRegionCount + 1 &&
				node2.regions.size() < minimumRegionCount) {
				for (iti = regions.iterator(); iti.hasNext(); ) {
					node2.add(iti.next());
				}
				
				break;
			} else {
				// select entry to assign
				assignToNode(node1, node2, regions.remove());
			}
		}
		
		if (idx != null) {
			assignToNode(node1, node2, idx);
		}
		
		// clear the index
		regions.clear();
		
		// add the Region of the first split node to this index
		regions.addAll(node1.regions);
		
		// return the partner node
		return node2;
	}
	
	/**
	 * Split this node.
	 * 
	 * @param node the Region entry to add.
	 * @return the new partner Node.
	 */
	protected Node splitNode(Node node) {
		int i = 0;

		Node node1 = new Node(), node2 = new Node();
		
		// fill the first node with the first half
		for ( ; i < entries.size() / 2; i++) {
			node1.add(entries.get(i));
		}
		
		// fill the second node with the second half
		for ( ; i < entries.size(); i++) {
			node2.add(entries.get(i));
		}
		
		// reset the bounds on this original node
		bounds = node1.bounds;
		
		// clear this original node's entries
		entries.clear();
		
		// copy the entries from the first node to this original 
		// node
		for (Iterator<Node> itn = node1.entries.iterator(); 
			 itn.hasNext(); ) {
			add(itn.next());
		}

		// assign the new node to one of the other two
		assignToNode(this, node2, node);
		
		// return the partner node
		return node2;
	}
	
	/**
	 * Return a String representation of the R-Tree data structure.
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		
		// get the level
		int level = 0;
		Node par = parent;
		while (par != null) {
			level++;
			par = par.parent;
		}
		
		// insert a number of tabs for each parent
		for (int i = 0; i < level; i++) {
			sb.append("\t");
		}
		sb.append(getClass().getName() + " (0x" + Long.toHexString(hashCode()) + ") [bounds=" + bounds + ", entries.size=" + entries.size() + ",regions.size=" + regions.size() + "]\n");
		
		// for each entry
		for (Iterator<Node> itn = entries.iterator();
			 itn.hasNext(); ) {
			sb.append(itn.next().toString());
		}
		
		return sb.toString();
	}
}
