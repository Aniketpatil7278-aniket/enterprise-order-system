package com.enterprise.user_service.service;

import com.enterprise.user_service.dto.UserRequestDto;
import com.enterprise.user_service.dto.UserResponseDto;
import com.enterprise.user_service.entity.User;
import com.enterprise.user_service.exception.UserNotFoundException;
import com.enterprise.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    public final UserRepository userRepository;

    //Convert Entity → DTO
    private UserResponseDto mapToResponse(User user){
        return UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    //create the user or insert the user data
    public UserResponseDto createUser(UserRequestDto req){
        if (userRepository.existsByEmail(req.getEmail())){
            throw new IllegalArgumentException("User already exists with email: " +req.getEmail());
        }

        User userData=User.builder()
                .name(req.getName())
                .email(req.getEmail())
                .password(req.getPassword())
                .build();

        User saveData=userRepository.save(userData);
        return mapToResponse(saveData);
    }

    //get all user data
    public List<UserResponseDto> getAllUsers(){
        return userRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    //Get by ID
    public UserResponseDto getUserById(Long id){
        User user=userRepository.findById(id)
                .orElseThrow(()-> new
                        UserNotFoundException("User not found with id: " +id));

        return mapToResponse(user);
    }

    //update the user
    public UserResponseDto updateUser(Long id , UserRequestDto dto){
        User user=userRepository.findById(id)
                .orElseThrow(()->new
                        UserNotFoundException("User not found with id: " +id));

        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());

        User update=userRepository.save(user);

        return mapToResponse(update);
    }


    //Delete user
    public void deleteUser(Long id){
        if(!userRepository.existsById(id)){
            throw new UserNotFoundException( "User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }



}

