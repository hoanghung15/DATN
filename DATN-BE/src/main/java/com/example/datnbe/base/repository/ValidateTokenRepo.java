package com.example.datnbe.base.repository;

import com.example.datnbe.base.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ValidateTokenRepo extends JpaRepository<VerificationToken, String> {
    Optional<VerificationToken> findByToken(String token);
}
