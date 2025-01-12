package dev.aj.hibernate_jpa.repositories.impl;

import dev.aj.hibernate_jpa.entities.Student;
import dev.aj.hibernate_jpa.repositories.StudentDao;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class StudentDaoImpl implements StudentDao {

    private final EntityManager entityManager;

    @Override
    @Transactional
    public Student save(Student student) {
        entityManager.persist(student);
        return student;
    }

    @Override
    @Transactional
    public List<Student> saveAllStudents(List<Student> students) {
        students.forEach(student -> entityManager.persist(student));
        return students;
    }

    @Override
    public List<Student> findAll() {
        return entityManager.createQuery("from Student", Student.class)
                .getResultList();
    }

    @Override
    public Student findById(Long id) {
        return entityManager.find(Student.class, id);
    }

    @Override
    public Student findByLastName(String lastName) {
        return entityManager.createQuery("select s from Student s where s.lastName=:lastName", Student.class)
                .setParameter("lastName", lastName)
                .getResultList().stream().findFirst().orElse(null);
    }

    @Override
    @Transactional
    public int deleteAllStudents() {
       int numberOdDeletedStudents = entityManager.createQuery("delete from Student")
                .executeUpdate();
       return numberOdDeletedStudents;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        entityManager.createQuery("delete from Student s where s.id=:id")
                .setParameter("id", id)
                .executeUpdate();
    }

    @Override
    public Student findByEmail(String email) {
        return entityManager.createQuery("select s from Student s where s.email=:email", Student.class)
                .setParameter("email", email)
                .getSingleResult();
    }

    @Override
    public Student findByLastNameOrEmailLike(String partialLastName, String partialEmail) {
        return entityManager.createQuery("select s from Student s where s.lastName like concat('%', :lastName, '%') or s.email like concat('%', :email, '%')", Student.class)
                .setParameter("email", partialEmail)
                .setParameter("lastName", partialLastName)
                .getResultList().stream().findFirst().orElse(null);
    }

    @Override
    @Transactional
    public Student updateStudent(Student existingStudent) {
        return entityManager.merge(existingStudent);
    }
}
