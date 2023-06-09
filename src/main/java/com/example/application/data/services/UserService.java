package com.example.application.data.services;

import com.example.application.data.entity.Users;
import com.example.application.data.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<Users> findAll(){
        return userRepository.findAll();
    }
    public Optional<Users> findById(long id){
        return userRepository.findById(id);
    }

    public Optional<Users> findByUsername(String username){
        return userRepository.findByUsername(username);
    }
    public Optional<Users> findByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public void saveUser(Users user){
        userRepository.save(user);
    }


}
