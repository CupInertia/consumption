package com.meter.consumption_meter;

import com.meter.consumption_meter.adapters.out.CustomerEntity;
import com.meter.consumption_meter.adapters.out.CustomerRepository;
import java.util.Collection;
import java.util.Collections;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(CustomerRepository customerRepository) {
        return new UserDetailsService() {

            @Override
            public UserDetails loadUserByUsername(String username)
                    throws UsernameNotFoundException {
                final CustomerEntity customer = customerRepository.findByUsername(username);
                return new UserDetails() {

                    @Override
                    public Collection<? extends GrantedAuthority> getAuthorities() {
                        return Collections.emptySet();
                    }

                    @Override
                    public String getPassword() {
                        return customer.getPassword();
                    }

                    @Override
                    public String getUsername() {
                        return customer.getUsername();
                    }
                };
            }
        };
    }
}
