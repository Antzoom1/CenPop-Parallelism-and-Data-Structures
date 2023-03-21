package ckpt1;

import base.QueryResponderTests;
import cse332.types.CensusGroup;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import queryresponders.SimpleParallel;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SimpleParallelTests extends QueryResponderTests {

    static CensusGroup[] data;

    @BeforeAll
    public static void init() {
        data = readCensusdata();
        STUDENT_100_500 = new SimpleParallel(data, 100, 500);
        STUDENT_20_40 = new SimpleParallel(data, 20, 40);

    }

    /**
     * 100 x 500 grid
     */

    @Test
    public void test_getPopulation_full100x500Map_correctPopulation() {
        assertEquals(312471327, STUDENT_100_500.getPopulation(1, 1, 100, 500));
    }

    @Test
    public void test_getPopulation_leftHalf100x500Map_correctPopulation() {
        assertEquals(27820072, STUDENT_100_500.getPopulation(1, 1, 50, 500));
    }

    @Test
    public void test_getPopulation_rightHalf100x500Map_correctPopulation() {
        assertEquals(284651255, STUDENT_100_500.getPopulation(51, 1, 100, 500));
    }

    @Test
    public void test_getPopulation_center100x500Map_correctPopulations() {
        assertEquals(7084297, STUDENT_100_500.getPopulation(20, 200, 60, 300));
        assertEquals(179539805, STUDENT_100_500.getPopulation(50, 100, 90, 250));
    }

    @Test
    public void test_getPopulation_corners100x500Map_correctPopulations() {
        assertEquals(0, STUDENT_100_500.getPopulation(1, 1, 1, 1));
        assertEquals(207111, STUDENT_100_500.getPopulation(100, 1, 100, 1));
        assertEquals(0, STUDENT_100_500.getPopulation(1, 500, 1, 500));
        assertEquals(0, STUDENT_100_500.getPopulation(100, 500, 100, 500));
    }

    @Test
    public void test_getPopulation_hawaii100x500Map_correctPopulation() {
        assertEquals(1360301, STUDENT_100_500.getPopulation(10, 1, 20, 40));
    }

    @Test
    public void test_getPopulation_canada100x500Map_correctPopulation() {
        assertEquals(0, STUDENT_100_500.getPopulation(50, 250, 100, 450));
    }

    @Test
    public void test_totalPopulation_full100x500Map_correctPopulation() {
        assertEquals(312471327, STUDENT_100_500.getTotalPopulation());
    }

    /**
     * 20 x 40 grid
     */

    @Test
    public void test_getPopulation_leftHalf20x40Map_correctPopulation() {
        assertEquals(27820072, STUDENT_20_40.getPopulation(1, 1, 10, 40));
    }

    @Test
    public void test_getPopulation_rightHalf20x40Map_correctPopulation() {
        assertEquals(284651255, STUDENT_20_40.getPopulation(11, 1, 20, 40));
    }

    @Test
    public void test_getPopulation_center20x40Map_correctPopulation() {
        assertEquals(62861861, STUDENT_20_40.getPopulation(5, 10, 15, 30));
    }

    @Test
    public void test_getPopulation_corners20x40Map_correctPopulations() {
        assertEquals(0, STUDENT_20_40.getPopulation(1, 1, 1, 1));
        assertEquals(3725789, STUDENT_20_40.getPopulation(20, 1, 20, 1));
        assertEquals(0, STUDENT_20_40.getPopulation(1, 40, 1, 40));
        assertEquals(0, STUDENT_20_40.getPopulation(20, 40, 20, 40));
    }

    @Test
    public void test_getPopulation_hawaii20x40Map_correctPopulation() {
        assertEquals(1360301, STUDENT_20_40.getPopulation(2, 1, 4, 3));
    }

    @Test
    public void test_getPopulation_canada20x40Map_correctPopulation() {
        assertEquals(0, STUDENT_20_40.getPopulation(10, 20, 20, 35));
    }

    @Test
    public void test_totalPopulation_full20x40Map_correctPopulation() {
        assertEquals(312471327, STUDENT_20_40.getTotalPopulation());
    }
}