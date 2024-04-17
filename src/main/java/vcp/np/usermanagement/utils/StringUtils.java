package vcp.np.usermanagement.utils;

import java.util.List;

public class StringUtils {

	public static String listToString(List list, String delimeter) {
        delimeter = (delimeter != null && !delimeter.isEmpty())? delimeter:",";

        String str = "";
        for (Object obj : list){
            str = obj + delimeter;
        }
        
        return str;
    }
	
}
