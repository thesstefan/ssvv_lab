package org.example.service;

import org.example.domain.*;
import org.example.repository.*;
import org.example.validation.AlreadyExistsException;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Locale;

public class Service {
    private StudentXMLRepository studentXmlRepo;
    private TemaXMLRepository temaXmlRepo;
    private NotaXMLRepository notaXmlRepo;

    public Service(StudentXMLRepository studentXmlRepo, TemaXMLRepository temaXmlRepo, NotaXMLRepository notaXmlRepo) {
        this.studentXmlRepo = studentXmlRepo;
        this.temaXmlRepo = temaXmlRepo;
        this.notaXmlRepo = notaXmlRepo;
    }

    public Iterable<Student> findAllStudents() { return studentXmlRepo.findAll(); }

    public Iterable<Tema> findAllTeme() { return temaXmlRepo.findAll(); }

    public Iterable<Nota> findAllNote() { return notaXmlRepo.findAll(); }

    public void saveStudent(String id, String nume, int grupa) throws AlreadyExistsException {
        Student student = new Student(id, nume, grupa);
        studentXmlRepo.save(student);
    }

    public void saveTema(String id, String descriere, int deadline, int startline) throws AlreadyExistsException {
        Tema tema = new Tema(id, descriere, deadline, startline);
        temaXmlRepo.save(tema);
    }

    public void saveNota(String idStudent, String idTema, double valNota, int predata, String feedback) throws AlreadyExistsException {
        if (studentXmlRepo.findOne(idStudent) == null || temaXmlRepo.findOne(idTema) == null) {
            throw new RuntimeException("Student sau tema invalida!");
        }
        else {
            int deadline = temaXmlRepo.findOne(idTema).getDeadline();

            if (predata - deadline > 2) {
                valNota =  1;
            } else {
                valNota =  valNota - 2.5 * (predata - deadline);
            }
            Nota nota = new Nota(new Pair(idStudent, idTema), valNota, predata, feedback);
            notaXmlRepo.save(nota);
        }
    }

    public int deleteStudent(String id) {
        Student result = studentXmlRepo.delete(id);

        if (result == null) {
            return 0;
        }
        return 1;
    }

    public int deleteTema(String id) {
        Tema result = temaXmlRepo.delete(id);

        if (result == null) {
            return 0;
        }
        return 1;
    }

    public int updateStudent(String id, String numeNou, int grupaNoua) {
        Student studentNou = new Student(id, numeNou, grupaNoua);
        Student result = studentXmlRepo.update(studentNou);

        if (result == null) {
            return 0;
        }
        return 1;
    }

    public int updateTema(String id, String descriereNoua, int deadlineNou, int startlineNou) {
        Tema temaNoua = new Tema(id, descriereNoua, deadlineNou, startlineNou);
        Tema result = temaXmlRepo.update(temaNoua);

        if (result == null) {
            return 0;
        }
        return 1;
    }

    public int extendDeadline(String id, int noWeeks) {
        Tema tema = temaXmlRepo.findOne(id);

        if (tema != null) {
            LocalDate date = LocalDate.now();
            WeekFields weekFields = WeekFields.of(Locale.getDefault());
            int currentWeek = date.get(weekFields.weekOfWeekBasedYear());

            if (currentWeek >= 39) {
                currentWeek = currentWeek - 39;
            } else {
                currentWeek = currentWeek + 12;
            }

            if (currentWeek <= tema.getDeadline()) {
                int deadlineNou = tema.getDeadline() + noWeeks;
                return updateTema(tema.getID(), tema.getDescriere(), deadlineNou, tema.getStartline());
            }
        }
        return 0;
    }

    public void createStudentFile(String idStudent, String idTema) {
        Nota nota = notaXmlRepo.findOne(new Pair(idStudent, idTema));

        notaXmlRepo.createFile(nota);
    }
}
