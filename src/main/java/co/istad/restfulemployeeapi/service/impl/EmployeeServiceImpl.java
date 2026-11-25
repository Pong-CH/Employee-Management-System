package co.istad.restfulemployeeapi.service.impl;

import co.istad.restfulemployeeapi.domain.Department;
import co.istad.restfulemployeeapi.domain.Employee;
import co.istad.restfulemployeeapi.dto.employee.CreateEmployeeRequest;
import co.istad.restfulemployeeapi.dto.employee.EmployeeResponse;
import co.istad.restfulemployeeapi.dto.employee.UpdateEmployeeRequest;
import co.istad.restfulemployeeapi.mapper.EmployeeMapper;
import co.istad.restfulemployeeapi.repository.DepartmentRepository;
import co.istad.restfulemployeeapi.repository.EmployeeRepository;
import co.istad.restfulemployeeapi.service.EmployeeService;
import co.istad.restfulemployeeapi.utils.PageResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
//@Transactional(readOnly = true)
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    private final DepartmentRepository departmentRepository;


    @Override
    public List<EmployeeResponse> getAll() {
        return employeeRepository.findAll()
                .stream()
                .map(employeeMapper::toResponse)
                .toList();
    }

    @Override
    public EmployeeResponse getEmployeeById(String id) {

        Employee emp = getById(id);

        return employeeMapper.toResponse(emp);

    }

    @Transactional
    @Override
    public EmployeeResponse createEmployee(CreateEmployeeRequest employeeRequest) {

        Department existDepartment = departmentRepository
                .findById(employeeRequest.departmentId())
                .orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Department has not been found"
                        )
                );

        if (employeeRepository.existsByEmail(employeeRequest.email())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email is already existed"
            );
        }

        Employee emp = employeeMapper.fromCreateEmployeeRequest(employeeRequest);
        emp.setIsActive(Boolean.TRUE);
        emp.setDepartment(existDepartment);

        employeeRepository.save(emp);

        return employeeMapper.toResponse(emp);

    }

    @Override
    public EmployeeResponse updateEmployeeById(String id, UpdateEmployeeRequest updateEmployeeRequest) {
        return null;
    }

    @Override
    public void deleteEmployeeById(String id) {

    }

    @Transactional
    @Override
    public void updateEmployeeStatus(String id, Boolean status) {

        checkIdExists(id);

        employeeRepository.updateEmployeeStatus(id, status);
    }

    @Override
    public PageResponse<EmployeeResponse> getAllWithPage(int page, int size) {

        if (page < 0 || size < 1) {
            throw  new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    """
                    Invalid page request
                    Page and Size must be positive"""
            );
        }

        Pageable pageable = PageRequest.of(page, size);

//        Page<Employee> employees = employeeRepository.findAll(pageable);

        Page<Employee> employees = employeeRepository.findAllByIsActiveTrue(pageable);

         return PageResponse.<EmployeeResponse>builder()
                .content(employees.getContent().stream().map(employeeMapper::toResponse).toList())
                .pageNumber(employees.getNumber())
                .size(employees.getSize())
                .totalElement(employees.getTotalElements())
                .totalPages(employees.getTotalPages())
                .build();
    }

    @Override
    public EmployeeResponse bulkCreate(List<CreateEmployeeRequest> createRequest) {

        for (CreateEmployeeRequest request: createRequest) {
            Department existDepartment = departmentRepository
                    .findById(request.departmentId())
                    .orElseThrow(
                            () -> new ResponseStatusException(
                                    HttpStatus.NOT_FOUND,
                                    "Department has not been found"
                            )
                    );

            if (employeeRepository.existsByEmail(request.email())) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Email is already existed"
                );
            }

            Employee emp = employeeMapper.fromCreateEmployeeRequest(request);
            emp.setIsActive(Boolean.TRUE);
            emp.setDepartment(existDepartment);

            employeeRepository.save(emp);
        }

        return null;
    }

    private void checkIdExists(String id) {
        if(!employeeRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Employee has not been found"
            );
        }
    }

    private Employee getById(String id) {
        return employeeRepository.findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Employee has not been found"
                        )
                );
    }

    @Override
    public List<EmployeeResponse> searchEmployee(String keyword) {

        return employeeRepository
                .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        keyword,
                        keyword
                )
                .stream()
                .map(employeeMapper::toResponse)
                .toList();
    }

    @Override
    public PageResponse<EmployeeResponse> getEmployeeByDepartment(String departmentId, int page, int size, String sortBy, String direction) {
        if (page < 0 || size < 1) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid page or size"
            );
        }

        if (!departmentRepository.existsById(departmentId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Department has not been found"
            );
        }

        // Prevent sorting by invalid properties
        List<String> allowedFields = List.of(
                "name", "email", "salary", "hireDate"
        );

        if (!allowedFields.contains(sortBy)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid sort field"
            );
        }

        Sort.Direction sortDirection;

        try {
            sortDirection = Sort.Direction.fromString(direction);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Direction must be asc or desc"
            );
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(sortDirection, sortBy)
        );

        Page<Employee> employees = employeeRepository
                .findByDepartment_Id(
                        departmentId,
                        pageable
                );

        return PageResponse.<EmployeeResponse>builder()
                .content(
                        employees.getContent()
                                .stream()
                                .map(employeeMapper::toResponse)
                                .toList()
                )
                .pageNumber(employees.getNumber())
                .size(employees.getSize())
                .totalElement(employees.getTotalElements())
                .totalPages(employees.getTotalPages())
                .build();
    }

    @Override
    public void transferEmployee(String employeeId, String departmentId) {
        Employee employee = getById(employeeId);

        Department department = departmentRepository
                .findById(departmentId)
                .orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "Destination department is inactive"
                        )
                );
        if (!Boolean.TRUE.equals(department.getIsActive())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Destination department is inactive"
            );
        }
        employee.setDepartment(department);

        employeeRepository.save(employee);
    }
}
