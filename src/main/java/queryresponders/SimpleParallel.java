package queryresponders;

import cse332.exceptions.NotYetImplementedException;
import cse332.interfaces.QueryResponder;
import cse332.types.CensusGroup;
import cse332.types.CornerFindingResult;
import cse332.types.MapCorners;
import paralleltasks.CornerFindingTask;
import paralleltasks.GetPopulationTask;

import java.util.concurrent.ForkJoinPool;

public class SimpleParallel extends QueryResponder {
    private static final ForkJoinPool POOL = new ForkJoinPool();
    private CensusGroup[] censusData;
    private int numColumns;
    private int numRows;
    private MapCorners mapOfCorners;

    public SimpleParallel(CensusGroup[] censusData, int numColumns, int numRows) {
        this.censusData = censusData;
        this.numColumns = numColumns;
        this.numRows = numRows;
        if (censusData.length > 0) {
            CornerFindingResult result = POOL.invoke(new CornerFindingTask(censusData, 0, censusData.length));
            mapOfCorners = result.getMapCorners();
            this.totalPopulation = result.getTotalPopulation();
        }
    }

    @Override
    public int getPopulation(int west, int south, int east, int north) {
            if ((west < 1 || west > numColumns) || (south < 1 || south > numRows) ||
                    (east < west || east > numColumns) || (north < south || north > numRows)) {
                throw new IllegalArgumentException();
            }
            double westBorder = mapOfCorners.west + (mapOfCorners.east - mapOfCorners.west ) / numColumns * (west - 1);
            double southBorder = mapOfCorners.south + (mapOfCorners.north - mapOfCorners.south) / numRows * (south - 1);
            double eastBorder = mapOfCorners.west + (mapOfCorners.east - mapOfCorners.west) / numColumns * east;
            double northBorder = mapOfCorners.south + (mapOfCorners.north - mapOfCorners.south) / numRows * north;

            return POOL.invoke(new GetPopulationTask(censusData, 0, censusData.length, westBorder,
                    southBorder, eastBorder, northBorder, mapOfCorners));
        }
}
