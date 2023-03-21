package queryresponders;

import cse332.exceptions.NotYetImplementedException;
import cse332.interfaces.QueryResponder;
import cse332.types.CensusData;
import cse332.types.CensusGroup;
import cse332.types.MapCorners;

import java.util.Arrays;
import java.util.Comparator;


public class ComplexSequential extends QueryResponder {
    CensusGroup[] censusData;
    MapCorners mapOfCorners;
    int numColumns;
    int numRows;
    int [][] grid;



    public ComplexSequential(CensusGroup[] censusData, int numColumns, int numRows) {
        this.numColumns = numColumns;
        this.numRows = numRows;
        this.censusData = censusData;
        this.grid = new int[numRows + 1][numColumns + 1];
        if (censusData.length > 0) {
            this.mapOfCorners = new MapCorners(censusData[0]);
            this.totalPopulation = censusData[0].population;
            for (int i = 1; i < censusData.length; i++) {
                MapCorners corner = new MapCorners(censusData[i]);
                mapOfCorners = corner.encompass(mapOfCorners);
                totalPopulation += censusData[i].population;
            }
        }
        double columnDistance = (mapOfCorners.east - mapOfCorners.west) / numColumns;
        double rowDistance = (mapOfCorners.north - mapOfCorners.south) / numRows;

        for (int i = 0; i < censusData.length; i++) {
            double latitude = censusData[i].latitude;
            double longitude = censusData[i].longitude;

            if (latitude < mapOfCorners.north && longitude < mapOfCorners.east) {
                int rowIndex = (int) ((latitude - mapOfCorners.south) / rowDistance) + 1;
                int columnIndex = (int) ((longitude - mapOfCorners.west) / columnDistance) + 1;
                grid[rowIndex][columnIndex] +=censusData[i].population;
            } else if (latitude < mapOfCorners.north && longitude == mapOfCorners.east) {
                int rowIndex = (int) ((latitude - mapOfCorners.south) / rowDistance) + 1;
                int columnIndex = grid[0].length - 1;
                grid[rowIndex][columnIndex] += censusData[i].population;
            } else if (latitude == mapOfCorners.north && longitude == mapOfCorners.east) {
                grid[grid.length - 1][grid[0].length - 1] += censusData[i].population;
            } else if (latitude == mapOfCorners.north && longitude < mapOfCorners.east) {
                int rowIndex = grid.length - 1;
                int columnIndex = (int) ((longitude - mapOfCorners.west) / columnDistance) + 1;
                grid[rowIndex][columnIndex] += censusData[i].population;
            }
        }

        for (int i = 1; i < grid.length; i++) {
            for (int j = 1; j < grid[i].length; j++) {
                grid[i][j] = grid[i][j] + grid[i - 1][j] + grid[i][j - 1]
                        - grid[i - 1][j - 1];
            }
        }
    }




    @Override
    public int getPopulation(int west, int south, int east, int north) {

        if (west < 1 || west > numColumns || east < 1 || east > numColumns
                || south < 1 || south > numRows || north < 1 || north > numRows)
            throw new IllegalArgumentException();

        return grid[north][east] - grid[south - 1][east] - grid[north][west -1] +
                grid[south - 1][west - 1];
    }
}
