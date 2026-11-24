package co.istad.restfulemployeeapi.service;

import co.istad.restfulemployeeapi.dto.department.CreateDepartmentRequest;
import co.istad.restfulemployeeapi.dto.department.DepartmentResponse;
import co.istad.restfulemployeeapi.dto.department.DepartmentStatisticsResponse;

import java.util.List;

public interface DepartmentService {

    List<DepartmentResponse> getAllDepartment();

    DepartmentResponse createDepartment(
            CreateDepartmentRequest request
    );

    void updateStatusById(String id, Boolean isActive);

    DepartmentStatisticsResponse getDepartmentStatistics(String departmentId);
}
