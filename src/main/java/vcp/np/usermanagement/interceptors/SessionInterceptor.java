package vcp.np.usermanagement.interceptors;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vcp.np.usermanagement.utils.Constants;


public class SessionInterceptor implements HandlerInterceptor{
    

    public SessionInterceptor() {
        super();
    }
    
	@Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler)
            throws Exception {
        // This method is called before the actual handler method is invoked
		
		System.out.println("Request received [handler: " + handler + "]");
		String requestUri = request.getRequestURI();
		String requestMethod = request.getMethod();
        System.out.println("Request[method: " + requestMethod + ", uri: " + requestUri + "]");

        if (requestUri.startsWith("/cas")) return true;

        HttpSession session = request.getSession();
        if (session != null) {
            
            String userId = (String) session.getServletContext().getAttribute(Constants.Session.USER_ID);
            String clientId = (String) session.getServletContext().getAttribute(Constants.Session.CLIENT_ID);

            System.out.println("userId:" + userId);
            System.out.println("clientId:" + clientId);

            if (userId != null && !userId.isEmpty() && clientId != null && !clientId.isEmpty()) {
                return true; // User is authenticated, proceed with the request
            }
        }

        if (requestUri.equals("/") || requestUri.startsWith("/user")) {
            response.sendRedirect("/cas/login?" + Constants.Request.Uri.KEY + "=" + requestUri);
        }else {
            response.sendRedirect("/cas/login");
        }
        return false;
    }

    @Override
    public void postHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler,
        @Nullable ModelAndView modelAndView) throws Exception {
        // This method is called after the handler method is invoked but before the view is rendered
        // You can perform post-processing here
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, @Nullable Exception ex)
            throws Exception {
        // This method is called after the view is rendered
        // You can perform cleanup or additional tasks here
    }
    
}
