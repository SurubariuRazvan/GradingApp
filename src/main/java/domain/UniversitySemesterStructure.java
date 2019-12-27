package domain;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Vector;

public class UniversitySemesterStructure extends Entity<Integer> {
    private final LocalDate startingDate;
    private final Vector<Holiday> freeWeeks;

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

        Integer weekNr = (int) ChronoUnit.WEEKS.between(startingDate, currentDate) + 1;
        for(var i : freeWeeks)
            if (i.getStartDate().isBefore(currentDate))
                if (currentDate.isBefore(i.getStartDate().plusWeeks(i.getDuration())))
                    weekNr -= (int) ChronoUnit.WEEKS.between(i.getStartDate(), currentDate);
                else
                    weekNr -= i.getDuration();
        return Math.min(weekNr, 14);
    }
}