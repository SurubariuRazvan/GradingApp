package domain;

public class GradeId {
    private Integer homeworkId;
    private Integer studentId;

    public GradeId(Integer homeworkId, Integer studentId) {
        this.homeworkId = homeworkId;
        this.studentId = studentId;
    }

    public Integer getHomeworkId() {
        return homeworkId;
    }

    public void setHomeworkId(Integer homeworkId) {
        this.homeworkId = homeworkId;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GradeId)) return false;

        GradeId gradeId = (GradeId) o;

        if (getHomeworkId() != null ? !getHomeworkId().equals(gradeId.getHomeworkId()) : gradeId.getHomeworkId() != null)
            return false;
        return getStudentId() != null ? getStudentId().equals(gradeId.getStudentId()) : gradeId.getStudentId() == null;
    }

    @Override
    public int hashCode() {
        int result = getHomeworkId() != null ? getHomeworkId().hashCode() : 0;
        result = 31 * result + (getStudentId() != null ? getStudentId().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "GradeId{" +
                "homeworkId=" + homeworkId +
                ", studentId=" + studentId +
                '}';
    }
}
