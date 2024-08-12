package kz.ozon.javaozonhub.mapper;

import kz.ozon.javaozonhub.model.RefreshToken;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.time.LocalDateTime;
import java.util.UUID;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RefreshTokenMapper {

    @Mapping(target = "user.id", source = "uuid")
    RefreshToken createRefreshToken(UUID uuid, String token, LocalDateTime expiryDate, LocalDateTime createdAt,
                                    Boolean revoked);
}
