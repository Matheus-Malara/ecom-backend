package br.com.ecommerce.ecom.service;

import br.com.ecommerce.ecom.dto.requests.BrandRequestDTO;
import br.com.ecommerce.ecom.dto.responses.BrandResponseDTO;
import br.com.ecommerce.ecom.entity.Brand;
import br.com.ecommerce.ecom.exception.BrandNotFoundException;
import br.com.ecommerce.ecom.exception.DuplicateBrandNameException;
import br.com.ecommerce.ecom.mappers.BrandMapper;
import br.com.ecommerce.ecom.repository.BrandRepository;
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
public class BrandService {

    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;

    public BrandResponseDTO createBrand(BrandRequestDTO dto) {
        log.info("Creating brand with name: {}", dto.getName());
        validateBrandyNameUniqueness(dto.getName());

        Brand brand = brandMapper.toEntity(dto);
        Brand savedBrand = brandRepository.save(brand);

        log.info("Brand created successfully with ID: {}", savedBrand.getId());
        return brandMapper.toResponseDTO(savedBrand);
    }

    public List<BrandResponseDTO> getAllBrands() {
        log.debug("Fetching all brands");
        return brandRepository.findAll()
                .stream()
                .map(brandMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public BrandResponseDTO getBrandById(Long id) {
        log.debug("Fetching brand by ID: {}", id);
        Brand brand = findByIdOrThrow(id);
        return brandMapper.toResponseDTO(brand);
    }

    public BrandResponseDTO updateBrand(Long id, BrandRequestDTO dto) {
        log.info("Updating brand with ID: {}", id);
        Brand brand = findByIdOrThrow(id);

        brandMapper.updateEntityFromDTO(dto, brand);
        Brand updatedBrand = brandRepository.save(brand);

        log.info("Brand with ID {} updated successfully", id);
        return brandMapper.toResponseDTO(updatedBrand);
    }

    public void deleteBrand(Long id) {
        log.info("Deleting brand with ID: {}", id);
        Brand brand = findByIdOrThrow(id);
        brandRepository.delete(brand);
        log.info("Brand with ID {} deleted successfully", id);
    }

    public void updateBrandStatus(Long id, boolean active) {
        log.info("Updating status of brand ID {} to: {}", id, active);
        Brand brand = findByIdOrThrow(id);
        brand.setActive(active);
        brandRepository.save(brand);
        log.info("Brand ID {} status updated to {}", id, active);
    }

    // ========= Helpers =========

    private Brand findByIdOrThrow(Long id) {
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
}
