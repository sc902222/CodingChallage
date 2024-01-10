package com.example.demo.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Product;
import com.example.demo.model.Queue;
import com.example.demo.request.ProductReqest;
import com.example.demo.service.ProductService;

@RestController
@RequestMapping(value = "/api/products")
public class ProductController {

	@Autowired
	private ProductService productService;

	@GetMapping(value = "")
	public List<Product> getProduct() {
		return productService.getProducts();
	}

	@GetMapping(value = "/search")
	public List<Product> searchProduct(@RequestParam(name = "productName", required = false) String productName,
			@RequestParam(name = "minPrice", required = false) Integer minPrice,
			@RequestParam(name = "maxPrice", required = false) Integer maxPrice,
			@RequestParam(name = "minPostedDate", required = false) LocalDate minPostedDate,
			@RequestParam(name = "maxPostedDate", required = false) LocalDate maxPostedDate) {
		return productService.searchProducts(productName, minPrice, maxPrice, minPostedDate, maxPostedDate);
	}

	@PostMapping(value = "")
	public Product addProduct(@RequestBody ProductReqest productRequest) {
		return productService.addProduct(productRequest);
	}

	@PutMapping(value = "/{productId}")
	public Product updateProduct(@PathVariable(name = "productId") Long productId, @RequestBody ProductReqest productRequest) {
		return productService.updateProduct(productId, productRequest);
	}

	@DeleteMapping(value = "/{productId}")
	public Map<String, String> deleteProduct(@PathVariable(name = "productId") Long productId) {
		return productService.deleteProduct(productId);
	}
	
	@GetMapping(value = "/approval-queue")
	public List<Queue> getApproveQueueProducts() {
		return productService.getApproveQueueProducts();
	}
	
	@PutMapping(value = "/approval-queue/{approvalId}/approve")
	public Map<String,String> approveQueueProductById(@PathVariable(name = "approvalId") Long approveId) {
		return productService.approveQueueProductById(approveId);
	}
	
	@PutMapping(value = "/approval-queue/{approvalId}/reject")
	public Map<String,String> rejectQueueProductById(@PathVariable(name = "approvalId") Long approveId) {
		return productService.approveQueueProductById(approveId);
	}

}
