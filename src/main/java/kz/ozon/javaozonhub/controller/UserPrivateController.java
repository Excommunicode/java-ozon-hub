package kz.ozon.javaozonhub.controller;

import jakarta.validation.Valid;
import kz.ozon.javaozonhub.dto.UserDto;
import kz.ozon.javaozonhub.dto.UserShortDto;
import kz.ozon.javaozonhub.service.api.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "users")
public class UserPrivateController {
    private final UserService userService;

    @GetMapping("{id}")
    public UserShortDto getUser(@PathVariable String id) {
        return userService.findById(id);
    }

    @PatchMapping("{id}")
    public UserShortDto updateUser(@PathVariable String id, @Valid @RequestBody UserDto userDto) {
        return userService.updateUser(id, userDto);
    }
}
