package com.roombooking.system.repository;
import com.roombooking.system.model.RecurringRule;
import com.roombooking.system.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RecurringRuleRepository extends JpaRepository<RecurringRule, Long> {
    
    List<RecurringRule> findByUser(User user);
    
    List<RecurringRule> findByUserAndIsActiveTrue(User user);
    
    List<RecurringRule> findByIsActiveTrue();
    
    @Query("SELECT r FROM RecurringRule r WHERE r.isActive = true " +
           "AND r.startDate <= :date AND r.endDate >= :date")
    List<RecurringRule> findActiveRulesForDate(@Param("date") LocalDate date);
}