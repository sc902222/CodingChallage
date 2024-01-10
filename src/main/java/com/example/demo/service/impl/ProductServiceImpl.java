package com.example.demo.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.demo.model.Product;
import com.example.demo.model.Queue;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.QueueRepository;
import com.example.demo.request.ProductReqest;
import com.example.demo.service.ProductService;
import com.example.demo.util.Constants;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class ProductServiceImpl implements ProductService {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private QueueRepository queueRepository;

	@Override
	public List<Product> getProducts() {
		return productRepository.findAllByOrderByCreatedDateDesc();
	}

	@Override
	public List<Product> searchProducts(String productName, Integer minPrice, Integer maxPrice, LocalDate minPostedDate,
			LocalDate maxPostedDate) {
		if (Objects.nonNull(maxPostedDate) && Objects.nonNull(minPostedDate) && maxPostedDate.isBefore(minPostedDate))
			throw new RuntimeException("Invalid date");

		return productRepository.findAll(new Specification<Product>() {

			@Override
			public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
				List<Predicate> predicates = new ArrayList<>();
				if (Objects.nonNull(productName) && !productName.isBlank()) {
					predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("name"), productName)));
				}

				if (Objects.nonNull(maxPrice)) {
					predicates.add(criteriaBuilder
							.and(criteriaBuilder.lessThanOrEqualTo(root.get(Constants.PRICE), maxPrice)));
				}

				if (Objects.nonNull(minPrice)) {
					predicates.add(criteriaBuilder
							.and(criteriaBuilder.greaterThanOrEqualTo(root.get(Constants.PRICE), minPrice)));
				}

				if (Objects.nonNull(minPostedDate)) {
					predicates.add(criteriaBuilder.and(
							criteriaBuilder.greaterThanOrEqualTo(root.get(Constants.CREATED_DATE), minPostedDate)));
				}

				if (Objects.nonNull(maxPostedDate)) {
					predicates.add(criteriaBuilder
							.and(criteriaBuilder.lessThanOrEqualTo(root.get(Constants.CREATED_DATE), maxPostedDate)));
				}

				return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
			}
		});
	}

	@Override
	public Product addProduct(ProductReqest productRequest) {

		if (productRequest.price() > 10000)
			throw new RuntimeException("can not add product price is more than 10000");

		Product newProduct = new Product();
		newProduct.setName(productRequest.name());
		newProduct.setPrice(productRequest.price());
		newProduct.setCreatedDate(LocalDateTime.now());
		newProduct.setStatus(productRequest.status());

		if (productRequest.price() > 5000) {
			Queue queue = new Queue(null, null, productRequest.name(), productRequest.price(), 
					Constants.CREATE, LocalDateTime.now());
			queueRepository.save(queue);
			throw new RuntimeException("Product pushed to Queue for Approve");
		}
		return productRepository.save(newProduct);
	}

	@Override
	public Product updateProduct(Long productId, ProductReqest productRequest) {
		Product newProduct = null;
		Optional<Product> product = productRepository.findById(productId);

		if (product.isPresent()) {
			newProduct = product.get();
			if (productRequest.price() > (2 * product.get().getPrice())) {
				Queue queue = new Queue(null, productId, productRequest.name(), productRequest.price(),
						Constants.UPDATE, LocalDateTime.now());
				queueRepository.save(queue);
				productRepository.delete(newProduct);
				throw new RuntimeException("Product pushed to Queue for Approve");
			}
			newProduct.setName(productRequest.name());
			newProduct.setPrice(productRequest.price());
			productRepository.save(newProduct);
		}
		return newProduct;
	}

	@Override
	public Map<String, String> deleteProduct(Long productId) {
		Optional<Product> productObj = productRepository.findById(productId);
		if (productObj.isEmpty())
			throw new RuntimeException("Product not found");

		Product product = productObj.get();
		productRepository.delete(product);

		Queue queue = new Queue(null, productId, product.getName(), product.getPrice(), Constants.DELETE,
				LocalDateTime.now());

		queueRepository.save(queue);
		return Map.of(Constants.MESSAGE, "Product pushed for delete successfully");
	}

	@Override
	public List<Queue> getApproveQueueProducts() {
		return queueRepository.findAllByOrderByDateDesc();
	}

	@Override
	public Map<String, String> approveQueueProductById(Long approveId) {

		Optional<Queue> queue = queueRepository.findById(approveId);
		if (queue.isPresent()) {
			Queue queueObj = queue.get();
			if (Constants.UPDATE.equals(queueObj.getStatus()) || Constants.CREATE.equals(queueObj.getStatus())) {
				Product product = new Product(queueObj.getPid(), queueObj.getName(), queueObj.getPrice(),
						queueObj.getStatus(), LocalDateTime.now());
				productRepository.save(product);
				queueRepository.deleteById(approveId);
			} else if (Constants.DELETE.equals(queueObj.getStatus())) {
				queueRepository.deleteById(approveId);
			} else {
				return Map.of(Constants.MESSAGE, "Invalid operation");
			}

		}
		return Map.of(Constants.MESSAGE, "Approved successfully");
	}

	@Override
	public Map<String, String> rejectQueueProductById(Long approveId) {

		Optional<Queue> queue = queueRepository.findById(approveId);
		if (queue.isPresent()) {
			Queue queueObj = queue.get();
			Product product = new Product(queueObj.getPid(), queueObj.getName(), queueObj.getPrice(),
					queueObj.getStatus(), LocalDateTime.now());
			if (Constants.UPDATE.equals(queueObj.getStatus())) {
				product = new Product(queueObj.getPid(), queueObj.getName(), queueObj.getPrice() / 2,
						queueObj.getStatus(), LocalDateTime.now());
			} else if (Constants.CREATE.equals(queueObj.getStatus())
					|| Constants.DELETE.equals(queueObj.getStatus())) {
			} else {
				return Map.of(Constants.MESSAGE, "Invalid operation");
			}

			productRepository.save(product);
			queueRepository.deleteById(approveId);

		}
		return Map.of(Constants.MESSAGE, "Rejected Opration");
	}

}
