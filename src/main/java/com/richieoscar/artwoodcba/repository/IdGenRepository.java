package com.richieoscar.artwoodcba.repository;

import com.richieoscar.artwoodcba.domain.IdGen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IdGenRepository extends JpaRepository<IdGen, Long> {
}
