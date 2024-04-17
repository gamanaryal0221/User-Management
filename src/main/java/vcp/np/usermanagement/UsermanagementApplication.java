package vcp.np.usermanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import vcp.np.usermanagement.utils.Constants.Environment;


@SpringBootApplication
public class UsermanagementApplication {

	public static void main(String[] args) {

        String definedEnvironment = "";
        try {
            definedEnvironment = System.getenv(Environment.KEY);
            System.out.println("Defined environment: " + definedEnvironment);
        }catch (Exception e) {
            System.out.println("Could not read environment");
            e.printStackTrace();
        }

        if (definedEnvironment == null || definedEnvironment.isEmpty()) definedEnvironment = Environment.DEV;
        definedEnvironment = definedEnvironment.toLowerCase();

        if (!Environment.isValid(definedEnvironment)) {
            throw new IllegalStateException("Invalid environment defined");
        }

        System.out.println("Active environment: " + definedEnvironment);
        SpringApplication app = new SpringApplication(UsermanagementApplication.class);
        app.setAdditionalProfiles(definedEnvironment);
        app.run(args);

        System.out.println("\n\nUM is up and running on '" + definedEnvironment + "' environment\n");
	}

}
