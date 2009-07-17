package com.sleepoverrated.twitter.teamcity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Authenticator;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class TwitterGateway {
    String username;
    String password;

    public TwitterGateway setUsername(String username){
        this.username = username;
        return this;
    }

    public TwitterGateway setPassword(String password){
        this.password = password;
        return this;
    }

    public void sendTweet(String psText) {
        try {
            //  Using paramaters known to work
            String urlString = "http://twitter.com/statuses/update.xml";
            String data = "status=" + URLEncoder.encode(psText, "UTF-8");

            //
            Authenticator.setDefault(new TeamCityAuthenticator(username, password));
            // Send data
            URL url = new URL(urlString);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
            out.write(data);
            out.flush();

            // Get the response
            StringBuffer sb = new StringBuffer();
            BufferedReader readIn = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = readIn.readLine()) != null) {
                sb.append(line + "\n");
            }

            // Close
            out.close();
            readIn.close();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }
}
