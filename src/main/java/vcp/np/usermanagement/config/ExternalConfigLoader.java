package vcp.np.usermanagement.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import vcp.np.usermanagement.profile.Profile;
import vcp.np.usermanagement.profile.Sprint;
import vcp.np.usermanagement.utils.Constants;


public class ExternalConfigLoader {

	public static Map<String, Object> load(Profile profile) {
    	System.out.println("\n:::::::::: Loading external config :::::::::::");

        String externalConfigPath = getExternalConfigFullPath(profile);
        System.out.println("Loading external config file from: '" + externalConfigPath + "' ...");


        File file = new File(externalConfigPath);
        if (!file.exists()) {
            throw new IllegalArgumentException("External config file does not exist: " + externalConfigPath);
        }
        

        try (InputStream inputStream = new FileInputStream(file)) {
        	
            Yaml yaml = new Yaml();
            Map<String, Object> externalConfig = yaml.load(inputStream);

            if (externalConfig == null || externalConfig.size() == 0) {
            	throw new NullPointerException("External config file seems to have null or no data");
            }

            return flattenYamlData(externalConfig);
        } catch (Exception e) {
        	e.printStackTrace();
            throw new RuntimeException("Failed to load external configuration file", e);
        }
    }


    private static String getExternalConfigFullPath(Profile profile) {
        String env = profile.getEnvironmentName();

        String basePath = profile.getProperty("external.config.basePath", "");
        if (basePath.isEmpty()) {
            throw new IllegalStateException("External config path is not specified.");
        }
        
        String fileName = profile.getProperty("external.config.filename", "");
        if (fileName == null) {
            throw new IllegalStateException("External config filename is not specified.");
        }


        if (env == null || env.isEmpty() || env.equalsIgnoreCase(Constants.Environment.DEV)) {
            // do nothing
        }else if (env.equalsIgnoreCase(Constants.Environment.QC)){
            basePath = basePath + "\\" + Constants.Environment.QC;
        }else {
            basePath = basePath + "\\" + env + "\\" + Sprint.VERSION;
        }

        return basePath + "\\" + fileName.toString();
    }
	
	
    private static Map<String, Object> flattenYamlData(Map<String, Object> yamlData) {

        try {

            Map<String, Object> flattenedData = new HashMap<>();
            flattenYamlDataRecursively("", yamlData, flattenedData);
            return flattenedData;

        }catch (Exception e) {
            throw new RuntimeException("Error encounterd on parsing external configuration file", e);
        }

    }

    private static void flattenYamlDataRecursively(String prefix, Map<String, Object> yamlData, Map<String, Object> flattenedData) throws Exception {
        for (Map.Entry<String, Object> entry : yamlData.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof Map) {
                flattenYamlDataRecursively(prefix + key + ".", (Map<String, Object>) value, flattenedData);
            } else {
                flattenedData.put(prefix + key, value);
            }
        }
    }
    
}

