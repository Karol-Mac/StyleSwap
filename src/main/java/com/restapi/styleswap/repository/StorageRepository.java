package com.restapi.styleswap.repository;

import com.restapi.styleswap.entity.Storage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StorageRepository extends JpaRepository<Storage, Long> {

    Optional<Storage> findByUserEmail(String email);
}