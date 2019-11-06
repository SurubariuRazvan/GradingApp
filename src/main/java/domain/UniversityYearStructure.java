package domain;

import java.time.LocalDate;

public class UniversityYearStructure extends Entity<Integer> {
    private Integer universityYear;
    private UniversitySemesterStructure sem1;
    private UniversitySemesterStructure sem2;

    public UniversityYearStructure(Integer id, Integer universityYear, UniversitySemesterStructure sem1, UniversitySemesterStructure sem2) {
        super.setId(id);
        this.universityYear = universityYear;
        this.sem1 = sem1;
        this.sem2 = sem2;
    }

    public Integer getWeek(LocalDate currentDate) {
        if (currentDate.isAfter(sem2.getStartingDate()))
            return sem2.getWeek(currentDate);
        else
            return sem1.getWeek(currentDate);
    }

    public Integer getUniversityYear() {
        return universityYear;
    }

    public UniversitySemesterStructure getSem1() {
        return sem1;
    }

    public UniversitySemesterStructure getSem2() {
        return sem2;
    }
}
