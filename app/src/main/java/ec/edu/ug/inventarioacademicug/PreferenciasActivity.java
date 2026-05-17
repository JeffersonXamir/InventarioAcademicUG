package ec.edu.ug.inventarioacademicug;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PreferenciasActivity extends Activity {

    private EditText etStudentName, etCourse;
    private Switch switchGreeting;
    private Button btnSavePreferences;

    // Objeto para manejar la persistencia
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferencias);

        // Instanciamos el mismo archivo de preferencias que usamos en MainActivity
        preferences = getSharedPreferences("ConfiguracionUG", MODE_PRIVATE);

        bindViews();
        loadExistingPreferences();
        configureSaveEvent();
    }

    private void bindViews() {
        etStudentName = findViewById(R.id.etStudentName);
        etCourse = findViewById(R.id.etCourse);
        switchGreeting = findViewById(R.id.switchGreeting);
        btnSavePreferences = findViewById(R.id.btnSavePreferences);
    }

    private void loadExistingPreferences() {
        // Leemos los datos guardados; si no hay nada, devuelve el valor por defecto (vacío o true)
        etStudentName.setText(preferences.getString("nombre_estudiante", ""));
        etCourse.setText(preferences.getString("paralelo_estudiante", ""));
        switchGreeting.setChecked(preferences.getBoolean("mostrar_saludo", true));
    }

    private void configureSaveEvent() {
        btnSavePreferences.setOnClickListener(v -> {
            // Obtenemos el editor para poder escribir en el archivo XML de SharedPreferences
            SharedPreferences.Editor editor = preferences.edit();

            editor.putString("nombre_estudiante", etStudentName.getText().toString().trim());
            editor.putString("paralelo_estudiante", etCourse.getText().toString().trim());
            editor.putBoolean("mostrar_saludo", switchGreeting.isChecked());

            // apply() guarda los cambios de forma asíncrona (buena práctica)
            editor.apply();

            Toast.makeText(this, getString(R.string.msg_preferences_saved), Toast.LENGTH_SHORT).show();

            // Cerramos la actividad para volver al menú principal
            finish();
        });
    }
}