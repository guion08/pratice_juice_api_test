package com.pratice.juicestock.repository;

import com.pratice.juicestock.entity.Juice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JuiceRepository extends JpaRepository<Juice, Long> {

    Optional<Juice> findByName(String name);
}
