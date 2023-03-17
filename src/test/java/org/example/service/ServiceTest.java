package org.example.service;

import org.example.domain.Student;
import org.example.repository.NotaXMLRepository;
import org.example.repository.StudentXMLRepository;
import org.example.repository.TemaXMLRepository;
import org.example.validation.NotaValidator;
import org.example.validation.StudentValidator;
import org.example.validation.TemaValidator;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
class ServiceTest {
    Service service;
    @BeforeEach
    protected void setUp() {
        StudentXMLRepository fileRepository1 = new StudentXMLRepository(new StudentValidator(), "studenti.xml");
        TemaXMLRepository fileRepository2 = new TemaXMLRepository(new TemaValidator(), "teme.xml");
        NotaXMLRepository fileRepository3 = new NotaXMLRepository(new NotaValidator(), "note.xml");

        this.service = new Service(fileRepository1, fileRepository2, fileRepository3);
    }

    int getIterableSize(Iterable<Student> it) {
        int size = 0;

        for (Student s : this.service.findAllStudents()) {
            size++;
        }

        return size;
    }
    @org.junit.jupiter.api.Test
    void saveStudentLowerGroup() {
        int oldSize = getIterableSize(this.service.findAllStudents());
        assertEquals(1, this.service.saveStudent("1", "gigi", 1));
        int newSize = getIterableSize(this.service.findAllStudents());

        assertEquals(oldSize, newSize);
    }

    @org.junit.jupiter.api.Test
    void saveStudentUpperGroup() {
        int oldSize = getIterableSize(this.service.findAllStudents());
        assertEquals(1, this.service.saveStudent("1", "gigi", 1000));
        int newSize = getIterableSize(this.service.findAllStudents());

        assertEquals(oldSize, newSize);
    }

    @org.junit.jupiter.api.Test
    void saveStudentCorrectGroup() {
        int oldSize = getIterableSize(this.service.findAllStudents());
        assertEquals(0, this.service.saveStudent("1", "gigi", 333));
        int newSize = getIterableSize(this.service.findAllStudents());

        assertEquals(oldSize, newSize);
    }
}