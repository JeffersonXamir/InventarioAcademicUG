package ec.edu.ug.inventarioacademicug.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ec.edu.ug.inventarioacademicug.R;
import ec.edu.ug.inventarioacademicug.model.MenuOption;
public class MenuAdapter extends ArrayAdapter<MenuOption> {

    private final LayoutInflater inflater;

    public MenuAdapter(Activity activity, List<MenuOption> options) {
        super(activity, R.layout.item_grid_menu, options);
        inflater = activity.getLayoutInflater();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;
        if (itemView == null) {
            itemView = inflater.inflate(R.layout.item_grid_menu, parent, false);
        }

        MenuOption option = getItem(position);
        TextView tvIcon = itemView.findViewById(R.id.tvMenuIcon);
        TextView tvTitle = itemView.findViewById(R.id.tvMenuTitle);
        TextView tvDesc = itemView.findViewById(R.id.tvMenuDesc);

        if (option != null) {
            tvIcon.setText(option.getShortLabel());
            tvTitle.setText(option.getTitle());
            tvDesc.setText(option.getDescription());
        }

        return itemView;
    }
}