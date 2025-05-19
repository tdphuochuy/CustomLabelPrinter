package buttons;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class SequenceHourPopup extends JDialog {
    private DefaultTableModel tableModel;
    private JTable table;
    private boolean applied = false;

    public SequenceHourPopup(JFrame parent,Map<String, TableEntry> dataMap ) {
        super(parent, "Setup Hour/Sequence", true);
        setSize(500, 350);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        // === Table Model ===
        tableModel = new DefaultTableModel(new Object[] {"Product Code", "Hour", "Sequence"}, 0);
        table = new JTable(tableModel);

        // === Load existing map entries ===
        for (Map.Entry<String, TableEntry> entry : dataMap.entrySet()) {
            tableModel.addRow(new Object[] {
                entry.getKey(),
                entry.getValue().getHour(),
                entry.getValue().getSequence()
            });
        }

        // === Manual Entry Panel ===
        JPanel addPanel = new JPanel(new FlowLayout());
        JTextField productCodeField = new JTextField(8);
        JTextField hourField = new JTextField(4);
        JTextField sequenceField = new JTextField(4);
        JButton addButton = new JButton("Add");
        addButton.setBackground(Color.WHITE);

        addPanel.add(new JLabel("Product Code:"));
        addPanel.add(productCodeField);
        addPanel.add(new JLabel("Hour:"));
        addPanel.add(hourField);
        addPanel.add(new JLabel("Sequence:"));
        addPanel.add(sequenceField);
        addPanel.add(addButton);

        // === Add Button Logic ===
        addButton.addActionListener(e -> {
            String code = productCodeField.getText().trim();
            String hourText = hourField.getText().trim();
            String sequenceText = sequenceField.getText().trim();

            if (code.isEmpty() || hourText.isEmpty() || sequenceText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields must be filled.", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                int hour = Integer.parseInt(hourText);
                int sequence = Integer.parseInt(sequenceText);
                tableModel.addRow(new Object[] { code, hour, sequence });

                productCodeField.setText("");
                hourField.setText("");
                sequenceField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Hour and Sequence must be integers.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // === Apply Button ===
        JButton saveBtn = new JButton("Save");
        saveBtn.setBackground(Color.WHITE);
        saveBtn.addActionListener(e -> {
            dataMap.clear();
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                String code = (String) tableModel.getValueAt(i, 0);
                String hour = tableModel.getValueAt(i, 1).toString();
                String sequence = tableModel.getValueAt(i, 2).toString();
                dataMap.put(code, new TableEntry(hour, sequence));
            }
            applied = true;
            dispose();
        });
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveBtn);
        // === Layout ===
        add(addPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public boolean isApplied() {
        return applied;
    }
}
