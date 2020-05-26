package Networking;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class AuthenticationHandler {

    private String baseURL = "http://127.0.0.1:5000/";
    private String requestURL = "https://api.spotify.com/v1/playlists/2MD55kyW1wIMmt053Gi1Xa/tracks";
    private String tokenURL = "http://127.0.0.1:5000/";
    private JSONObject userCreds;

    public AuthenticationHandler(){

        userCreds = new JSONObject();

    }

    public void AuthenticateUser(){

        //Load User Credentials
        Object rawObj;
        try{

            rawObj = new JSONParser().parse(new FileReader("user_credentials.json"));
            userCreds = (JSONObject) rawObj;

        }catch (Exception e){

            e.printStackTrace();

        }

    }

    public void getAuthenticationToken(String code) throws Exception{

        URL url = new URL(tokenURL + "authToken");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("code", code);
        connection.setDoOutput(true);

        DataOutputStream  wr = new DataOutputStream(connection.getOutputStream());
        wr.writeBytes(code);
        wr.close();

        InputStream is = connection.getInputStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        StringBuilder response = new StringBuilder();

        String line;
        while((line = rd.readLine()) != null){

            System.out.println(line);
            response.append(line);
            response.append('\n');

        }
        rd.close();

        System.out.println(response.toString());

        //Update userCreds
        JSONObject newCreds = (JSONObject) new JSONParser().parse(response.toString());
        try{

            ((String) newCreds.get("error")).isEmpty();

        }catch (Exception e){

            //No error
            userCreds.put("token", (String) newCreds.get("access_token"));
            userCreds.put("refresh_token", (String) newCreds.get("refresh_token"));

        }

        try{

            PrintWriter pw = new PrintWriter("user_credentials.json");
            pw.write(userCreds.toJSONString());

            pw.flush();
            pw.close();

        }catch (Exception e){

            e.printStackTrace();

        }

    }

    public void getRefreshToken(String code)throws Exception {

        URL url = new URL(tokenURL + "refreshToken");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("refresh_token", code);
        connection.setDoOutput(true);

        DataOutputStream  wr = new DataOutputStream(connection.getOutputStream());
        wr.writeBytes(code);
        wr.close();

        InputStream is = connection.getInputStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        StringBuilder response = new StringBuilder();

        String line;
        while((line = rd.readLine()) != null){

            System.out.println(line);
            response.append(line);
            response.append('\n');

        }
        rd.close();

        System.out.println(response.toString());

        //Update usercreds
        JSONObject newCreds = (JSONObject) new JSONParser().parse(response.toString());
        try{

            ((String)newCreds.get("error")).isEmpty();

        }catch (Exception e){

            //No error
            userCreds.put("token", (String) newCreds.get("access_token"));

        }

        try{

            PrintWriter pw = new PrintWriter("user_credentials.json");
            pw.write(userCreds.toJSONString());

            pw.flush();
            pw.close();

        }catch (Exception e){

            e.printStackTrace();

        }

    }

    private String arrayStr(String[] uris){

        String newStr = "[";
        for(int i = 0; i < uris.length; i++){

            newStr += uris[i];
            if(i != uris.length - 1){

                newStr += ", ";

            }

        }

        newStr += "]";
        return newStr;

    }

    private void sendRequest(String token) throws Exception{

        URL url = new URL(tokenURL + "/request");
        String[] uriArray = {"spotify:track:2NvAdwDIQ71uXIWZkr6tIO", "spotify:track:4EJFCpthcrP6oOUUXQZ22Z"};

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("token", token);
        connection.setRequestProperty("uris", arrayStr(uriArray));
        connection.setDoOutput(true);

        DataOutputStream  wr = new DataOutputStream(connection.getOutputStream());

        wr.writeBytes(token);
        wr.close();

        InputStream is = connection.getInputStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        StringBuilder response = new StringBuilder();

        String line;
        while((line = rd.readLine()) != null){

            response.append(line);
            response.append('\n');

        }
        rd.close();

        System.out.println(response.toString());

    }

    public String makeRequest(){

        String code = (String)userCreds.get("code");
        String token;

        //See if code is there.
        if(code.isEmpty()){

            return "Please enter a valid code before making a request";

        }

        //See if token exists, and is valid
        token = (String) userCreds.get("token");
        if(token.isEmpty()){

            //Get token with code
            try{

                getAuthenticationToken(code);

            }catch (Exception e){

                e.printStackTrace();
                //Try with refresh code
                try{

                    getRefreshToken((String) userCreds.get("refresh_token"));

                }catch (Exception e1){

                    //Failed with both, prompt user for valid code
                    e1.printStackTrace();
                    return "Please enter a valid code before making a request";

                }

            }

            token = (String) userCreds.get("token");

        }

        //Try to make request
        try{

            System.out.println(token);
            sendRequest(token);

        }catch (Exception e){

            //Invalid token, try to get another one with code
            try{

                e.printStackTrace();
                getAuthenticationToken(code);
                System.out.println((String) userCreds.get("token"));
                sendRequest((String) userCreds.get("token"));

            }catch (Exception e1){

                //Invalid code, try with refresh token
                try{

                    e1.printStackTrace();
                    getRefreshToken((String) userCreds.get("refresh_token"));
                    System.out.println((String) userCreds.get("token"));
                    sendRequest((String) userCreds.get("token"));

                }catch (Exception e2){

                    e2.printStackTrace();
                    //Invalid code and refresh token, prompt user for valid code
                    return "Please enter a valid code before making a request";

                }

            }

        }

        return "Success";

    }

}
