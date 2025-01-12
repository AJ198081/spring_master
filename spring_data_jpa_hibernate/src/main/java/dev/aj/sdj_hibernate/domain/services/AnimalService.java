package dev.aj.sdj_hibernate.domain.services;

import dev.aj.sdj_hibernate.domain.entities.Animal;
import dev.aj.sdj_hibernate.domain.entities.Cat;
import dev.aj.sdj_hibernate.domain.entities.Dog;
import dev.aj.sdj_hibernate.domain.repositories.CatRepository;
import dev.aj.sdj_hibernate.domain.repositories.DogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnimalService {

    private final DogRepository dogRepository;
    private final CatRepository catRepository;

    public Animal saveAnAnimal(Animal animal) {
        if (animal instanceof Dog) {
            return dogRepository.save((Dog) animal);
        } else if (animal instanceof Cat) {
            return catRepository.save((Cat) animal);
        } else {
            return null;
        }
    }
}
