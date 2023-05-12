package org.example.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

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
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class IncrementalIntegration {
    private StudentXMLRepository realStudentRepository;
    private StudentXMLRepository mockStudentRepository;

    private TemaXMLRepository realAssignmentRepository;
    private TemaXMLRepository mockAssignmentRepository;
    private NotaXMLRepository realGradeRepository;
    private NotaXMLRepository mockGradeRepository;

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

    @After
    public void tearDown() {
        new File("students.xml").delete();
        new File("assignments.xml").delete();
        new File("grades.xml").delete();
    }

    @Before
    public void setup() throws IOException {
        mockStudentRepository = mock(StudentXMLRepository.class);
        mockAssignmentRepository = mock(TemaXMLRepository.class);
        mockGradeRepository = mock(NotaXMLRepository.class);

        File studentFile = createXMLFile("students.xml");
        File temeFile = createXMLFile("assignments.xml");
        File noteFile = createXMLFile("grades.xml");

        realStudentRepository = new StudentXMLRepository(new StudentValidator(), studentFile.getPath());
        realAssignmentRepository = new TemaXMLRepository(new TemaValidator(), temeFile.getPath());
        realGradeRepository = new NotaXMLRepository(new NotaValidator(), noteFile.getPath());
    }

    @Test
    public void addStudent_DB_Success() throws IOException {
        service = new Service(this.realStudentRepository, this.mockAssignmentRepository, this.mockGradeRepository);

        assertDoesNotThrow(() -> service.saveStudent("1", "IONEL", 935));

        StudentXMLRepository studentRepo = new StudentXMLRepository(new StudentValidator(), "STUDENTS.xml");
        Iterable<Student> students = studentRepo.findAll();
        ArrayList<Student> studentList = new ArrayList<>();
        students.forEach(studentList::add);

        assertEquals(studentList.size(), 1);
        assertEquals(studentList.get(0).getID(), "1");
        assertEquals(studentList.get(0).getNume(), "IONEL");
        assertEquals(studentList.get(0).getGrupa(), 935);
    }

    @Test
    public void addAssignment_Integration() {
        service = new Service(this.realStudentRepository, this.realAssignmentRepository, this.mockGradeRepository);

        assertDoesNotThrow(() -> service.saveStudent("1", "IONEL", 935));
        assertDoesNotThrow(() -> service.saveTema("1", "DESCRIPTION", 12, 1));

        StudentXMLRepository studentRepo = new StudentXMLRepository(new StudentValidator(), "STUDENTS.xml");
        Iterable<Student> students = studentRepo.findAll();
        ArrayList<Student> studentList = new ArrayList<>();
        students.forEach(studentList::add);

        TemaXMLRepository assignmentRepo = new TemaXMLRepository(new TemaValidator(), "ASSIGNMENTS.xml");
        Iterable<Tema> assignments = assignmentRepo.findAll();
        ArrayList<Tema> assignmentList = new ArrayList<>();
        assignments.forEach(assignmentList::add);

        assertEquals(studentList.size(), 1);
        assertEquals(studentList.get(0).getID(), "1");
        assertEquals(studentList.get(0).getNume(), "IONEL");
        assertEquals(studentList.get(0).getGrupa(), 935);

        assertEquals(assignmentList.size(), 1);
        assertEquals(assignmentList.get(0).getID(), "1");
        assertEquals(assignmentList.get(0).getDescriere(), "DESCRIPTION");
        assertEquals(assignmentList.get(0).getDeadline(), 12);
        assertEquals(assignmentList.get(0).getStartline(), 1);
    }

    @Test
    public void addGrade_Integration() {
        service = new Service(this.mockStudentRepository, this.mockAssignmentRepository, this.realGradeRepository);

        Student student = new Student("1", "IONEL", 935);
        Tema assignment = new Tema("1", "DESCRIPTION", 12, 1);

        when(this.mockStudentRepository.findOne("1")).thenReturn(student);
        when(this.mockAssignmentRepository.findOne("1")).thenReturn(assignment);

        assertDoesNotThrow(() -> service.saveNota("1", "1", 6, 11, "SLAB"));

        NotaXMLRepository gradeRepo = new NotaXMLRepository(new NotaValidator(), "GRADES.xml");
        Iterable<Nota> grades = gradeRepo.findAll();
        ArrayList<Nota> gradeList = new ArrayList<>();
        grades.forEach(gradeList::add);

        assertEquals(gradeList.size(), 1);
        assertEquals(gradeList.get(0).getID(), new Pair<>("1", "1"));
        assertEquals(gradeList.get(0).getNota(), 8.5);
        assertEquals(gradeList.get(0).getSaptamanaPredare(), 11);
        assertEquals(gradeList.get(0).getFeedback(), "SLAB");
    }
}