package cc.chenchi.android.tool.string;

/**
 * Created by chenchi on 2017/3/9.
 */
public class CharSquare {
    //matrix(10, 9);

    public static void getNextLB(int w, int h, int[][] matrix, int x, int y, int start) {
        System.out.println("LB:\t" + w + ", " + h + ", " + x + ", " + y + ", " + start);
        if (w == 0 || h == 0) return;
        for (int i = 0; i < w; ++i) {
            matrix[x - i][y] = start % 36;
            start++;
        }
        x = x - w + 1;
        for (int i = 1; i < h; ++i) {
            matrix[x][y - i] = start % 36;
            start++;
        }
        y = y - h + 1;
        getNextTR(w - 1, h - 1, matrix, x + 1, y, start);
    }

    public static void getNextTR(int w, int h, int[][] matrix, int x, int y, int start) {
        System.out.println("TR:\t" + w + ", " + h + ", " + x + ", " + y + ", " + start);
        if (w == 0 || h == 0) return;

        for (int i = 0; i < w; ++i) {
            matrix[x + i][y] = start % 36;
            start++;
        }
        for (int i = 1; i < h; ++i) {
            matrix[x + w - 1][y + i] = start % 36;
            start++;
        }
        getNextLB(w - 1, h - 1, matrix, x + w - 1 - 1, y + h - 1, start);
    }

    public static void matrix(int w, int h) {
        int[][] matrix = new int[w][h];
        for (int j = 0; j < h; ++j) {
            for (int i = 0; i < w; ++i) {
                matrix[i][j] = -1;
            }
        }
        getNextTR(w, h, matrix, 0, 0, 0);
        printMatrix(matrix, w, h);
    }

    private static void printMatrix(int[][] matrix, int w, int h){
        String alp = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        for (int i = 0; i < h; ++i) {
            for (int j = 0; j < w; ++j) {
                int data = matrix[j][i];
                if (data < 10) {
                    System.out.print(matrix[j][i]);
                }else{
                    String dataStr = alp.substring(data - 10, data - 9);
                    System.out.print(dataStr);
                }
                System.out.print("\t");
            }
            System.out.println();
        }
    }

}
