package com.classes.services;

import com.classes.dtos.Class.ClassRequest;
import com.classes.dtos.Class.ClassResponse;

import java.util.List;
import java.util.UUID;

public interface ClassService {


    ClassResponse createClass(ClassRequest request);

    List<ClassResponse> findAll();

    ClassResponse updateClass(UUID id, ClassRequest request);

    void deleteClass(UUID id);
}
