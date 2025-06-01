package warehouse;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

public class ProductGUI extends JFrame {
    private ArrayList<Product> productList = new ArrayList<>();
    private JTable table;
    private ProductTableModel tableModel;

    public ProductGUI() {
        setTitle("Система за управление на склад");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        tableModel = new ProductTableModel(productList);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttons = new JPanel();
        JButton addBtn = new JButton("Добави");
        JButton loadBtn = new JButton("Зареди");
        JButton saveBtn = new JButton("Запази");

        addBtn.addActionListener(e -> addProduct());
        loadBtn.addActionListener(e -> loadProducts());
        saveBtn.addActionListener(e -> saveProducts());

        buttons.add(addBtn);
        buttons.add(loadBtn);
        buttons.add(saveBtn);
        add(buttons, BorderLayout.SOUTH);
    }

    private void addProduct() {
        try {
            String code = JOptionPane.showInputDialog(this, "Код:");
            String name = JOptionPane.showInputDialog(this, "Име:");
            int quantity = Integer.parseInt(JOptionPane.showInputDialog(this, "Количество:"));
            double price = Double.parseDouble(JOptionPane.showInputDialog(this, "Цена:"));
            String manufacturerName = JOptionPane.showInputDialog(this, "Производител:");
            String contact = JOptionPane.showInputDialog(this, "Контакт:");
            if (code.isEmpty() || name.isEmpty() || manufacturerName.isEmpty() || contact.isEmpty())
                throw new IllegalArgumentException("Полетата не могат да бъдат празни!");
            if (quantity < 0 || price < 0)
                throw new IllegalArgumentException("Невалидни стойности за количество или цена!");
            Manufacturer m = new Manufacturer(manufacturerName, contact);
            Product p = new Product(code, name, quantity, price, m);
            productList.add(p);
            tableModel.fireTableDataChanged();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Грешка: " + ex.getMessage());
        }
    }

    private void loadProducts() {
        try (BufferedReader br = new BufferedReader(new FileReader("products.txt"))) {
            productList.clear();
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 6) {
                    String code = parts[0], name = parts[1];
                    int qty = Integer.parseInt(parts[2]);
                    double price = Double.parseDouble(parts[3]);
                    Manufacturer m = new Manufacturer(parts[4], parts[5]);
                    productList.add(new Product(code, name, qty, price, m));
                }
            }
            tableModel.fireTableDataChanged();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Грешка при зареждане!");
        }
    }

    private void saveProducts() {
        try (PrintWriter pw = new PrintWriter(new FileWriter("products.txt"))) {
            for (Product p : productList)
                pw.println(p.toString());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Грешка при запис!");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ProductGUI().setVisible(true));
    }
}
