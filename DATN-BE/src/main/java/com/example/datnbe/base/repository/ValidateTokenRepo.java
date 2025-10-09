package com.example.datnbe.base.repository;

import com.example.datnbe.base.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ValidateTokenRepo extends JpaRepository<VerificationToken, String> {
    Optional<VerificationToken> findByToken(String token);

    @Query("""
    select v from com.example.datnbe.base.entity.VerificationToken v where v.expiryDate < :threshold
            """)
    List<VerificationToken> getAllTokenExpiry(LocalDateTime threshold);
}
