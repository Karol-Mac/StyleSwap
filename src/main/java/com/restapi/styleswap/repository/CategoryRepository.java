package com.restapi.styleswap.repository;

import com.restapi.styleswap.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {}