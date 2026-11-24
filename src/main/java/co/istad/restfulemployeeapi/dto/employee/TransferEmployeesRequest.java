package co.istad.restfulemployeeapi.dto.employee;

import jakarta.validation.constraints.NotBlank;

public record TransferEmployeesRequest(
        @NotBlank(message = "Department ID is required")
        String departmentId
) {
}
