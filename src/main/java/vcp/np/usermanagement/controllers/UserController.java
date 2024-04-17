package vcp.np.usermanagement.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vcp.np.usermanagement.services.UserService;
import vcp.np.usermanagement.utils.Constants;
import vcp.np.usermanagement.utils.Helper;


@Controller
@RequestMapping("/user")
public class UserController {
    
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping("")
    public String getUserModulePage(HttpServletRequest request, HttpServletResponse response) {
        return "user/index";
    }

    @GetMapping("/list")
    public String getUserListPage(HttpServletRequest request, HttpServletResponse response) {
        return "user/list";
    }
    
    @GetMapping("/add")
    public String getAddUserPage() {
        return "user/add";
    }
    
    @GetMapping("/profile/{userId}")
    public String getUserProfilePage(@PathVariable String userId, Model model, HttpServletResponse response) {

        try {
            if (userId == null || userId.isEmpty()) {
                model.addAllAttributes(Helper.error(Constants.Error.Title.INVALID_REQUEST, Constants.Error.Message.INVALID_REQUEST));
            }

            Long longUserId = Long.parseLong(userId);
            model.addAllAttributes(userService.getUserProfile(longUserId));
        }catch (Exception e) {
            e.printStackTrace();
            
            model.addAllAttributes(Helper.error(null, null));
        }


        if (model.containsAttribute(Constants.Error.Title.KEY)) {
            model.addAttribute("userFullName", "Profile");
        }

        return "user/profile";

    }

}
