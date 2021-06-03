package com.elearning.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.elearning.repository.CourseRepository;


@RestController
public class CourseController {

    @Autowired
    CourseRepository courseRepository;

    @GetMapping("course/{hsk}/{lesson}")
    public String greeting() {
    	
        return courseRepository.findAll().toString();
    }

}
