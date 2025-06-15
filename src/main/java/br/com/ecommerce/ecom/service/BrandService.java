package br.com.ecommerce.ecom.service;

import br.com.ecommerce.ecom.dto.requests.BrandRequestDTO;
import br.com.ecommerce.ecom.dto.responses.BrandResponseDTO;
import br.com.ecommerce.ecom.entity.Brand;
import br.com.ecommerce.ecom.exception.BrandNotFoundException;
import br.com.ecommerce.ecom.mappers.BrandMapper;
import br.com.ecommerce.ecom.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BrandService {

    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;

    public BrandResponseDTO createBrand(BrandRequestDTO dto) {
        Brand brand = brandMapper.toEntity(dto);
        Brand savedBrand = brandRepository.save(brand);
        return brandMapper.toResponseDTO(savedBrand);
    }

    public List<BrandResponseDTO> getAllBrands() {
        return brandRepository.findAll()
                .stream()
                .map(brandMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public BrandResponseDTO getBrandById(Long id) {
        Brand brand = findByIdOrThrow(id);
        return brandMapper.toResponseDTO(brand);
    }

    public BrandResponseDTO updateBrand(Long id, BrandRequestDTO dto) {
        Brand brand = findByIdOrThrow(id);

        brandMapper.updateEntityFromDTO(dto, brand);
        Brand updatedBrand = brandRepository.save(brand);

        return brandMapper.toResponseDTO(updatedBrand);
    }

    public void deleteBrand(Long id) {
        Brand brand = findByIdOrThrow(id);
        brandRepository.delete(brand);
    }

    private Brand findByIdOrThrow(Long id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new BrandNotFoundException(id));
    }
}
