package com.baizhi.dao;

import com.baizhi.entity.Employee;

import java.util.List;

public interface EmployeeDao {
    void save(Employee employee);

    //员工列表
    List<Employee> lists();
}
