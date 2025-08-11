package com.example.notefolio.schedule.repository;

import com.example.notefolio.schedule.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}