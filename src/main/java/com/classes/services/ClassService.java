package com.classes.services;

import com.classes.dtos.ClassDTO;

import java.util.List;
import java.util.UUID;

public interface ClassService {

    ClassDTO createClass(ClassDTO dto);
    List<ClassDTO> findAll();
    ClassDTO updateClass(UUID id, ClassDTO dto);
    void deleteClass(UUID id);
}
