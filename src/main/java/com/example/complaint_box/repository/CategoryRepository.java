package com.example.complaint_box.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.complaint_box.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
