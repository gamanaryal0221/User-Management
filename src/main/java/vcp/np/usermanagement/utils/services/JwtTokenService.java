package vcp.np.usermanagement.utils.services;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import vcp.np.usermanagement.profile.Profile;


public class JwtTokenService {
	
    private Profile profile;
	private PublicKey publicKey;
    
    public JwtTokenService(Profile profile) throws NoSuchAlgorithmException, InvalidKeySpecException {
        this.profile = profile;

        String _publicKey = profile.getProperty("cas.jwtToken.publicKey", "");
        _publicKey = "";
        if (_publicKey.isEmpty()) {
            // throw new Exception("Not found: public key");

            _publicKey = "-----BEGIN PUBLIC KEY-----\r\n" + //
                        "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxXR2GT5EZIS54LAa8cY3\r\n" + //
                        "77Bh4UNwrjS+dApmJ5ceYQHnOqOUU+8eOwEZ2qEZlqQPTa7GLBRjDuGEskd7fWVW\r\n" + //
                        "+FdS6yhJbYLiosbEaGud698+NcX4Qs6VupAPUYw+k/MbyN3fOKv7SAn4HI2dQWSv\r\n" + //
                        "8//GN8q/yhutLYSA/H1ZO01qlIArCoctsZ62Oa/jiu9f8GtqEbM7KlqfYlrT0x1R\r\n" + //
                        "nKiMUmkPPAWExoncde8MvwtUMy349BtwIQSNIgzbeZWiNfryHWGYhaX78Yxaeqow\r\n" + //
                        "ywjZlUFWyxd6KUgccTMe+/VaXRQmmLvuWLZQB/M60BMhc7dT1viXstmnptIV24Hd\r\n" + //
                        "KQIDAQAB\r\n" + //
                        "-----END PUBLIC KEY-----\r\n" + //
                        "";
        }
        
        // Decode public key
		_publicKey = _publicKey.replaceAll("-----BEGIN (.*)-----", "")
            .replaceAll("-----END (.*)----", "")
            .replaceAll("\r\n", "")
            .replaceAll("\n", "");

        byte[] publicKeyBytes = Base64.getDecoder().decode(_publicKey);
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

		publicKey = keyFactory.generatePublic(publicKeySpec);

	}
	
	public Claims parseToken(String jwtToken) {
		System.out.println("Parsing jwt token: " + jwtToken);
        try {

            Claims claims =  Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(jwtToken)
                    .getBody();
            
            
            // Checking if token is expired
            Date expirationDate = claims.getExpiration();
            if (expirationDate != null && expirationDate.before(new Date())) {
                throw new ExpiredJwtException(null, claims, "Token: " + jwtToken + " is expired");
            }

            if (!claims.getOrDefault("purpose", "").equals(profile.getProperty("cas.jwtToken.loginPurposeCode"))) {
                throw new JwtException("Invalid jwt token for successful login.");
            }
            
            return claims;
            
        } catch (ExpiredJwtException ex) {
        	ex.printStackTrace();
            System.out.println("Token is expired : " + jwtToken);
            return null;
            
        } catch (JwtException ex) {
        	ex.printStackTrace();
            System.out.println("jwt token: " + jwtToken + " parsing error: " + ex.getMessage());
            return null;
            
        } catch (Exception ex) {
			ex.printStackTrace();
            System.out.println("Unexpected error while parsing jwt token: " + jwtToken + " -> " + ex.getMessage());
            return null;
            
        }
        
    }
	
}
