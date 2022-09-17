package com.baizhi.controller;

import com.baizhi.entity.Employee;
import com.baizhi.service.impl.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("employee")
public class EmployeeController {
    private static final Logger log = LoggerFactory.getLogger(EmployeeController.class);

    private EmployeeService employeeService;

    //将我们自己在yml写的配置导入到java类中
    @Value("${photo.file.dir}")
    private String realpath;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

//
//    //上传头像方法
//    private String uploadPhoto(MultipartFile img, String originalFilename) throws IOException {
//        String fileNamePrefix = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
//        String fileNameSuffix = originalFilename.substring(originalFilename.lastIndexOf("."));
//        String newFileName = fileNamePrefix + fileNameSuffix;
//        img.transferTo(new File(realpath, newFileName));
//        return newFileName;
//    }

    /**
     * 保存员工信息
     * 文件上传: 1.表单提交方式必须是post  2.表单enctype属性必须为 multipart/form-data
     *
     * @return
     */
    @RequestMapping("save")
    public String save(Employee employee, MultipartFile img) throws IOException {
        log.debug("姓名:{}, 薪资:{}, 生日:{} ", employee.getName(), employee.getSalary(), employee.getBirthday());
        String originalFilename = img.getOriginalFilename();
        log.debug("头像名称: {}", originalFilename);
        log.debug("头像大小: {}", img.getSize());
        log.debug("上传的路径: {}", realpath);

        //1.处理头像的上传&修改文件名称
        //通过时间戳标记文件
        String fileNamePrefix = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        String fileNameSuffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFileName = fileNamePrefix + fileNameSuffix;
        img.transferTo(new File(realpath, newFileName));
        //2.保存员工信息
        return "redirect:/employee/lists";//保存成功跳转到列表页面
    }

    /**
     * 员工列表
     *
     * @return
     */
    @RequestMapping("lists")
    public String lists(Model model) {
        log.debug("查询所有员工的信息");
        List<Employee> employeeList = employeeService.lists();
        //将列表存储到作用域再跳转到页面
        model.addAttribute("employeeList", employeeList);
        return "emplist";
    }
}
