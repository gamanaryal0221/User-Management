package vcp.np.usermanagement.utils.services.email;

import java.util.List;

import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.mail.internet.MimeMessage;

@Service
public class CustomMailSender {

	
    private JavaMailSenderImpl mailSender;
	
	private final MailCredential mailCredential;
	
	    
    public CustomMailSender(MailCredential emailConfig) {
        this.mailCredential = emailConfig;
    }


	@Async
    private void trigger(MailModel emailModel) {
        try {
        	
        	if (emailModel == null) throw new Exception("Received null email data");
        	
        	List<String> receiverAddressList = emailModel.getReceiverAddressList();
        	if (receiverAddressList == null) throw new Exception("Received null receivers to send email");
        	if (receiverAddressList.size() < 1) throw new Exception("Did not found any receivers to send email");
        	
			// Putting the sender credentials
			mailSender.setUsername(mailCredential.getSenderMailAddress());
			mailSender.setPassword(mailCredential.getPassword());

			// Creating mime message
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            String totalReceivers = "";
			// COllecting all to addresses
            String[] receiverAddressArray = StringUtils.toStringArray(receiverAddressList);
            totalReceivers = StringUtils.arrayToCommaDelimitedString(receiverAddressArray);
            mimeMessageHelper.setTo(receiverAddressArray);
            
			// COllecting all cc addresses
            String[] ccAddressArray = StringUtils.toStringArray(emailModel.getCcAddressList());
            totalReceivers = totalReceivers + " | " + StringUtils.arrayToCommaDelimitedString(ccAddressArray);
            if (ccAddressArray.length > 0) mimeMessageHelper.setCc(ccAddressArray);
            
			// COllecting all bcc addresses
            String[] bccAddressArray = StringUtils.toStringArray(emailModel.getBccAddressList());
            totalReceivers = totalReceivers + " | " + StringUtils.arrayToCommaDelimitedString(bccAddressArray);
            if (bccAddressArray.length > 0) mimeMessageHelper.setBcc(bccAddressArray);
            
            // Putting contents
            mimeMessageHelper.setSubject(emailModel.getSubject());
            mimeMessageHelper.setText(emailModel.getContent(), true);
            
			// Sending mail
            System.out.println("Sending email[ " + emailModel.getSubject() + "] to receivers: " + totalReceivers);
            mailSender.send(mimeMessage);
            System.out.println("Email[" + emailModel.getSubject() + "] sent successfully to: " + totalReceivers);
            
        } catch (MailException mex) {
            System.out.println("Failed to send mail: " + mex.getMessage());
        	mex.printStackTrace();
        	
        } catch (Exception ex) {
            System.out.println("An unexpected error occurred sending mail: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
	
}
