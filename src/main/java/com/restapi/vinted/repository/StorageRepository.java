package com.restapi.vinted.repository;

import com.restapi.vinted.entity.Storage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StorageRepository extends JpaRepository<Storage, Long> {

    Optional<Storage> findByUserEmail(String email);
}