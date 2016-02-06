public class RangeNode {
  boolean root;
  int leftEnd = Integer.MIN_VALUE;
  int rightEnd = Integer.MAX_VALUE;
  int coveredBelow = 0;
  int extraCover = 0;
  int depth = 1;
  RangeNode left = null; // Both or none are null;
  RangeNode right = null;

  public static void main(String[] a) {
    RangeNode root = new RangeNode();
    p("Inserting");
    for (int i = 0; i < 20; i++) {
      root.insert(i, i+1);
      p(root.coveredBelow);
    }
    p("Deleting");
    for (int i = 0; i < 10; i++) {
      root.remove(2*i, 2*i + 1);
      p(root.coveredBelow);
    }
    p(root);
    p("Querying");
    for (int i = 0; i < 20; i++) {
      p(root.covers(i));
    }
  }

  static void p(String s) {
    System.out.println(s);
  }

  static void p(int s) {
    System.out.println(s);
  }

  static void p(RangeNode n) {
    p(n.toString(0));
  }

  public String dataString(int indent) {
    return new String(new char[2*indent]).replace("\0", " ")
      + "[(" + leftEnd + "," + rightEnd + ") "
      + coveredBelow + " " + extraCover + " " + depth + "]";
  }

  public String toString(int indent) {
    return depth == 1
      ? dataString(indent)
      : left.toString(indent + 1) + "\n" + dataString(indent) + "\n" + right.toString(indent + 1);
  } 

  void insert(int leftInsert, int rightInsert) {
    // Interval missed this subtree.
    if (rightInsert <= leftEnd || rightEnd <= leftInsert) {

    // Interval covers this subtree.
    } else if (leftInsert <= leftEnd && rightEnd <= rightInsert) {
      extraCover += 1;

    // Subdivision required.
    } else if (depth == 1) {
      split(leftInsert, rightInsert);

    // Look deeper for subdivision.
    } else {
      left.insert(leftInsert, rightInsert);
      right.insert(leftInsert, rightInsert);
      update();
      balance();
    }
  }

  // Might have to rework balance code since split can add 2 depth.
  void split(int leftInsert, int rightInsert) {
    // Arbitrarily split by leftInsert first
    if (leftEnd < leftInsert) {
      splitOne(leftInsert);
      // Ideally, balance on the path up to root here.
      right.insert(leftInsert, rightInsert); // Handle possible second split.
    } else {
      splitOne(rightInsert);
      left.insert(leftInsert, rightInsert);
    }
    update();
  }

  void splitOne(int middle) {
    left = new RangeNode();
    left.leftEnd = leftEnd;
    left.rightEnd = middle;

    right = new RangeNode();
    right.leftEnd = middle;
    right.rightEnd = rightEnd;

    depth = 2;
  }

  void update() {
    int mergeNumber = Math.min(left.extraCover, right.extraCover);
    left.extraCover -= mergeNumber;
    right.extraCover -= mergeNumber;
    extraCover += mergeNumber;

    depth = 1 + Math.max(left.depth, right.depth);

    coveredBelow = left.covered() + right.covered();
  }

  int covered() {
    return extraCover > 0 ? rightEnd - leftEnd : coveredBelow;
  }

  void balance() {
    if (left.depth > right.depth + 1) {
      RangeNode a = left.left;
      RangeNode b = left.right;

      if (a.depth < b.depth) {
        left.rotateLeft();
      }
      rotateRight();
    } else if (left.depth + 1 < right.depth) {
      RangeNode c = right.left;
      RangeNode d = right.right;

      if (c.depth > d.depth) {
        right.rotateRight();
      }
      rotateLeft();
    }
  }
  
  void rotateRight() {
    dump();
    left.dump();

    RangeNode a = left.left;
    RangeNode b = left.right;
    RangeNode c = right;

    right = left;
    left = a;
    right.left = b;
    right.right = c;

    right.build();
    build();
  }

  void dump() {
    left.extraCover += extraCover;
    right.extraCover += extraCover;
    extraCover = 0;
  }

  void build() {
    leftEnd = left.leftEnd;
    rightEnd = right.rightEnd;
    update();
  }

  void rotateLeft() {
    dump();
    right.dump();

    RangeNode a = left;
    RangeNode b = right.left;
    RangeNode c = right.right;

    left = right;
    left.left = a;
    left.right = b;
    right = c;

    left.build();
    build();
  }

  void remove(int leftRemove, int rightRemove) {
    // Interval missed this subtree.
    if (rightRemove <= leftEnd || rightEnd <= leftRemove) {

    // Interval covers this subtree.
    } else if (leftRemove <= leftEnd && rightEnd <= rightRemove) {
      extraCover -= 1;

    // Subdivision required.
    } else if (depth == 1) {
      throw new Error();

    // Look deeper for subdivision.
    } else {
      dump(); // In case we need to split a cover
      left.remove(leftRemove, rightRemove);
      right.remove(leftRemove, rightRemove);
      update(); // Fix covers.
      balance();
    }
  }

  int covers(int spot) {
    if (spot < leftEnd || rightEnd <= spot) {
      return 0;
    } else if (depth == 1) {
      return extraCover;
    }
    return extraCover + left.covers(spot) + right.covers(spot);
  }
}
