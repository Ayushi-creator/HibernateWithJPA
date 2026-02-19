package org.example.Dao;

import org.example.Employee;

public interface EmployeeDao {
    void saveEmployee(Employee emp);
    Employee getEmployee(int id);
    void updateEmployee(Employee emp);
    void deleteEmployee(int id);
}
