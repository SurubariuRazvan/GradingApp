package domain;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Vector;

public class UniversitySemesterStructure extends Entity<Integer> {
    private LocalDate startingDate;
    private Vector<Holiday> freeWeeks;

    public UniversitySemesterStructure(LocalDate startingDate, Vector<Holiday> freeWeeks) {
        this.startingDate = startingDate;
        this.freeWeeks = freeWeeks;
    }

    public UniversitySemesterStructure(Integer id, LocalDate startingDate, Vector<Holiday> freeWeeks) {
        super.setId(id);
        this.startingDate = startingDate;
        this.freeWeeks = freeWeeks;
    }

    LocalDate getStartingDate() {
        return startingDate;
    }

    /**
     * @param currentDate is a date
     * @return the number of the week reported to te start of the semester
     */
    public Integer getWeek(LocalDate currentDate) {
        if (currentDate == null)
            throw new IllegalArgumentException();
        TemporalField week = WeekFields.of(DayOfWeek.MONDAY, 1).weekOfWeekBasedYear();

        int startingWeek = startingDate.get(week);
        int currentWeek = currentDate.get(week);
        if (startingDate.getYear() < currentDate.getYear())
            currentWeek = currentWeek + 52;
        int weekNr = currentWeek - startingWeek + 1;
        for (var i : freeWeeks)
            if (i.getStartDate().isBefore(currentDate))
                weekNr = weekNr - i.getDuration();
        return weekNr;
    }
}