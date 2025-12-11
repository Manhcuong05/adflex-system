package com.adflex.leadwebhook.repository;

import com.adflex.leadwebhook.entity.Lead;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LeadRepository extends JpaRepository<Lead, UUID> {
    Optional<Lead> findByPhone(String phone);
}
