package br.com.ecommerce.ecom.service;

import br.com.ecommerce.ecom.config.exception.ResourceNotFoundException;
import br.com.ecommerce.ecom.dto.requests.ProductRequestDTO;
import br.com.ecommerce.ecom.dto.responses.ProductResponseDTO;
import br.com.ecommerce.ecom.entity.Brand;
import br.com.ecommerce.ecom.entity.Category;
import br.com.ecommerce.ecom.entity.Product;
import br.com.ecommerce.ecom.entity.ProductImage;
import br.com.ecommerce.ecom.mappers.ProductMapper;
import br.com.ecommerce.ecom.repository.BrandRepository;
import br.com.ecommerce.ecom.repository.CategoryRepository;
import br.com.ecommerce.ecom.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductMapper productMapper;

    public ProductResponseDTO createProduct(ProductRequestDTO dto) {
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + dto.getCategoryId()));

        Brand brand = brandRepository.findById(dto.getBrandId())
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with id: " + dto.getBrandId()));

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

        // Map imageUrls to ProductImage list
        List<ProductImage> images = dto.getImageUrls().stream()
                .map(url -> ProductImage.builder()
                        .imageUrl(url)
                        .product(product)
                        .build())
                .collect(Collectors.toList());

        product.setImages(images);

        Product savedProduct = productRepository.save(product);

        return productMapper.toResponseDTO(savedProduct);
    }

    public List<ProductResponseDTO> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public ProductResponseDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return productMapper.toResponseDTO(product);
    }

    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + dto.getCategoryId()));

        Brand brand = brandRepository.findById(dto.getBrandId())
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with id: " + dto.getBrandId()));

        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setCategory(category);
        product.setBrand(brand);
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setWeightGrams(dto.getWeightGrams());
        product.setFlavor(dto.getFlavor());

        // Replace images
        product.getImages().clear();
        List<ProductImage> images = dto.getImageUrls().stream()
                .map(url -> ProductImage.builder()
                        .imageUrl(url)
                        .product(product)
                        .build())
                .toList();
        product.getImages().addAll(images);

        Product updatedProduct = productRepository.save(product);

        return productMapper.toResponseDTO(updatedProduct);
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        productRepository.delete(product);
    }
}