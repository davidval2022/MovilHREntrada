package davidvalentin.ioc.hrentradaclienteapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import davidvalentin.ioc.hrentradaclienteapp.utilidades.LogoutAsyn;
import davidvalentin.ioc.hrentradaclienteapp.utilidades.SelectEmpleadosAsyn;
import davidvalentin.ioc.hrentradaclienteapp.utilidades.Utilidades;

public class MenuAdminActivity extends AppCompatActivity {
    private TextView mensajeNombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_admin);
        mensajeNombre = findViewById(R.id.textViewMensajeConNombre);
        mensajeNombre.setText("BIENVENIDO "+Utilidades.nombreUser.toUpperCase());
    }

    public void logout(View view) {
        try{
            LogoutAsyn logout = new LogoutAsyn(Utilidades.socketManager,getApplicationContext());
            logout.execute();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            Log.d("Correcto","Voy a LogoutAsyn ");
        }catch(Exception e){
            Log.d("Error","Errores en logout: "+e);
        }
    }

    public void empleados(View view){
        Intent intent = new Intent(this, EmpleadosActivity.class);
        startActivity(intent);
    }

    public void users(View view){
        //Toast.makeText(this, "Mensaje: "+"Estoy en el menu de users.. y el metodo", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, UsersActivity.class);
        startActivity(intent);
    }

    public void empresas(View view) {
        Intent intent = new Intent(this,  EmpresasActivity.class);
        startActivity(intent);
    }

    public void jornadas(View view){
        Intent intent = new Intent(this,  JornadasActivity.class);
        startActivity(intent);
    }
}