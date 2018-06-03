package com.aniket.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> celebsurl = new ArrayList<String>();
    ArrayList<String> celebnames = new ArrayList<String>();
    int chosenCeleb;
    ImageView imageView;
    int locationOfCorrectAnswer;
   String[] answer = new String[4] ;
   Button button;
    Button button2;
    Button button3;
    Button button4;

    public  void  celebChosen(View view){

        if (view.getTag().toString().equals(Integer.toString(locationOfCorrectAnswer))){
            Toast.makeText(getApplicationContext(),"Correct!",Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getApplicationContext(),"Wrong! It was :" + celebnames.get(chosenCeleb),Toast.LENGTH_LONG).show();
        }
        generateQuestion();
    }

    public  class  ImageDownload extends  AsyncTask<String,Void,Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {

            URL url = null;
            try {
                url = new URL(urls[0]);
                HttpURLConnection connection= (HttpURLConnection)url.openConnection();

                connection.connect();
                InputStream inputStream = connection.getInputStream();

                Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);

                return  myBitmap;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }
    }

    public  class  DownloadTask extends AsyncTask<String,Void,String>   {

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;
            try{

                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data= reader.read();
                while (data != -1){
                    char current= (char) data;

                    result += current;

                    data = reader.read();
                }
                return  result;
            }
            catch (Exception e){
                e.printStackTrace();
            }


            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.imageView);

        button = (Button) findViewById(R.id.button);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        button4 = (Button) findViewById(R.id.button4);

        DownloadTask task = new DownloadTask();
        String result = null;

        try {

            result = task.execute("http://www.posh24.se/kandisar").get();

            String[] splitresult = result.split("<div class=\"sidebarInnerContainer\">");

            Pattern p = Pattern.compile("<img src=\"(.*?)\"");
            Matcher m = p.matcher(splitresult[0]);

            while (m.find()) {
                celebsurl.add(m.group(1));
            }
            p = Pattern.compile("alt=\"(.*?)\"");
            m = p.matcher(splitresult[0]);

            while (m.find()) {
                celebnames.add(m.group(1));
            }


        } catch (InterruptedException e) {

            e.printStackTrace();

        } catch (ExecutionException e) {

            e.printStackTrace();
        }
        generateQuestion();
    }

        public void generateQuestion(){

        Random random = new Random();
        chosenCeleb = random.nextInt(celebsurl.size());

        ImageDownload imageTask = new ImageDownload();
        Bitmap celebImage;

            try {
                celebImage = imageTask.execute(celebsurl.get(chosenCeleb)).get();

                imageView.setImageBitmap(celebImage);

                locationOfCorrectAnswer = random.nextInt(4);
                int incorrectAnswer;
                for (int  i=0;i<4;i++){

                    if (i== locationOfCorrectAnswer){
                        answer[i]= celebnames.get(chosenCeleb);
                    }else{
                        incorrectAnswer = random.nextInt(celebsurl.size());

                        while (incorrectAnswer==chosenCeleb){
                            incorrectAnswer = random.nextInt(celebsurl.size());
                        }

                        answer[i] = celebnames.get(incorrectAnswer);
                    }
                }

                button.setText(answer[0]);

                button2.setText(answer[1]);

                button3.setText(answer[2]);

                button4.setText(answer[3]);





            } catch (Exception e) {
                e.printStackTrace();
            }



    }
    }

