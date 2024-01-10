package com.example.demo.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "Queue")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Queue {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long qid;

	private Long pid;

	private String name;

	private Double price;

	private String status;

	private LocalDateTime date;

}
