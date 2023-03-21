package paralleltasks;

import cse332.exceptions.NotYetImplementedException;
import cse332.types.CensusGroup;
import cse332.types.MapCorners;

import java.util.concurrent.locks.Lock;

/*
   1) This class is used in version 5 to create the initial grid holding the total population for each grid cell
        - You should not be using the ForkJoin framework but instead should make use of threads and locks
        - Note: the resulting grid after all threads have finished running should be the same as the final grid from
          PopulateGridTask.java
 */

public class PopulateLockedGridTask extends Thread {
    CensusGroup[] censusGroups;
    int lo, hi, numRows, numColumns;
    MapCorners corners;
    double cellWidth, cellHeight;
    int[][] populationGrid;
    Lock[][] lockGrid;


    public PopulateLockedGridTask(CensusGroup[] censusGroups, int lo, int hi, int numRows, int numColumns, MapCorners corners,
                                  double cellWidth, double cellHeight, int[][] popGrid, Lock[][] lockGrid) {
        this.censusGroups = censusGroups;
        this.lo = lo;
        this.hi = hi;
        this.numRows = numRows;
        this.numColumns = numColumns;
        this.corners = corners;
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
        this.populationGrid = popGrid;
        this.lockGrid = lockGrid;    }

    @Override
    public void run() {
        for (int i = lo; i < hi; i++) {
            if (censusGroups[i] != null) {
                double latitude = censusGroups[i].latitude;
                double longitude = censusGroups[i].longitude;
                if (longitude < corners.east && latitude < corners.north) {
                    int rowIndex = (int) ((latitude - corners.south) / cellHeight) + 1;
                    int columnIndex = (int) ((longitude - corners.west) / cellWidth) + 1;
                    lockGrid[rowIndex][columnIndex].lock();
                    populationGrid[rowIndex][columnIndex] += censusGroups[i].population;
                    lockGrid[rowIndex][columnIndex].unlock();
                } else if (longitude == corners.east && latitude == corners.north) {
                    lockGrid[populationGrid.length - 1][populationGrid[0].length - 1].lock();
                    populationGrid[populationGrid.length - 1][populationGrid[0].length - 1] += censusGroups[i].population;
                    lockGrid[populationGrid.length - 1][populationGrid[0].length - 1].unlock();
                } else if (longitude < corners.east && latitude == corners.north) {
                    int rowIndex = populationGrid.length - 1;
                    int columnIndex = (int) ((longitude - corners.west) / cellWidth) + 1;
                    lockGrid[rowIndex][columnIndex].lock();
                    populationGrid[rowIndex][columnIndex] += censusGroups[i].population;
                    lockGrid[rowIndex][columnIndex].unlock();
                } else if (longitude == corners.east && latitude < corners.north) {
                    int rowIndex = (int) ((latitude - corners.south) / cellHeight) + 1;
                    int columnIndex = populationGrid[0].length - 1;
                    lockGrid[rowIndex][columnIndex].lock();
                    populationGrid[rowIndex][columnIndex] += censusGroups[i].population;
                    lockGrid[rowIndex][columnIndex].unlock();
                }
            }
        }
    }
}
