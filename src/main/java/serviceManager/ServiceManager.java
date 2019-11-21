package serviceManager;

import domain.*;
import repository.CrudRepository;
import repository.sql.GradePostgreSQLRepository;
import repository.sql.HomeworkPostgreSQLRepository;
import repository.sql.ProfessorPostgreSQLRepository;
import repository.sql.StudentPostgreSQLRepository;
import service.GradeService;
import service.HomeworkService;
import service.ProfessorService;
import service.StudentService;
import validation.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Vector;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ServiceManager {
    private HomeworkService homeworkServo;
    private GradeService gradeServo;
    private StudentService studentServo;
    private ProfessorService professorServo;
    private UniversityYearStructure year;
    private String filePath;

    public ServiceManager() throws SQLException, ClassNotFoundException {
        year = yearSetUp();
        filePath = "./src/main/resources/";
        String url = "jdbc:postgresql://localhost:5432/MAP";
        String user = "postgres";
        String password = "793582";

        Validator<Homework> homeworkVali = new HomeworkValidator();
        CrudRepository<Integer, Homework> homeworkRepo = new HomeworkPostgreSQLRepository(homeworkVali, url, user, password);
        //CrudRepository<Integer, Homework> homeworkRepo = new HomeworkJsonFileRepository(homeworkVali, filePath + "Homework.json");
        homeworkServo = new HomeworkService(homeworkRepo, homeworkVali, year);

        Validator<Grade> gradeVali = new GradeValidator();
        CrudRepository<GradeId, Grade> gradeRepo = new GradePostgreSQLRepository(gradeVali, url, user, password);
        //CrudRepository<GradeId, Grade> gradeRepo = new GradeXmlFileRepository(gradeVali, filePath + "Grade.xml");
        gradeServo = new GradeService(gradeRepo, gradeVali, year);

        Validator<Student> studentVali = new StudentValidator();
        CrudRepository<Integer, Student> studentRepo = new StudentPostgreSQLRepository(studentVali, url, user, password);
        //CrudRepository<Integer, Student> studentRepo = new StudentJsonFileRepository(studentVali, filePath + "Student.json");
        studentServo = new StudentService(studentRepo, studentVali, year);

        Validator<Professor> professorVali = new ProfessorValidator();
        CrudRepository<Integer, Professor> professorRepo = new ProfessorPostgreSQLRepository(professorVali, url, user, password);
        //CrudRepository<Integer, Professor> professorRepo = new ProfessorJsonFileRepository(professorVali, filePath + "Professor.json");
        professorServo = new ProfessorService(professorRepo, professorVali, year);
    }

    /**
     * creates a year structure for 2019
     *
     * @return the 2019 university year structure
     */
    private UniversityYearStructure yearSetUp() {
        Vector<Holiday> v1 = new Vector<>();
        v1.add(new Holiday(LocalDate.of(2019, 12, 25), 2));
        v1.add(new Holiday(LocalDate.of(2020, 2, 10), 1));
        Vector<Holiday> v2 = new Vector<>();
        v2.add(new Holiday(LocalDate.of(2020, 4, 20), 1));
        v2.add(new Holiday(LocalDate.of(2020, 6, 29), 1));
        UniversitySemesterStructure s1 = new UniversitySemesterStructure(1, LocalDate.of(2019, 10, 1), v1);
        UniversitySemesterStructure s2 = new UniversitySemesterStructure(1, LocalDate.of(2020, 2, 24), v2);
        return new UniversityYearStructure(1, 2019, s1, s2);
    }

    /**
     * creates a new Homework entity and saves it
     *
     * @param h Homework with the description and deadlineWeek fields filled
     */
    public void saveHomework(Homework h) {
        Homework homework = homeworkServo.createHomework(h.getDescription(), h.getDeadlineWeek());
        homeworkServo.save(homework);
    }

    /**
     * creates a new Student entity and saves it
     *
     * @param s Student with the familyName, firstName, group, email, labProfessorId fields filled in
     */
    public void saveStudent(Student s) {
        Student student = studentServo.createStudent(s.getFamilyName(), s.getFirstName(), s.getGroup(), s.getEmail(), s.getLabProfessorId());
        studentServo.save(student);
    }

    /**
     * tests if the provided id's are found in the database, if everything is found
     * it creates a new Grade entity and saves it in the provided repository and
     * in a report file
     *
     * @param g             Grade with the id, professorId, givenGrade, feedback fields filled in
     * @param lateProfessor the number of weeks a professor was late to save a grade
     * @throws ValidationException if any of the provided id's aren't found in the database
     */
    public void saveGrade(Grade g, Integer lateProfessor) throws ValidationException {
        String errors = "";
        Homework homework = homeworkServo.findOne(g.getId().getHomeworkId());
        if (homework == null)
            errors += "Tema inexistenta";
        Student student = studentServo.findOne(g.getId().getStudentId());
        if (student == null)
            errors += "Student inexistent";
        Professor professor = professorServo.findOne(g.getProfessorId());
        if (professor == null)
            errors += "Profesor inexistent";
        if (homework == null || student == null || professor == null)
            throw new ValidationException(errors);
        Grade grade = gradeServo.createGrade(student, professor, g.getGivenGrade(), homework, g.getFeedback(), lateProfessor);
        gradeServo.save(grade);
        gradeServo.saveInStudentNameFile(grade, homework, student);
    }

    /**
     * creates a new Professor entity and saves it
     *
     * @param p Professor with the familyName, firstName, email fields filler in
     */
    public void saveProfessor(Professor p) {
        Professor professor = professorServo.createProfessor(p.getFamilyName(), p.getFirstName(), p.getEmail());
        professorServo.save(professor);
    }

    /**
     * deletes the Homework with the given id
     *
     * @param id of the Homework to be deleted
     */
    public void deleteHomework(int id) {
        homeworkServo.delete(id);
    }

    /**
     * deletes the Student with the given id
     *
     * @param id of the Student to be deleted
     */
    public void deleteStudent(int id) {
        studentServo.delete(id);
    }

    /**
     * deletes the Grade with the given id
     *
     * @param id of the Grade to be deleted
     */
    public void deleteGrade(GradeId id) {
        gradeServo.delete(id);
    }

    /**
     * deletes the professor with the given id
     *
     * @param id of the Professor to be deleted
     */
    public void deleteProfessor(int id) {
        professorServo.delete(id);
    }

    /**
     * updates the fields of the Homework entity found at the given id with the new Homework
     *
     * @param id of the Homework to be updated
     * @param h  Homework to be saved at the given id
     */
    public void updateHomework(Integer id, Homework h) {
        Homework homework = homeworkServo.createHomework(h.getDescription(), h.getDeadlineWeek());
        homework.setId(id);
        homeworkServo.update(homework);
    }

    /**
     * updates the fields of the Student entity found at the given id with the new Student
     *
     * @param id of the Student to be updated
     * @param s  Student to be saved at the given id
     */
    public void updateStudent(Integer id, Student s) {
        Student student = studentServo.createStudent(s.getFamilyName(), s.getFirstName(), s.getGroup(), s.getEmail(), s.getLabProfessorId());
        student.setId(id);
        studentServo.update(student);
    }

    /**
     * tests if the provided id's are found in the database, if everything is found
     * it creates a new Grade entity and updates it in the provided repository
     *
     * @param g             Grade with the id, professorId, givenGrade, feedback fields filled in
     * @param lateProfessor the number of weeks a professor was late to save a grade
     * @throws ValidationException if any of the provided id's aren't found in the database
     */
    public void updateGrade(GradeId id, Grade g, Integer lateProfessor) throws ValidationException {
        String errors = "";
        Homework homework = homeworkServo.findOne(g.getId().getHomeworkId());
        if (homework == null)
            errors += "Tema inexistenta";
        Student student = studentServo.findOne(g.getId().getStudentId());
        if (student == null)
            errors += "Student inexistent";
        Professor professor = professorServo.findOne(g.getProfessorId());
        if (professor == null)
            errors += "Profesor inexistent";
        if (homework == null || student == null || professor == null)
            throw new ValidationException(errors);
        Grade grade = gradeServo.createGrade(student, professor, g.getGivenGrade(), homework, g.getFeedback(), lateProfessor);
        grade.setId(id);
        gradeServo.update(grade);
    }

    /**
     * updates the fields of the Professor entity found at the given id with the new Professor
     *
     * @param id of the Professor to be updated
     * @param p  Professor to be saved at the given id
     */
    public void updateProfessor(Integer id, Professor p) {
        Professor professor = professorServo.createProfessor(p.getFamilyName(), p.getFirstName(), p.getEmail());
        professor.setId(id);
        professorServo.update(professor);
    }

    /**
     * @return all Homework entities found in the repository
     */
    public Iterable<Homework> findAllHomework() {
        return homeworkServo.findAll();
    }

    /**
     * @return all Student entities found in the repository
     */
    public Iterable<Student> findAllStudent() {
        return studentServo.findAll();
    }

    /**
     * @return all Grade entities found in the repository
     */
    public Iterable<Grade> findAllGrade() {
        return gradeServo.findAll();
    }

    /**
     * @return all Professor entities found in the repository
     */
    public Iterable<Professor> findAllProfessor() {
        return professorServo.findAll();
    }

    /**
     * saves all the changes made in memory to file
     */
    public void saveAll() {
        homeworkServo.saveAll();
        gradeServo.saveAll();
        studentServo.saveAll();
        professorServo.saveAll();
    }

    /**
     * @param group Student field
     * @return an iterable with all the students from a given group
     */
    public Iterable<Student> filterByStudentGroup(Integer group) {
        return StreamSupport.stream(studentServo.findAll().spliterator(), false)
                .filter(s -> s.getGroup().equals(group))
                .collect(Collectors.toList());
    }

    /**
     * @param homeworkId Grade field
     * @return an iterable with all the Students that handed over a given homework
     */
    public Iterable<Student> filterByHandOverHomework(Integer homeworkId) {
        return StreamSupport.stream(gradeServo.findAll().spliterator(), false)
                .filter(g -> g.getHomeworkId().equals(homeworkId))
                .map(g -> studentServo.findOne(g.getId().getStudentId()))
                .collect(Collectors.toList());
    }

    /**
     * @param homeworkId  Grade field
     * @param professorId Grade field
     * @return an iterable with all the Students that handed over a given homework to a given professor
     */
    public Iterable<Student> filterByHandOverHomeworkAndProfessor(Integer homeworkId, Integer professorId) {
        return StreamSupport.stream(gradeServo.findAll().spliterator(), false)
                .filter(g -> g.getHomeworkId().equals(homeworkId) && g.getProfessorId().equals(professorId))
                .map(g -> studentServo.findOne(g.getId().getStudentId()))
                .collect(Collectors.toList());
    }

    /**
     * @param homeworkId   Grade field
     * @param handOverWeek Grade field
     * @return an iterable with all the Grades from a Homework handed oven in a given week
     */
    public Iterable<Grade> filterByHomeworkAndHandOverWeek(Integer homeworkId, Integer handOverWeek) {
        return StreamSupport.stream(gradeServo.findAll().spliterator(), false)
                .filter(g -> g.getHomeworkId().equals(homeworkId) && year.getWeek(g.getHandOverDate()).equals(handOverWeek))
                .collect(Collectors.toList());
    }
}
