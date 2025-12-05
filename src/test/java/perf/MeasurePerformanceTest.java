package perf;

import org.junit.jupiter.api.Test;

// Things we do for Code Coverage... altough "perf/MeasurePerformance.java"
// is only to be manually run, it is included in build, so
// we get code coverage whether we want it or not. So let's have
// a silly little driver to exercise it from unit tests and avoid dinging
// overall test coverage
public class MeasurePerformanceTest
{
    @Test
    public void runMinimalPerfTest() throws Exception
    {
        new MeasurePerformance(10, false).test();
    }
}
