package com.opd.patient;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    @Query("""
            select p from Patient p
            where lower(p.name) like lower(concat('%', :query, '%'))
               or p.phoneNumber like concat('%', :query, '%')
            order by p.createdAt desc
            """)
    List<Patient> search(@Param("query") String query);
}
