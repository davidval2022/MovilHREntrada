package davidvalentin.ioc.hrentradaclienteapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import davidvalentin.ioc.hrentradaclienteapp.utilidades.SocketManager;
import davidvalentin.ioc.hrentradaclienteapp.utilidades.Utilidades;

public class EmpresaInsertActivity extends AppCompatActivity {

    private Socket socket;
    private String nombreTabla = "2";//empresa es 2
    private String crud = "1";//inserts es el codigo crud 1
    private String orden = "0";//orden no lo utilizamos por lo tanto es siempres será 0
    private EditText editTextNom;
    private EditText editTextAddress;
    private EditText editTextTelefono;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //agrego esta linea de abajo para que mantega la pantalla en vertical y tiene que ir justa aquí
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa_insert);

        socket = Utilidades.socketManager.getSocket();

        editTextNom = findViewById(R.id.editTextNom);
        editTextAddress = findViewById(R.id.editTextAddress);
        editTextTelefono = findViewById(R.id.editTextTelefono);


    }

    public void insertarEmpresa(View view){

        try {

            if (socket != null && socket.isConnected()) {
                BufferedReader lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter escriptor = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                ObjectInputStream perEnt;

                String codigo = "0";
                String nombre = "0";
                String address = "0";
                String telephon = "0";

                nombre = editTextNom.getText().toString();
                address = editTextAddress.getText().toString();
                telephon = editTextTelefono.getText().toString();

                if(telephon.equals("") || telephon.equals(" ") || telephon.equals(null)){
                    telephon = "0";
                }

                if(!nombre.equals("") || address.equals("")){
                    String palabra = Utilidades.codigo+","+crud+","+nombreTabla+",nom,"+nombre+",address,"+address+",telephon,"+telephon+","+orden;
                    //ahora escribimos en servidor , enviandole el login
                    escriptor.write(palabra);
                    escriptor.newLine();
                    escriptor.flush();
                    Log.d("Enviado", "Le enviamos esto al server: "+palabra);
                    if(palabra.equalsIgnoreCase("exit")){
                        lector.close();
                        escriptor.close();
                        socket.close();
                    }else{
                        perEnt = new ObjectInputStream(socket.getInputStream());
                        //leemos los datos del objeto y comprobamos que sea un arrayList, sino un String
                        Object receivedData = perEnt.readObject();

                        if (receivedData instanceof List) {
                           // Utilidades.listaEmpleados = (ArrayList) receivedData;
                            Utilidades.mensajeDelServer = "Se ha creado correctamente el registro";
                        } else if (receivedData instanceof String) {
                            Utilidades.mensajeDelServer = (String) receivedData;
                        } else {
                            Utilidades.mensajeDelServer ="Datos inesperados recibidos del servidor";
                        }
                        mostrarToast(Utilidades.mensajeDelServer );

                    }
                }else{
                    mostrarToast("Molt malament, tienes que insertar el nombre y direccion de la empresa" );
                }





            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public  void mostrarToast(String mensaje){
        Toast.makeText(this, ""+mensaje, Toast.LENGTH_LONG).show();


    }

    public void reset(View view) {
    }

    public void volver(View view) {
        Intent intent = new Intent(this, EmpresasActivity.class);
        startActivity(intent);

    }




}