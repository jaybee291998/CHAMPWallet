package com.cwallet.CHAMPWallet.mappers.account;

import com.cwallet.CHAMPWallet.dto.account.UserEntityDTO;
import com.cwallet.CHAMPWallet.models.account.UserEntity;

public class UserMapper {
    public static UserEntity mapToUser(UserEntityDTO userEntityDTO) {
        return UserEntity.builder()
                .id(userEntityDTO.getId())
                .username(userEntityDTO.getUsername())
                .email(userEntityDTO.getEmail())
                .password(userEntityDTO.getPassword())
                .isActive(userEntityDTO.isActive())
                .roles(userEntityDTO.getRoles())
                .build();
    }

    public static UserEntityDTO mapToUserEntityDTO(UserEntity user) {
        return UserEntityDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword())
                .isActive(user.isActive())
                .roles(user.getRoles())
                .build();
    }
}
