package ec.edu.ug.inventarioacademicug;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ec.edu.ug.inventarioacademicug.adapter.MenuAdapter;
import ec.edu.ug.inventarioacademicug.database.InventarioDbHelper;
import ec.edu.ug.inventarioacademicug.model.ItemInventario;
import ec.edu.ug.inventarioacademicug.model.MenuOption;

public class MainActivity extends Activity {

    private TextView tvStudentGreeting;
    private GridView gridMenu;
    private SharedPreferences preferences;
    private List<MenuOption> menuOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences("ConfiguracionUG", MODE_PRIVATE);

        tvStudentGreeting = findViewById(R.id.tvStudentGreeting);
        gridMenu = findViewById(R.id.gridMenu);

        prepareMenuData();
        configureMenuAdapter();
        configureMenuEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Carga automática del nombre del estudiante guardado (RF-02)
        String studentName = preferences.getString("nombre_estudiante", "");
        if (!studentName.isEmpty()) {
            String cadena = getString(R.string.bienvenido) + " " + studentName;
            tvStudentGreeting.setText(cadena);
        } else {
            tvStudentGreeting.setText(getString(R.string.welcome_default));
        }
    }

    // 1. Preparamos las opciones del menú
    private void prepareMenuData() {
        menuOptions = new ArrayList<>();
        menuOptions.add(new MenuOption(1, getString(R.string.btn_new_item), getString(R.string.btn_new_item_description), "➕"));
        menuOptions.add(new MenuOption(2, getString(R.string.btn_view_inventory), getString(R.string.btn_view_inventory_description), "📋"));
        menuOptions.add(new MenuOption(3, getString(R.string.btn_preferences), getString(R.string.btn_preferences_description), "⚙️"));
        menuOptions.add(new MenuOption(4, getString(R.string.btn_generate_report), getString(R.string.btn_generate_report_description), "📄"));
        menuOptions.add(new MenuOption(5, getString(R.string.btn_open_web), getString(R.string.btn_open_web_description), "🌐"));
    }

    // 2. Conectamos los datos con la vista
    private void configureMenuAdapter() {
        MenuAdapter adapter = new MenuAdapter(this, menuOptions);
        gridMenu.setAdapter(adapter);
    }

    // 3. Evaluamos qué tarjeta se presionó usando el ActionId
    private void configureMenuEvents() {
        gridMenu.setOnItemClickListener((parent, view, position, id) -> {
            MenuOption selectedOption = menuOptions.get(position);

            Intent intent = null;
            switch (selectedOption.getActionId()) {
                case 1: // Registrar
                    // Navegación con Intents Explícitos (Requerimiento Técnico)
                    intent = new Intent(this, FormItemActivity.class);
                    startActivity(intent);
                    Toast.makeText(this, R.string.form_item_description, Toast.LENGTH_SHORT).show();
                    break;
                case 2: // Ver Inventario
                    intent = new Intent(this, ListaItemsActivity.class);
                    startActivity(intent);
                    Toast.makeText(this, R.string.lista_items_description, Toast.LENGTH_SHORT).show();
                    break;
                case 3: // Preferencias
                    intent = new Intent(this, PreferenciasActivity.class);
                    startActivity(intent);
                    Toast.makeText(this, R.string.preferencias_description, Toast.LENGTH_SHORT).show();
                    break;
                case 4: // Reporte
                    generarYCompartirReporte();
                    break;
                case 5: // Web
                    Intent intentImplicit = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.ug.edu.ec"));
                    startActivity(intentImplicit);
                    break;
            }
        });
    }

    // Se extrajo la lógica del reporte a un método para mantener el código limpio
    private void generarYCompartirReporte() {
        // 1. Recopilar información para el reporte
        String studentName = preferences.getString("nombre_estudiante", "Estudiante UG");
        String fechaActual = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(new java.util.Date());

        ec.edu.ug.inventarioacademicug.database.InventarioDbHelper dbHelper = new ec.edu.ug.inventarioacademicug.database.InventarioDbHelper(this);
        java.util.List<ec.edu.ug.inventarioacademicug.model.ItemInventario> lista = dbHelper.obtenerTodosLosItems();

        // 2. Construir el texto del reporte (RF-07)
        StringBuilder reporte = new StringBuilder();
        reporte.append("=== REPORTE DE INVENTARIO UG ===\n");
        reporte.append("Responsable: ").append(studentName).append("\n");
        reporte.append("Fecha: ").append(fechaActual).append("\n");
        reporte.append("Total de elementos: ").append(lista.size()).append("\n\n");
        reporte.append("--- DETALLE ---\n");

        for (ec.edu.ug.inventarioacademicug.model.ItemInventario item : lista) {
            reporte.append("- ").append(item.getNombre())
                    .append(" | Cat: ").append(item.getCategoria())
                    .append(" | Cant: ").append(item.getCantidad()).append("\n");
        }

        String contenidoReporte = reporte.toString();

        // 3. Guardar los archivos llamando a nuestro ArchivoHelper
        boolean okInterno = ArchivoHelper.generarReporteInterno(this, contenidoReporte);
        boolean okExterno = ArchivoHelper.generarReporteExterno(this, contenidoReporte);

        if (okInterno && okExterno) {
            Toast.makeText(this, getString(R.string.msg_report_success), Toast.LENGTH_LONG).show();

            // 4. Compartir usando Intent Implícito (RF-09)
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, contenidoReporte);
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_prompt)));

        } else {
            Toast.makeText(this, getString(R.string.msg_report_error), Toast.LENGTH_SHORT).show();
        }
    }
}