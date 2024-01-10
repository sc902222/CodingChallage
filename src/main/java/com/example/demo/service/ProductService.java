package com.example.demo.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.example.demo.model.Product;
import com.example.demo.model.Queue;
import com.example.demo.request.ProductReqest;

public interface ProductService {

	List<Product> getProducts();

	List<Product> searchProducts(String productName, Integer minPrice, Integer maxPrice, LocalDate minPostedDate,
			LocalDate maxPostedDate);

	Product addProduct(ProductReqest productRequest);

	Product updateProduct(Long productId, ProductReqest productRequest);

	Map<String, String> deleteProduct(Long productId);

	List<Queue> getApproveQueueProducts();

	Map<String, String> approveQueueProductById(Long approveId);

	Map<String, String> rejectQueueProductById(Long approveId);
}
