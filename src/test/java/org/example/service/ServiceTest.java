package org.example.service;

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
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceTest {
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
        File studentFile = createXMLFile("student.xml");
        File temeFile = createXMLFile("assignments.xml");
        File noteFile = createXMLFile("grades.xml");

        StudentXMLRepository fileRepository1 = new StudentXMLRepository(new StudentValidator(), studentFile.getPath());
        TemaXMLRepository fileRepository2 = new TemaXMLRepository(new TemaValidator(), temeFile.getPath());
        NotaXMLRepository fileRepository3 = new NotaXMLRepository(new NotaValidator(), noteFile.getPath());
        service = new Service(fileRepository1, fileRepository2, fileRepository3);
    }

    @After
    public void tearDown() {
        new File("student.xml").delete();
        new File("assignments.xml").delete();
        new File("grades.xml").delete();
    }

    @Test
    public void addStudent_Fail_nullIdTest() {
        assertThrows(ValidationException.class, () -> service.saveStudent(null, "Banel", 935), "Invalid ID!");
    }

    @Test
    public void addStudent_Fail_emptyIdTest() {
        assertThrows(ValidationException.class, () -> service.saveStudent("", "Banel", 935), "Invalid ID!");
    }

    @Test
    public void addStudent_Fail_nullNameTest() {
        assertThrows(ValidationException.class, () -> service.saveStudent("7", null, 935), "Invalid name!");
    }

    @Test
    public void addStudent_Fail_emptyNameTest() {
        assertThrows(ValidationException.class, () -> service.saveStudent("7", "", 935), "Invalid name!");
    }

    @Test
    public void addStudent_Fail_tooLowGroupTest() {
        assertThrows(ValidationException.class, () -> service.saveStudent("7", "Banel", 110), "Invalid group!");
    }

    @Test
    public void addStudent_Fail_tooHighGroupTest() {
        assertThrows(ValidationException.class, () -> service.saveStudent("7", null, 938), "Invalid group!");
    }

    @Test
    public void addStudent_OK_lowLimitGroupTest() {
        assertDoesNotThrow(() -> service.saveStudent("7", "Banel", 111));
        var addedStudentOptional = StreamSupport.stream(service.findAllStudents().spliterator(), false).filter(student -> student.getID().equals("7")).findFirst();
        assertDoesNotThrow(addedStudentOptional::get);

        var addedStudent = addedStudentOptional.orElse(null);
        assertNotNull(addedStudent);

        assertEquals(addedStudent.getID(), "7");
        assertEquals(addedStudent.getNume(), "Banel");
        assertEquals(addedStudent.getGrupa(), 111);
    }

    @Test
    public void addStudent_OK_lowLimitPlusOneGroupTest() {
        assertDoesNotThrow(() -> service.saveStudent("7", "Banel", 112));
        var addedStudentOptional = StreamSupport.stream(service.findAllStudents().spliterator(), false).filter(student -> student.getID().equals("7")).findFirst();
        assertDoesNotThrow(addedStudentOptional::get);

        var addedStudent = addedStudentOptional.orElse(null);
        assertNotNull(addedStudent);

        assertEquals(addedStudent.getID(), "7");
        assertEquals(addedStudent.getNume(), "Banel");
        assertEquals(addedStudent.getGrupa(), 112);
    }

    @Test
    public void addStudent_OK_upperLimitMinusOneGroupTest() {
        assertDoesNotThrow(() -> service.saveStudent("7", "Banel", 936));
        var addedStudentOptional = StreamSupport.stream(service.findAllStudents().spliterator(), false).filter(student -> student.getID().equals("7")).findFirst();
        assertDoesNotThrow(addedStudentOptional::get);

        var addedStudent = addedStudentOptional.orElse(null);
        assertNotNull(addedStudent);

        assertEquals(addedStudent.getID(), "7");
        assertEquals(addedStudent.getNume(), "Banel");
        assertEquals(addedStudent.getGrupa(), 936);
    }

    @Test
    public void addStudent_OK_upperLimitGroupTest() {
        assertDoesNotThrow(() -> service.saveStudent("7", "Banel", 935));
        var addedStudentOptional = StreamSupport.stream(service.findAllStudents().spliterator(), false).filter(student -> student.getID().equals("7")).findFirst();
        assertDoesNotThrow(addedStudentOptional::get);

        var addedStudent = addedStudentOptional.orElse(null);
        assertNotNull(addedStudent);

        assertEquals(addedStudent.getID(), "7");
        assertEquals(addedStudent.getNume(), "Banel");
        assertEquals(addedStudent.getGrupa(), 935);
    }

    @Test
    public void addStudent_OK_upperIdTest() {
        String testId = "77";

        assertDoesNotThrow(() -> service.saveStudent(testId, "Banel", 935));
        var addedStudentOptional = StreamSupport.stream(service.findAllStudents().spliterator(), false).filter(student -> student.getID().equals(testId)).findFirst();
        assertDoesNotThrow(addedStudentOptional::get);

        var addedStudent = addedStudentOptional.orElse(null);
        assertNotNull(addedStudent);

        assertEquals(addedStudent.getID(), testId);
        assertEquals(addedStudent.getNume(), "Banel");
        assertEquals(addedStudent.getGrupa(), 935);
    }

    @Test
    public void addStudent_OK_oneLetterNameTest() {
        String testName = "A";

        assertDoesNotThrow(() -> service.saveStudent("7", testName, 935));
        var addedStudentOptional = StreamSupport.stream(service.findAllStudents().spliterator(), false).filter(student -> student.getID().equals("7")).findFirst();
        assertDoesNotThrow(addedStudentOptional::get);

        var addedStudent = addedStudentOptional.orElse(null);
        assertNotNull(addedStudent);

        assertEquals(addedStudent.getID(), "7");
        assertEquals(addedStudent.getNume(), testName);
        assertEquals(addedStudent.getGrupa(), 935);
    }

    @Test
    public void addStudent_OK_twoLettersNameTest() {
        String testName = "Ab";

        assertDoesNotThrow(() -> service.saveStudent("7", testName, 935));
        var addedStudentOptional = StreamSupport.stream(service.findAllStudents().spliterator(), false).filter(student -> student.getID().equals("7")).findFirst();
        assertDoesNotThrow(addedStudentOptional::get);

        var addedStudent = addedStudentOptional.orElse(null);
        assertNotNull(addedStudent);

        assertEquals(addedStudent.getID(), "7");
        assertEquals(addedStudent.getNume(), testName);
        assertEquals(addedStudent.getGrupa(), 935);
    }

    @Test
    public void addAssignment_Fail_nullIdTest() {
        assertThrows(ValidationException.class, () -> service.saveTema(null, "Ionut", 3, 5), "Invalid ID!");
    }

    @Test
    public void addAssignment_Fail_nullDescriptionTest() {
        assertThrows(ValidationException.class, () -> service.saveTema("4", null, 3, 5), "Invalid ID!");
    }

    @Test
    public void addAssignment_Fail_emptyIdTest() {
        assertThrows(ValidationException.class, () -> service.saveTema("", "Ionut", 3, 5), "Invalid ID!");
    }

    @Test
    public void addAssignment_Fail_emptyDescriptionTest() {
        assertThrows(ValidationException.class, () -> service.saveTema("4", "", 3, 5), "Invalid description!");
    }

    @Test
    public void addAssignment_smallerDeadlineTest() {
        assertThrows(ValidationException.class, () -> service.saveTema("4", "Ionut", 0, 5), "Invalid deadline!");
    }

    @Test
    public void addAssignment_largerDeadlineTest() {
        assertThrows(ValidationException.class, () -> service.saveTema("4", "Ionut", 15, 5), "Invalid deadline!");
    }

    @Test
    public void addAssignment_smallerStartTest() {
        assertThrows(ValidationException.class, () -> service.saveTema("4", "Ionut", 5, 0), "Invalid start!");
    }

    @Test
    public void addAssignment_largerStartTest() {
        assertThrows(ValidationException.class, () -> service.saveTema("4", "Ionut", 5, 15), "Invalid start!");
    }

    @Test
    public void addAssignment_badDeadlineRange() {
        assertThrows(ValidationException.class, () -> service.saveTema("4", "Ionut", 5, 14), "Invalid deadline!");
    }
}
