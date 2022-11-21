package com.example.exchangerate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Date;
import java.util.List;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {

    ExchangeRate findByDate(Date date);

    @Query("select e from ExchangeRate e where e.date >= :dateAfter and e.date <= :dateBefore")
    List<ExchangeRate> findByDate(Date dateAfter, Date dateBefore);
}
