package ec.edu.ug.inventarioacademicug;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ec.edu.ug.inventarioacademicug.database.InventarioDbHelper;
import ec.edu.ug.inventarioacademicug.model.ItemInventario;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class FormItemActivity extends Activity {

    private EditText etNombre, etCategoria, etCantidad, etUbicacion, etObservacion;
    private Button btnGuardarItem;
    private InventarioDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_item);

        dbHelper = new InventarioDbHelper(this);

        bindViews();
        configureSaveEvent();
    }

    private void bindViews() {
        etNombre = findViewById(R.id.etNombre);
        etCategoria = findViewById(R.id.etCategoria);
        etCantidad = findViewById(R.id.etCantidad);
        etUbicacion = findViewById(R.id.etUbicacion);
        etObservacion = findViewById(R.id.etObservacion);
        btnGuardarItem = findViewById(R.id.btnGuardarItem);
    }

    private void configureSaveEvent() {
        btnGuardarItem.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String categoria = etCategoria.getText().toString().trim();
            String cantidadTexto = etCantidad.getText().toString().trim();
            String ubicacion = etUbicacion.getText().toString().trim();
            String observacion = etObservacion.getText().toString().trim();

            // 1. Validaciones obligatorias
            if (nombre.isEmpty()) {
                etNombre.setError(getString(R.string.error_required));
                return;
            }
            if (categoria.isEmpty()) {
                etCategoria.setError(getString(R.string.error_required));
                return;
            }

            int cantidad = 0;
            try {
                cantidad = Integer.parseInt(cantidadTexto);
                if (cantidad <= 0) {
                    etCantidad.setError(getString(R.string.error_quantity));
                    return;
                }
            } catch (NumberFormatException e) {
                etCantidad.setError(getString(R.string.error_required));
                return;
            }

            // 2. Obtener fecha actual
            String fechaActual = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

            // 3. Crear objeto modelo
            ItemInventario nuevoItem = new ItemInventario(0, nombre, categoria, cantidad, ubicacion, observacion, fechaActual);

            // 4. Guardar en SQLite
            long id = dbHelper.insertarItem(nuevoItem);

            if (id != -1) {
                Toast.makeText(this, "Elemento guardado con éxito", Toast.LENGTH_SHORT).show();
                finish(); // Cierra el formulario y vuelve al menú
            } else {
                Toast.makeText(this, "Error al guardar el elemento", Toast.LENGTH_SHORT).show();
            }
        });
    }
}