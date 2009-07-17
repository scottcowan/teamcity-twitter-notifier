package com.sleepoverrated.twitter.teamcity;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

public class TeamCityAuthenticator extends Authenticator {

    String username;
    String password;

    public TeamCityAuthenticator(String user, String pass) {
        username = user;
        password = pass;
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(username, password.toCharArray());
    }
}
