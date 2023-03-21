package paralleltasks;

import cse332.exceptions.NotYetImplementedException;
import cse332.types.CensusGroup;
import cse332.types.CornerFindingResult;
import cse332.types.MapCorners;

import java.util.concurrent.RecursiveTask;

/*
   1) This class will do the corner finding from version 1 in parallel for use in versions 2, 4, and 5
   2) SEQUENTIAL_CUTOFF refers to the maximum number of census groups that should be processed by a single parallel task
   3) The compute method returns a result of a MapCorners and an Integer.
        - The MapCorners will represent the extremes/bounds/corners of the entire land mass (latitude and longitude)
        - The Integer value should represent the total population contained inside the MapCorners
 */

public class CornerFindingTask extends RecursiveTask<CornerFindingResult> {
    final int SEQUENTIAL_CUTOFF = 10000;
    CensusGroup[] censusGroups;
    int lo, hi;

    public CornerFindingTask(CensusGroup[] censusGroups, int lo, int hi) {
            this.lo = lo;
            this.hi = hi;
            this.censusGroups = censusGroups;
        }

    @Override
    protected CornerFindingResult compute() {
        if (hi - lo <= SEQUENTIAL_CUTOFF) {
            return sequentialCornerFinding(censusGroups, lo, hi);
        }
        int middle = lo + (hi - lo) / 2;
        CornerFindingTask left = new CornerFindingTask(censusGroups, lo, middle);
        CornerFindingTask right = new CornerFindingTask(censusGroups, middle, hi);

        right.fork();
        CornerFindingResult rightResult = right.join();
        CornerFindingResult leftResult = left.compute();

        return new CornerFindingResult(rightResult.getMapCorners().encompass(leftResult.getMapCorners()),
                leftResult.getTotalPopulation() + rightResult.getTotalPopulation());
    }

    private CornerFindingResult sequentialCornerFinding(CensusGroup[] censusGroups, int lo, int hi) {
        int totalPopulation = censusGroups[lo].population;
        MapCorners mapOfCorners = new MapCorners(censusGroups[lo]);
        for (int i = lo + 1; i < hi; i++) {
            MapCorners corner = new MapCorners(censusGroups[i]);
            mapOfCorners = corner.encompass(mapOfCorners);
            totalPopulation += censusGroups[i].population;
        }
        return new CornerFindingResult(mapOfCorners, totalPopulation);    }
}

