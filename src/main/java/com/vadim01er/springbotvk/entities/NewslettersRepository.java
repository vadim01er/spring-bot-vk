package com.vadim01er.springbotvk.entities;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewslettersRepository extends JpaRepository<Newsletter, Integer> {
}
