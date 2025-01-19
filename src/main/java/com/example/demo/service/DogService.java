package com.example.demo.service;

import com.example.demo.entity.animal.Dog;
import com.example.demo.repository.animal.DogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
public class DogService {

    private final DogRepository dogRepository;

    public DogService(DogRepository dogRepository) {
        this.dogRepository = dogRepository;
    }

    public List<Dog> getDogs() {
        log.info("[TUNASERVICE] Transaction read-only: {}, synchronization active: {}",
                TransactionSynchronizationManager.isCurrentTransactionReadOnly(),
                TransactionSynchronizationManager.isSynchronizationActive());
        log.info("[TUNASERVICE] Calling DogService.getDogs()");
        log.info("[TUNASERVICE] Is transaction synchronization active? {}", TransactionSynchronizationManager.isSynchronizationActive());
        boolean readOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
        log.info("[TUNASERVICE] what is the type? {}", readOnly);
        return dogRepository.findAll();
    }

    @Transactional
    public Dog saveDog(Dog dog) {
        return dogRepository.save(dog);
    }
}

