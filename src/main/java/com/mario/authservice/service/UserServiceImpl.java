package com.mario.authservice.service;

import com.mario.authservice.data.repository.UserRepository;
import com.mario.authservice.dto.UserDto;
import com.mario.authservice.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username){
        return userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFoundException("User not found")
        );
    }

    @Override
    public UserDto getUserById(Long id){
        UserDto user = userRepository.findUserDtoById(id).orElseThrow(
                () -> new UserNotFoundException("User not found")
        );

        return user;
    }

    @Override
    public UserDto getCurrentUser(){
        return null;
    }

    @Override
    public List<UserDto> getAllUsers(){
        return userRepository.findAllActiveUsers();
    }

    @Override
    public void deleteUser(Long id){
        if(!userRepository.existsById(id)){
            throw new UserNotFoundException("User not found");
        }
        userRepository.deleteById(id);
    }

    @Override
    public void softDeleteUser(Long id){
        if(!userRepository.existsById(id)){
            throw new UserNotFoundException("User not found");
        }
        userRepository.softDeleteUserById(id);
    }
}
