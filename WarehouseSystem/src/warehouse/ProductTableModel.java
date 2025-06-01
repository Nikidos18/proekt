package warehouse;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class ProductTableModel extends AbstractTableModel {
    private final String[] columnNames = {"Код", "Име", "Количество", "Цена", "Производител", "Контакт"};
    private final List<Product> products;

    public ProductTableModel(List<Product> products) {
        this.products = products;
    }

    @Override
    public int getRowCount() {
        return products.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Product p = products.get(rowIndex);
        switch (columnIndex) {
            case 0: return p.getCode();
            case 1: return p.getName();
            case 2: return p.getQuantity();
            case 3: return p.getPrice();
            case 4: return p.getManufacturer().getName();
            case 5: return p.getManufacturer().getContact();
            default: return null;
        }
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }
}
