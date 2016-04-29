public class RectTest {
  static class Rect {
    int left;
    int right;
    int top;
    int bottom;

    public String toString() {
      return "[" + left + " " + right + " " + top + " " + bottom + "]";
    }
  }

  public static void main(String[] a) {
    Rect[] rects = genRects(5, 5);
    p(rects);
    p(naiveShadow(rects, 5) + "\n");
  }

  static <E> void p(E[] array) {
    p("(");
    for (E elem : array) {
      p(elem.toString());
      p(" ");
    }
    p(")");
    p("\n");
  }
   
  static void p(String s) {
    System.out.print(s);
  }

  static Rect[] genRects(int num, int scale) {
    Rect[] rects = new Rect[num];
    for (int i = 0; i < num; i++) {
      rects[i] = new Rect();
      rects[i].left = (int) (Math.random() * scale);
      rects[i].right = ((int) (Math.random() * (scale - rects[i].left - 1))) + rects[i].left + 1;

      rects[i].top = (int) (Math.random() * scale);
      rects[i].bottom = ((int) (Math.random() * (scale - rects[i].top - 1))) + rects[i].top + 1;
    }
    return rects;
  }

  static int naiveShadow(Rect[] rects, int scale) {
    boolean[][] grid = new boolean[scale][scale];
    for (int i = 0; i < scale; i++) {
      for (int j = 0; j < scale; j++) {
        grid[i][j] = false;
      }
    }
    for (Rect thisRect : rects) {
      for (int i = thisRect.left; i < thisRect.right; i++) {
        for (int j = thisRect.top; j < thisRect.bottom; j++) {
          grid[i][j] = true;
        }
      }
    }
    int total = 0;
    for (boolean[] row : grid) {
      for (boolean val : row) {
        total += val ? 1 : 0;
      }
    }
    return total;
  }
}
