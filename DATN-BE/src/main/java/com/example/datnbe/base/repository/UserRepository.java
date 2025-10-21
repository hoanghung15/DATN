package com.example.datnbe.base.repository;

import com.example.datnbe.Enum.ProviderEnum;
import com.example.datnbe.base.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    User findByUsername(String username);
    Optional<User> findByEmailAndProvider(String email, ProviderEnum provider);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    Optional<User> findByUsernameAndProvider(String username, ProviderEnum provider);

    User findByEmail(String email);
}
