package co.istad.restfulemployeeapi.dto.department;


public record DepartmentStatisticsResponse(
        Long totalEmployees,

        Long activeEmployee,

        Double averageSalary
) {
}
