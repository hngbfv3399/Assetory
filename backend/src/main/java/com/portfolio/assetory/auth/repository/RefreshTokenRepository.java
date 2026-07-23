package com.portfolio.assetory.auth.repository;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.portfolio.assetory.auth.domain.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

	Optional<RefreshToken> findByTokenHashAndRevokedAtIsNull(String tokenHash);

	List<RefreshToken> findAllByUser_IdAndRevokedAtIsNull(Long userId);
}
