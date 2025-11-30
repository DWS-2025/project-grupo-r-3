package com.example.unitalk.services;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import org.springframework.stereotype.Service;

@Service
public class TwoFactorService {

    private final GoogleAuthenticator gAuth;

    public TwoFactorService() {
        this.gAuth = new GoogleAuthenticator();
    }

    public String generateNewSecret() {
        final GoogleAuthenticatorKey key = gAuth.createCredentials();
        return key.getKey();
    }

    public String generateOtpAuthUrl(String username, String secret) {
        return "otpauth://totp/UniTalk:" + username + "?secret=" + secret + "&issuer=UniTalk";
    }

    public boolean isOtpValid(String secret, int code) {
        return gAuth.authorize(secret, code);
    }
}
