public class Node<E extends Comparable<E>> {
  int depth = 0;
  E data = null;
  Node<E> left = null;
  Node<E> right = null;

  public static void main(String[] a) {
    Node<Integer> tree = new Node<>();
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
    return data == null
      ? "."
      : depth == 1
      ? data.toString()
      : "(" + left.toString() + " " + data.toString() + " " + right.toString() + ")";
  }

  void insert(E toInsert) {
    if (data == null) {
      data = toInsert;
      depth = 1;
      left = new Node();
      right = new Node();
      return;
    }
    (toInsert.compareTo(data) <= 0 ? left : right).insert(toInsert);
    balance();
  }

  void balance() {
    if (left.depth > right.depth + 1) {
      Node a = left.left;
      Node b = left.right;

      if (a.depth < b.depth) {
        left.rotateLeft();
      }
      rotateRight();
    } else if (left.depth + 1 < right.depth) {
      Node c = right.left;
      Node d = right.right;

      if (c.depth > d.depth) {
        right.rotateRight();
      }
      rotateLeft();
    }
    left.updateDepth();
    right.updateDepth();
    updateDepth();
  }
  
  void rotateRight() {
    swap(left);

    Node<E> a = left.left;
    Node<E> b = left.right;
    Node<E> c = right;

    right = left;
    left = a;
    right.left = b;
    right.right = c;
  }

  void rotateLeft() {
    swap(right);

    Node<E> a = left;
    Node<E> b = right.left;
    Node<E> c = right.right;

    left = right;
    left.left = a;
    left.right = b;
    right = c;
  }

  void swap(Node<E> other) {
    E theirData = other.data;
    other.data = data;
    data = theirData;
  }

  void updateDepth() {
    depth = (data == null)
      ? 0
      : Math.max(left.depth, right.depth) + 1;
  }

  boolean remove(E toRemove) {
    if (data == null) {
      return false;
    } else if (toRemove.compareTo(data) == 0) {
      // Arbitrarily replace with node to the right
      data = right.getLeftmost();

      // If nothing's to the right use what's to the left.
      if (data == null) {
        data = left.data;
        depth = left.depth;
        right = left.right;
        left = left.left;
        return true;
      }
      // Otherwise remove what was used on the right.
      right.remove(data);
      balance();
      return true;
    }
    boolean found = (toRemove.compareTo(data) < 0 ? left : right).remove(toRemove);
    balance();
    return found;
  }

  E getLeftmost() {
    return data == null
      ? null
      : left.data == null
      ? data
      : left.getLeftmost();
  }

  boolean query(E toFind) {
    return data == null
      ? false
      : toFind.compareTo(data) == 0
      ? true
      : (toFind.compareTo(data) < 0 ? left : right).query(toFind);
  }
}
