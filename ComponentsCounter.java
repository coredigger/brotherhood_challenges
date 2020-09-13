import java.io.File;
import java.util.Scanner;

public class ComponentsCounter {

    private static boolean[][] states;
    private static int N;
    private static int K;

    private static int[] groupIds;
    private static int[] groupSize;
    private static int groupsCount = 0;

    public static void main(String[] args) throws Exception {
        String fileName = args[0];
        init(fileName);

        for (int row = 0; row < N; row++) {
            for (int col = 0; col < K; col++) {
                if (isOpen(row, col)) {
                    union(row, col, row - 1, col);
                    union(row, col, row, col - 1);
                }
            }
        }

        System.out.println("Groups count: " + groupsCount);
    }

    private static void init(String fileName) throws Exception {
        // Matrix and dimensions, N rows, K cols
        states = readMatrix(fileName);
        N = states.length;
        K = states[0].length;

        groupIds = new int[N * K];
        groupSize = new int[N * K];

        for (int i = 0; i < N * K; ++i) {
            groupIds[i] = i;
            groupSize[i] = 1;

            if (isOpen(i)) {
                groupsCount++;
            }
        }
    }

    private static void union(int sourceRow, int sourceCell, int targetRow, int targetCol) {
        if (targetCol < 0 || targetRow < 0 || targetRow >= N || targetCol >= K) {
            return;
        }

        if (isOpen(targetRow, targetCol)) {
            int sourceCellId = cellId(sourceRow, sourceCell);
            int targetCellId = cellId(targetRow, targetCol);

            int sourceRoot = findRootGroupForCell(sourceCellId);
            int targetRoot = findRootGroupForCell(targetCellId);

            if (sourceRoot == targetRoot) {
                return;
            }

            union(sourceCellId, targetCellId);
        }
    }

    public static void union(int cellId1, int cellId2) {
        int group1 = findRootGroupForCell(cellId1);
        int group2 = findRootGroupForCell(cellId2);

        if (group1 == group2) return;

        int groupSize1 = groupSize[group1];
        int groupSize2 = groupSize[group2];

        if (groupSize1 < groupSize2) {
            groupIds[group1] = group2;
            groupSize[group2] += groupSize[group1];
        } else {
            groupIds[group2] = group1;
            groupSize[group1] += groupSize[group2];
        }

        groupsCount--;
    }

    public static int findRootGroupForCell(int cellId) {
        while (cellId != groupIds[cellId]) {
            groupIds[cellId] = groupIds[ComponentsCounter.groupIds[cellId]];
            cellId = groupIds[cellId];
        }
        return cellId;
    }

    public static boolean isOpen(int row, int col) {
        return states[row][col];
    }

    public static boolean isOpen(int cellId) {
        int row = cellId / K;
        int col = cellId % K;
        return states[row][col];
    }

    private static int cellId(int row, int col) {
        return row * K + col;
    }

    public static boolean[][] readMatrix(String file) throws Exception {
        int maxLineLength = 0;
        int linesCount = 0;

        try (Scanner scanner = new Scanner(new File(file))) {
            while (scanner.hasNextLine()) {
                maxLineLength = Math.max(maxLineLength, scanner.nextLine().length());
                linesCount++;
            }
        }

        boolean[][] matrix = new boolean[linesCount][maxLineLength];

        try (Scanner scanner = new Scanner(new File(file))) {
            for (int rowId = 0; scanner.hasNextLine(); rowId++) {
                String line = scanner.nextLine();

                for (int colId = 0; colId < line.length(); colId++) {
                    boolean isMark = '.' != line.charAt(colId);
                    matrix[rowId][colId] = isMark;
                }
            }
        }

        return matrix;
    }

}
