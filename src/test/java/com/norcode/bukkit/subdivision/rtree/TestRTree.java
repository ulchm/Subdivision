package com.norcode.bukkit.subdivision.rtree;

import com.norcode.bukkit.subdivision.datastore.RegionData;
import com.norcode.bukkit.subdivision.region.Region;
import junit.framework.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.UUID;

public class TestRTree {

	Point3D p1 = new Point3D(1, 1, 1);
	Point3D p2 = new Point3D(2, 2, 2);
	Point3D p3 = new Point3D(3, 3, 3);
	Point3D p4 = new Point3D(4, 4, 4);
	Point3D p5 = new Point3D(5, 5, 5);
	Point3D p6 = new Point3D(6, 6, 6);
	Point3D p7 = new Point3D(7, 7, 7);
	Point3D p8 = new Point3D(8, 8, 8);
	Point3D p9 = new Point3D(9, 9, 9);
	Point3D p10 = new Point3D(10, 10, 10);
	Bounds b1 = new Bounds(0, 0, 0, 3, 2, 3);
	Bounds b2 = new Bounds(2, 2, 2, 6, 5, 6);
	Bounds b3 = new Bounds(6, 6, 6, 8, 8, 8);
	Bounds b4 = new Bounds(4, 4, 4, 10, 10, 10);

	Region i1 = new Region(new RegionData((int)b1.getMin().getX(), (int)b1.getMin().getY(), (int)b1.getMin().getZ(),
										  (int)b1.getMax().getX(), (int)b1.getMax().getY(), (int)b1.getMax().getZ(),
										  UUID.randomUUID(), null, UUID.randomUUID(), 1,
										  new HashSet<UUID>(), new HashSet<UUID>(), new HashMap<String, String>()));
	Region i2 = new Region(new RegionData((int)b2.getMin().getX(), (int)b2.getMin().getY(), (int)b2.getMin().getZ(),
										  (int)b2.getMax().getX(), (int)b2.getMax().getY(), (int)b2.getMax().getZ(),
										  UUID.randomUUID(), null, UUID.randomUUID(), 1,
										  new HashSet<UUID>(), new HashSet<UUID>(), new HashMap<String, String>()));
	Region i3 = new Region(new RegionData((int)b3.getMin().getX(), (int)b3.getMin().getY(), (int)b3.getMin().getZ(),
										  (int)b3.getMax().getX(), (int)b3.getMax().getY(), (int)b3.getMax().getZ(),
										  UUID.randomUUID(), null, UUID.randomUUID(), 1,
										  new HashSet<UUID>(), new HashSet<UUID>(), new HashMap<String, String>()));

	@Test
	public void testInsertion() {
		Node tree = Node.create();
		tree.insert(i1);
		Bounds b = tree.getBounds();
		Assert.assertEquals(0, tree.getBounds().getX1());
		Assert.assertEquals(3, tree.getBounds().getX2());
		Assert.assertEquals(0, tree.getBounds().getY1());
		Assert.assertEquals(2, tree.getBounds().getY2());
		Assert.assertEquals(0, tree.getBounds().getZ1());
		Assert.assertEquals(3, tree.getBounds().getZ2());
		tree.insert(i2);
		Assert.assertEquals(0, tree.getBounds().getX1());
		Assert.assertEquals(6, tree.getBounds().getX2());
		Assert.assertEquals(0, tree.getBounds().getY1());
		Assert.assertEquals(5, tree.getBounds().getY2());
		Assert.assertEquals(0, tree.getBounds().getZ1());
		Assert.assertEquals(6, tree.getBounds().getZ2());
		tree.insert(i3);
		Assert.assertEquals(0, tree.getBounds().getX1());
		Assert.assertEquals(8, tree.getBounds().getX2());
		Assert.assertEquals(0, tree.getBounds().getY1());
		Assert.assertEquals(8, tree.getBounds().getY2());
		Assert.assertEquals(0, tree.getBounds().getZ1());
		Assert.assertEquals(8, tree.getBounds().getZ2());
	}

	@Test
	public void testRemoval() {
		Node tree = Node.create();
		tree.insert(i1);
		tree.insert(i2);
		tree.insert(i3);
		Assert.assertEquals(0, tree.getBounds().getX1());
		Assert.assertEquals(8, tree.getBounds().getX2());
		Assert.assertEquals(0, tree.getBounds().getY1());
		Assert.assertEquals(8, tree.getBounds().getY2());
		Assert.assertEquals(0, tree.getBounds().getZ1());
		Assert.assertEquals(8, tree.getBounds().getZ2());
		tree.remove(i1);
		Assert.assertEquals(2, tree.getBounds().getX1());
		Assert.assertEquals(8, tree.getBounds().getX2());
		Assert.assertEquals(2, tree.getBounds().getY1());
		Assert.assertEquals(8, tree.getBounds().getY2());
		Assert.assertEquals(2, tree.getBounds().getZ1());
		Assert.assertEquals(8, tree.getBounds().getZ2());
		tree.remove(i2);
		Assert.assertEquals(6, tree.getBounds().getX1());
		Assert.assertEquals(8, tree.getBounds().getX2());
		Assert.assertEquals(6, tree.getBounds().getY1());
		Assert.assertEquals(8, tree.getBounds().getY2());
		Assert.assertEquals(6, tree.getBounds().getZ1());
		Assert.assertEquals(8, tree.getBounds().getZ2());
		tree.remove(i3);
		Assert.assertNull(tree.getBounds());
	}

	@Test
	public void testSearch() {
		Node tree = Node.create();
		tree.insert(i1);
		tree.insert(i2);
		tree.insert(i3);
		LinkedList<Region> results = tree.search(new Bounds(4,4,4,5,5,5));
		Assert.assertEquals(1, results.size());
		Assert.assertTrue(results.contains(i2));
		Assert.assertFalse(results.contains(i3));
		Assert.assertFalse(results.contains(i1));
	}

	@Test
	public void testSearchMore() {
		Region r1 = new Region(new RegionData(11,86,335,34,106,351,
				UUID.randomUUID(), null, UUID.randomUUID(), 1,
				new HashSet<UUID>(), new HashSet<UUID>(), new HashMap<String, String>()));
		Region r2 = new Region(new RegionData(7,70,306,11,73,312,
				UUID.randomUUID(), null, UUID.randomUUID(), 1,
				new HashSet<UUID>(), new HashSet<UUID>(), new HashMap<String, String>()));

		Node tree = Node.create();
		tree.insert(r1);
		tree.insert(r2);
		LinkedList<Region> results = tree.search(new Bounds(6,6,6,7,7,7));
		Assert.assertEquals(0, results.size());
	}
}