package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.CategoryDao;
import org.yearup.data.ProductDao;
import org.yearup.models.Category;
import org.yearup.models.Product;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping(path = "/categories")
@CrossOrigin
public class CategoriesController
{
	private CategoryDao categoryDao;
	private ProductDao productDao;

	@Autowired
	public CategoriesController(CategoryDao categoryDao, ProductDao productDao)
	{
		this.categoryDao = categoryDao;
		this.productDao = productDao;
	}

	// Get All //

	@GetMapping()
	public List<Category> getAll()
	{
		List<Category> result = new ArrayList<>();

		return categoryDao.getAllCategories();
	}

	// Get By ID //

	@GetMapping("/{id}")
	public ResponseEntity<Category> getById(@PathVariable int id)
	{
			Category categoryById = categoryDao.getById(id);
			if (categoryById == null)
			{
				return ResponseEntity.notFound().build();
			}
			return ResponseEntity.ok(categoryById);
	}

    // Get All Products By ID //

	@GetMapping("{categoryId}/products")
	public List<Product> getProductsById(@PathVariable int categoryId)
	{
		return productDao.listByCategoryId(categoryId);
	}

	// Admin Role Required //

	@PostMapping()
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public Category addCategory(@RequestBody Category category)
	{
		return categoryDao.create(category);
	}


	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void updateCategory(@PathVariable int id, @RequestBody Category category)
	{
		categoryDao.update(id, category);
	}


	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteCategory(@PathVariable int id)
	{
		categoryDao.delete(id);

	}

}
