package co.istad.restfulemployeeapi.service.impl;

import co.istad.restfulemployeeapi.domain.Employee;
import co.istad.restfulemployeeapi.dto.CreateEmployeeRequest;
import co.istad.restfulemployeeapi.dto.EmployeeResponse;
import co.istad.restfulemployeeapi.dto.UpdateEmployeeRequest;
import co.istad.restfulemployeeapi.mapper.EmployeeMapper;
import co.istad.restfulemployeeapi.repository.EmployeeRepository;
import co.istad.restfulemployeeapi.service.EmployeeService;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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

        if (employeeRepository.existsByEmail(employeeRequest.email())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email is already existed"
            );
        }

        Employee emp = employeeMapper.fromCreateEmployeeRequest(employeeRequest);
        emp.setIsActive(Boolean.TRUE);

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

}
