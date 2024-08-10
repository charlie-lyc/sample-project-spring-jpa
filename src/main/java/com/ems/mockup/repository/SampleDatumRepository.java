package com.ems.mockup.repository;

import com.ems.mockup.domain.SampleDatum;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SampleDatumRepository extends JpaRepository<SampleDatum, Long> {

    SampleDatum findSampleDatumByHour(int hour);

}
