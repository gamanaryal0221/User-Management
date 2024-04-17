package vcp.np.usermanagement.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/client")
public class ClientController {

    
    @GetMapping("")
    public String getUserModulePage(HttpServletRequest request, HttpServletResponse response) {
        return "client/index";
    }
    
}
