package vcp.np.usermanagement.utils.services.email;

import java.util.List;

public class MailModel {

    private List<String> receiverAddressList;
    private List<String> ccAddressList;
    private List<String> bccAddressList;
    private String subject;
    private String content;
    
    
    // Getters and setters for all fields

	public List<String> getReceiverAddressList() {
		return receiverAddressList;
	}
	
	public void setReceiverAddressList(List<String> receiverAddressList) {
		this.receiverAddressList = receiverAddressList;
	}
	

	public List<String> getCcAddressList() {
		return ccAddressList;
	}
	
	public void setCcAddressList(List<String> ccAddressList) {
		this.ccAddressList = ccAddressList;
	}
	
	
	public List<String> getBccAddressList() {
		return bccAddressList;
	}
	
	public void setBccAddressList(List<String> bccAddressList) {
		this.bccAddressList = bccAddressList;
	}
	
	
	public String getSubject() {
		return subject;
	}
	
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	

}
