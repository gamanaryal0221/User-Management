package vcp.np.usermanagement.utils;

import java.util.ArrayList;
import java.util.List;

public class Constants {
	
	public class DataSource {
		public class Usermanagement {
			public final static String ENTITY_MANAGER_FACTORY_BEAN_NAME = "localContainerEntityManagerFactoryBean";
			public final static String TRANSACTION_MANAGER_BEAN_NAME = "platformTransactionManager";
			
			public final static String REPOSITORIES_PACKAGE = "vcp.np.datasource.usermanagement.repositories";
			public final static String DOMAINS_PACKAGE = "vcp.np.datasource.usermanagement.domains";
		}
		
	}

	public class Request {
		public static String HOST_URL = "hostUrl";

		public class Uri {
			public static String KEY = "uri";

		}
		

		public class Method {

			public static String GET = "GET";
			public static String POST = "POST";
		}
	}
	
	public class Templates {
		public static String ERROR = "error";
	}

	public class Default {
		public static int PAGE_SIZE = 20;
	}

	
	public class Environment {
		public static String KEY = "environment";
		public static String DEV = "dev";
		public static String QC = "qc";
		public static String PROD = "prod";

		private static List<String> ALL = new ArrayList<String>(List.of(DEV, QC, PROD));
		public static boolean isValid(String nvironment) {
			return ALL.contains(nvironment);
		}
	}

	public class Error {

		public class Title {
			public static String KEY = "errorTitle";

			public static String TECHNICAL_ERROR = "Technical Error";
			public static String INVALID_REQUEST = "Invalid Request";
			public static String NOT_FOUND = "Not Found";
			
		}
		

		public class Message {
			public static String KEY = "errorMessage";

			public static String SMTH_WENT_WRONG = "Something went wrong. Please try again later.";

			public static String USERNAME_TAKEN = "Username is already taken";
			public static String USERNAME_IS_MANDATORY = "Username is mandatory";
			
			public static String MAIL_IS_MANDATORY = "Mail Address is mandatory";
			public static String INVALID_MAIL = "Invalid mail address";

			public static String FIRSTNAME_IS_MANDATORY = "First Name is mandatory";
			public static String LASTNAME_IS_MANDATORY = "Last Name is mandatory";
			
			public static String EMPLOYER_IS_MANDATORY = "Employer is mandatory";
			public static String EMPLOYER_NOT_FOUND = "Employer not found";

			public static String INVALID_REQUEST = "Could not procces the requet. Please try with valid request.";
			public static String USER_NOT_FOUND = "The user you are looking for does not exists.";
			
		}
	}

	public class Session {
	
		public static String USER_ID = "userId";
		public static String USERNAME = "username";
		public static String FIRST_NAME = "firstName";
		public static String MIDDLE_NAME = "middleName";
		public static String LAST_NAME = "lastName";
		public static String MAIL_ADDRESS = "mail_address";

		public static String CLIENT_ID = "clientId";
		public static String CLIENT_DISPLAY_NAME = "clientDisplayName";
		public static String IS_ADMIN_CLIENT = "isAdminClient";
		
	}
	
	
	public static String USERNAME = "username";
	
	public static String MODEL_AND_VIEW = "modelAndView";
	
	public static String CLIENT_ID = "clientId";
	public static String CLIENT_DISPLAY_NAME = "clientDisplayName";

	public static String SERVICE_ID = "serviceId";

	public static String CLIENTSERVICE_ID = "clientServiceId";

	public static String POST_REQUEST_URL = "postRequestUrl";

	
	public static String CONTENT = "content";
	public static String PAGE_NUMBER = "pageNumber";
	public static String PAGE_SIZE = "pageSize";
	public static String TOTAL_PAGES = "totalPages";
	public static String TOTAL_RECORDS = "totalRecords";
	public static String SORT_BY = "sortBy";
	public static String SORT_ORDER = "sortOrder";

}
