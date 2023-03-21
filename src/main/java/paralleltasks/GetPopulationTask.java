package paralleltasks;

import cse332.exceptions.NotYetImplementedException;
import cse332.types.CensusGroup;
import cse332.types.MapCorners;

import java.util.concurrent.RecursiveTask;

/*
   1) This class is the parallel version of the getPopulation() method from version 1 for use in version 2
   2) SEQUENTIAL_CUTOFF refers to the maximum number of census groups that should be processed by a single parallel task
   3) The double parameters(w, s, e, n) represent the bounds of the query rectangle
   4) The compute method returns an Integer representing the total population contained in the query rectangle
 */
public class GetPopulationTask extends RecursiveTask<Integer> {
    final static int SEQUENTIAL_CUTOFF = 1000;
    CensusGroup[] censusGroups;
    int lo, hi;
    double w, s, e, n;
    MapCorners grid;

    public GetPopulationTask(CensusGroup[] censusGroups, int lo, int hi, double w, double s, double e, double n, MapCorners grid) {
        this.censusGroups = censusGroups;
        this.lo = lo;
        this.hi = hi;
        this.w = w;
        this.s = s;
        this.e = e;
        this.n = n;
        this.grid = grid;    }

    @Override
    protected Integer compute() {
        if (hi - lo <= SEQUENTIAL_CUTOFF) {
            return sequentialGetPopulation(censusGroups, lo, hi, w, s, e, n);
        }

        int middle = lo + (hi - lo) / 2;
        GetPopulationTask left = new GetPopulationTask(censusGroups, lo, middle, w, s, e, n, grid);
        GetPopulationTask right = new GetPopulationTask(censusGroups, middle, hi, w, s, e, n, grid);

        right.fork();
        return right.join() + left.compute();
    }

    private Integer sequentialGetPopulation(CensusGroup[] censusGroups, int lo, int hi, double w, double s, double e, double n) {
        int population = 0;
        for (int i = lo; i < hi; i++) {
            double latitude = censusGroups[i].latitude;
            double longitude = censusGroups[i].longitude;
            if (longitude >= w && latitude >= s) {
                if (latitude < n && longitude < e) {
                    population += censusGroups[i].population;
                } else if (latitude == grid.north && latitude == n && longitude < e) {
                    population += censusGroups[i].population;
                } else if (longitude == grid.east && longitude == e && latitude == grid.north && latitude == n) {
                    population += censusGroups[i].population;
                } else if (longitude == grid.east && longitude == e && latitude < n) {
                    population += censusGroups[i].population;
                }
            }
        }
        return population;
    }
}
