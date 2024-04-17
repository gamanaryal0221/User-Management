package vcp.np.usermanagement.utils.services;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vcp.np.datasource.usermanagement.domains.User;
import vcp.np.usermanagement.utils.Helper;

@Service
public class AuthenticationService {

	@Autowired
	public PlainSqlQueries plainSqlQueries;
	

    public AuthenticationService() {
    }

    public boolean isPasswordValid(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }

        // Check length
        if (password.length() < 8 || password.length() > 12) {
            return false;
        }

        // Check for uppercase, lowercase, special character, and number
        if (!password.matches(".*[A-Z].*")) { // Uppercase
            return false;
        }
        if (!password.matches(".*[a-z].*")) { // Lowercase
            return false;
        }
        if (!password.matches(".*\\W.*")) { // Special character
            return false;
        }
        if (!password.matches(".*\\d.*")) { // Number
            return false;
        }

        return true;
    }
	
    public String hashPassword(String saltValue, String rawPassword) {
        String passwordWithSalt = saltValue + rawPassword;
        return sha2Hash512(passwordWithSalt);
    }
    
    private String sha2Hash512(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            byte[] hash = digest.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-512 algorithm not available", e);
        }
    }

    public PasswordDetails makePassword(String rawPassword) {
        String saltValue = Helper.generateRandomValue(-1);
        String hashedPassword = hashPassword(saltValue, rawPassword);
        return new PasswordDetails(saltValue, hashedPassword);
    }
    
    public boolean doesItMatchWithOldPassword(User user, String rawPassword) {
    	System.out.println("Checking if the password is in the user[id: " + user.getId() + "]'s password history ...");
		String enteredPasswordHash = hashPassword(user.getSaltValue(), rawPassword);
		if (enteredPasswordHash.equals(user.getPassword())) {
	    	System.out.println("User[id: " + user.getId() + "] provided the current password to reset");
			return true;
		}else {
			return plainSqlQueries.isItInUserPasswordHistory(user.getId(), rawPassword);
		}
	}
    
    public static class PasswordDetails {
    	String saltValue = "";
    	String hashedPassword = "";
    	
		PasswordDetails(String saltValue, String hashedPassword) {
			this.saltValue = saltValue;
			this.hashedPassword = hashedPassword;
		}
		
		public String getSaltValue() {
			return saltValue;
		}
		
		public void setSaltValue(String saltValue) {
			this.saltValue = saltValue;
		}
		
		public String getHashedPassword() {
			return hashedPassword;
		}
		
		public void setHashedPassword(String hashedPassword) {
			this.hashedPassword = hashedPassword;
		}
    }
	
}
