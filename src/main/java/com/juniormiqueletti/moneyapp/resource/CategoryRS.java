package com.juniormiqueletti.moneyapp.resource;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.juniormiqueletti.moneyapp.event.ResourceCreatedEvent;
import com.juniormiqueletti.moneyapp.model.Category;
import com.juniormiqueletti.moneyapp.repository.CategoryRepository;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/v1/category")
public class CategoryRS {

	private CategoryRepository repository;
	private ApplicationEventPublisher publisher;

	@Autowired
    public CategoryRS(
        CategoryRepository repository,
        ApplicationEventPublisher publisher
    ) {
        this.repository = repository;
        this.publisher = publisher;
    }

    @GetMapping
	@PreAuthorize("hasAuthority('ROLE_CREATE_CATEGORY')")
	public List<Category> listAll() {

		List<Category> categories = repository.findAll();
		return categories;
	}

	@PostMapping
	@ResponseStatus(code = CREATED)
	@PreAuthorize("hasAuthority('ROLE_CREATE_CATEGORY')")
	public ResponseEntity<Category> create(@Valid @RequestBody Category category, HttpServletResponse response) {

		Category created = repository.save(category);

		publisher.publishEvent(new ResourceCreatedEvent(this, response, created.getId()));

		return ResponseEntity.status(CREATED).body(created);
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAuthority('ROLE_SEARCH_CATEGORY')")
	public ResponseEntity<Category> findById(@PathVariable Long id) {

		Optional<Category> category = repository.findById(id);

		if (!category.isPresent())
			return ResponseEntity.notFound().build();

        return ResponseEntity.ok(category.get());
	}
}
