package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.example.demo.model.Product;
import com.example.demo.model.Queue;

public interface QueueRepository extends JpaRepository<Queue, Long>, JpaSpecificationExecutor<Product> {

	List<Queue> findAllByOrderByDateDesc();

}
