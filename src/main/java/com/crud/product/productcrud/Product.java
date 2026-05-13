	package com.crud.product.productcrud;
	
	import jakarta.persistence.Entity;
	import jakarta.persistence.GeneratedValue;
	import jakarta.persistence.GenerationType;
	import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
	
	@Entity
	public class Product {
	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;
	    @NotBlank(message = "Product Name is mandatory")
	    @Size(min = 4, message = "Product Name should have at least 8 characters")
	    private String productName;
	    private String productQuantity;
	    private int productPrice;
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public String getProductName() {
			return productName;
		}
		
		public void setProductName(String productName) {
			this.productName = productName;
		}
		public void getProductQuantity(String productName) {
			this.productName = productName;
		}
		public String getProductQuantity() {
			return productQuantity;
		}
		public void setProductQuantity(String productQuantity) {
			this.productQuantity = productQuantity;
		}
		public int getProductPrice() {
			return productPrice;
		}
		public void setProductPrice(int productPrice) {
			this.productPrice = productPrice;
		}
		
	    
	}
