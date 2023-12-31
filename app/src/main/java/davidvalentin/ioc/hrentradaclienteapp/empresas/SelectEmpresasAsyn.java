package davidvalentin.ioc.hrentradaclienteapp.empresas;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import davidvalentin.ioc.hrentradaclienteapp.login.SocketManager;
import davidvalentin.ioc.hrentradaclienteapp.utilidades.Utilidades;
import modelo.Empresa;

/**
 * @author David Valentin Mateo
 * La clase `SelectEmpresasAsyn` es una subclase de `AsyncTask` diseñada para realizar operaciones
 * de consulta en segundo plano en un servidor utilizando sockets. Esta clase maneja la obtención de datos
 * desde el servidor y actualiza un `RecyclerView` con la lista de empresas obtenida.
 *
 * @param \<String\> Tipo de parámetro de entrada para el método `doInBackground`, que representa la operación CRUD.
 * @param \<Void\> Tipo de parámetro de progreso para el método `onProgressUpdate` (no utilizado en esta implementación).
 * @param \<ArrayList\<Empresa\>\> Tipo de resultado devuelto por el método `doInBackground` y pasado al método `onPostExecute`,
 *                              que representa la lista de empresas obtenida del servidor.
 */


public class SelectEmpresasAsyn extends AsyncTask<String, Void, ArrayList<Empresa>> {

    // Gestor de sockets para manejar la conexión con el servidor
    private SocketManager socketManager;

    // Contexto de la aplicación
    private Context context;

    // Detalles de la consulta al servidor
    private String nombreTabla;
    private String columna;
    private String filtro;
    private String orden;
    private String crud;

    // Socket utilizado para la conexión con el servidor
    private Socket socket;

    // Componentes de la interfaz de usuario
    private  RecyclerView recycler;
    private AdaptadorEmpresas mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    //variable que enviaremos en un bundle a la activty UpdateDeleteActivity
    private Empresa empresa;




    /**
     * Constructor de la clase `SelectEmpresasAsyn`.
     *
     * @param socketManager Instancia de `SocketManager` utilizada para gestionar la conexión del socket.
     * @param context       Contexto de la aplicación utilizado para interactuar con la interfaz de usuario.
     * @param crud          Operación CRUD a realizar en el servidor (Create, Read, Update, Delete).
     * @param nombreTabla   Nombre de la tabla en la base de datos del servidor.
     * @param columna       Nombre de la columna a utilizar en la consulta.
     * @param filtro        Filtro a aplicar en la consulta.
     * @param orden         Orden de los resultados de la consulta.
     * @param recycler      Instancia de `RecyclerView` para mostrar la lista de empresas.
     * @param mAdapter      Adaptador para el `RecyclerView`.
     * @param layoutManager Administrador de diseño para el `RecyclerView`.
     */

    public SelectEmpresasAsyn(SocketManager socketManager, Context context, String crud, String nombreTabla, String columna, String filtro, String orden, RecyclerView recycler
    , AdaptadorEmpresas mAdapter, RecyclerView.LayoutManager layoutManager) {
        this.socketManager = socketManager;
        this.context = context;
        this.nombreTabla = nombreTabla;
        this.columna = columna;
        this.filtro = filtro;
        this.orden = orden;
        this.crud = crud;
        this.recycler = recycler;
        this.mAdapter = mAdapter;
        this.layoutManager = layoutManager;


    }


    /**
     * Método que se ejecuta en segundo plano para realizar la consulta al servidor y obtener la lista de empresas.
     *
     * @param params Parámetros de entrada que representan la operación CRUD.
     * @return Lista de empresas obtenida del servidor.
     */
    @Override
    protected ArrayList<Empresa> doInBackground(String... params) {

        try {
            // Obtiene el socket del SocketManager
            socket = socketManager.getSocket();

            // Verifica si el socket no es nulo y está conectado
            if (socket != null && socket.isConnected()) {
                // Configura los flujos de entrada y salida del socket
                BufferedReader lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter escriptor = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                ObjectInputStream perEnt;

                //String mensajeServer = lector.readLine();   //leemos el mensaje de bienvenidoa del server
                // Construye la cadena de consulta
                String palabra = Utilidades.codigo+","+crud+","+nombreTabla+","+columna+","+filtro+","+orden;
                //ahora escribimos en servidor , enviandole el login
                // Envia la cadena de consulta al servidor
                escriptor.write(palabra);
                escriptor.newLine();
                escriptor.flush();
                Log.d("Enviado", "Le enviamos esto al server: "+palabra);

                // Verifica si la cadena es "exit" y cierra los flujos y el socket
                if(palabra.equalsIgnoreCase("exit")){
                    lector.close();
                    escriptor.close();
                    socket.close();
                }else{
                    // Lee los datos del servidor y actualiza la lista de empresas
                    perEnt = new ObjectInputStream(socket.getInputStream());
                    Object receivedData = perEnt.readObject();

                    if (receivedData instanceof List) {
                        Utilidades.listaEmpresas = (ArrayList) receivedData;
                        Utilidades.mensajeDelServer = "";
                    } else if (receivedData instanceof String) {
                        Utilidades.mensajeDelServer = (String) receivedData;
                    } else {
                        Utilidades.mensajeDelServer ="Datos inesperados recibidos del servidor";
                    }
                }
                return Utilidades.listaEmpresas;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null ;
    }


    /**
     * Método que se ejecuta en el hilo principal después de que se completa la tarea en segundo plano.
     *
     * @param result Lista de empresas obtenida del servidor.
     */
    @Override
    protected void onPostExecute(ArrayList<Empresa> result) {
        // Configura el RecyclerView con la lista de empresas
        super.onPostExecute(result);
        empresa = new Empresa();

        layoutManager = new LinearLayoutManager(context);
        recycler.setLayoutManager(layoutManager);

        // Crea un adaptador y lo establece en el RecyclerView
        mAdapter = new AdaptadorEmpresas(Utilidades.listaEmpresas);
        mAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context,"Seleccion: "+Utilidades.listaEmpresas.get(recycler.getChildAdapterPosition(v)).getNom(),Toast.LENGTH_SHORT).show();
                //empresa.setNom(Utilidades.listaEmpresas.get(recycler.getChildAdapterPosition(v)).getNom());
                //empresa.setAddress(Utilidades.listaEmpresas.get(recycler.getChildAdapterPosition(v)).getAddress());
                //empresa.setTelephon(Utilidades.listaEmpresas.get(recycler.getChildAdapterPosition(v)).getTelephon());

                empresa = Utilidades.listaEmpresas.get(recycler.getChildAdapterPosition(v));
                Log.d("update: ",empresa.getNom());
                Intent intent=new Intent(context, UpdateDeleteEmpresaActivity.class);
                // Agregar el flag FLAG_ACTIVITY_NEW_TASK para que me deje enviar los datos a una
                //activity desde una clase que no es una activity
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Bundle bundle = new Bundle();
                bundle.putSerializable("empresa",empresa);
                intent.putExtras(bundle);
                context.startActivity(intent);

            }
        });
        recycler.setAdapter(mAdapter);

        // Muestra un Toast si hay un mensaje del servidor
        if(!Utilidades.mensajeDelServer.equals("")){
            mostrarToast(context.getApplicationContext(),Utilidades.mensajeDelServer );
        }


    }

    /**
     * Método estático para mostrar un Toast en la aplicación.
     *
     * @param context Contexto de la aplicación.
     * @param mensaje Mensaje a mostrar en el Toast.
     */

    public static void mostrarToast(Context context, String mensaje){
        Toast.makeText(context, ""+mensaje, Toast.LENGTH_SHORT).show();
    }




}
