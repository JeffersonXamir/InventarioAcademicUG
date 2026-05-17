package ec.edu.ug.inventarioacademicug;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ec.edu.ug.inventarioacademicug.database.InventarioDbHelper;
import ec.edu.ug.inventarioacademicug.model.ItemInventario;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DetalleItemActivity extends Activity {

    private EditText etNombre, etCategoria, etCantidad, etUbicacion, etObservacion;
    private Button btnActualizarItem, btnEliminarItem;

    private InventarioDbHelper dbHelper;
    private ItemInventario itemActual;
    private int itemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_item);

        dbHelper = new InventarioDbHelper(this);

        // Recuperar el ID enviado desde el ListView
        itemId = getIntent().getIntExtra("ITEM_ID", -1);

        bindViews();
        cargarDatos();
        configureActionEvents();
    }

    private void bindViews() {
        etNombre = findViewById(R.id.etDetalleNombre);
        etCategoria = findViewById(R.id.etDetalleCategoria);
        etCantidad = findViewById(R.id.etDetalleCantidad);
        etUbicacion = findViewById(R.id.etDetalleUbicacion);
        etObservacion = findViewById(R.id.etDetalleObservacion);
        btnActualizarItem = findViewById(R.id.btnActualizarItem);
        btnEliminarItem = findViewById(R.id.btnEliminarItem);
    }

    private void cargarDatos() {
        if (itemId != -1) {
            itemActual = dbHelper.obtenerItemPorId(itemId);
            if (itemActual != null) {
                etNombre.setText(itemActual.getNombre());
                etCategoria.setText(itemActual.getCategoria());
                etCantidad.setText(String.valueOf(itemActual.getCantidad()));
                etUbicacion.setText(itemActual.getUbicacion());
                etObservacion.setText(itemActual.getObservacion());
            } else {
                Toast.makeText(this, "Error al cargar el elemento", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void configureActionEvents() {
        // Lógica de Actualización
        btnActualizarItem.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String categoria = etCategoria.getText().toString().trim();
            String cantidadTexto = etCantidad.getText().toString().trim();

            if (nombre.isEmpty() || categoria.isEmpty() || cantidadTexto.isEmpty()) {
                Toast.makeText(this, "Por favor llena los campos obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int cantidad = Integer.parseInt(cantidadTexto);
                if (cantidad <= 0) {
                    Toast.makeText(this, getString(R.string.error_quantity), Toast.LENGTH_SHORT).show();
                    return;
                }

                // Actualizamos el modelo
                itemActual.setNombre(nombre);
                itemActual.setCategoria(categoria);
                itemActual.setCantidad(cantidad);
                itemActual.setUbicacion(etUbicacion.getText().toString().trim());
                itemActual.setObservacion(etObservacion.getText().toString().trim());

                // Guardamos los cambios en SQLite
                int filasAfectadas = dbHelper.actualizarItem(itemActual);
                if (filasAfectadas > 0) {
                    Toast.makeText(this, "Inventario actualizado", Toast.LENGTH_SHORT).show();
                    finish(); // Regresa al listado
                }

            } catch (NumberFormatException e) {
                Toast.makeText(this, "Cantidad inválida", Toast.LENGTH_SHORT).show();
            }
        });

        // Lógica de Eliminación (RF-05 requiere AlertDialog)
        btnEliminarItem.setOnClickListener(v -> mostrarDialogoConfirmacion());
    }

    private void mostrarDialogoConfirmacion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_title_delete);
        builder.setMessage(R.string.dialog_msg_delete);

        builder.setPositiveButton(R.string.btn_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dbHelper.eliminarItem(itemId);
                Toast.makeText(DetalleItemActivity.this, "Elemento eliminado", Toast.LENGTH_SHORT).show();
                finish(); // Regresa al listado
            }
        });

        builder.setNegativeButton(R.string.btn_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); // Cierra el diálogo sin hacer nada
            }
        });

        builder.create().show();
    }
}