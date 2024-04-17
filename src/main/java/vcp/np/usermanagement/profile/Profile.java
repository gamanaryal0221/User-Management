package vcp.np.usermanagement.profile;

import java.util.Map;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import vcp.np.usermanagement.config.ExternalConfigLoader;
import vcp.np.usermanagement.utils.Helper;

@Component
public class Profile {
	
    private final Environment environment;
    private final Map<String, Object> externalConfig;

    public Profile(Environment environment) {
        System.out.println(Helper.getUsermanagementPrintLog());

        this.environment = environment;
        this.externalConfig = ExternalConfigLoader.load(this);
        System.out.println("externalConfig:"+externalConfig);
    }
    

    public String getEnvironmentName() {
        String[] activeProfiles = this.environment.getActiveProfiles();
        if (activeProfiles.length > 0) {
            return activeProfiles[0];
        }else{
            System.out.println("Not found: environment name");
            return null;
        }
    }


	public Environment getEnvironment() {
		return this.environment;
    }

    
    public String getProperty(String key) {
        return getProperty(key, null);
    }

    public String getProperty(String key, String defaultValue) {
        if (key != null && !key.isEmpty()) {

            try{

                // Reading from internal config
                String value = this.environment.getProperty(key);
                if (value == null || value.isEmpty()) {

                    // Reading from external config
                    Object object = this.externalConfig.get(key);
                    if (object == null) {
                        value = null;
                    }else {
                        value = String.valueOf(this.externalConfig.get(key));
                    }

                }

                if (value == null || value.isEmpty()) {
                    System.out.println("Property: '" + key + "' not found");
                    return defaultValue;
                }else {
                    System.out.println("Reading property: '" + key + "' - value:'" + value + "'");
                    return value;
                }

            }catch(Exception e){
                System.out.println("Error encountered on reading property: '" + key + "'");
                e.printStackTrace();
                return defaultValue;
            }

        }else{
            System.out.println("Invalid property key: " + key);
            return defaultValue;
        }
    }

}
