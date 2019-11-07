package domain;

import java.util.Objects;

public class Homework extends Entity<Integer> {
    private String description;
    private Integer startWeek;
    private Integer deadlineWeek;

    public Homework(Integer id, String description, Integer startWeek, Integer deadlineWeek) {
        super.setId(id);
        this.description = description;
        this.startWeek = startWeek;
        this.deadlineWeek = deadlineWeek;
    }

    public Homework(String description, Integer startWeek, Integer deadlineWeek) {
        this.description = description;
        this.startWeek = startWeek;
        this.deadlineWeek = deadlineWeek;
    }

    @Override
    public String toString() {
        return "Homework{" + " id=" + super.getId() +
                ", description='" + description + '\'' +
                ", startWeek=" + startWeek +
                ", deadlineWeek=" + deadlineWeek +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Homework homework = (Homework) o;
        return description.equals(homework.description) &&
                startWeek.equals(homework.startWeek) &&
                deadlineWeek.equals(homework.deadlineWeek);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, startWeek, deadlineWeek);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStartWeek() {
        return startWeek;
    }

    public void setStartWeek(Integer startWeek) {
        this.startWeek = startWeek;
    }

    public Integer getDeadlineWeek() {
        return deadlineWeek;
    }

    public void setDeadlineWeek(Integer deadlineWeek) {
        this.deadlineWeek = deadlineWeek;
    }
}
