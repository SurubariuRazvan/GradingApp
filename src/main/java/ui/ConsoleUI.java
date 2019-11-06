package ui;

import domain.*;
import serviceManager.ServiceManager;
import validation.ValidationException;

import java.util.Scanner;

public class ConsoleUI {
    private ServiceManager service = new ServiceManager();

    private String input(String prompt) {
        Scanner input = new Scanner(System.in);
        System.out.println(prompt);
        return input.nextLine();
    }

    private void printMenu() {
        System.out.println(
                " 0 - Printare meniu\n" +
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
                        " x - Exit\n"
        );
    }

    public void run() {
        printMenu();
        String command;
        boolean running = true;
        while (running) {
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
                        System.out.println(service.findAllHomework());
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
                        System.out.println(service.findAllStudent());
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
                        System.out.println(service.findAllGrade());
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
                        System.out.println(service.findAllProfessor());
                        break;
                    default:
                        printMenu();
                        break;
                }
            } catch (ValidationException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void saveHomework() {
        service.saveHomework(readHomework());
    }

    private void saveStudent() {
        service.saveStudent(readStudent());
    }

    private void saveGrade() {
        Grade grade = readGrade();
        Integer lateProfessor = Integer.parseInt(input("Introduteti numarul de saptamani intarziate de cand trebuia trecuta nota:"));
        service.saveGrade(grade, lateProfessor);
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
        Integer lateProfessor = Integer.parseInt(input("Introduteti numarul de saptamani intarziate de cand trebuia trecuta nota:"));
        GradeId id = new GradeId(homeworkId, studentId);
        Grade g = readGrade();
        service.updateGrade(id, g, lateProfessor);
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
