package com.example.demo.controller;

import com.example.demo.entity.animal.Dog;
import com.example.demo.service.DogService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dogs")
public class DogController {

    private final DogService dogService;

    public DogController(DogService dogService) {

        this.dogService = dogService;
        System.out.println("[TUNA] Injected DogService class: " + dogService.getClass().getName());
    }

    @GetMapping
    public List<Dog> getAllDogs() {
        return dogService.getDogs();
    }

    @PostMapping
    public Dog addDog(@RequestBody Dog dog) {
        return dogService.saveDog(dog);
    }
}
