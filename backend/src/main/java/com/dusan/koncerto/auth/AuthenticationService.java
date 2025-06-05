package com.dusan.koncerto.auth;

import com.dusan.koncerto.config.JwtService;
import com.dusan.koncerto.enums.Role;
import com.dusan.koncerto.exceptions.RegistrationException;
import com.dusan.koncerto.model.User;
import com.dusan.koncerto.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthenticationResponse register(AuthRegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RegistrationException("Email '" + request.getEmail() + "' is already registered.");
        }

        var user = new User(request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                Role.USER);

        userRepository.save(user);
        String jwt = jwtService.generateToken(user);

        return new AuthenticationResponse(jwt,user.getRole(), user.getId());
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            var user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            var jwt = jwtService.generateToken(user);
            return new AuthenticationResponse(jwt, user.getRole(), user.getId());
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid email or password");
        }
    }


}
