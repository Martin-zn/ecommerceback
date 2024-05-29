package com.martin.ecommerce.springecommerce.services;

import com.martin.ecommerce.springecommerce.entities.LocalUser;
import com.martin.ecommerce.springecommerce.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LocalUser localUser = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(()-> new UsernameNotFoundException("El usuario "+username+" No existe."));

        Collection<? extends GrantedAuthority> authorities = localUser.getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_".concat(role.getName().name())))
                .collect(Collectors.toSet());

        //List<SimpleGrantedAuthority> authorityList = new ArrayList<>();

//        localUser.getRoles()
//                .forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_".concat(role.getName().name()))));


        return new User(
                localUser.getUsername(),
                localUser.getPassword(),
                true,
                true,
                true,
                true,
                authorities
                );
    }
}
