package br.com.ecommerce.ecom.service;

import br.com.ecommerce.ecom.dto.filters.ProductFilterDTO;
import br.com.ecommerce.ecom.dto.requests.ProductRequestDTO;
import br.com.ecommerce.ecom.dto.responses.ProductImageResponseDTO;
import br.com.ecommerce.ecom.dto.responses.ProductResponseDTO;
import br.com.ecommerce.ecom.entity.Brand;
import br.com.ecommerce.ecom.entity.Category;
import br.com.ecommerce.ecom.entity.Product;
import br.com.ecommerce.ecom.entity.ProductImage;
import br.com.ecommerce.ecom.exception.ProductNotFoundException;
import br.com.ecommerce.ecom.exception.ResourceNotFoundException;
import br.com.ecommerce.ecom.mappers.ProductMapper;
import br.com.ecommerce.ecom.repository.ProductImageRepository;
import br.com.ecommerce.ecom.repository.ProductRepository;
import br.com.ecommerce.ecom.specification.ProductSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryService categoryService;
    private final BrandService brandService;
    private final S3Service s3Service;


    @Transactional
    public ProductResponseDTO createProduct(ProductRequestDTO dto) {
        log.info("Creating product with name: {}", dto.getName());

        Category category = categoryService.findExistingCategory(dto.getCategoryId());
        Brand brand = brandService.getExistingBrand(dto.getBrandId());

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

    @Transactional
    public ProductImageResponseDTO uploadImage(Long productId, MultipartFile file) throws IOException {
        Product product = getExistingProduct(productId);
        String imageUrl = s3Service.uploadFile(file, productId, product.getName());

        int order = product.getImages().size();

        ProductImage image = ProductImage.builder()
                .imageUrl(imageUrl)
                .product(product)
                .displayOrder(order)
                .build();

        ProductImage savedImage = productImageRepository.save(image);

        product.getImages().add(savedImage);

        log.info("Image uploaded successfully for product with ID: {}", productId);

        return ProductImageResponseDTO.builder()
                .id(savedImage.getId())
                .imageUrl(savedImage.getImageUrl())
                .displayOrder(savedImage.getDisplayOrder())
                .build();
    }

    @Transactional
    public void deleteProductImage(Long productId, Long imageId) {
        Product product = getExistingProduct(productId);

        ProductImage image = product.getImages().stream()
                .filter(img -> img.getId().equals(imageId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Image not found for this product"));

        String s3Key = extractS3KeyFromUrl(image.getImageUrl());
        s3Service.deleteFile(s3Key);

        product.getImages().remove(image);
        productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponseDTO> getProductFiltered(ProductFilterDTO filter, Pageable pageable) {
        Specification<Product> spec = ProductSpecification.withFilters(filter);
        log.debug("Fetching products with filters: {}", filter);
        return productRepository.findAll(spec, pageable)
                .map(productMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public ProductResponseDTO getProductById(Long id) {
        log.debug("Fetching product by ID: {}", id);
        Product product = getExistingProduct(id);
        return productMapper.toResponseDTO(product);
    }

    @Transactional
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO dto) {
        log.info("Updating product with ID: {}", id);

        Product product = getExistingProduct(id);
        Category category = categoryService.findExistingCategory(dto.getCategoryId());
        Brand brand = brandService.getExistingBrand(dto.getBrandId());

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

    @Transactional
    public void deleteProduct(Long id) {
        log.info("Deleting product with ID: {}", id);
        Product product = getExistingProduct(id);
        productRepository.delete(product);
        log.info("Product with ID {} deleted successfully", id);
    }

    @Transactional
    public void updateProductStatus(Long id, boolean active) {
        log.info("Updating status of product ID {} to: {}", id, active);
        Product product = getExistingProduct(id);
        product.setActive(active);
        productRepository.save(product);
        log.info("Product ID {} status updated to {}", id, active);
    }

    // ========= Helpers =========

    private List<ProductImage> convertToProductImages(List<String> imageUrls, Product product) {
        AtomicInteger index = new AtomicInteger(0);
        return imageUrls.stream()
                .distinct()
                .map(url -> ProductImage.builder()
                        .imageUrl(url)
                        .product(product)
                        .displayOrder(index.getAndIncrement())
                        .build())
                .collect(Collectors.toList());
    }

    public Product getExistingProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Product with ID {} not found", id);
                    return new ProductNotFoundException(id);
                });
    }

    private String extractS3KeyFromUrl(String imageUrl) {
        String rawKey = imageUrl.replaceFirst(".+amazonaws\\.com/", "");
        return java.net.URLDecoder.decode(rawKey, java.nio.charset.StandardCharsets.UTF_8);
    }
}