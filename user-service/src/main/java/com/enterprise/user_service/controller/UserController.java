package com.enterprise.user_service.controller;


import com.enterprise.user_service.dto.UserRequestDto;
import com.enterprise.user_service.dto.UserResponseDto;
import com.enterprise.user_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    //post
    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserRequestDto dto){
        UserResponseDto res=userService.createUser(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(res);
    }

    //get all data
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllData(){

        return ResponseEntity.ok(userService.getAllUsers());
    }

    //get by Id
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getById(@PathVariable Long id){
        return ResponseEntity.ok(userService.getUserById(id));
    }

    //delete user
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id){
        userService.deleteUser(id);

        return ResponseEntity.noContent().build();
    }

    //Update id
    @PutMapping("/{id}")
    public ResponseEntity updateUser(@PathVariable Long id, @Valid @RequestBody UserRequestDto dto){

        return ResponseEntity.ok(userService.updateUser(id,dto));

    }



}
