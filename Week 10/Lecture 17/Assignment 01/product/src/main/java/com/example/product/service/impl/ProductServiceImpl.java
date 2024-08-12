package com.example.product.service.impl;

import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.example.product.data.model.Product;
import com.example.product.data.model.Status;
import com.example.product.data.repository.ProductRepository;
import com.example.product.dto.ProductDTO;
import com.example.product.dto.ProductSaveDTO;
import com.example.product.dto.ProductSearchCriteriaDTO;
import com.example.product.dto.ProductShowDTO;
import com.example.product.exception.BadRequestException;
import com.example.product.exception.DuplicateStatusException;
import com.example.product.exception.ResourceNotFoundException;
import com.example.product.mapper.ProductMapper;
import com.example.product.service.ProductService;

import jakarta.validation.Valid;

@Service
@Validated
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Autowired
    public ProductServiceImpl(ProductMapper productMapper, ProductRepository productRepository) {
        this.productMapper = productMapper;
        this.productRepository = productRepository;
    }

    /**
     * Finds products based on the given criteria and sorts them according to the provided sort rules.
     *
     * @param criteria The search criteria containing the product name, minimum and maximum price, and sorting options.
     * @param pageable The pagination information, including the page number and size.
     * @return A page of {@link ProductShowDTO} objects representing the products that match the criteria and are sorted according to the provided rules.
     */
    @Override
    public Page<ProductShowDTO> findByCriteria(ProductSearchCriteriaDTO criteria, Pageable pageable) {
        // Listing all the criteria
        String productName = criteria.getName();
        String sortByName = criteria.getSortByName();
        String sortByPrice = criteria.getSortByPrice();
        Double minPrice = criteria.getMinPrice();
        Double maxPrice = criteria.getMaxPrice();

        // Define the sort rules
        Sort sort = Sort.unsorted();

        if (sortByName != null && !sortByName.isEmpty()) {
            Sort nameSort = Sort.by("name");
            if (sortByName.equalsIgnoreCase("desc")) {
                nameSort = nameSort.descending();
            } else {
                nameSort = nameSort.ascending();
            }
            sort = sort.and(nameSort);
        }

        if (sortByPrice != null && !sortByPrice.isEmpty()) {
            Sort priceSort = Sort.by("price");
            if (sortByPrice.equalsIgnoreCase("desc")) {
                priceSort = priceSort.descending();
            } else {
                priceSort = priceSort.ascending();
            }
            sort = sort.and(priceSort);
        }

        // Set the pageable
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        // Get the product data from the repo
        Page<Product> products = productRepository.findByFilters(Status.ACTIVE, productName, minPrice, maxPrice, sortedPageable);
        return products.map(productMapper::toShowDTO);
    }

    /**
     * Creates a new product based on the provided {@link ProductSaveDTO} and saves it to the database.
     *
     * @param productSaveDTO The data transfer object containing the details of the new product to be created.
     * @return A {@link ProductDTO} representing the newly created product with its ID and other relevant details.
     * @throws BadRequestException If the provided Excel file is not in the correct format.
     */
    @Override
    public ProductDTO createProduct(@Valid ProductSaveDTO productSaveDTO) {
        Product product = productMapper.toProduct(productSaveDTO);
        product.setStatus(Status.ACTIVE); // Ensure the product is set to active when saving
        product.setCreatedAt(new Date());
        product.setUpdatedAt(new Date());
        Product savedProduct = productRepository.save(product);
        return productMapper.toProductDTO(savedProduct);
    }

    /**
     * Updates an existing product in the database with the provided details.
     *
     * @param id The unique identifier of the product to be updated.
     * @param productSaveDTO The data transfer object containing the details of the updated product.
     * @return A {@link ProductDTO} representing the updated product with its ID and other relevant details.
     * @throws ResourceNotFoundException If the product with the given ID is not found in the database.
     */
    @Override
    public ProductDTO updateProduct(UUID id, @Valid ProductSaveDTO productSaveDTO) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        product.setName(productSaveDTO.getName());
        product.setPrice(productSaveDTO.getPrice());
        product.setQuantity(productSaveDTO.getQuantity());
        product.setUpdatedAt(new Date());
        Product updateProduct = productRepository.save(product);
        return productMapper.toProductDTO(updateProduct);
    }

    /**
     * Updates the status of a product in the database.
     *
     * @param id The unique identifier of the product to be updated.
     * @param status The new status of the product.
     * @return A {@link ProductDTO} representing the updated product with its ID and other relevant details.
     * @throws ResourceNotFoundException If the product with the given ID is not found in the database.
     */
    @Override
    public ProductDTO updateProductStatus(UUID id, Status status) {
        Product prodCheck = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if(status == prodCheck.getStatus()) {
            throw new DuplicateStatusException("Product status is already " + status);
        }

        if (prodCheck.getStatus() == Status.ACTIVE) {
            prodCheck.setStatus(Status.DEACTIVE);
        } else if (prodCheck.getStatus() == Status.DEACTIVE) {
            prodCheck.setStatus(Status.ACTIVE);
        }
        prodCheck.setUpdatedAt(new Date());
        Product updatedProduct = productRepository.save(prodCheck);
        return productMapper.toProductDTO(updatedProduct);
    }    
}