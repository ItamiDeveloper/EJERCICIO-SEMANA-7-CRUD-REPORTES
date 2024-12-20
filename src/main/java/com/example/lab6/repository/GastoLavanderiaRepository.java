package com.example.lab6.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.lab6.model.GastoLavanderia;

@Repository
public interface GastoLavanderiaRepository extends JpaRepository<GastoLavanderia, Integer> {
}
