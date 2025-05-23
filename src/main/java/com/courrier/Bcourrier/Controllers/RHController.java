package com.courrier.Bcourrier.Controllers;

import com.courrier.Bcourrier.DTO.RH.EmployeeListDTO;
import com.courrier.Bcourrier.Services.RHService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Data
@RestController
@RequestMapping("/api/RH")
@RequiredArgsConstructor
public class RHController {
    @Autowired
    private RHService employeeService;

    @GetMapping("/employees")
    public List<EmployeeListDTO> getAllEmployees() {
        return employeeService.getAllEmployeeList();
    }
}
