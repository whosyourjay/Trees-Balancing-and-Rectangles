import java.util.*;

class RangeLattice {
  public static void main(String[] args) {
    int num = 10;
    int dim = 2;
    int size = 10;

    testGenTup();
    testGenTups();
    List<TuplePair> tups = genTups(num, dim, size);

    testNaiveArea();
    p(naiveArea(tups));

    testTupleMethods();
    testFastArea();
    p(fastArea(tups));
  }

  static List<TuplePair> genTups(int num, int dim, int size) {
    List<TuplePair> tups = new ArrayList<>();
    for (int pos = 0; pos < num; pos++) {
      tups.add(genTup(dim, size));
    }
    return tups;
  }

  /* size must be > 0 */
  static TuplePair genTup(int dim, int size) {
    TuplePair tup = new TuplePair();
    tup.starts = new int[dim];
    tup.ends = new int[dim];
    for (int pos = 0; pos < dim; pos++) {
      int start = (int) (Math.random() * size);
      tup.starts[pos] = start;
      tup.ends[pos] = 1 + start + (int) (Math.random() * (size - start));
    }
    return tup;
  }

  static void testGenTups() {
    for (int num = 0; num < 10; num++) {
      List<TuplePair> tups = genTups(num, 10, 10);
      assert tups.size() == num;
    }
  }

  static void testGenTup() {
    for (int dim = 0; dim < 10; dim++) {
      for (int size = 1; size < 10; size++) {
        TuplePair tup = genTup(dim, size);
        assert tup.starts != null;
        assert tup.ends != null;
        assert tup.starts.length == dim;
        assert tup.ends.length == dim;
        for (int pos = 0; pos < dim; pos++) {
          assert tup.starts[pos] < size;
          assert tup.starts[pos] < tup.ends[pos];
          assert tup.ends[pos] <= size;
        }
      }
    }
  }
  
  static void testNaiveArea() {
    Collection<TuplePair> plus = new HashSet<>();

    TuplePair firstTup = new TuplePair();
    firstTup.starts = new int[]{0, 1};
    firstTup.ends = new int[]{3, 2};
    plus.add(firstTup);

    TuplePair secondTup = new TuplePair();
    secondTup.starts = new int[]{1, 0};
    secondTup.ends = new int[]{2, 3};
    plus.add(secondTup);

    assert naiveArea(plus) == 5;
  }

  /* Only for dim = 2 */
  static int naiveArea(Collection<TuplePair> tups) {
    int sizeX = 0;
    int sizeY = 0;
    for (TuplePair tup : tups) {
      sizeX = Math.max(sizeX, tup.ends[0]);
      sizeY = Math.max(sizeY, tup.ends[1]);
    }

    boolean[][] grid = new boolean[sizeX][sizeY];
    for (boolean[] row : grid) {
      for (int pos = 0; pos < sizeY; pos++) {
        row[pos] = false;
      }
    }
    
    int area = 0;
    for (TuplePair tup : tups) {
      for (int x = tup.starts[0]; x < tup.ends[0]; x++) {
        for (int y = tup.starts[1]; y < tup.ends[1]; y++) {
          if (!grid[x][y]) {
            area++;
            grid[x][y] = true;
          }
        }
      }
    }
    return area;
  }

  static void testTupleMethods() {
    TuplePair firstTup = new TuplePair();
    firstTup.starts = new int[]{0, 1};
    firstTup.ends = new int[]{3, 2};

    TuplePair secondTup = new TuplePair();
    secondTup.starts = new int[]{1, 0};
    secondTup.ends = new int[]{2, 3};

    TuplePair thirdTup = new TuplePair();
    thirdTup.starts = new int[]{1, 0};
    thirdTup.ends = new int[]{2, 3};

    assert !firstTup.equals(secondTup);
    assert secondTup.equals(thirdTup);
  }

  static void testFastArea() {
    List<TuplePair> plus = new ArrayList<>();

    TuplePair firstTup = new TuplePair();
    firstTup.starts = new int[]{0, 1};
    firstTup.ends = new int[]{3, 2};
    plus.add(firstTup);

    TuplePair secondTup = new TuplePair();
    secondTup.starts = new int[]{1, 0};
    secondTup.ends = new int[]{2, 3};
    plus.add(secondTup);

    System.out.println(fastArea(plus));
    assert fastArea(plus) == 5;
  }

  static int fastArea(List<TuplePair> tups) {
    int num = tups.size();
    int dim = tups.get(0).starts.length;

    // Prepare bounds for use in propagation.
    int[] bounds = new int[dim];
    for (TuplePair tup : tups) {
      for (int pos = 0; pos < dim; pos++) {
        bounds[pos] = Math.max(bounds[pos], tup.ends[pos]);
      }
    }

    // Prepare a bounding tuple to get the final area.
    TuplePair boundTup = tups.get(0).copy();
    for (int pos = 0; pos < dim; pos++) {
      boundTup.starts[pos] = 0;
      int upper = 1;
      while (upper < bounds[pos]) {
        upper *= 2;
      }
      boundTup.ends[pos] = upper;
    }
    p("Prepared a bounding tuple " + boundTup.toString());
/*
    // Compress the coordinates by sorting in each dimension. A coordinate can now be just a logn bit number.
    int[][] edges = new int[tups[0].starts.length][];

    for (int i = 0; i < dim; i++) {
      TreeSet<Integer> coords = new TreeSet<>();
      for (int j = 0; j < num; j++) {
        coords.add(tups[j].starts[i]);
        coords.add(tups[j].ends[i]);
      }
      ArrayList<Integer> coordList = new ArrayList<Integer>(coords);
      edges[i] = new int[coords.size()];
      for (int pos = 0; pos < edges[i].length; pos++) {
        edges[i][pos] = coordList.get(i);
      }
    }
*/
    HashMap<TuplePair, Integer> covered = new DefaultHashMap<>(0);
    for (TuplePair tup : splitTups(tups, new HashSet<>())) {
      covered.put(tup, tup.volume()); // Cover this whole tup

      //p("Starting to propagate " + tup.toString());
      propagate(tup, covered, new HashSet<>(), bounds);
    }
    p("Calculations done!");
    //p(covered.toString());
    return covered.get(boundTup); // The whole space
  }

  static Set<TuplePair> splitTups(List<TuplePair> trueTups, HashSet<TuplePair> splitTups) {
    List<TuplePair> tups = new ArrayList<>(trueTups);
    while (!tups.isEmpty()) {
      TuplePair tup = tups.get(0);
      boolean primitive = true;

      for (int splitDim = 0; splitDim < tup.starts.length; splitDim++) {
        if (!tup.primitiveIn(splitDim)) {
          primitive = false;
          tups.add(tup.leftSplit(splitDim));
          tups.add(tup.rightSplit(splitDim));
          break;
        }
      }
      if (primitive) {
        splitTups.add(tup);
      }
      tups.remove(0);
    }
    p("Splitting complete!");
    return splitTups;
  }

  /**
   * Propagate a node's cover to all ancestors. 
   * Perhaps a log factor could be saved with an iterative approach.
   * @param visited A memo table so we can stop if already visited.
   */
  static void propagate(TuplePair tup, HashMap<TuplePair, Integer> covered, HashSet<TuplePair> visited, int[] bounds) {
    //p("Augmenting " + tup.toString());
    // I think it can be proved by induction that visiting some path from new node to ancestor, in increasing order of size, guarantees we get the right area.
    if (visited.contains(tup)) {
      return;
    }
    visited.add(tup);

    for (int splitDim = 0; splitDim < tup.starts.length; splitDim++) {
      if (tup.starts[splitDim] == 0 && tup.ends[splitDim] >= bounds[splitDim]) {
        continue;
      }
      TuplePair leftSib = tup.leftSib(splitDim);
      TuplePair rightSib = tup.rightSib(splitDim);
      TuplePair parent = merge(leftSib, rightSib);

      int newCover = covered.get(parent);
      newCover = Math.max(newCover, covered.get(leftSib) + covered.get(rightSib));
      covered.put(parent, newCover);

      propagate(parent, covered, visited, bounds);
    }
  }

  static TuplePair merge(TuplePair left, TuplePair right) {
    TuplePair parent = new TuplePair();
    parent.starts = left.starts;
    parent.ends = right.ends;
    return parent;
  }

  static void p(int i) {
    System.out.println(i);
  }

  static void p(String s) {
    System.out.println(s);
  }

  static void wait(int time) {
    try {
      Thread.sleep(time);
    } catch(InterruptedException ex) {
      Thread.currentThread().interrupt();
    }
  }
}

class DefaultHashMap<K,V> extends HashMap<K,V> {
  protected V defaultValue;
  public DefaultHashMap(V defaultValue) {
      this.defaultValue = defaultValue;
  }

  @Override
  public V get(Object k) {
      return containsKey(k) ? super.get(k) : defaultValue;
  }

  @Override
  public String toString() {
    String self = "";
    for (K key : keySet()) {
      self = self + key.toString() + " " + get(key) + "\n";
    }
    return self;
  }
}

class TuplePair {
  int[] starts;
  int[] ends;
  
  @Override
  public int hashCode() {
    int hash = 1;
    for (int pos = 0; pos < starts.length; pos++) {
      hash = 31*hash + starts[pos];
      hash = 31*hash + ends[pos];
    }
    return hash;
  }

  @Override
  public boolean equals(Object otherObj) {
    if (!(otherObj instanceof TuplePair)) {
      return false;
    }
    TuplePair other = (TuplePair) otherObj;
    if (starts.length != other.starts.length) {
      return false;
    }
    for (int pos = 0; pos < starts.length; pos++) {
      if (starts[pos] != other.starts[pos]
          || ends[pos] != other.ends[pos]) {
        return false;
      }
    }
    return true;
  }

  int volume() {
    int volume = 1;
    for (int pos = 0; pos < starts.length; pos++) {
      volume *= ends[pos] - starts[pos];
    }
    return volume;
  }

  // All these methods will have several superfluous log factors for now.
  // Even calling these methods iteratively like this, rather than having them generate all tuples in one pass, gives a log factor or 2.
  TuplePair leftSib(int splitDim) {
    int fact = commonTwos(splitDim);
    int left = starts[splitDim];

    if ((left / fact) % 2 == 0) {
      return this;                      // [ this | ]
    }
    TuplePair sib = copy();
    sib.starts[splitDim] = left - fact;
    sib.ends[splitDim] = left;
    return sib;                      // [ sib | this ]
  }

  int commonTwos(int splitDim) {
    int left = starts[splitDim];
    int right = ends[splitDim];
    int fact = 1;
    while (left % 2 != 1 && right % 2 != 1) {
      fact *= 2;
      left /= 2;
      right /= 2;
    }
    return fact;
  }

  TuplePair copy() {
    TuplePair sib = new TuplePair();
    int dim = starts.length;
    sib.starts = new int[dim];
    sib.ends = new int[dim];
    System.arraycopy(starts, 0, sib.starts, 0, dim);
    System.arraycopy(ends, 0, sib.ends, 0, dim);
    return sib;
  }

  TuplePair rightSib(int splitDim) {
    int fact = commonTwos(splitDim);
    int right = ends[splitDim];

    if ((right / fact) % 2 == 0) {
      return this;                      // [ | this ]
    }
    TuplePair sib = copy();
    sib.starts[splitDim] = right;
    sib.ends[splitDim] = right + fact;
    return sib;                      // [ this | sib ]
  }

  boolean primitiveIn(int splitDim) {
    return splitCoord(splitDim) == starts[splitDim];
  }

  /* Arbitrarily split out the lowest power of two at one of the two sides. */
  TuplePair leftSplit(int splitDim) {
    int splitCoord = splitCoord(splitDim);
    TuplePair split = copy();
    split.ends[splitDim] = splitCoord;
    return split;
  }

  int splitCoord(int splitDim) {
    int fact = commonTwos(splitDim);
    int start = starts[splitDim];
    int end = ends[splitDim];

    if ((end / fact) % 2 == 1) {
      if (start < end - fact) {
        return end - fact;
      }
    }
    if ((start / fact) % 2 == 1) {
      if (start + fact < end) {
        return start + fact;
      }
    }
    return start; // No protocol for the case when a split is impossible.
  }

  TuplePair rightSplit(int splitDim) {
    int splitCoord = splitCoord(splitDim);
    TuplePair split = copy();
    split.starts[splitDim] = splitCoord;
    return split;
  }
  
  @Override
  public String toString() {
    return Arrays.toString(starts) + " " + Arrays.toString(ends);
  }
}
