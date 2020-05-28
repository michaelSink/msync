package Networking;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class AuthHandler {

    private JSONObject userCreds;
    private String tokenURL = "http://127.0.0.1:5000/";
    private String artists[];
    private String tracks[];

    public AuthHandler(JSONObject mainCreds, String[] inputArtists, String[] inputTracks){

        userCreds = mainCreds;
        artists = inputArtists;
        tracks = inputTracks;

    }

    public void getAuthenticationToken(String code) throws Exception{

        URL url = new URL(tokenURL + "authToken");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("code", code);
        connection.setDoOutput(true);

        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
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

    }

    public String makePlaylist(String token) throws Exception{

        URL url = new URL(tokenURL + "playlist");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("token", token);
        connection.setDoOutput(true);

        DataOutputStream  wr = new DataOutputStream(connection.getOutputStream());
        wr.writeBytes(token);
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

        return response.toString();

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

    public String makeRequest(){

        //Get valid token
        String code = (String) userCreds.get("code");

        //If token is set, and is valid, go ahead with requests (Add Later)

        //Check if code is set
        if(code.isEmpty()){

            return "Please enter a valid code.";

        }

        //If so, try to get access token
        try{

            //Try with current code
            getAuthenticationToken(code);

        }catch (Exception e){

            //Try with refresh token
            try{

                getRefreshToken((String) userCreds.get("refresh_token"));

            }catch (Exception e1){

                return "Please enter a valid access token";

            }

        }

        //With valid code, make a playlist
        String playListID;
        try{

            playListID =  makePlaylist((String) userCreds.get("token"));

        }catch (Exception e){

            //Error making playlist, adding exception message w/lack of testing
            return "Error making playlist, please try again later. " + e.getMessage();

        }

        //With playlist and token, start adding songs
        try{

            URL url = new URL(tokenURL + "addSongs");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("playlistID", playListID);
            connection.setRequestProperty("artists", arrayStr(artists));
            connection.setRequestProperty("tracks", arrayStr(tracks));
            connection.setDoOutput(true);

            DataOutputStream  wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(playListID);
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

        }catch (Exception e){

            return "Error adding songs, please try again later.";

        }


        return "Success.";

    }

}
