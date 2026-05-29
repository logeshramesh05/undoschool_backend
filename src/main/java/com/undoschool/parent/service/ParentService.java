package com.undoschool.parent.service;

import com.undoschool.parent.dto.CreateParentRequest;
import com.undoschool.parent.entity.Parent;
import com.undoschool.parent.repository.ParentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ParentService {

    private final ParentRepository parentRepo;

    @Transactional
    public Parent createParent(CreateParentRequest req) {
        Parent parent = Parent.builder()
                .name(req.getName())
                .email(req.getEmail())
                .timezone(req.getTimezone())
                .build();
        return parentRepo.save(parent);
    }
}
