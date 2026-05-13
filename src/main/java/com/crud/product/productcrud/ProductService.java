package com.crud.product.productcrud;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
	@Autowired
	private ProductRepository ProdRepository;


	public List<Product> getAllProduct() {
		return ProdRepository.findAll();
	}

	public Optional<Product> getProductById(Long id) {
		return ProdRepository.findById(id);
	}

	public Product addProduct(Product Prod) {
		return ProdRepository.save(Prod);
	}

	public Product updateProduct(Long ProdId, Product updatedProd) {
		
		Product existingProd = ProdRepository.findById(ProdId).orElse(null);
		System.out.println("Prod update "+existingProd);
		if (existingProd != null) {
			existingProd.setProductName(updatedProd.getProductName());
			existingProd.setProductQuantity(updatedProd.getProductQuantity());
			existingProd.setProductPrice(updatedProd.getProductPrice());
			return ProdRepository.save(existingProd);
		}
		return null;
	}

	public void deleteProduct(Long id) {
		ProdRepository.deleteById(id);
	}
}
