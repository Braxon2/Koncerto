package com.dusan.koncerto.auth;

import com.dusan.koncerto.enums.Role;

public class AuthenticationResponse {
    private String token;

    private Role role;

    private Long id;

    public AuthenticationResponse(String token, Role role, Long id) {
        this.token = token;
        this.role = role;
        this.id = id;
    }

    public AuthenticationResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
