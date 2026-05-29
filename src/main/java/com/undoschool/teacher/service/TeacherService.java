package com.undoschool.teacher.service;

import com.undoschool.teacher.dto.CreateTeacherRequest;
import com.undoschool.teacher.entity.Teacher;
import com.undoschool.teacher.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepo;

    @Transactional
    public Teacher createTeacher(CreateTeacherRequest req) {
        Teacher teacher = Teacher.builder()
                .name(req.getName())
                .email(req.getEmail())
                .timezone(req.getTimezone())
                .build();
        return teacherRepo.save(teacher);
    }
}
