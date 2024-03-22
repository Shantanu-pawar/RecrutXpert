package com.tool.RecruitXpert.Repository;

import com.tool.RecruitXpert.Entities.JobsApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends JpaRepository<JobsApplication,Long> {

}
