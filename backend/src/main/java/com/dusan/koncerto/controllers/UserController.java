package com.dusan.koncerto.controllers;

import com.dusan.koncerto.dto.response.TicketDTO;
import com.dusan.koncerto.dto.response.UserResponseDTO;
import com.dusan.koncerto.model.Ticket;
import com.dusan.koncerto.repository.UserRepository;
import com.dusan.koncerto.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {


    private final UserService userService;

    public UserController(UserRepository userRepository, UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/{userId}/tickets")
    public List<TicketDTO> getALlTickets(@PathVariable Long userId) throws Exception {
        return userService.getAllTickets(userId);
    }

    @GetMapping("/{userId}")
    public UserResponseDTO getUser(@PathVariable Long userId) throws Exception {
        return userService.getUser(userId);
    }



}
