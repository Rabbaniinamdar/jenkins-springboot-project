package com.crud.product.productcrud;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/product")

public class ProductController {
	@Autowired
	private ProductService Productervice;

	@GetMapping
	public List<Product> getAllProduct() {
		return Productervice.getAllProduct();
	}

	@PostMapping
	public ResponseEntity<Product> addProduct(@Valid @RequestBody Product product) {
		System.out.println("1k1kg"+product.getProductName());
		return ResponseEntity.ok(Productervice.addProduct(product));
	}


	@GetMapping("/{productId}")
	public ResponseEntity<Product> getProductById(@PathVariable("productId") Long ProductId) throws Exception {
		Optional<Product> Product = Productervice.getProductById(ProductId);
		return Product.map(ResponseEntity::ok).orElseThrow(() -> new UserNotFoundException("No user by ID: " + ProductId));
	}

	@DeleteMapping("/{productId}")
	public ResponseEntity<String> deleteProduct(@PathVariable("productId") Long ProductId) {
		Productervice.deleteProduct(ProductId);
		return ResponseEntity.ok("Deleted");
	}

	@PutMapping("/{productId}")
	public ResponseEntity<String> updateProduct(@PathVariable("productId") Long ProductId, @RequestBody Product updatedProduct) {
		
		Product Product = Productervice.updateProduct(ProductId, updatedProduct);
		return Product != null ? ResponseEntity.ok("Product Updated Succesfullly") : ResponseEntity.notFound().build();
	}
}