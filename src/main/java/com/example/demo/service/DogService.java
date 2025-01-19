package com.example.demo.service;

import com.example.demo.config.RoutingContext;
import com.example.demo.entity.Dog;
import com.example.demo.repository.DogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DogService {

    private final DogRepository dogRepository;

    public DogService(DogRepository dogRepository) {
        this.dogRepository = dogRepository;
    }

    public List<Dog> getDogs() {
        RoutingContext.setDataSourceKey("SECONDARY");
        try {
            return dogRepository.findAll();
        } finally {
            RoutingContext.clear();
        }
    }

    public Dog saveDog(Dog dog) {
        RoutingContext.setDataSourceKey("PRIMARY");
        try {
            return dogRepository.save(dog);
        } finally {
            RoutingContext.clear();
        }
    }
}

