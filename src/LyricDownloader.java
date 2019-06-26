import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;


public class LyricDownloader extends Thread{
    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String search_artist = "https://www.lyricsfreak.com/search.php?q=";
    private static String lyrics = "null";

    private String title;
    private String artist;

    public String getLyrics() {
        return lyrics;
    }

    public LyricDownloader(String artist, String title){
        this.title = title;
        this.artist = artist;
    }

    public String downloader(int i) throws IOException{
        String lyric = "Lyrics downloaded from www.lyricsfreak.com \n \n" ;
        lyric += "Title: " + title + '\n';
        lyric += "Artist: " + artist + "\n \n";

        title = title.toLowerCase();
        artist = artist.toLowerCase();

        artist = artist.replace(" ", "+");
        title = title.replace(" ", "+");

        String search_response;

        String req;
        if (i == 0)
            req = search_artist + artist;
        else
            req = search_artist + artist + "&p=" + "2" + "&per-page=50";
        search_response = sendGET(req);

        if (search_response.equals("NO INTERNET ACCESS")){
            return "NO INTERNET ACCESS";
        }


        String find = "href=\"/" + artist.substring(0, 1) + '/' + artist + '/' + title;

        int firstIndex = search_response.indexOf(find);
        int lastindex = firstIndex + find.length();

        if (firstIndex == -1){
            return "NOT FOUND";
        }


        while(true){
            if (search_response.substring(lastindex, lastindex + 1).equals("\""))
                break;
            else
                lastindex++;
        }

        String request_page = search_response.substring(firstIndex, lastindex);

        request_page = "https://www.lyricsfreak.com" + request_page.substring(6, request_page.length());

        String response_page = sendGET(request_page);
        if (response_page.equals("NO INTERNET ACCESS")){
            return "NO INTERNET ACCESS";
        }

        find = "lyrictxt js-lyrics js-share-text-content";

        firstIndex = response_page.indexOf(find);
        lastindex = firstIndex + find.length();
        while(true){

            if (response_page.substring(lastindex, lastindex + 3).equals("div"))
                break;
            else
                lastindex++;
        }


        String parse = response_page.substring(firstIndex, lastindex);
        firstIndex = parse.indexOf(">");
        parse = parse.substring(firstIndex + 1, parse.length() - 2);
        parse = parse.trim();

        parse = parse.replace("<br />", "");

        parse = parse.replace("&#039;", "'");

        lyric += parse;

//        System.out.println(lyric);

        return lyric;

    }

    private String sendGET(String address) throws IOException {
        String res = "";
        try {


            System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
            URL obj = new URL(address);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", USER_AGENT);
            int responseCode = con.getResponseCode();
//            System.out.println("GET Response Code :: " + responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) { // success
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {

//                System.out.println(inputLine);

                    response.append(inputLine + '\n');
                }
                in.close();

                // print result
//            System.out.println(response.toString());

                res = response.toString();
            } else {
                System.out.println("GET request not worked");
            }

        } catch (UnknownHostException e){
            return "NO INTERNET ACCESS";
        }
        return res;
    }


    public void downloadManager() throws IOException{

        String lyric = "NOT FOUND";
        for (int i = 0; i < 10; i++){
            lyric = downloader(i);
            if (!lyric.equals("NOT FOUND"))
                break;
            if (lyric.equals("NO INTERNET ACCESS"))
                break;
        }

        lyrics = lyric;
//        System.out.println(lyric);
    }

    public void run(){
        try {
            downloadManager();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
