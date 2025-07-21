package br.com.ecommerce.ecom.service;

import br.com.ecommerce.ecom.dto.filters.BrandFilterDTO;
import br.com.ecommerce.ecom.dto.requests.BrandRequestDTO;
import br.com.ecommerce.ecom.dto.requests.BrandWithImageRequestDTO;
import br.com.ecommerce.ecom.dto.responses.BrandResponseDTO;
import br.com.ecommerce.ecom.entity.Brand;
import br.com.ecommerce.ecom.enums.UploadType;
import br.com.ecommerce.ecom.exception.BrandNotFoundException;
import br.com.ecommerce.ecom.exception.DuplicateBrandNameException;
import br.com.ecommerce.ecom.mappers.BrandMapper;
import br.com.ecommerce.ecom.repository.BrandRepository;
import br.com.ecommerce.ecom.specification.BrandSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrandService {

    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;
    private final S3Service s3Service;

    @Transactional
    public BrandResponseDTO createBrand(BrandWithImageRequestDTO dto) throws IOException {
        log.info("Creating brand with name: {}", dto.getName());
        validateBrandyNameUniqueness(dto.getName());

        Brand brand = Brand.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .active(true)
                .build();

        brand = brandRepository.save(brand);

        if (dto.getImage() != null && !dto.getImage().isEmpty()) {
            brand = uploadImageInternal(brand, dto.getImage(), brand.getId());
        }

        log.info("Brand '{}' created successfully with ID: {}", brand.getName(), brand.getId());
        return brandMapper.toResponseDTO(brand);
    }


    @Transactional
    public BrandResponseDTO uploadImage(Long brandId, MultipartFile file) throws IOException {
        Brand brand = getExistingBrand(brandId);

        if (brand.getLogoUrl() != null) {
            String previousKey = s3Service.extractKeyFromUrl(brand.getLogoUrl());
            s3Service.deleteFile(previousKey);
        }

        brand = uploadImageInternal(brand, file, brand.getId());

        log.info("Replaced logo for brand ID {}", brandId);
        return brandMapper.toResponseDTO(brand);
    }

    public Page<BrandResponseDTO> getBrandFiltered(BrandFilterDTO filter, Pageable pageable) {
        Specification<Brand> spec = BrandSpecification.withFilters(filter);

        log.debug("Fetching brands with filters: name='{}', active={}", filter.getName(), filter.getActive());

        return brandRepository.findAll(spec, pageable)
                .map(brandMapper::toResponseDTO);
    }

    public BrandResponseDTO getBrandById(Long id) {
        log.debug("Fetching brand by ID: {}", id);
        Brand brand = getExistingBrand(id);
        return brandMapper.toResponseDTO(brand);
    }

    @Transactional
    public BrandResponseDTO updateBrand(Long id, BrandRequestDTO dto) {
        log.info("Updating brand with ID: {}", id);
        Brand brand = getExistingBrand(id);

        brandMapper.updateEntityFromDTO(dto, brand);
        Brand updatedBrand = brandRepository.save(brand);

        log.info("Brand with ID {} updated successfully", id);
        return brandMapper.toResponseDTO(updatedBrand);
    }

    @Transactional
    public void deleteBrand(Long id) {
        log.info("Deleting brand with ID: {}", id);
        Brand brand = getExistingBrand(id);
        brandRepository.delete(brand);
        log.info("Brand with ID {} deleted successfully", id);
    }

    @Transactional
    public void deleteImage(Long brandId) {
        Brand brand = getExistingBrand(brandId);

        if (brand.getLogoUrl() != null) {
            String key = s3Service.extractKeyFromUrl(brand.getLogoUrl());
            s3Service.deleteFile(key);
            brand.setLogoUrl(null);
            brandRepository.save(brand);

            log.info("Deleted logo for brand ID {}", brandId);
        }
    }

    @Transactional
    public void updateBrandStatus(Long id, boolean active) {
        log.info("Updating status of brand ID {} to: {}", id, active);
        Brand brand = getExistingBrand(id);
        brand.setActive(active);
        brandRepository.save(brand);
        log.info("Brand ID {} status updated to {}", id, active);
    }

    public long getBrandCount() {
        return brandRepository.count();
    }

    // ========= Helpers =========

    public Brand getExistingBrand(Long id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Brand with ID {} not found", id);
                    return new BrandNotFoundException(id);
                });
    }

    private void validateBrandyNameUniqueness(String brandName) {
        if (brandRepository.existsByNameIgnoreCase(brandName)) {
            log.warn("Brand name '{}' already exists", brandName);
            throw new DuplicateBrandNameException(brandName);
        }
    }

    private Brand uploadImageInternal(Brand brand, MultipartFile file, Long brandId) throws IOException {
        String logoUrl = s3Service.uploadFile(file, UploadType.BRANDS, brandId, brand.getName());
        brand.setLogoUrl(logoUrl);
        return brandRepository.save(brand);
    }

}
