package com.baizhi.service.impl;

import com.baizhi.entity.Employee;

import java.util.List;

public interface EmployeeService {
    //员工列表的方法
    List<Employee> lists();

    //保存员工信息
    void save(Employee employee);
}
