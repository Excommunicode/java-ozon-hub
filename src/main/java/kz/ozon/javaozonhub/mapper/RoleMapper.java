package kz.ozon.javaozonhub.mapper;

import kz.ozon.javaozonhub.dto.RoleDto;
import kz.ozon.javaozonhub.model.Role;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
         nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RoleMapper {
    RoleDto toRoleDto(Role role);
}
