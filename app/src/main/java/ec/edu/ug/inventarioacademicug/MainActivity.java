package ec.edu.ug.inventarioacademicug;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends Activity {

    private TextView tvStudentGreeting;
    private Button btnNewItem, btnViewInventory, btnPreferences, btnGenerateReport, btnOpenWeb;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences("ConfiguracionUG", MODE_PRIVATE);

        bindViews();
        configureNavigationEvents();
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

    private void bindViews() {
        tvStudentGreeting = findViewById(R.id.tvStudentGreeting);
        btnNewItem = findViewById(R.id.btnNewItem);
        btnViewInventory = findViewById(R.id.btnViewInventory);
        btnPreferences = findViewById(R.id.btnPreferences);
        btnGenerateReport = findViewById(R.id.btnGenerateReport);
        btnOpenWeb = findViewById(R.id.btnOpenWeb);
    }

    private void configureNavigationEvents() {
        // Navegación con Intents Explícitos (Requerimiento Técnico)
        btnNewItem.setOnClickListener(v -> {
            Intent intent = new Intent(this, FormItemActivity.class);
            startActivity(intent);
            Toast.makeText(this, "Ir a Formulario de Registro", Toast.LENGTH_SHORT).show();
        });

        btnViewInventory.setOnClickListener(v -> {
            Intent intent = new Intent(this, ListaItemsActivity.class);
            startActivity(intent);
            Toast.makeText(this, "Ir al Listado del Inventario", Toast.LENGTH_SHORT).show();
        });

        btnPreferences.setOnClickListener(v -> {
            Intent intent = new Intent(this, PreferenciasActivity.class);
            startActivity(intent);
            Toast.makeText(this, "Ir a Preferencias", Toast.LENGTH_SHORT).show();
        });

        btnGenerateReport.setOnClickListener(v -> {
            Toast.makeText(this, "Funcionalidad de Reportes (Próxima Fase)", Toast.LENGTH_SHORT).show();
        });

        // Intent Implícito para abrir la página web institucional (RF-01)
        btnOpenWeb.setOnClickListener(v -> {
            String url = "https://www.ug.edu.ec";
            Intent intentImplicit = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intentImplicit);
        });

        btnGenerateReport.setOnClickListener(v -> {
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
        });
    }
}