package com.cwallet.CHAMPWallet.security;

import com.cwallet.CHAMPWallet.models.UserEntity;
import com.cwallet.CHAMPWallet.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;
@Service
public class CustomUserDetailService implements UserDetailsService{
    private UserRepository userRepository;
    @Autowired
    public CustomUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findFirstByUsername(username);

        System.out.println(user);
        if(user != null){
            if(!user.isActive()) {
                throw new UsernameNotFoundException("User email not validated");
            }
            User authUser = new User(
                    user.getUsername(),
                    user.getPassword(),
                    user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList())
            );
            return authUser;
        }else{
            throw new UsernameNotFoundException("Invalid username or password");
        }
    }
}
