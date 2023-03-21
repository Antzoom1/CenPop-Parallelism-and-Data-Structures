package queryresponders;

import cse332.exceptions.NotYetImplementedException;
import cse332.interfaces.QueryResponder;
import cse332.types.CensusGroup;
import cse332.types.MapCorners;

public class SimpleSequential extends QueryResponder {
    CensusGroup[] censusData;
    final private int numColumns;
    final private int numRows;
    MapCorners mapOfCorners;


    public SimpleSequential(CensusGroup[] censusData, int numColumns, int numRows) {
        this.censusData = censusData;
        this.numColumns = numColumns;
        this.numRows = numRows;
        if (censusData.length > 0) {
            this.totalPopulation = censusData[0].population;
            this.mapOfCorners = new MapCorners(censusData[0]);
            for (int i = 1; i < censusData.length; i++) {
                MapCorners corners = new MapCorners(censusData[i]);
                mapOfCorners = corners.encompass(mapOfCorners);
                totalPopulation += censusData[i].population;
            }
        }
    }


    @Override
    public int getPopulation(int west, int south, int east, int north) {
        if ((west < 1 || west > numColumns) || (south < 1 || south > numRows) ||
                (east < west || east > numColumns) || (north < south || north > numRows)) {
            throw new IllegalArgumentException();
        }
        int population = 0;
        double westBorder = mapOfCorners.west + (mapOfCorners.east - mapOfCorners.west ) / numColumns * (west - 1);
        double southBorder = mapOfCorners.south + (mapOfCorners.north - mapOfCorners.south) / numRows * (south - 1);
        double eastBorder = mapOfCorners.west + (mapOfCorners.east - mapOfCorners.west) / numColumns * east;
        double northBorder = mapOfCorners.south + (mapOfCorners.north - mapOfCorners.south) / numRows * north;

        for(int i = 0; i < censusData.length; i++) {
            CensusGroup data = censusData[i];
            double longitude = data.longitude;
            double latitude = data.latitude;
            if (longitude >= westBorder && latitude >= southBorder) {
                if (longitude < eastBorder && latitude < northBorder) {
                    population = population + data.population;
                } else if (latitude == mapOfCorners.north && latitude == northBorder && longitude < eastBorder) {
                    population += data.population;
                } else if (longitude == mapOfCorners.east && longitude == eastBorder && latitude == mapOfCorners.north &&
                        latitude == northBorder) {
                    population += data.population;
                } else if (longitude == mapOfCorners.east && longitude == eastBorder && latitude < northBorder) {
                    population += data.population;
                }
            }
        }
        return population;
    }
}
