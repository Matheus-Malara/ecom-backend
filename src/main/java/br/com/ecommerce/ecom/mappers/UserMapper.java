package br.com.ecommerce.ecom.mappers;

import br.com.ecommerce.ecom.dto.requests.UpdateUserRequestDTO;
import br.com.ecommerce.ecom.dto.responses.UserResponseDTO;
import br.com.ecommerce.ecom.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponseDTO toResponse(User user);

    void updateUserFromDto(UpdateUserRequestDTO dto, @MappingTarget User user);
}