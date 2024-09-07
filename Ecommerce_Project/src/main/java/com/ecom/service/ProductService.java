package com.ecom.service;


import java.util.List;

import com.ecom.model.Product;

public interface ProductService {

	public Product saveProduct(Product product);
	
	public List<Product> getAllProduct();
	
	public Boolean deleteProduct(int id);
	
	public Product getProductById(int id);
	
	public List<Product> getAllActiveProduct(String category);
}
