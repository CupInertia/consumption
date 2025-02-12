package com.meter.consumption_meter;

import com.meter.consumption_meter.adapters.CustomerEntity;
import com.meter.consumption_meter.adapters.CustomerRepository;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class ConsumptionMeterApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConsumptionMeterApplication.class, args);
    }

    @Bean
    public CommandLineRunner setUp(
            CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        return (args) -> {
            if (customerRepository.count() == 0)
                customerRepository.save(
                        CustomerEntity.builder()
                                .ID(UUID.randomUUID().toString())
                                .firstName("Billy")
                                .lastName("Bonanza")
                                .username("billy")
                                .password(passwordEncoder.encode("bonanza"))
                                .build());
        };
    }

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
