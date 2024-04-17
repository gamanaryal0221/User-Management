package vcp.np.usermanagement.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;


@Controller
public class DashboardController {
    
    @GetMapping("/")
    public String getDashboard(HttpServletRequest request) {
        return "dashboard";
    }
    
}
