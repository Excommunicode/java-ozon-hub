package kz.ozon.javaozonhub.service.api;

import kz.ozon.javaozonhub.dto.UserDto;
import kz.ozon.javaozonhub.dto.UserShortDto;

public interface UserService {
    UserShortDto findById(String id);

    UserShortDto updateUser(String id, UserDto userDto);
}
