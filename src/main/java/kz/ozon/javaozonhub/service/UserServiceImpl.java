package kz.ozon.javaozonhub.service;

import kz.ozon.javaozonhub.dto.UserDto;
import kz.ozon.javaozonhub.dto.UserShortDto;
import kz.ozon.javaozonhub.exception.NotFoundException;
import kz.ozon.javaozonhub.mapper.UserMapper;
import kz.ozon.javaozonhub.model.User;
import kz.ozon.javaozonhub.repository.UserRepository;
import kz.ozon.javaozonhub.service.api.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserShortDto findById(String id) {
        UUID uuid = getUuid(id);
        return getUserShortDto(uuid);
    }

    @Override
    @Transactional
    public UserShortDto updateUser(String id, UserDto userDto) {
        UUID uuid = getUuid(id);
        User user = getUser(uuid);

        userDto.setPassword(passwordEncoder.encode(user.getPassword()));
        User updateUser = userMapper.updateUser(user, userDto);
        User save = userRepository.save(updateUser);

        return userMapper.toShortDto(save);
    }

    private UserDto getUserDto(UUID uuid) {
        return userMapper.toUserDto(getUser(uuid));
    }

    private UUID getUuid(String id) {
        return UUID.fromString(id);
    }

    private UserShortDto getUserShortDto(UUID uuid) {
        return userMapper.toShortDto(getUser(uuid));
    }

    private User getUser(UUID uuid) {
        return userRepository.findById(uuid).orElseThrow(() -> NotFoundException.builder()
                .message(String.format("User with id: %s not found", uuid))
                .build());
    }



}
