package domain;

import java.time.LocalDate;

public class Grade extends Entity<GradeId> {
    private LocalDate handOverDate;
    private Integer professorId;
    private Double givenGrade;
    private Integer homeworkId;
    private String feedback;

    public Grade(LocalDate handOverDate, Integer professorId, Double givenGrade, Integer homeworkId, String feedback) {
        this.handOverDate = handOverDate;
        this.professorId = professorId;
        this.givenGrade = givenGrade;
        this.homeworkId = homeworkId;
        this.feedback = feedback;
    }

    public Grade(GradeId id, LocalDate handOverDate, Integer professorId, Double givenGrade, Integer homeworkId, String feedback) {
        super.setId(id);
        this.handOverDate = handOverDate;
        this.professorId = professorId;
        this.givenGrade = givenGrade;
        this.homeworkId = homeworkId;
        this.feedback = feedback;
    }

    public LocalDate getHandOverDate() {
        return handOverDate;
    }

    public void setHandOverDate(LocalDate handOverDate) {
        this.handOverDate = handOverDate;
    }

    public Integer getProfessorId() {
        return professorId;
    }

    public void setProfessorId(Integer professorId) {
        this.professorId = professorId;
    }

    public Double getGivenGrade() {
        return givenGrade;
    }

    public void setGivenGrade(Double givenGrade) {
        this.givenGrade = givenGrade;
    }

    public Integer getHomeworkId() {
        return homeworkId;
    }

    public void setHomeworkId(Integer homeworkId) {
        this.homeworkId = homeworkId;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    @Override
    public String toString() {
        return "Grade{" +
                "handOverDate=" + handOverDate +
                ", professor=" + professorId +
                ", givenGrade=" + givenGrade +
                ", homework=" + homeworkId +
                ", feedback='" + feedback + '\'' +
                '}';
    }
}
