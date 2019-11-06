package domain;

import java.time.LocalDate;

public class Holiday {
    private LocalDate startDate;
    private Integer duration;

    public Holiday(LocalDate startDate, Integer duration) {
        this.startDate = startDate;
        this.duration = duration;
    }

    LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }
}