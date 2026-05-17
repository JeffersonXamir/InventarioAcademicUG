package ec.edu.ug.inventarioacademicug.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;
import android.database.Cursor;

import ec.edu.ug.inventarioacademicug.model.ItemInventario;
public class InventarioDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "InventarioUG.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_INVENTARIO = "inventario";

    public InventarioDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Creación de la tabla según las especificaciones del requerimiento
        String createTable = "CREATE TABLE " + TABLE_INVENTARIO + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT NOT NULL, " +
                "categoria TEXT NOT NULL, " +
                "cantidad INTEGER NOT NULL, " +
                "ubicacion TEXT, " +
                "observacion TEXT, " +
                "fecha_registro TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INVENTARIO);
        onCreate(db);
    }

    // Método para insertar usando ContentValues (Requisito Obligatorio)
    public long insertarItem(ItemInventario item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("nombre", item.getNombre());
        values.put("categoria", item.getCategoria());
        values.put("cantidad", item.getCantidad());
        values.put("ubicacion", item.getUbicacion());
        values.put("observacion", item.getObservacion());
        values.put("fecha_registro", item.getFechaRegistro());

        long id = db.insert(TABLE_INVENTARIO, null, values);
        db.close();
        return id;
    }

    // Método para obtener todos los registros (RF-04)
    public List<ItemInventario> obtenerTodosLosItems() {
        List<ItemInventario> listaItems = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_INVENTARIO, null);

        if (cursor.moveToFirst()) {
            do {
                ItemInventario item = new ItemInventario(
                        cursor.getInt(0),    // id
                        cursor.getString(1), // nombre
                        cursor.getString(2), // categoria
                        cursor.getInt(3),    // cantidad
                        cursor.getString(4), // ubicacion
                        cursor.getString(5), // observacion
                        cursor.getString(6)  // fechaRegistro
                );
                listaItems.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return listaItems;
    }

    // Método para buscar por nombre o categoría (RF-06)
    public List<ItemInventario> buscarItems(String query) {
        List<ItemInventario> listaItems = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Buscamos coincidencias en nombre o categoría
        String selection = "nombre LIKE ? OR categoria LIKE ?";
        String[] selectionArgs = new String[]{"%" + query + "%", "%" + query + "%"};

        Cursor cursor = db.query(TABLE_INVENTARIO, null, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                ItemInventario item = new ItemInventario(
                        cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                        cursor.getInt(3), cursor.getString(4), cursor.getString(5), cursor.getString(6)
                );
                listaItems.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return listaItems;
    }

    // Método para obtener un elemento específico por su ID
    public ItemInventario obtenerItemPorId(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_INVENTARIO, null, "id=?", new String[]{String.valueOf(id)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            ItemInventario item = new ItemInventario(
                    cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                    cursor.getInt(3), cursor.getString(4), cursor.getString(5), cursor.getString(6)
            );
            cursor.close();
            return item;
        }
        return null;
    }

    // Método para actualizar usando ContentValues (RF-05)
    public int actualizarItem(ItemInventario item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("nombre", item.getNombre());
        values.put("categoria", item.getCategoria());
        values.put("cantidad", item.getCantidad());
        values.put("ubicacion", item.getUbicacion());
        values.put("observacion", item.getObservacion());
        // Nota: Mantenemos la fecha_registro original, no la actualizamos.

        return db.update(TABLE_INVENTARIO, values, "id=?", new String[]{String.valueOf(item.getId())});
    }

    // Método para eliminar un registro
    public void eliminarItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_INVENTARIO, "id=?", new String[]{String.valueOf(id)});
        db.close();
    }
}
