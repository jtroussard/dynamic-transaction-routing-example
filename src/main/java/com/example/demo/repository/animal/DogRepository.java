package com.example.demo.repository.animal;

import com.example.demo.entity.animal.Dog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DogRepository extends JpaRepository<Dog, Long> {
}

