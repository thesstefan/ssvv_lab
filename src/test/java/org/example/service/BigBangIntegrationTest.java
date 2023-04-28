package org.example.service;

import org.example.domain.Nota;
import org.example.domain.Pair;
import org.example.domain.Student;
import org.example.domain.Tema;
import org.example.repository.NotaXMLRepository;
import org.example.repository.StudentXMLRepository;
import org.example.repository.TemaXMLRepository;
import org.example.validation.NotaValidator;
import org.example.validation.StudentValidator;
import org.example.validation.TemaValidator;
import org.example.validation.ValidationException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;

public class BigBangIntegrationTest {
    private String STUDENTS_PATH = "students.xml";
    private String ASSIGNMENTS_PATH = "assignments.xml";
    private String GRADES_PATH = "grades.xml";


    private Service service;

    private File createXMLFile(String fileName) throws IOException {
        File createdFile = new File(fileName);
        if (createdFile.createNewFile()) {
            try (FileWriter fileWriter = new FileWriter(createdFile)) {
                fileWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<Entitati>\n</Entitati>");
            }
        } else {
            throw new RuntimeException("File " + fileName + " could not be created!");
        }
        return createdFile;
    }

    @Before
    public void setUp() throws IOException {
        File studentFile = createXMLFile(STUDENTS_PATH);
        File temeFile = createXMLFile(ASSIGNMENTS_PATH);
        File noteFile = createXMLFile(GRADES_PATH);

        StudentXMLRepository fileRepository1 = new StudentXMLRepository(new StudentValidator(), studentFile.getPath());
        TemaXMLRepository fileRepository2 = new TemaXMLRepository(new TemaValidator(), temeFile.getPath());
        NotaXMLRepository fileRepository3 = new NotaXMLRepository(new NotaValidator(), noteFile.getPath());
        service = new Service(fileRepository1, fileRepository2, fileRepository3);
    }

    @After
    public void tearDown() {
        new File("students.xml").delete();
        new File("assignments.xml").delete();
        new File("grades.xml").delete();
    }

    @Test
    public void addStudent_DB_Success() {
        assertDoesNotThrow(() -> service.saveStudent("1", "IONEL", 935));

        StudentXMLRepository studentRepo = new StudentXMLRepository(new StudentValidator(), STUDENTS_PATH);
        Iterable<Student> students = studentRepo.findAll();
        ArrayList<Student> studentList = new ArrayList<>();
        students.forEach(studentList::add);

        assertEquals(studentList.size(), 1);
        assertEquals(studentList.get(0).getID(), "1");
        assertEquals(studentList.get(0).getNume(), "IONEL");
        assertEquals(studentList.get(0).getGrupa(), 935);
    }

    @Test
    public void addAssignment_DB_Success() {
        assertDoesNotThrow(() -> service.saveTema("1", "DESCRIPTION", 12, 1));

        TemaXMLRepository assignmentRepo = new TemaXMLRepository(new TemaValidator(), ASSIGNMENTS_PATH);

        Iterable<Tema> assignments = assignmentRepo.findAll();
        ArrayList<Tema> assignmentList = new ArrayList<>();
        assignments.forEach(assignmentList::add);

        assertEquals(assignmentList.size(), 1);
        assertEquals(assignmentList.get(0).getID(), "1");
        assertEquals(assignmentList.get(0).getDescriere(), "DESCRIPTION");
        assertEquals(assignmentList.get(0).getDeadline(), 12);
        assertEquals(assignmentList.get(0).getStartline(), 1);
    }

    @Test
    public void addGrade_DB_Nonexistent_Fail() {
        assertThrows(RuntimeException.class, () -> service.saveNota("1", "1", 10, 5, "SLAB"));
    }

    @Test
    public void addAll_DB_Success() {
        assertDoesNotThrow(() -> service.saveStudent("1", "IONEL", 935));
        assertDoesNotThrow(() -> service.saveTema("1", "DESCRIPTION", 12, 1));
        assertDoesNotThrow(() -> service.saveNota("1", "1", 6, 11, "SLAB"));

        StudentXMLRepository studentRepo = new StudentXMLRepository(new StudentValidator(), STUDENTS_PATH);
        Iterable<Student> students = studentRepo.findAll();
        ArrayList<Student> studentList = new ArrayList<>();
        students.forEach(studentList::add);

        TemaXMLRepository assignmentRepo = new TemaXMLRepository(new TemaValidator(), ASSIGNMENTS_PATH);
        Iterable<Tema> assignments = assignmentRepo.findAll();
        ArrayList<Tema> assignmentList = new ArrayList<>();
        assignments.forEach(assignmentList::add);

        NotaXMLRepository gradeRepo = new NotaXMLRepository(new NotaValidator(), GRADES_PATH);
        Iterable<Nota> grades = gradeRepo.findAll();
        ArrayList<Nota> gradeList = new ArrayList<>();
        grades.forEach(gradeList::add);

        assertEquals(studentList.size(), 1);
        assertEquals(studentList.get(0).getID(), "1");
        assertEquals(studentList.get(0).getNume(), "IONEL");
        assertEquals(studentList.get(0).getGrupa(), 935);

        assertEquals(assignmentList.size(), 1);
        assertEquals(assignmentList.get(0).getID(), "1");
        assertEquals(assignmentList.get(0).getDescriere(), "DESCRIPTION");
        assertEquals(assignmentList.get(0).getDeadline(), 12);
        assertEquals(assignmentList.get(0).getStartline(), 1);

        assertEquals(gradeList.size(), 1);
        assertEquals(gradeList.get(0).getID(), new Pair<>("1", "1"));
        assertEquals(gradeList.get(0).getNota(), 8.5);
        assertEquals(gradeList.get(0).getSaptamanaPredare(), 11);
        assertEquals(gradeList.get(0).getFeedback(), "SLAB");
    }
}
