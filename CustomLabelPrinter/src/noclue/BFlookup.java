package noclue;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import config.Config;
import paperwork.marel.paperworkMarelGen;

public class BFlookup extends JPanel{
	private JFrame frame;
	private boolean isVerified = false;
	public BFlookup(JFrame frame) throws IOException, ParseException {
    	this.frame = frame;
    	this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JSONParser jsonParser = new JSONParser();
		FileReader reader = new FileReader("backflush.json");
	    JSONObject obj = (JSONObject)jsonParser.parse(reader);
	    
    	// Parse JSON into Map<String, List<String>>
        ObjectMapper mapper = new ObjectMapper();
        Map<String, List<String>> data = mapper.readValue(obj.toJSONString(), new TypeReference<>() {});

        // Sort keys numerically if possible (otherwise lexicographically)
        List<String> keys = new ArrayList<>(data.keySet());
        keys.sort((a, b) -> {
            try {
                return Integer.compare(Integer.parseInt(a), Integer.parseInt(b));
            } catch (NumberFormatException nfe) {
                return a.compareTo(b);
            }
        });

        // --- Master table: Keys ---
        DefaultTableModel keysModel = new DefaultTableModel(new Object[]{"Product code"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (String k : keys) keysModel.addRow(new Object[]{k});

        JTable keysTable = new JTable(keysModel);
        keysTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        keysTable.setRowHeight(22);

        // --- Detail table: Items for selected key ---
        DefaultTableModel itemsModel = new DefaultTableModel(new Object[]{"Backflush codes"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable itemsTable = new JTable(itemsModel);
        itemsTable.setRowHeight(22);

        // When user selects a key, populate items table
        keysTable.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (e.getValueIsAdjusting()) return;
            int row = keysTable.getSelectedRow();
            if (row < 0) {
                setItems(itemsModel, List.of());
                return;
            }
            String key = String.valueOf(keysModel.getValueAt(row, 0));
            List<String> items = data.getOrDefault(key, List.of());
            setItems(itemsModel, items);
        });
        
        TableRowSorter<DefaultTableModel> keysSorter = new TableRowSorter<>(keysModel);
        keysTable.setRowSorter(keysSorter);
        
        JTextField lookupField = new JTextField(9);
        setPlaceholder(lookupField,"Product code");

        lookupField.getDocument().addDocumentListener(new DocumentListener() {
            private void update() {
                SwingUtilities.invokeLater(() -> {
                    String text = lookupField.getText().trim();
                    if (text.isEmpty() || text.equals("Product code")) {
                        keysSorter.setRowFilter(null);
                    } else {
                        keysSorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(text), 0));
                    }

                    // keep the right table in sync after filtering
                    if (keysTable.getRowCount() > 0) {
                        if (keysTable.getSelectedRow() < 0) {
                            keysTable.setRowSelectionInterval(0, 0);
                        }
                    } else {
                        setItems(itemsModel, List.of());
                    }
                });
            }
            @Override public void insertUpdate(DocumentEvent e) { update(); }
            @Override public void removeUpdate(DocumentEvent e) { update(); }
            @Override public void changedUpdate(DocumentEvent e) { update(); }
        });

        // Preselect first key
        if (keysModel.getRowCount() > 0) keysTable.setRowSelectionInterval(0, 0);


    	JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(keysTable),
                new JScrollPane(itemsTable)
        );
    	splitPane.setResizeWeight(0.35);
    	
    	splitPane.setPreferredSize(new Dimension(550, 310)); // choose what you want
    	splitPane.setMinimumSize(new Dimension(550, 310));

    	JPanel wrapper = new JPanel(new GridBagLayout());
    	GridBagConstraints gbc = new GridBagConstraints();
    	gbc.gridx = 0;
    	gbc.gridy = 0;
    	gbc.weightx = 0;              // <-- key: do NOT expand horizontally
    	gbc.weighty = 1;
    	gbc.fill = GridBagConstraints.VERTICAL; // grow only vertically
    	gbc.anchor = GridBagConstraints.NORTHWEST;
    	wrapper.add(splitPane, gbc);
    	
    	JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    	top.add(lookupField);
    	top.setBorder(new EmptyBorder(0, 0, 6, 0));

    	this.add(top, BorderLayout.NORTH);
    	
    	this.add(wrapper,BorderLayout.CENTER);
		
	}
	
	private static void setItems(DefaultTableModel itemsModel, List<String> items) {
        itemsModel.setRowCount(0);
        for (String it : items) {
            // If you want to remove the trailing ".0", uncomment:
            // it = it.endsWith(".0") ? it.substring(0, it.length() - 2) : it;
            itemsModel.addRow(new Object[]{it});
        }
    }
	
    // Method to set the placeholder text
    public static void setPlaceholder(JTextField textField, String placeholderText) {
        // Set initial placeholder
        textField.setText(placeholderText);
        textField.setForeground(Color.GRAY); // Set the placeholder text color

        // Add FocusListener to manage the prompt behavior
        textField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                // When the user clicks on the text field, clear the placeholder if it's still there
                if (textField.getText().equals(placeholderText)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK); // Set the text color to normal when typing
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                // When the text field loses focus, reset the placeholder if the field is empty
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholderText);
                    textField.setForeground(Color.GRAY); // Set the placeholder text color back
                }
            }
        });
    }
}