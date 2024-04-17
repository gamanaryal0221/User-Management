package vcp.np.usermanagement.utils.services.email;


public class MailCredential {
    
    private String senderMailAddress = "";
    private String password = "";


    public MailCredential(String sender, String password) {
        this.senderMailAddress = sender;
        this.password = password;
    }


    public String getSenderMailAddress() {
        return senderMailAddress;
    }


    public String getPassword() {
        return password;
    }

}
