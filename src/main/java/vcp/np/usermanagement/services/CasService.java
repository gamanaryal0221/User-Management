package vcp.np.usermanagement.services;

import java.net.URL;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import vcp.np.usermanagement.profile.Profile;
import vcp.np.usermanagement.utils.Constants;
import vcp.np.usermanagement.utils.Helper;
import vcp.np.usermanagement.utils.services.JwtTokenService;

@Service
public class CasService {

    private Profile profile;
    private final static String CONFIG_CAS_BASE_KEY = "cas.";

    private JwtTokenService jwtTokenService;

    
    public CasService(Profile profile, JwtTokenService jwtTokenService) {
        this.profile = profile;
        this.jwtTokenService = jwtTokenService;
    }

    
    public String getCasUrl() throws Exception {
        String environment = profile.getEnvironmentName();

        String casUrl = profile.getProperty(CONFIG_CAS_BASE_KEY + "baseUrl", "");
        casUrl = casUrl.replace("<" + Constants.Environment.KEY + ">" , ((environment.equalsIgnoreCase(Constants.Environment.PROD))? "":environment));
        if (casUrl.isEmpty()) {
            throw new Exception("Empty CAS url");
        }

        String casPort = profile.getProperty(CONFIG_CAS_BASE_KEY + "port", "");
        if (!casPort.isEmpty()) casUrl = casUrl + ":" + casPort;

        return casUrl;
    }

    public String getHostUrlOfApp(HttpServletRequest request) {

        try {
            URL url = Helper.parseUrl(request.getRequestURL().toString());
            	
            String hostUrl = "";
            if (url != null) hostUrl = url.getProtocol().toString() + "://" + url.getHost().toString();
            System.out.println("hostUrl: " + hostUrl);

            
            String port = (url.getPort() != -1)? (String.valueOf(url.getPort())):"";
            if (!port.isEmpty()) hostUrl = hostUrl + ":" + port;
            System.out.println("hostUrl: " + hostUrl);
            
            Object requestUriObj = request.getParameter(Constants.Request.Uri.KEY);
            String requestUri = (requestUriObj != null)? (requestUriObj.toString()):null;

            if (hostUrl != null && !hostUrl.isEmpty()) {
                hostUrl = hostUrl + requestUri;
                System.out.println("hostUrl + requestUri: " + hostUrl);
            }else {
                hostUrl = request.getRequestURL().toString();
                System.out.println("hostUrl: " + hostUrl);
            }

            return hostUrl;
        } catch(Exception e) {
            e.printStackTrace();
            return request.getRequestURL().toString();
        }

    }


    public HttpSession getSessionDataToSave(HttpServletRequest request) throws Exception {
        
        String token = request.getParameter("token");
        if (token == null || token.isEmpty()) throw new Exception("Token not found :" + token);

        Claims claims = jwtTokenService.parseToken(token);
        if (claims == null) throw new Exception("Could not parse token");
        System.out.println("claims:" + claims);

        System.out.println("Saving data on session");
        HttpSession session = request.getSession(true);
        session.getServletContext().setAttribute(Constants.Session.USER_ID, String.valueOf(claims.getSubject()));
        session.getServletContext().setAttribute(Constants.Session.USERNAME, claims.get(Constants.Session.USERNAME));
        session.getServletContext().setAttribute(Constants.Session.MAIL_ADDRESS, claims.get(Constants.Session.MAIL_ADDRESS));
        session.getServletContext().setAttribute(Constants.Session.FIRST_NAME, claims.get(Constants.Session.FIRST_NAME));
        session.getServletContext().setAttribute(Constants.Session.MIDDLE_NAME, claims.get(Constants.Session.MIDDLE_NAME));
        session.getServletContext().setAttribute(Constants.Session.LAST_NAME, claims.get(Constants.Session.LAST_NAME));
        session.getServletContext().setAttribute(Constants.Session.CLIENT_ID, String.valueOf(claims.get(Constants.Session.CLIENT_ID)));
        session.getServletContext().setAttribute(Constants.Session.CLIENT_DISPLAY_NAME, claims.get(Constants.Session.CLIENT_DISPLAY_NAME));
        
        return null;
    }
}
