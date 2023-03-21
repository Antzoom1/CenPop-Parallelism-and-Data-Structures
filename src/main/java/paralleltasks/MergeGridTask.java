package paralleltasks;

import cse332.exceptions.NotYetImplementedException;

import java.util.concurrent.RecursiveAction;

/*
   1) This class is used by PopulateGridTask to merge two grids in parallel
   2) SEQUENTIAL_CUTOFF refers to the maximum number of grid cells that should be processed by a single parallel task
 */

public class MergeGridTask extends RecursiveAction {
    final static int SEQUENTIAL_CUTOFF = 10;
    int[][] left, right;
    int rowLo, rowHi, colLo, colHi;

    public MergeGridTask(int[][] left, int[][] right, int rowLo, int rowHi, int colLo, int colHi) {
        this.left = left;
        this.right = right;
        this.rowLo = rowLo;
        this.rowHi = rowHi;
        this.colLo = colLo;
        this.colHi = colHi;    }

    @Override
    protected void compute() {

        if ((rowHi - rowLo) * (colHi - colLo) <= SEQUENTIAL_CUTOFF) {
            sequentialMergeGrid(left, right, rowLo, rowHi, colLo, colHi); //fill in parameters
        } else {
            int rowMiddle = rowLo + (rowHi - rowLo) / 2;
            int colMiddle = colLo + (colHi - colLo) / 2;
            MergeGridTask task1 = new MergeGridTask(left, right, rowMiddle, rowHi, colLo, colMiddle);
            MergeGridTask task2 = new MergeGridTask(left, right, rowLo, rowMiddle, colLo, colMiddle);
            MergeGridTask task3 = new MergeGridTask(left, right, rowLo, rowMiddle, colMiddle, colHi);
            MergeGridTask task4 = new MergeGridTask(left, right, rowMiddle, rowHi, colMiddle, colHi);
            task1.fork();
            task2.fork();
            task3.fork();
            task4.compute();
            task1.join();
            task2.join();
            task3.join();
        }    }

    private void sequentialMergeGrid(int[][] left, int[][] right, int rowLo, int rowHi, int colLo, int colHi) {
        for (int i = rowLo; i < rowHi; i++) {
            for (int j = colLo; j < colHi; j++) {
                left[i][j] += right[i][j];
            }
        }
    }
}
