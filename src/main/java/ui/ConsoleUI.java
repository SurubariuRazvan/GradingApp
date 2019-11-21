package ui;

import domain.*;
import repository.RepositoryException;
import serviceManager.ServiceManager;
import validation.ValidationException;

import java.sql.SQLException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConsoleUI {
    private ServiceManager service;

    public ConsoleUI() {
        try {
            service = new ServiceManager();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private String input(String prompt) {
        Scanner input = new Scanner(System.in);
        System.out.println(prompt);
        return input.nextLine();
    }

    private void printMenu() {
        System.out.println(" 0 - Printare meniu\n" +
                " 1 - Adaugare tema\n" +
                " 2 - Stergere tema\n" +
                " 3 - Modificare tema\n" +
                " 4 - Afisare teme\n" +
                " 5 - Adaugare student\n" +
                " 6 - Stergere student\n" +
                " 7 - Modificare student\n" +
                " 8 - Afisare studenti\n" +
                " 9 - Adaugare nota\n" +
                "10 - Stergere nota\n" +
                "11 - Modificare nota\n" +
                "12 - Afisare note\n" +
                "13 - Adaugare profesor\n" +
                "14 - Stergere profesor\n" +
                "15 - Modificare profesor\n" +
                "16 - Afisare profesori\n" +
                "17 - Filtrari\n" +
                " x - Inchidere aplicatie\n"
        );
    }

    public void run() {
        printMenu();
        String command;
        boolean running = true;
        while(running) {
            command = input("Introduceti comanda:");
            try {
                switch (command) {
                    case "x":
                        running = false;
                        service.saveAll();
                        break;
                    case "1":
                        saveHomework();
                        break;
                    case "2":
                        deleteHomework();
                        break;
                    case "3":
                        updateHomework();
                        break;
                    case "4":
                        service.findAllHomework().forEach(System.out::println);
                        break;
                    case "5":
                        saveStudent();
                        break;
                    case "6":
                        deleteStudent();
                        break;
                    case "7":
                        updateStudent();
                        break;
                    case "8":
                        service.findAllStudent().forEach(System.out::println);
                        break;
                    case "9":
                        saveGrade();
                        break;
                    case "10":
                        deleteGrade();
                        break;
                    case "11":
                        updateGrade();
                        break;
                    case "12":
                        service.findAllGrade().forEach(System.out::println);
                        break;
                    case "13":
                        saveProfessor();
                        break;
                    case "14":
                        deleteProfessor();
                        break;
                    case "15":
                        updateProfessor();
                        break;
                    case "16":
                        service.findAllProfessor().forEach(System.out::println);
                        break;
                    case "17":
                        filtersMenu();
                        break;
                    default:
                        printMenu();
                        break;
                }
            } catch (ValidationException | RepositoryException e) {
                System.out.println(e.getMessage());
            } catch (NumberFormatException e) {
                Pattern p = Pattern.compile("\".*\"");
                Matcher m = p.matcher(e.getMessage());
                if (m.find()) {
                    String userInput = m.group(0);
                    System.out.println(userInput + " nu este un numar");
                }
            }
        }
    }

    private void printFiltersMenu() {
        System.out.println(" 1 - Toți studenții unei grupe\n" +
                " 2 - Toți studenții care au predat o anumita tema\n" +
                " 3 - Toți studenții care au predat o anumita tema unui profesor anume\n" +
                " 4 - Toate notele la o anumita tema, dintr-o saptamana data\n" +
                " x - Inchidere meniu filtrare"
        );
    }

    private void filtersMenu() {
        printFiltersMenu();
        String command;
        boolean running = true;
        while(running) {
            command = input("Filtrare: Introduceti comanda:");
            try {
                switch (command) {
                    case "x":
                        running = false;
                        break;
                    case "1":
                        filterByStudentGroup();
                        break;
                    case "2":
                        filterByHandOverHomework();
                        break;
                    case "3":
                        filterByHandOverHomeworkToProfessor();
                        break;
                    case "4":
                        filterByHomeworkAndHandOverWeek();
                        break;
                    default:
                        printFiltersMenu();
                        break;
                }
            } catch (ValidationException e) {
                System.out.println(e.getMessage());
            } catch (NumberFormatException e) {
                Pattern p = Pattern.compile("\".*\"");
                Matcher m = p.matcher(e.getMessage());
                if (m.find()) {
                    String userInput = m.group(0);
                    System.out.println(userInput + " nu este un numar");
                }
            }
        }
    }

    private void filterByStudentGroup() {
        Integer group = Integer.parseInt(input("Introduceti grupa cautata:"));
        Iterable<Student> filteredResult = service.filterByStudentGroup(group);
        if (filteredResult.iterator().hasNext())
            filteredResult.forEach(System.out::println);
        else
            System.out.println("Nu exista studenti din grupa: " + group);
    }

    private void filterByHandOverHomework() {
        Integer homeworkId = Integer.parseInt(input("Introduceti id-ul temei predate:"));
        Iterable<Student> filteredResult = service.filterByHandOverHomework(homeworkId);
        if (filteredResult.iterator().hasNext())
            filteredResult.forEach(System.out::println);
        else
            System.out.println("Nu exista studenti care au predat tema cu id-ul: " + homeworkId);
    }

    private void filterByHandOverHomeworkToProfessor() {
        Integer homeworkId = Integer.parseInt(input("Introduceti id-ul temei predate:"));
        Integer professorId = Integer.parseInt(input("Introduceti id-ul profesorului care a preluat tema:"));
        Iterable<Student> filteredResult = service.filterByHandOverHomeworkAndProfessor(homeworkId, professorId);
        if (filteredResult.iterator().hasNext())
            filteredResult.forEach(System.out::println);
        else
            System.out.println("Nu exista studenti care au predat tema cu id-ul: " + homeworkId + " profesorului cu id-ul: " + professorId);
    }

    private void filterByHomeworkAndHandOverWeek() {
        Integer homeworkId = Integer.parseInt(input("Introduceti id-ul temei predate:"));
        Integer handOverWeek = Integer.parseInt(input("Introduceti saptamana in care a fost predata tema:"));
        Iterable<Grade> filteredResult = service.filterByHomeworkAndHandOverWeek(homeworkId, handOverWeek);
        if (filteredResult.iterator().hasNext())
            filteredResult.forEach(System.out::println);
        else
            System.out.println("Nu exista studenti care au predat tema cu id-ul: " + homeworkId + " in saptamana: " + handOverWeek);
    }

    private void saveHomework() {
        service.saveHomework(readHomework());
    }

    private void saveStudent() {
        service.saveStudent(readStudent());
    }

    private void saveGrade() {
        Grade grade = readGrade();
        Integer a = Integer.parseInt(input("Introduteti numarul de saptamani motivate:"));
        Integer lateProfessor = Integer.parseInt(input("Introduteti numarul de saptamani intarziate de cand trebuia trecuta nota:"));
        service.saveGrade(grade, lateProfessor + a);
    }

    private void saveProfessor() {
        service.saveProfessor(readProfessor());
    }

    private void deleteHomework() {
        service.deleteHomework(Integer.parseInt(input("Introduceti id-ul temei:")));
    }

    private void deleteStudent() {
        service.deleteStudent(Integer.parseInt(input("Introduceti id-ul studentului:")));
    }

    private void deleteGrade() {
        Integer homeworkId = Integer.parseInt(input("Introduceti id-ul temei:"));
        Integer studentId = Integer.parseInt(input("Introduceti id-ul studentului:"));
        service.deleteGrade(new GradeId(homeworkId, studentId));
    }

    private void deleteProfessor() {
        service.deleteProfessor(Integer.parseInt(input("Introduceti id-ul profesorului:")));
    }

    private void updateHomework() {
        Integer id = Integer.parseInt(input("Introduceti id-ul temei:"));
        Homework h = readHomework();
        service.updateHomework(id, h);
    }

    private void updateStudent() {
        Integer id = Integer.parseInt(input("Introduceti id-ul studentului:"));
        Student s = readStudent();
        service.updateStudent(id, s);
    }

    private void updateGrade() {
        Integer homeworkId = Integer.parseInt(input("Introduceti id-ul temei:"));
        Integer studentId = Integer.parseInt(input("Introduceti id-ul studentului:"));
        Integer a = Integer.parseInt(input("Introduteti numarul de saptamani motivate:"));
        Integer lateProfessor = Integer.parseInt(input("Introduteti numarul de saptamani intarziate de cand trebuia trecuta nota:"));
        GradeId id = new GradeId(homeworkId, studentId);
        Grade g = readGrade();
        service.updateGrade(id, g, lateProfessor + a);
    }

    private void updateProfessor() {
        Integer id = Integer.parseInt(input("Introduceti id-ul profesorului:"));
        Professor p = readProfessor();
        service.updateProfessor(id, p);
    }

    private Homework readHomework() {
        String description = input("Introduceti descrierea:");
        Integer deadlineWeek = Integer.parseInt(input("Introduceti termenul limita:"));

        return new Homework(description, null, deadlineWeek);
    }

    private Student readStudent() {
        String familyName = input("Introduceti numele studentului:");
        String firstName = input("Introduceti prenumele studentului:");
        Integer group = Integer.parseInt(input("Introduceti grupa studentului:"));
        String email = input("Introduceti emailul studentului:");
        Integer professorId = Integer.parseInt(input("Introduceti ID-ul profesorului indrumator:"));

        return new Student(familyName, firstName, group, email, professorId);
    }

    private Grade readGrade() {
        Integer homeworkId = Integer.parseInt(input("Introduceti ID-ul temei predate:"));
        Integer studentId = Integer.parseInt(input("Introduceti ID-ul studentului:"));
        Integer professorId = Integer.parseInt(input("Introduceti ID-ul profesorului indrumator:"));
        Double givenGrade = Double.parseDouble(input("Introduceti nota:"));
        String feedback = input("Introduceti feedback-ul:");

        return new Grade(new GradeId(homeworkId, studentId), null, professorId, givenGrade, homeworkId, feedback);
    }

    private Professor readProfessor() {
        String familyName = input("Introduceti numele profesorului:");
        String firstName = input("Introduceti prenumele profesorului:");
        String email = input("Introduceti emailul profesorului:");

        return new Professor(familyName, firstName, email);
    }
}
