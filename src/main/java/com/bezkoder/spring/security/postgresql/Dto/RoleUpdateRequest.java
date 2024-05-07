package com.bezkoder.spring.security.postgresql.Dto;

public class RoleUpdateRequest {
    private String newRoleName;

    public String getNewRoleName() {
        return newRoleName;
    }

    public void setNewRoleName(String newRoleName) {
        this.newRoleName = newRoleName;
    }
}
