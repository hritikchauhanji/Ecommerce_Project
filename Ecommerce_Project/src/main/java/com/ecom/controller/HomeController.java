package com.ecom.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Category;
import com.ecom.model.Product;
import com.ecom.model.UserDtls;
import com.ecom.service.CategoryService;
import com.ecom.service.ProductService;
import com.ecom.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {
	
	@Autowired
	private CategoryService categoryService;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private UserService userService;
	
	@ModelAttribute
	public void getUserDetails(Principal p, Model m) {
		if (p!=null) {
			String email = p.getName();
			UserDtls userDtls = userService.getUserByEmail(email);
			m.addAttribute("user", userDtls);
		}
		
		List<Category> allActiveCategories = categoryService.getAllActiveCategories();
		m.addAttribute("categories", allActiveCategories);
	}

	@GetMapping("/")
	public String index() {
		return "index";
	}
	
	@GetMapping("/signin")
	public String login() {
		return "login";
	}
	
	@GetMapping("/register")
	public String register() {
		return "register";
	}
	
	@GetMapping("/products")
	public String products(Model m, @RequestParam(value = "category", defaultValue = "") String category) {
		m.addAttribute("categories", categoryService.getAllActiveCategories());
		m.addAttribute("products", productService.getAllActiveProduct(category));
		m.addAttribute("paramValue", category);
		return "products";
	}
	
	@GetMapping("/view_product/{id}")
	public String view_product(@PathVariable int id, Model m) {
		m.addAttribute("product", productService.getProductById(id));
		return "view_product";
	}
	
	@PostMapping("/saveUser")
	public String saveUser(@ModelAttribute UserDtls user, @RequestParam("file") MultipartFile file, HttpSession session) throws IOException {
		
		String imageName = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();
		user.setProfileImage(imageName);
		UserDtls saveUser = userService.saveUser(user);
		
		if (!ObjectUtils.isEmpty(saveUser)) {
			if (!ObjectUtils.isEmpty(imageName)) {
				
				File saveFile = new ClassPathResource("static/img").getFile();
				
				 Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_img" + File.separator + file.getOriginalFilename());
				
				 System.out.println(path);
				 
				 Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				
			}
			session.setAttribute("succMsg", "Register Saved..");
		} else {
			session.setAttribute("errorMsg", "Something is wrong on server");
		}
		return "redirect:/register";
	}
}
