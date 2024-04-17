package vcp.np.usermanagement.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vcp.np.usermanagement.profile.Profile;
import vcp.np.usermanagement.services.CasService;
import vcp.np.usermanagement.utils.Constants;


@RestController
@RequestMapping("cas")
public class CasController {

    private Profile profile;
    private CasService casService;
    private static String CONFIG_CAS_BASE_KEY = "cas.";

    
    public CasController(Profile profile, CasService casService) {
        this.profile = profile;
        this.casService = casService;
    }


    @GetMapping("login")
    public String login(HttpServletRequest request, HttpServletResponse response) {

        try {
            System.out.println("Making url of cas for login");

            String casLoginUrl = casService.getCasUrl();
            String loginUri = profile.getProperty(CONFIG_CAS_BASE_KEY + "loginUri", "");
            if (!loginUri.isEmpty()) casLoginUrl = casLoginUrl + loginUri;

            Object requestUriObj = request.getParameter(Constants.Request.Uri.KEY);
            String hostUrl = (requestUriObj != null)? casService.getHostUrlOfApp(request):(request.getRequestURL().toString());
            casLoginUrl = casLoginUrl + "?hostUrl=" + hostUrl;

            System.out.println("Redirecting to cas for login: " + casLoginUrl);
            response.sendRedirect(casLoginUrl);

            return null;

        } catch (Exception e) {
            e.printStackTrace();
            return Constants.Error.Message.SMTH_WENT_WRONG;
        }
        
    }

    
    @GetMapping("loginSuccess")
    public String loginSuccess(HttpServletRequest request, HttpServletResponse response) {

        try {
            casService.getSessionDataToSave(request);

            String hostUrl = request.getParameter(Constants.Request.HOST_URL);
            if (hostUrl == null || hostUrl.isEmpty()) {
                hostUrl = "/";
            }

            System.out.println("Redirecting to '" + hostUrl + "'");
            response.sendRedirect(hostUrl);
            return null;

        } catch (Exception e) {
            e.printStackTrace();
            return Constants.Error.Message.SMTH_WENT_WRONG;
        }
        
    }

}
