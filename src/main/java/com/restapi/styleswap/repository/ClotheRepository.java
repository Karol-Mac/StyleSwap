package com.restapi.styleswap.repository;

import com.restapi.styleswap.entity.Clothe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClotheRepository extends JpaRepository<Clothe, Long> {

    Page<Clothe> findByCategoryIdAndIsAvailableTrue(long categoryId, Pageable pageable);

    Page<Clothe> findByUserId(long userId, Pageable pageable);

    List<Clothe> findByUserEmail(String email);
    Page<Clothe> findByUserEmail(String email, Pageable pageable);
}
