package co.istad.restfulemployeeapi.controller;

import co.istad.restfulemployeeapi.dto.department.CreateDepartmentRequest;
import co.istad.restfulemployeeapi.dto.department.DepartmentResponse;
import co.istad.restfulemployeeapi.dto.department.DepartmentStatisticsResponse;
import co.istad.restfulemployeeapi.dto.employee.EmployeeResponse;
import co.istad.restfulemployeeapi.service.DepartmentService;
import co.istad.restfulemployeeapi.service.EmployeeService;
import co.istad.restfulemployeeapi.utils.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    private final EmployeeService employeeService;

    @GetMapping
    public List<DepartmentResponse> getAll() {
        return departmentService.getAllDepartment();
    }

    @PostMapping
    public DepartmentResponse createDepartment(
            @Valid @RequestBody CreateDepartmentRequest request
            ) {
        return departmentService.createDepartment(request);
    }

    @PutMapping("/{id}/status")
    public void updateDepartmentStatus(
            @PathVariable String id,
            @RequestParam Boolean isActive
    ){
        departmentService.updateStatusById(id, isActive);
    }

    @GetMapping("/{departmentId}/employees")
   public PageResponse<EmployeeResponse> getEmployeesByDepartment(
           @PathVariable String departmentId,

           @RequestParam(defaultValue =  "0") int page,

           @RequestParam(defaultValue = "10") int size,

           @RequestParam(defaultValue = "name") String sotBy,

           @RequestParam(defaultValue = "asc") String direction
   ) {
        return employeeService.getEmployeeByDepartment(
                departmentId,
                page,
                size,
                sotBy,
                direction
        );
   }

    @GetMapping("/{departmentId}/statistics")
    public DepartmentStatisticsResponse getDepartmentStatistics(
            @PathVariable String departmentId
    ) {
        return departmentService.getDepartmentStatistics(
                departmentId
        );
    }
}
