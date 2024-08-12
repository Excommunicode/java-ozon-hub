package kz.ozon.javaozonhub.mapper;

import kz.ozon.javaozonhub.dto.RegisterRequest;
import kz.ozon.javaozonhub.dto.RoleDto;
import kz.ozon.javaozonhub.dto.UserDto;
import kz.ozon.javaozonhub.dto.UserShortDto;
import kz.ozon.javaozonhub.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    User toUserEntity(RegisterRequest request);

    UserShortDto toShortDto(User user);

    UserDto toUserDto(User user);

    @Mapping(target = "id", ignore = true)
    User updateUser(@MappingTarget User user, UserDto userDto);

    default User toUserEntity(RegisterRequest request, PasswordEncoder passwordEncoder, Set<RoleDto> roleSet) {
        RegisterRequest registerRequest = request.toBuilder()
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(roleSet)
                .build();

        return toUserEntity(registerRequest);
    }
}