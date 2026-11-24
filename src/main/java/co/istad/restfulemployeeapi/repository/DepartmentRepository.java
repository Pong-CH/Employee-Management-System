package co.istad.restfulemployeeapi.repository;

import co.istad.restfulemployeeapi.domain.Department;
import co.istad.restfulemployeeapi.dto.department.DepartmentStatisticsResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DepartmentRepository extends
        JpaRepository<Department, String> {

    @Query("""
        SELECT new co.istad.restfulemployeeapi.dto.department.DepartmentStatisticsResponse(
            COUNT(e),
            COUNT(CASE WHEN e.isActive = true THEN e.id END),
            COALESCE(AVG(e.salary), 0.0)
        )
        FROM Employee e
        WHERE e.department.id = :departmentId
        """)
    DepartmentStatisticsResponse getDepartmentStatistics(
            @Param("departmentId") String departmentId
    );

}

