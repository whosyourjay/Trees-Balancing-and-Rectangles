public class BloatNode<E extends Comparable<E>> {
  int depth = 0;
  int count = 0;
  E data;
  BloatNode<E> left;
  BloatNode<E> right;

  public static void main(String[] a) {
    BloatNode<Integer> tree = new BloatNode<>();
    for (int i = 0; i < 20; i++) {
      System.out.println(tree);
      tree.insert(i);
    }
    for (int i = 0; i < 10; i++) {
      System.out.println(tree);
      tree.remove(2*i);
    }
    for (int i = 0; i < 20; i++) {
      System.out.println(tree.query(i));
    }
    System.out.println(tree);
  }

  public String toString() {
    return depth == 0
      ? "."
      : depth == 1
      ? data.toString()
      : "(" + left.toString() + " " + data.toString() + " " + right.toString() + ")";
  }

  void insert(E toInsert) {
    if (depth == 0) {
      data = toInsert;
      depth = 1;
      count = 1;
      left = new BloatNode();
      right = new BloatNode();
    } else if (toInsert.compareTo(data) == 0) {
      count += 1;
    } else {
      (toInsert.compareTo(data) < 0 ? left : right).insert(toInsert);
      balance();
    }
  }

  void balance() {
    if (left.depth > right.depth + 1) {
      if (left.left.depth < left.right.depth) {
        left.rotateLeft();
      }
      rotateRight();
    } else if (left.depth + 1 < right.depth) {
      if (right.left.depth > right.right.depth) {
        right.rotateRight();
      }
      rotateLeft();
    }
    updateDepth();
  }
  
  void rotateRight() {
    BloatNode<E> a = left.left;
    BloatNode<E> b = left.right;
    BloatNode<E> c = right;

    swap(left);
    right = left;

    left = a;
    right.left = b;
    right.right = c;
    right.updateDepth();
  }

  void rotateLeft() {
    BloatNode<E> a = left;
    BloatNode<E> b = right.left;
    BloatNode<E> c = right.right;

    swap(right);
    left = right;

    left.left = a;
    left.right = b;
    right = c;
    left.updateDepth();
  }

  void swap(BloatNode<E> other) {
    E otherData = other.data;
    other.data = data;
    data = otherData;

    int otherCount = other.count;
    other.count = count;
    count = otherCount;
  }

  void updateDepth() {
    depth = 1 + Math.max(left.depth, right.depth);
  }

  void remove(E toRemove) {
    if (stopSearch(toRemove)) {
      count = Math.max(0, count - 1);
    } else {
      (toRemove.compareTo(data) < 0 ? left : right).remove(toRemove);
    }
  }

  boolean stopSearch(E toFind) {
    return depth == 0 || toFind.compareTo(data) == 0;
  }

  int query(E toFind) {
    return (stopSearch(toFind))
      ? count
      : (toFind.compareTo(data) < 0 ? left : right).query(toFind);
  }
}
