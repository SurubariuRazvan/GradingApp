package domain;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Vector;

class UniversitySemesterStructureTest {
    private static UniversitySemesterStructure s;

    @BeforeAll
    static void setUp() {
        Vector<Holiday> v = new Vector<>();
        v.add(new Holiday(LocalDate.of(2019, 12, 23), 2));
        v.add(new Holiday(LocalDate.of(2020, 2, 10), 1));
        s = new UniversitySemesterStructure(1, LocalDate.of(2019, 9, 30), v);
    }

    @Test
    void getWeek() {
        assert (s.getWeek(LocalDate.of(2019, 10, 1)) == 1);
        assert (s.getWeek(LocalDate.of(2019, 10, 6)) == 1);
        assert (s.getWeek(LocalDate.of(2019, 10, 7)) == 2);
        assert (s.getWeek(LocalDate.of(2019, 12, 21)) == 12);
        assert (s.getWeek(LocalDate.of(2019, 12, 23)) == 13);
        assert (s.getWeek(LocalDate.of(2019, 12, 27)) == 13);
        assert (s.getWeek(LocalDate.of(2019, 12, 30)) == 13);
        assert (s.getWeek(LocalDate.of(2020, 1, 1)) == 13);
        assert (s.getWeek(LocalDate.of(2020, 1, 6)) == 13);
        assert (s.getWeek(LocalDate.of(2020, 1, 19)) == 14);
        assert (s.getWeek(LocalDate.of(2020, 1, 20)) == 14);
    }
}