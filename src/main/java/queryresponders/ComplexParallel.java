package queryresponders;

import cse332.exceptions.NotYetImplementedException;
import cse332.interfaces.QueryResponder;
import cse332.types.CensusGroup;
import cse332.types.CornerFindingResult;
import cse332.types.MapCorners;
import paralleltasks.CornerFindingTask;
import paralleltasks.PopulateGridTask;

import java.util.concurrent.ForkJoinPool;

public class ComplexParallel extends QueryResponder {
    private static final ForkJoinPool POOL = new ForkJoinPool();
    private final int[][] grid;
    private final int numRows;
    private final int numCols;

    public ComplexParallel(CensusGroup[] censusData, int numColumns, int numRows) {
        CornerFindingResult cornerFindingResult = POOL.invoke(new CornerFindingTask(censusData, 0, censusData.length));
        MapCorners mapOfCorners = cornerFindingResult.getMapCorners();
        totalPopulation = cornerFindingResult.getTotalPopulation();
        this.numCols = numColumns;
        this.numRows = numRows;
        double rowDistance = (mapOfCorners.north - mapOfCorners.south) / numRows;
        double columnDistance = (mapOfCorners.east - mapOfCorners.west) / numColumns;

        grid = new int[numRows + 1][numColumns + 1];

        int[][] population = POOL.invoke(new PopulateGridTask(censusData, 0, censusData.length,
                numRows, numColumns,
                mapOfCorners, columnDistance, rowDistance));
        for (int i = 1; i < numRows + 1; i++) {
            for (int j = 1; j < numColumns + 1; j++) {
                grid[i][j] = population[i][j] + grid[i - 1][j] + grid[i][j - 1] - grid[i - 1][j - 1];
            }
        }
    }

    @Override
    public int getPopulation(int west, int south, int east, int north) {
        if (east < 1 || east > numCols || west < 1 || west > numCols
                || north < 1 || north > numRows || south < 1 || south > numRows) {
            throw new IllegalArgumentException();
        }
        return grid[north][east] - grid[south - 1][east]
                - grid[north][west - 1] + grid[south - 1][west - 1];
    }
}
