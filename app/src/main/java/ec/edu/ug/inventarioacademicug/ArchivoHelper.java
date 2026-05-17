package ec.edu.ug.inventarioacademicug;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class ArchivoHelper {

    // RF-07: Escribir en Memoria Interna
    public static boolean generarReporteInterno(Context context, String contenido) {
        try {
            // Se crea o sobrescribe el archivo en la memoria interna de la app
            FileOutputStream fos = context.openFileOutput("resumen_interno.txt", Context.MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            osw.write(contenido);
            osw.flush();
            osw.close();
            return true;
        } catch (Exception e) {
            Log.e("ArchivoHelper", "Error al escribir archivo interno", e);
            return false;
        }
    }

    // RF-08: Escribir en Memoria Externa propia de la app
    public static boolean generarReporteExterno(Context context, String contenido) {
        try {
            // getExternalFilesDir(null) obtiene la ruta externa propia de la app (no requiere permisos extra)
            File directorio = context.getExternalFilesDir(null);
            if (directorio != null) {
                File archivo = new File(directorio, "reporte_inventario.txt");
                FileOutputStream fos = new FileOutputStream(archivo);
                OutputStreamWriter osw = new OutputStreamWriter(fos);
                osw.write(contenido);
                osw.flush();
                osw.close();
                return true;
            }
            return false;
        } catch (Exception e) {
            Log.e("ArchivoHelper", "Error al escribir archivo externo", e);
            return false;
        }
    }
}