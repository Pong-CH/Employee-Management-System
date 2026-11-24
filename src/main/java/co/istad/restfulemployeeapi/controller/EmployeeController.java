package co.istad.restfulemployeeapi.controller;

import co.istad.restfulemployeeapi.dto.employee.CreateEmployeeRequest;
import co.istad.restfulemployeeapi.dto.employee.EmployeeResponse;
import co.istad.restfulemployeeapi.dto.employee.TransferEmployeesRequest;
import co.istad.restfulemployeeapi.service.EmployeeService;
import co.istad.restfulemployeeapi.utils.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService service;



    @GetMapping
    public PageResponse<EmployeeResponse> getAllEmployees(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "8") int size
    ) {
        return service.getAllWithPage(page, size);
    }

    @GetMapping("/{id}")
    public EmployeeResponse getEmployeeById(@PathVariable String id) {
        return service.getEmployeeById(id);
    }

    @PutMapping("/{id}")
    public void updateEmployeeStatus(@PathVariable String id,
                                     @RequestParam Boolean isActive) {
        service.updateEmployeeStatus(id, isActive);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeResponse createEmployee(
            @Valid @RequestBody CreateEmployeeRequest createRequest
            ) {
        return service.createEmployee(createRequest);
    }


    @PostMapping("/bulkCreate")
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeResponse bulkCreate(
            @Valid @RequestBody List<CreateEmployeeRequest> createRequest
    ) {
        return service.bulkCreate(createRequest);
    }

    @GetMapping("/search")
    public List<EmployeeResponse> searchEmployees(
            @RequestParam String keyword
    ) {
        return service.searchEmployee(keyword);
    }

    @PatchMapping("/{employeeId}/transfer")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void transferEmployee(
            @PathVariable String employeeId,
            @Valid @RequestBody TransferEmployeesRequest request
            ) {
        service.transferEmployee(
                employeeId,
                request.departmentId()
        );
    }
}
