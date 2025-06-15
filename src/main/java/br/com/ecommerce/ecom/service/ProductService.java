package br.com.ecommerce.ecom.service;

import br.com.ecommerce.ecom.dto.requests.ProductRequestDTO;
import br.com.ecommerce.ecom.dto.responses.ProductResponseDTO;
import br.com.ecommerce.ecom.entity.Brand;
import br.com.ecommerce.ecom.entity.Category;
import br.com.ecommerce.ecom.entity.Product;
import br.com.ecommerce.ecom.entity.ProductImage;
import br.com.ecommerce.ecom.exception.BrandNotFoundException;
import br.com.ecommerce.ecom.exception.CategoryNotFoundException;
import br.com.ecommerce.ecom.exception.ProductNotFoundException;
import br.com.ecommerce.ecom.mappers.ProductMapper;
import br.com.ecommerce.ecom.repository.BrandRepository;
import br.com.ecommerce.ecom.repository.CategoryRepository;
import br.com.ecommerce.ecom.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductMapper productMapper;

    public ProductResponseDTO createProduct(ProductRequestDTO dto) {
        log.info("Creating product with name: {}", dto.getName());

        Category category = findCategoryByIdOrThrow(dto.getCategoryId());
        Brand brand = findBrandByIdOrThrow(dto.getBrandId());

        Product product = Product.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .category(category)
                .brand(brand)
                .price(dto.getPrice())
                .stock(dto.getStock())
                .weightGrams(dto.getWeightGrams())
                .flavor(dto.getFlavor())
                .active(true)
                .build();

        List<ProductImage> images = convertToProductImages(dto.getImageUrls(), product);
        product.setImages(images);

        Product savedProduct = productRepository.save(product);
        log.info("Product '{}' created successfully with ID: {}", savedProduct.getName(), savedProduct.getId());
        return productMapper.toResponseDTO(savedProduct);
    }

    public List<ProductResponseDTO> getAllProducts() {
        log.debug("Fetching all products");
        return productRepository.findAll()
                .stream()
                .map(productMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public ProductResponseDTO getProductById(Long id) {
        log.debug("Fetching product by ID: {}", id);
        Product product = findProductByIdOrThrow(id);
        return productMapper.toResponseDTO(product);
    }

    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO dto) {
        log.info("Updating product with ID: {}", id);

        Product product = findProductByIdOrThrow(id);
        Category category = findCategoryByIdOrThrow(dto.getCategoryId());
        Brand brand = findBrandByIdOrThrow(dto.getBrandId());

        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setCategory(category);
        product.setBrand(brand);
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setWeightGrams(dto.getWeightGrams());
        product.setFlavor(dto.getFlavor());

        product.getImages().clear();
        List<ProductImage> images = convertToProductImages(dto.getImageUrls(), product);
        product.getImages().addAll(images);

        Product updatedProduct = productRepository.save(product);
        log.info("Product with ID {} updated successfully", id);
        return productMapper.toResponseDTO(updatedProduct);
    }

    public void deleteProduct(Long id) {
        log.info("Deleting product with ID: {}", id);
        Product product = findProductByIdOrThrow(id);
        productRepository.delete(product);
        log.info("Product with ID {} deleted successfully", id);
    }

    public void updateProductStatus(Long id, boolean active) {
        log.info("Updating status of product ID {} to: {}", id, active);
        Product product = findProductByIdOrThrow(id);
        product.setActive(active);
        productRepository.save(product);
        log.info("Product ID {} status updated to {}", id, active);
    }

    // ========= Helpers =========

    private List<ProductImage> convertToProductImages(List<String> imageUrls, Product product) {
        return imageUrls.stream()
                .distinct()
                .map(url -> ProductImage.builder()
                        .imageUrl(url)
                        .product(product)
                        .build())
                .collect(Collectors.toList());
    }

    private Product findProductByIdOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Product with ID {} not found", id);
                    return new ProductNotFoundException(id);
                });
    }

    private Category findCategoryByIdOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Category with ID {} not found (used in product)", id);
                    return new CategoryNotFoundException(id);
                });
    }

    private Brand findBrandByIdOrThrow(Long id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Brand with ID {} not found (used in product)", id);
                    return new BrandNotFoundException(id);
                });
    }
}
