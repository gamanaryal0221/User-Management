package vcp.np.usermanagement.controllers.api;

import java.util.HashMap;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import vcp.np.usermanagement.services.UserService;

@RestController
@RequestMapping("/api")
public class UserApiController {

    private final UserService userService;

    public UserApiController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/user/list")
    public Object getUserList(HttpServletRequest request) {

        try {
            return userService.getUserList(request);
        } catch (Exception e) {
            e.printStackTrace();
            
            return new HashMap<>();
        }

    }

    
    @PostMapping("/user/add")
    public Object addUser(HttpServletRequest request) {
       
        try {
            Object response = userService.saveUser(request);
            return response;
        } catch (Exception e) {
            System.out.println("Error:" + e.getLocalizedMessage());
            e.printStackTrace();
            
            return new HashMap<>();
        }

    }

    
    @PostMapping("/user/profile/access/{userId}")
    public Object getUserAccessList(@PathVariable String userId, HttpServletRequest request) {

        try {

            if (userId == null || userId.isEmpty()) {
                return new HashMap<>();
            }

            Long longUserId = Long.parseLong(userId);
            return userService.getUserAccessList(longUserId, request);
        } catch (Exception e) {
            e.printStackTrace();
            
            return new HashMap<>();
        }

    }
}
