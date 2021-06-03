package com.elearning.jwt;

import java.security.Key;
import java.util.Date;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.springframework.stereotype.Service;

import com.elearning.model.SubjectHolderForJWT;
import com.elearning.util.Util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;

/*
    Our simple static class that demonstrates how to create and decode JWTs.
 */
@Service
@RequiredArgsConstructor
public class JWTUtil {

	private final Util util;
    // The secret key. This should be in a property file NOT under source
    // control and not hard coded in real life. We're putting it here for
    // simplicity.
    private static final String SECRET_KEY = "oeRaYY7Wo24sDqKSX3IM9ASGmdGPmkTd9jo1QTy4b7P9Ze5_9hKolVX8xNrQDcNRfVEdTZNOuOyqEGhXEbdJI-ZQ19k_o9MI0y3eZN2lp9jow55FfXMiINEdt1XR85VipRLSOkT6kSpzs2x-jbLDiz9iFVzkd81YKxMgPA7VfZeQUm4n-mOmnWMaVX30zGFU4L3oPBctYKkl4dYfqYWqRNfrgPJVi5DGFjywgxx0ASEiJHtV72paI3fDR2XwlSkyhhmY-ICjCRmsJN4fX1pdoL8a18-aQrvyu4j0Os6dVPYIoPvvY0SAZtWYKHfM15g7A3HD4cVREf9cUsprCRK93w";
    private static final String ISSUER = "EL3arn!ngT@x";
    //Sample method to construct a JWT
    public static String createJWT(String id, String subject, long ttlMillis) {

        //The JWT signature algorithm we will be using to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        //We will sign our JWT with our ApiKey secret
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(SECRET_KEY);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        //Let's set the JWT Claims
        JwtBuilder builder = Jwts.builder().setId(id)
                .setIssuedAt(now)
                .setSubject(subject)
                .setIssuer(ISSUER)
                .signWith(signatureAlgorithm, signingKey);

        //if it has been specified, let's add the expiration
        if (ttlMillis >= 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }

        //Builds the JWT and serializes it to a compact, URL-safe string
        return builder.compact();
    }

    public static Claims decodeJWT(String jwt) throws Exception {

        //This line will throw an exception if it is not a signed JWS (as expected)
        Claims claims = Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary(SECRET_KEY))
                .parseClaimsJws(jwt).getBody();
        return claims;
    }
    
    public static boolean isValidAdmin(String token) {
    	try {
    		Claims claims = decodeJWT(token);
        	if(claims.getIssuer().equals(ISSUER)) {
        		if (Integer.parseInt(claims.getId()) == 1) {
        			return true;
        		}
        	}
		} catch (Exception e) {
			return false;
		}
    	
    	return false;
    }
    
    public static boolean isValidUser(String token) {
    	try {
    		Claims claims = decodeJWT(token);
        	if(claims.getIssuer().equals(ISSUER)) {
        		if (Integer.parseInt(claims.getId()) == 2) {
        			return true;
        		}
        	}
		} catch (Exception e) {
			return false;
		}
    	
    	return false;
    }
    
    public Long getUserId(String token) {
    	try {
    		Claims claims = decodeJWT(token);
        	if(claims.getIssuer().equals(ISSUER)) {
        		SubjectHolderForJWT subjectHolder = util.jsonToObject(claims.getSubject(), SubjectHolderForJWT.class);
        		return subjectHolder.getUser_id();
        	}
		} catch (Exception e) {
			return 0L;
		}
    	
    	return 0L;
    }
    public static void main(String[] args) {
		/*
		 * String jwtId = Integer.toString(1); String jwtIssuer = "EL3arn!ngT@x"; String
		 * jwtSubject = Integer.toString(2); long jwtTimeToLive = Long.parseLong("5");
		 * String jwt = createJWT( jwtId, // claim = jti jwtSubject, // claim = sub
		 * jwtTimeToLive // used to calculate expiration (claim = exp) );
		 */
//		System.out.println(decodeJWT("eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxIiwiaWF0IjoxNTgzODU1MjkzLCJzdWIiOiIyIiwiaXNzIjoiRUwzYXJuIW5nVEB4IiwiZXhwIjoxNTgzODU1MjkzfQ.v_aPHyHGsoFnTzBnBen5d24hXnh-Clvqcg6eMZiIoQQ"));
		
	}
}
