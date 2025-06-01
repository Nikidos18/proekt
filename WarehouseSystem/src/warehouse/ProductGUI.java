package warehouse;

import javax.swing.*;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class ProductGUI extends JFrame {
    private ArrayList<Product> products;
    private JTable table;
    private DefaultTableModel tableModel;

    public ProductGUI() {
        products = new ArrayList<>();
        setTitle("Система за управление на склад");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 450);
        setLocationRelativeTo(null);

        tableModel = new DefaultTableModel(new Object[]{"Код", "Име", "Количество", "Цена", "Производител", "Контакт"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        JButton addButton = new JButton("Добави");
        JButton editButton = new JButton("Редактирай");
        JButton deleteButton = new JButton("Изтрий");
        JButton saveButton = new JButton("Запази");
        JButton loadButton = new JButton("Зареди");
        JButton sortButton = new JButton("Сортирай по количество");

        JTextField searchField = new JTextField(15);
        JButton searchButton = new JButton("Търси");

        panel.add(addButton);
        panel.add(editButton);
        panel.add(deleteButton);
        panel.add(saveButton);
        panel.add(loadButton);
        panel.add(sortButton);
        panel.add(new JLabel("Търси по код, име или производител:"));
        panel.add(searchField);
        panel.add(searchButton);

        add(scrollPane, BorderLayout.CENTER);
        add(panel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> {
            AddEditDialog dialog = new AddEditDialog(this, null);
            dialog.setVisible(true);
            Product p = dialog.getResultProduct();
            if (p != null) {
                products.add(p);
                tableModel.addRow(new Object[]{
                        p.getCode(),
                        p.getName(),
                        p.getQuantity(),
                        p.getPrice(),
                        p.getManufacturer().getName(),
                        p.getManufacturer().getContact()
                });
            }
        });

        editButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Моля, изберете продукт за редакция.", "Грешка", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Product selectedProduct = products.get(selectedRow);
            AddEditDialog dialog = new AddEditDialog(this, selectedProduct);
            dialog.setVisible(true);
            Product editedProduct = dialog.getResultProduct();
            if (editedProduct != null) {
                products.set(selectedRow, editedProduct);
                tableModel.setValueAt(editedProduct.getCode(), selectedRow, 0);
                tableModel.setValueAt(editedProduct.getName(), selectedRow, 1);
                tableModel.setValueAt(editedProduct.getQuantity(), selectedRow, 2);
                tableModel.setValueAt(editedProduct.getPrice(), selectedRow, 3);
                tableModel.setValueAt(editedProduct.getManufacturer().getName(), selectedRow, 4);
                tableModel.setValueAt(editedProduct.getManufacturer().getContact(), selectedRow, 5);
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Моля, изберете продукт за изтриване.", "Грешка", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Сигурни ли сте, че искате да изтриете избрания продукт?",
                    "Потвърждение", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                products.remove(selectedRow);
                tableModel.removeRow(selectedRow);
            }
        });

        saveButton.addActionListener(e -> saveToFile());
        loadButton.addActionListener(e -> loadFromFile());
        sortButton.addActionListener(e -> selectionSortByQuantity());
        searchButton.addActionListener(e -> {
            String query = searchField.getText().trim().toLowerCase();
            if (query.isEmpty()) {
                refreshTable();
                return;
            }
            List<Product> filtered = new ArrayList<>();
            for (Product p : products) {
                if (p.getCode().toLowerCase().contains(query) ||
                        p.getName().toLowerCase().contains(query) ||
                        p.getManufacturer().getName().toLowerCase().contains(query)) {
                    filtered.add(p);
                }
            }
            tableModel.setRowCount(0);
            for (Product p : filtered) {
                tableModel.addRow(new Object[]{
                        p.getCode(),
                        p.getName(),
                        p.getQuantity(),
                        p.getPrice(),
                        p.getManufacturer().getName(),
                        p.getManufacturer().getContact()
                });
            }
        });
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Product p : products) {
            tableModel.addRow(new Object[]{
                    p.getCode(),
                    p.getName(),
                    p.getQuantity(),
                    p.getPrice(),
                    p.getManufacturer().getName(),
                    p.getManufacturer().getContact()
            });
        }
    }

    private void selectionSortByQuantity() {
        for (int i = 0; i < products.size() - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < products.size(); j++) {
                if (products.get(j).getQuantity() < products.get(minIndex).getQuantity()) {
                    minIndex = j;
                }
            }
            if (minIndex != i) {
                Product temp = products.get(i);
                products.set(i, products.get(minIndex));
                products.set(minIndex, temp);
            }
        }
        refreshTable();
    }

    private void saveToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter("products.txt"))) {
            for (Product p : products) {
                pw.println(p.getCode() + ";" + p.getName() + ";" + p.getQuantity() + ";" + p.getPrice() + ";" +
                        p.getManufacturer().getName() + ";" + p.getManufacturer().getContact());
            }
            JOptionPane.showMessageDialog(this, "Данните са записани успешно.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Грешка при запис на файла.", "Грешка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader("products.txt"))) {
            products.clear();
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 6) {
                    Manufacturer m = new Manufacturer(parts[4], parts[5]);
                    Product p = new Product(parts[0], parts[1], Integer.parseInt(parts[2]), Double.parseDouble(parts[3]), m);
                    products.add(p);
                }
            }
            refreshTable();
            JOptionPane.showMessageDialog(this, "Данните са заредени успешно.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Грешка при зареждане на файла.", "Грешка", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Грешка във формата на данните.", "Грешка", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ProductGUI gui = new ProductGUI();
            gui.setVisible(true);
        });
    }
}
