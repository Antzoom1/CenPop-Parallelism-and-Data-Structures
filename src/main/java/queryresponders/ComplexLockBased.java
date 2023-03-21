package queryresponders;

import cse332.exceptions.NotYetImplementedException;
import cse332.interfaces.QueryResponder;
import cse332.types.CensusGroup;
import cse332.types.CornerFindingResult;
import cse332.types.MapCorners;
import paralleltasks.CornerFindingTask;
import paralleltasks.PopulateLockedGridTask;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ComplexLockBased extends QueryResponder {
    private static final ForkJoinPool POOL = new ForkJoinPool(); // only to invoke CornerFindingTask
    public int NUM_THREADS = 4;
    private MapCorners mapOfCorners;
    private int[][] population;
    private int numColumns;
    private int numRows;

    public ComplexLockBased(CensusGroup[] censusData, int numColumns, int numRows) {
        this.numColumns = numColumns;
        this.numRows = numRows;
        if (censusData.length > 0) {
            CornerFindingResult result = POOL.invoke(new CornerFindingTask(censusData, 0, censusData.length));
            PopulateLockedGridTask[] thread = new PopulateLockedGridTask[NUM_THREADS];
            this.totalPopulation = result.getTotalPopulation();
            this.mapOfCorners = result.getMapCorners();
            double rowDistance = (mapOfCorners.north - mapOfCorners.south) / numRows;
            double columnDistance = (mapOfCorners.east - mapOfCorners.west) / numColumns;
            Lock[][] lock = new ReentrantLock[numRows + 1][numColumns + 1];
            for (int i = 0; i < numRows + 1; i++) {
                for (int j = 0; j < numColumns + 1; j++) {
                    lock[i][j] = new ReentrantLock();
                }
            }
            int[] censusFront = new int[NUM_THREADS];
            int[] censusBack = new int[NUM_THREADS];
            this.population = new int[numRows + 1][numColumns + 1];

            for (int i = 0; i < NUM_THREADS - 1; i++) {
                censusFront[i] = (censusData.length / NUM_THREADS) * i;
                censusBack[i] = (censusData.length / NUM_THREADS) * (i + 1);
                thread[i] = new PopulateLockedGridTask(censusData, censusFront[i], censusBack[i],
                        numRows, numColumns, mapOfCorners, columnDistance, rowDistance, population, lock);
                thread[i].start();
            }

            thread[NUM_THREADS - 1] = new PopulateLockedGridTask(censusData, (censusData.length / NUM_THREADS)
                    * (NUM_THREADS - 1),
                    censusData.length, numRows, numColumns,
                    mapOfCorners, columnDistance, rowDistance, population, lock);
            thread[NUM_THREADS - 1].run();
            try {
                for (int i = 0; i < NUM_THREADS - 1; i++) {
                    thread[i].join();
                }
            } catch (Exception e) {
                System.out.println(e.getStackTrace());
            }
            for (int i = 1; i < population.length; i++) {
                for (int j = 1; j < population[i].length; j++) {
                    population[i][j] = population[i][j] + population[i][j - 1] + population[i - 1][j]
                            - population[i - 1][j - 1];
                }
            }
        }
    }

    @Override
    public int getPopulation(int west, int south, int east, int north) {

        if ((west < 1 || west > numColumns) || (south < 1 || south > numRows)
                || (east < west || east > numColumns) || (north < south || north > numRows)) {
            throw new IllegalArgumentException();
        }
        return population[north][east] - population[south - 1][east] - population[north][west - 1] +
                population[south - 1][west - 1];
    }
}
