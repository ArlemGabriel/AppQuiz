package com.example.appquiz;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    // Best practice: descargar contenido en un thread distinto al thread de UI
    // Primero: tipo de variable que le vamos a mandar a la clases para decirle qué hacer. En este caso, un url
    // Segundo: nombre de método que podríamos usar para mostrar el progreso de la tarea
    // Tercero: tipo de variable que va a ser retornado por la clase
    ArrayList<String> nombresPersonajes = new ArrayList<>();
    ArrayList<String> imagenesPersonajes = new ArrayList<>();
    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;

            // Agregar permiso en AndroidManifest.xml
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);

                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                // Esto es muy estilo C
                // Se lee un caracter a la vez (como cuando se hace gets() en C o C++)
                int data = inputStreamReader.read();
                while (data != -1){
                    char character = (char)data;
                    result += character;
                    data = inputStreamReader.read();
                }

                return result;
            }
            catch (Exception e){
                e.printStackTrace();
                return "Error";
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cargarHTML();

    }
    public void cargarHTML(){
        DownloadTask downloadTask = new DownloadTask();
        String result = null;
        try {
            result = downloadTask.execute("https://listas.20minutos.es/lista/personajes-de-shingeki-no-kyojin-380789/").get();
            parsearHTML(result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        //Log.i("Result", result);
    }
    public void parsearHTML(String html){
        Log.i("ENTRE","PARSEAR");
        if(html!=null) {
            Document doc = Jsoup.parse(html);
            Elements personajes = doc.select("div .picture a img.lazy");
            arregloPersonajes(personajes);
        }else{
            Toast.makeText(this,"El html está vacío",Toast.LENGTH_LONG).show();
        }
    }
    public void arregloPersonajes(Elements personajes){
        int largoPersonajes = personajes.size()-1;
        for(int i=0;i<=largoPersonajes;i++){
            nombresPersonajes.add(personajes.get(i).attr("alt"));
            imagenesPersonajes.add(personajes.get(i).attr("data-original"));
        }
        elegirPersonajes();
        elegirPersonajes();
        elegirPersonajes();
        elegirPersonajes();
        elegirPersonajes();
        elegirPersonajes();
        elegirPersonajes();

    }
    public void iniciarJuego(View view){
        //int personaje2=
    }
    public int numeroAleatorio(int pLargoLista){
        //generador de numeros aleatorios
        Random generadorAleatorios = new Random();
        //Genera un número entre 0 y el largo del arreglo de personajes
        int numero = generadorAleatorios.nextInt(pLargoLista+1);
        return numero;
    }
    public void elegirPersonajes(){
        int largoPersonajes = nombresPersonajes.size()-1;
        int personaje1=numeroAleatorio(largoPersonajes);
        int personaje2=numeroAleatorio(largoPersonajes);
        int personaje3=numeroAleatorio(largoPersonajes);
        int personaje4=numeroAleatorio(largoPersonajes);
        if(personaje2==personaje1){
            while(personaje1==personaje2){
                personaje2=numeroAleatorio(largoPersonajes);
            }
        }
        if(personaje3==personaje1 || personaje3==personaje2){
            while(personaje3==personaje1 || personaje3==personaje2){
                personaje3=numeroAleatorio(largoPersonajes);
            }
        }
        if(personaje4==personaje1 || personaje4==personaje2 || personaje4==personaje3){
            while(personaje4==personaje1 || personaje4==personaje2 || personaje4==personaje3){
                personaje4=numeroAleatorio(largoPersonajes);
            }
        }
        Log.i("PERSONAJE1:",String.valueOf(personaje1));
        Log.i("PERSONAJE2:",String.valueOf(personaje2));
        Log.i("PERSONAJE3:",String.valueOf(personaje3));
        Log.i("PERSONAJE4:",String.valueOf(personaje4));
    }
}

