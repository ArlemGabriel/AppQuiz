package com.example.appquiz;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
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
    int personajeActual;
    String personajeActualStr;
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
    public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {


            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.connect();
                InputStream inputStream = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
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
        iniciarJuego();
    }
    public void iniciarJuego(){
        ArrayList<Integer> personajesElegidos = new ArrayList<Integer>();
        ArrayList<String> personajes= new ArrayList<String>();

        personajesElegidos = elegirPersonajesAleatorios();
        personajes = obtenerNombresPersonajes(personajesElegidos);

        establecerTextoBotones(personajes);

        int largoPersonajes = personajesElegidos.size()-1;
        int posicionPersonaje = numeroAleatorio(largoPersonajes);
        personajeActual = personajesElegidos.get(posicionPersonaje);
        personajeActualStr = personajes.get(posicionPersonaje);

        String imagenPersonaje = obtenerImagenPersonaje(personajeActual);
        establecerImagenPersonaje(imagenPersonaje);

    }
    public int numeroAleatorio(int pLargoLista){
        //generador de numeros aleatorios
        Random generadorAleatorios = new Random();
        //Genera un número entre 0 y el largo del arreglo de personajes
        int numero = generadorAleatorios.nextInt(pLargoLista+1);
        return numero;
    }
    public ArrayList<Integer> elegirPersonajesAleatorios(){
        ArrayList<Integer> personajesEscogidos = new ArrayList<Integer>();
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
        personajesEscogidos.add(personaje1);
        personajesEscogidos.add(personaje2);
        personajesEscogidos.add(personaje3);
        personajesEscogidos.add(personaje4);

        return personajesEscogidos;
    }
    public ArrayList<String> obtenerNombresPersonajes(ArrayList<Integer> personajesElegidos){
        ArrayList<String> personajes= new ArrayList<String>();
        int largoEscogidos = personajesElegidos.size()-1;
        for(int i=0;i<=largoEscogidos;i++){
            int posicion = personajesElegidos.get(i);
            personajes.add(nombresPersonajes.get(posicion));
        }
        return personajes;
    }
    public String obtenerImagenPersonaje(int posicionImagen){
        String imagenPersonaje = imagenesPersonajes.get(posicionImagen);
        return  imagenPersonaje;
    }
    public void establecerTextoBotones(ArrayList<String> personajes){
        Button btnOp1 = (Button)findViewById(R.id.btnOpcion1);
        Button btnOp2 = (Button)findViewById(R.id.btnOpcion2);
        Button btnOp3 = (Button)findViewById(R.id.btnOpcion3);
        Button btnOp4 = (Button)findViewById(R.id.btnOpcion4);


        btnOp1.setText(personajes.get(0));
        btnOp2.setText(personajes.get(1));
        btnOp3.setText(personajes.get(2));
        btnOp4.setText(personajes.get(3));
    }
    public void establecerImagenPersonaje(String imagenPersonaje){
        ImageView imagen = (ImageView)findViewById(R.id.imgPersonaje);
        ImageDownloader imageDownloader = new ImageDownloader();
        try {
            Bitmap image = imageDownloader.execute(imagenPersonaje).get();
            imagen.setImageBitmap(image);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void btnOp1Clicked(View view){
        Button btnOp1 = (Button)findViewById(R.id.btnOpcion1);
        if(btnOp1.getText() == personajeActualStr){
            btnOp1.setBackgroundResource(R.drawable.correcto);
        }else{
            btnOp1.setBackgroundResource(R.drawable.incorrecto);
        }
    }
    public void btnOp2Clicked(View view){
        Button btnOp2 = (Button)findViewById(R.id.btnOpcion2);
        if(btnOp2.getText() == personajeActualStr){
            btnOp2.setBackgroundResource(R.drawable.correcto);
        }else{
            btnOp2.setBackgroundResource(R.drawable.incorrecto);
        }
    }
    public void btnOp3Clicked(View view){
        Button btnOp3 = (Button)findViewById(R.id.btnOpcion3);
        if(btnOp3.getText() == personajeActualStr){
            btnOp3.setBackgroundResource(R.drawable.correcto);
        }else{
            btnOp3.setBackgroundResource(R.drawable.incorrecto);
        }
    }
    public void btnOp4Clicked(View view){
        Button btnOp4 = (Button)findViewById(R.id.btnOpcion4);
        if(btnOp4.getText() == personajeActualStr){
            btnOp4.setBackgroundResource(R.drawable.correcto);
        }else{
            btnOp4.setBackgroundResource(R.drawable.incorrecto);
        }
    }
}

