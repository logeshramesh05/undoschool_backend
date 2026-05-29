package com.undoschool.offering.repository;

import com.undoschool.offering.entity.Offering;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OfferingRepository extends JpaRepository<Offering, Long> {

    @Query("SELECT o FROM Offering o WHERE o.teacher.id = :teacherId")
    List<Offering> findByTeacherId(@Param("teacherId") Long teacherId);

    @Query("SELECT o FROM Offering o WHERE o.status = 'ACTIVE'")
    List<Offering> findAllActive();
}
