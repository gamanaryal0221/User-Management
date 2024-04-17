package vcp.np.usermanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import vcp.np.usermanagement.interceptors.SessionInterceptor;
import vcp.np.usermanagement.profile.Profile;
import vcp.np.usermanagement.utils.services.JwtTokenService;
import vcp.np.usermanagement.utils.services.email.MailCredential;


@Configuration
@Primary
public class BeansConfiguration extends WebMvcConfigurationSupport{
    
	private final Profile profile;

    public BeansConfiguration(Profile profile) {
    	System.out.println("\n:::::::::: Initializing bean creation ::::::::::");
		this.profile = profile;
	}
	
	
    @Bean
    SessionInterceptor sessionInterceptor() {
        return new SessionInterceptor();
    }

    @Override
	protected
    void addInterceptors(@NonNull InterceptorRegistry registry) {
		System.out.println("adding interceptors");
        registry.addInterceptor(sessionInterceptor());
    }

	
    
	@Bean
    JwtTokenService jwtTokenService() throws Exception {
        System.out.println("\n:::::::::: Initializing jwt token service ::::::::::");
        return new JwtTokenService(profile);
    }
    

	@Bean
	MailCredential emailConfig() throws Exception {
		return MailConfigLoader.configure(profile);
	}
}
