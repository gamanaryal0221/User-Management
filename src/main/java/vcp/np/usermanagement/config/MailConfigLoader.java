package vcp.np.usermanagement.config;

import vcp.np.usermanagement.profile.Profile;
import vcp.np.usermanagement.utils.services.email.MailCredential;
import vcp.np.usermanagement.utils.Helper;

public class MailConfigLoader {

    private static final String MAIL_BASE_KEY = "mail.sender.";
    
    public static MailCredential configure(Profile profile) throws Exception {
        System.out.println("\n:::::::::: Reading mail credential ::::::::::");
            
        String address = profile.getProperty(MAIL_BASE_KEY + "address", "");
        if (address.isEmpty()) throw new Exception("Not found: Sender mail address");
        if (!Helper.isValidMailAddress(address)) throw new Exception("Invalid sender mail address");

        String password = profile.getProperty(MAIL_BASE_KEY + "password", "");
        if (password.isEmpty()) throw new Exception("Not found: Password of mail sender");
            
        return new MailCredential(address, password);
    }
}
