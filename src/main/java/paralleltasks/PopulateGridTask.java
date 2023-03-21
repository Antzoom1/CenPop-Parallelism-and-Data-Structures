package paralleltasks;

import cse332.exceptions.NotYetImplementedException;
import cse332.types.CensusGroup;
import cse332.types.MapCorners;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
/*
   1) This class is used in version 4 to create the initial grid holding the total population for each grid cell
   2) SEQUENTIAL_CUTOFF refers to the maximum number of census groups that should be processed by a single parallel task
   3) Note that merging the grids from the left and right subtasks should NOT be done in this class.
      You will need to implement the merging in parallel using a separate parallel class (MergeGridTask.java)
 */

public class PopulateGridTask extends RecursiveTask<int[][]> {
    final static int SEQUENTIAL_CUTOFF = 10000;
    private static final ForkJoinPool POOL = new ForkJoinPool();
    CensusGroup[] censusGroups;
    int lo, hi, numRows, numColumns;
    MapCorners corners;
    double cellWidth, cellHeight;

    public PopulateGridTask(CensusGroup[] censusGroups, int lo, int hi, int numRows, int numColumns, MapCorners corners, double cellWidth, double cellHeight) {
        this.censusGroups = censusGroups;
        this.lo = lo;
        this.hi = hi;
        this.numRows = numRows;
        this.numColumns = numColumns;
        this.corners = corners;
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;    }

    @Override
    protected int[][] compute() {
        if (hi - lo <= SEQUENTIAL_CUTOFF) {
            return sequentialPopulateGrid(censusGroups, lo, hi, numRows, numColumns, corners, cellWidth, cellHeight);
        }
        int middle = lo + (hi - lo) / 2;
        PopulateGridTask left = new PopulateGridTask(censusGroups, lo, middle, numRows, numColumns,
                corners, cellWidth, cellHeight);
        PopulateGridTask right = new PopulateGridTask(censusGroups, middle, hi, numRows, numColumns,
                corners, cellWidth, cellHeight);
        right.fork();
        int[][] rightAnswer = right.join();
        int[][] leftAnswer = left.compute();

        POOL.invoke(new MergeGridTask(leftAnswer, rightAnswer, 0, numRows + 1, 0, numColumns + 1));
        return leftAnswer;
    }

    private int[][] sequentialPopulateGrid(CensusGroup[] censusGroups, int lo, int hi, int numRows, int numColumns,
                                           MapCorners corners, double cellWidth, double cellHeight) {
        int[][] population = new int[numRows + 1][numColumns + 1];
        for (int i = lo; i < hi; i++) {
            double latitude = censusGroups[i].latitude;
            double longitude = censusGroups[i].longitude;
            if (latitude < corners.north && longitude < corners.east) {
                int rowIndex = (int) ((latitude - corners.south) / cellHeight) + 1;
                int columnIndex = (int) ((longitude - corners.west) / cellWidth) + 1;
                population[rowIndex][columnIndex] += censusGroups[i].population;
            } else if (latitude == corners.north && longitude < corners.east) {
                int rowIndex = population.length - 1;
                int columnIndex = (int) ((longitude - corners.west) / cellWidth) + 1;
                population[rowIndex][columnIndex] += censusGroups[i].population;
            } else if (latitude == corners.north && longitude == corners.east) {
                population[population.length - 1][population[0].length - 1] += censusGroups[i].population;
            } else if (latitude < corners.north && longitude == corners.east) {
                int rowIndex = (int) ((latitude - corners.south) / cellHeight) + 1;
                int columnIndex = population[0].length - 1;
                population[rowIndex][columnIndex] += censusGroups[i].population;
            }
        }
        return population;
    }
}

