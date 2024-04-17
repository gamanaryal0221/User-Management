package vcp.np.usermanagement.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.servlet.http.HttpSession;
import vcp.np.usermanagement.utils.Constants.Error;

public class Helper {
	
	public static URL parseUrl(String _url) {
        System.out.println("Parsing url: " + _url);
        
        URL url = null;

		if (_url != null && !_url.isEmpty()) {
			try {
				
                url = new URL(_url);
                
	        } catch (MalformedURLException e) {
	            System.out.println("Malformed url:" + _url + " :" + e.getMessage());
	        	e.printStackTrace();
	        } catch (Exception e) {
	            e.printStackTrace();
	            System.out.println("Could not parse url:" + _url + " :" + e.getMessage());
	        }
		}else {
            System.out.println("Provided url(i.e. " + _url + ") is empty or null.");
		}
		return url;
	}
	
	
	public static Map<String, Object> error(String title, String message){
		Map<String, Object> errorMap = new HashMap<String, Object>();
		errorMap.put(Error.Title.KEY, (title != null && !title.isEmpty())? title:Error.Title.TECHNICAL_ERROR);
		errorMap.put(Error.Message.KEY, (message != null && !message.isEmpty())? message:Error.Message.SMTH_WENT_WRONG);
		return errorMap;
	}


	public static String getUsermanagementPrintLog() {
		return "\nUser Management Application\n";
	}


	public static boolean isValidMailAddress(String mailAddress) {
        try {
            InternetAddress internetAddress = new InternetAddress(mailAddress);
            internetAddress.validate();
			return true;
        } catch (AddressException e) {
			e.printStackTrace();
			return false;
        }
    }

	public static Map<String, Object> getTableMetadata(Page page) {
		Map<String, Object> tableMetadata = new HashMap<>();

		tableMetadata.put(Constants.PAGE_NUMBER, page.getPageable().getPageNumber());
		tableMetadata.put(Constants.PAGE_SIZE, page.getPageable().getOffset());
		tableMetadata.put(Constants.TOTAL_PAGES, page.getTotalPages());
		tableMetadata.put(Constants.TOTAL_RECORDS, page.getTotalElements());

		return tableMetadata;
	}

	public static Pageable getPageable(Map<String, String[]> params, int defaultPageSize) {
		String stringPageNumber = (params.get(Constants.PAGE_NUMBER) != null)? params.get(Constants.PAGE_NUMBER)[0]:null;
		String stringPageSize = (params.get(Constants.PAGE_SIZE) != null)? params.get(Constants.PAGE_SIZE)[0]:null;

		String sortByWithOrder = (params.get(Constants.SORT_BY) != null)? params.get(Constants.SORT_BY)[0]:null;
		String sortBy = null;
		String sortOrder = null;
		if (sortByWithOrder != null && !sortByWithOrder.isEmpty()) {
			String[] splittedSortByAndOrder = sortByWithOrder.split("-");
			sortBy = splittedSortByAndOrder[0];
			if (splittedSortByAndOrder.length > 1) {
				sortOrder = splittedSortByAndOrder[1];
			}
		}


		Pageable pageable = null;
        if (sortBy != null && !sortBy.isEmpty()) {
			System.out.println("sortBy:" + sortBy);

			pageable = PageRequest.of(
				(stringPageNumber != null && !stringPageNumber.isEmpty())? Integer.parseInt(stringPageNumber) : 0,
				(stringPageSize != null && !stringPageSize.isEmpty())? Integer.parseInt(stringPageSize) : defaultPageSize,
				Sort.by(sortOrder != null && !sortOrder.isEmpty() && sortOrder.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy)
			);

		} else {
            
			pageable = PageRequest.of(
				(stringPageNumber != null && !stringPageNumber.isEmpty())? Integer.parseInt(stringPageNumber) : 0,
				(stringPageSize != null && !stringPageSize.isEmpty())? Integer.parseInt(stringPageSize) : defaultPageSize
			);
        }

		return pageable;
	}

	public static String generateRandomValue(int limit) {
		int _limit = (limit != -1)? limit:10;

        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*_+=-`~";
        StringBuilder randomStringBuilder = new StringBuilder();
        for (int i = 0; i < _limit; i++) {
            int index = (int) (characters.length() * Math.random());
            randomStringBuilder.append(characters.charAt(index));
        }
        return randomStringBuilder.toString();
    }


    public static boolean isAdminClient(HttpSession session, String adminClientId) {
		try {
			return (session.getServletContext().getAttribute(Constants.Session.CLIENT_ID).toString().equals(adminClientId));
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
    }

}
