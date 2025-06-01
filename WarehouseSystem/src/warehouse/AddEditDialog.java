package warehouse;

import javax.swing.*;
import java.awt.*;

public class AddEditDialog extends JDialog {

    private JTextField codeField, nameField, quantityField, priceField, manufacturerField, contactField;
    private Product resultProduct;

    public AddEditDialog(Frame owner, Product product) {
        super(owner, true);
        setTitle(product == null ? "Добавяне на продукт" : "Редактиране на продукт");
        setSize(350, 300);
        setLocationRelativeTo(owner);
        setLayout(new GridLayout(7, 2, 5, 5));

        add(new JLabel("Код:"));
        codeField = new JTextField();
        add(codeField);

        add(new JLabel("Име:"));
        nameField = new JTextField();
        add(nameField);

        add(new JLabel("Количество:"));
        quantityField = new JTextField();
        add(quantityField);

        add(new JLabel("Цена:"));
        priceField = new JTextField();
        add(priceField);

        add(new JLabel("Производител:"));
        manufacturerField = new JTextField();
        add(manufacturerField);

        add(new JLabel("Контакт:"));
        contactField = new JTextField();
        add(contactField);

        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Отказ");
        add(okButton);
        add(cancelButton);

        if (product != null) {
            codeField.setText(product.getCode());
            nameField.setText(product.getName());
            quantityField.setText(String.valueOf(product.getQuantity()));
            priceField.setText(String.valueOf(product.getPrice()));
            manufacturerField.setText(product.getManufacturer().getName());
            contactField.setText(product.getManufacturer().getContact());
        }

        okButton.addActionListener(e -> {
            try {
                String code = codeField.getText().trim();
                String name = nameField.getText().trim();
                String quantityText = quantityField.getText().trim();
                String priceText = priceField.getText().trim();
                String manuName = manufacturerField.getText().trim();
                String manuContact = contactField.getText().trim();

                if (code.isEmpty() || name.isEmpty() || quantityText.isEmpty() || priceText.isEmpty() ||
                        manuName.isEmpty() || manuContact.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Всички полета трябва да са попълнени.");
                    return;
                }

                int quantity = Integer.parseInt(quantityText);
                double price = Double.parseDouble(priceText);

                if (quantity < 0 || price < 0) {
                    JOptionPane.showMessageDialog(this, "Количеството и цената трябва да са положителни.");
                    return;
                }

                Manufacturer m = new Manufacturer(manuName, manuContact);
                resultProduct = new Product(code, name, quantity, price, m);
                dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Моля, въведи валидни числа за количество и цена.");
            }
        });

        cancelButton.addActionListener(e -> {
            resultProduct = null;
            dispose();
        });
    }

    public Product getResultProduct() {
        return resultProduct;
    }
}
