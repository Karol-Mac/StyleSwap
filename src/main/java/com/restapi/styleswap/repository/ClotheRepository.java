package com.restapi.styleswap.repository;

import com.restapi.styleswap.entity.Clothe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ClotheRepository extends JpaRepository<Clothe, Long> {

    Page<Clothe> findByCategoryId(long categoryId, Pageable pageable);

    Page<Clothe> findByUserId(long userId, Pageable pageable);

    Page<Clothe> findByUserEmail(String email, Pageable pageable);

    boolean existsByIdAndUserEmail(long id, String email);
}
