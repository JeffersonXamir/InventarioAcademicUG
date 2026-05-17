package ec.edu.ug.inventarioacademicug;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import ec.edu.ug.inventarioacademicug.database.InventarioDbHelper;
import ec.edu.ug.inventarioacademicug.model.ItemInventario;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ListaItemsActivity extends Activity {

    private EditText etBuscar;
    private Button btnBuscar;
    private ListView lvInventario;

    private InventarioDbHelper dbHelper;
    private List<ItemInventario> listaActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_items);

        dbHelper = new InventarioDbHelper(this);

        bindViews();
        configureSearchEvent();
        configureListClickEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cargamos todos los datos cada vez que la pantalla se vuelva visible
        cargarListaCompleta();
    }

    private void bindViews() {
        etBuscar = findViewById(R.id.etBuscar);
        btnBuscar = findViewById(R.id.btnBuscar);
        lvInventario = findViewById(R.id.lvInventario);
    }

    private void cargarListaCompleta() {
        listaActual = dbHelper.obtenerTodosLosItems();
        actualizarListView();
    }

    private void configureSearchEvent() {
        btnBuscar.setOnClickListener(v -> {
            String query = etBuscar.getText().toString().trim();
            if (query.isEmpty()) {
                cargarListaCompleta(); // Si está vacío, mostramos todo
            } else {
                listaActual = dbHelper.buscarItems(query);
                actualizarListView();
            }
        });
    }

    private void actualizarListView() {
        if (listaActual.isEmpty()) {
            Toast.makeText(this, getString(R.string.msg_empty_list), Toast.LENGTH_SHORT).show();
        }

        // Usamos el layout simple por defecto de Android para listas
        ArrayAdapter<ItemInventario> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                listaActual
        );
        lvInventario.setAdapter(adapter);
    }

    private void configureListClickEvent() {
        lvInventario.setOnItemClickListener((parent, view, position, id) -> {
            // Obtenemos el item seleccionado
            ItemInventario itemSeleccionado = listaActual.get(position);

            //RF-05: Enviar el ID a la pantalla de detalle usando Intent explícito
            Intent intent = new Intent(this, DetalleItemActivity.class);
            intent.putExtra("ITEM_ID", itemSeleccionado.getId());
            startActivity(intent);

            Toast.makeText(this, "Seleccionaste: " + itemSeleccionado.getNombre() + " (Fase 6)", Toast.LENGTH_SHORT).show();
        });
    }
}