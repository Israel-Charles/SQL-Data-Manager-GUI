import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import com.mysql.cj.jdbc.MysqlDataSource;

public class SQLManager extends JFrame {
    private static final int WINDOW_WIDTH = 1200;
    private static final int WINDOW_HEIGHT = 800;
    private static final String NOT_CONNECTED_STATUS = "SQL DATABASE STATUS: NOT CONNECTED";
    private static final String CONNECTED_STATUS = "SQL DATABASE STATUS: CONNECTED";

    private JTextArea commandInputBox;
    private JTable resultDisplay;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;

    // Labels
    private static final String DB_URL_LABEL = "Database URL";
    private static final String USERNAME_LABEL = "Username";
    private static final String PASSWORD_LABEL = "Password";
    private static final String RESULT_WINDOW_LABEL = "SQL Execution Result Window";

    private JTextField dbUrlInput;
    private JTextField usernameInput;
    private JPasswordField passwordInput;

    private Connection connection;

    public SQLManager() {
        // Main Frame setup
        JFrame mainFrame = new JFrame("SQL Data Manager - GUI");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        mainFrame.setLayout(new BorderLayout());

        // Adding components to the main frame
        mainFrame.add(createControlArea(), BorderLayout.NORTH);
        mainFrame.add(createResultArea(), BorderLayout.CENTER);

        // Make the frame visible
        mainFrame.setVisible(true);

        // Automatically connect to operations log database
    }

    private JPanel createControlArea() {
        JPanel controlArea = new JPanel(new GridLayout(1, 2, 50, 0));
        controlArea.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));

        // Create and add connection parameters and command input sections
        controlArea.add(createConnectionParamSection());
        controlArea.add(createCommandInputSection());

        return controlArea;
    }

    private Box createConnectionParamSection() {
        Box connectionParam = new Box(BoxLayout.Y_AXIS);

        // Database URL section
        dbUrlInput = new JTextField("jdbc:mysql://localhost:3306/example_database?");
        JPanel dbUrlField = createLabeledComponent(DB_URL_LABEL, dbUrlInput);

        // Username section
        usernameInput = new JTextField();
        JPanel usernameField = createLabeledComponent(USERNAME_LABEL, usernameInput);

        // Password Section
        passwordInput = new JPasswordField();
        JPanel passwordField = createLabeledComponent(PASSWORD_LABEL, passwordInput);

        // Connection Buttons
        String[] connectionActionButtons = new String[]{"Connect to Database", "Disconnect from Database"};
        JPanel connectionActionGroup = createButtonGroup(connectionActionButtons);

        connectionParam.add(createSectionTitle("Connection Details"));
        connectionParam.add(dbUrlField);
        connectionParam.add(Box.createVerticalStrut(5));
        connectionParam.add(usernameField);
        connectionParam.add(Box.createVerticalStrut(5));
        connectionParam.add(passwordField);
        connectionParam.add(Box.createVerticalStrut(10));
        connectionParam.add(connectionActionGroup);

        return connectionParam;
    }

    private JPanel createCommandInputSection() {
        JPanel commandInput = new JPanel();
        commandInput.setLayout(new BoxLayout(commandInput, BoxLayout.Y_AXIS));

        commandInputBox = new JTextArea(7, 0);
        JPanel commandActionGroup = createButtonGroup(new String[]{"Clear Command", "Export Command", "Execute Command"});

        commandInput.add(createSectionTitle("Enter an SQL Command"));
        commandInput.add(new JScrollPane(commandInputBox));
        commandInput.add(Box.createVerticalStrut(10));
        commandInput.add(commandActionGroup);

        return commandInput;
    }

    private JPanel createResultArea() {
        JPanel resultArea = new JPanel(new BorderLayout());
        resultArea.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));

        // Status field
        JPanel statusField = createStatusField();

        // Result display field
        JPanel resultDisplayField = createResultDisplayField();

        // Result action buttons
        String[] resultActionButtons = new String[]{"Clear Result", "Export Result"};
        JPanel resultActionGroup = createButtonGroup(resultActionButtons);

        resultArea.add(statusField, BorderLayout.NORTH);
        resultArea.add(resultDisplayField, BorderLayout.CENTER);
        resultArea.add(resultActionGroup, BorderLayout.SOUTH);

        return resultArea;
    }

    private JPanel createStatusField() {
        statusLabel = new JLabel(NOT_CONNECTED_STATUS);
        statusLabel.setOpaque(true);
        statusLabel.setBackground(Color.RED);
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("", Font.BOLD, 15));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel statusField = new JPanel(new BorderLayout());
        statusField.add(Box.createVerticalStrut(10), BorderLayout.NORTH);
        statusField.add(statusLabel, BorderLayout.CENTER);
        statusField.add(Box.createVerticalStrut(10), BorderLayout.SOUTH);

        return statusField;
    }

    private JPanel createResultDisplayField() {
        tableModel = new DefaultTableModel();
        resultDisplay = new JTable(tableModel);
        JPanel resultDisplayField = new JPanel(new BorderLayout());
        resultDisplayField.add(createSectionTitle(RESULT_WINDOW_LABEL), BorderLayout.NORTH);
        resultDisplayField.add(new JScrollPane(resultDisplay), BorderLayout.CENTER);

        return resultDisplayField;
    }

    private JPanel createLabeledComponent(String labelText, Component component) {
        JLabel label = new JLabel(labelText);

        // Set a preferred size to ensure consistent width
        int charWidth = label.getFontMetrics(label.getFont()).charWidth('W');
        int labelWidth = charWidth * 11;
        label.setPreferredSize(new Dimension(labelWidth, label.getPreferredSize().height));

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(label, BorderLayout.WEST);
        panel.add(component, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createButtonGroup(String[] buttonNames) {
        JPanel buttonGroupPanel = new JPanel(new GridLayout(1, buttonNames.length, 50, 0));

        for (String buttonName : buttonNames) {
            JButton button = new JButton(buttonName);
            button.addActionListener(new ButtonActionListener(buttonName));
            buttonGroupPanel.add(button);
        }
        return buttonGroupPanel;
    }

    private JPanel createSectionTitle(String titleText) {
        JLabel title = new JLabel(titleText);
        title.setForeground(Color.BLUE);
        title.setFont(new Font("", Font.BOLD, 12));

        JPanel panel = new JPanel();
        panel.add(title);
        return panel;
    }

    private class ButtonActionListener implements ActionListener {
        private final String buttonName;

        public ButtonActionListener(String buttonName) {
            this.buttonName = buttonName;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            switch (buttonName) {
                case "Clear Command":
                    commandInputBox.setText("");
                    break;
                case "Clear Result":
                    tableModel.setRowCount(0);
                    tableModel.setColumnCount(0);
                    break;
                case "Export Command":
                    exportCommand();
                    break;
                case "Export Result":
                    exportResult();
                    break;
                case "Execute Command":
                    executeCommand();
                    break;
                case "Connect to Database":
                    connectToDatabase();
                    break;
                case "Disconnect from Database":
                    disconnectFromDatabase();
                    break;
            }
        }

        private void exportCommand() {
            JFileChooser fileChooser = new JFileChooser("./out");
            fileChooser.setSelectedFile(new File("sqlCommand.txt"));
            int option = fileChooser.showSaveDialog(SQLManager.this);
            if (option == JFileChooser.APPROVE_OPTION) {
                try (FileWriter writer = new FileWriter(fileChooser.getSelectedFile())) {
                    writer.write(commandInputBox.getText());
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(SQLManager.this, "Error saving command: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        private void exportResult() {
            JFileChooser fileChooser = new JFileChooser("./out");
            fileChooser.setSelectedFile(new File("commandResult.csv"));
            int option = fileChooser.showSaveDialog(SQLManager.this);
            if (option == JFileChooser.APPROVE_OPTION) {
                try (FileWriter writer = new FileWriter(fileChooser.getSelectedFile())) {
                    for (int i = 0; i < tableModel.getColumnCount(); i++) {
                        writer.write(tableModel.getColumnName(i) + ",");
                    }
                    writer.write("\n");

                    for (int i = 0; i < tableModel.getRowCount(); i++) {
                        for (int j = 0; j < tableModel.getColumnCount(); j++) {
                            writer.write(tableModel.getValueAt(i, j) + ",");
                        }
                        writer.write("\n");
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(SQLManager.this, "Error saving result: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        private void executeCommand() {
            if (connection == null) {
                JOptionPane.showMessageDialog(SQLManager.this, "No database connection established.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String sql = commandInputBox.getText();
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                boolean hasResultSet = stmt.execute();
                if (hasResultSet) {
                    try (ResultSet rs = stmt.getResultSet()) {
                        ResultSetMetaData metaData = rs.getMetaData();
                        int columnCount = metaData.getColumnCount();
                        String[] columnNames = new String[columnCount];
                        for (int i = 1; i <= columnCount; i++) {
                            columnNames[i - 1] = metaData.getColumnName(i);
                        }
                        tableModel.setColumnIdentifiers(columnNames);

                        tableModel.setRowCount(0); // Clear existing rows

                        while (rs.next()) {
                            Object[] row = new Object[columnCount];
                            for (int i = 1; i <= columnCount; i++) {
                                row[i - 1] = rs.getObject(i);
                            }
                            tableModel.addRow(row);
                        }
                    }
                } else {
                    int updateCount = stmt.getUpdateCount();
                    JOptionPane.showMessageDialog(SQLManager.this, "Update count: " + updateCount, "Update Result", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(SQLManager.this, "Error executing command: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void connectToDatabase() {
            String dbUrl = dbUrlInput.getText();
            String username = usernameInput.getText();
            String password = new String(passwordInput.getPassword());

            if (dbUrl.isEmpty() || username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(SQLManager.this, "Please provide database URL, username, and password.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                MysqlDataSource dataSource = new MysqlDataSource();
                dataSource.setURL(dbUrl);
                dataSource.setUser(username);
                dataSource.setPassword(password);

                connection = dataSource.getConnection();
                statusLabel.setText(CONNECTED_STATUS);
                statusLabel.setBackground(Color.GREEN);
                JOptionPane.showMessageDialog(SQLManager.this, "Connected to the database successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(SQLManager.this, "Error connecting to the database: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void disconnectFromDatabase() {
            if (connection != null) {
                try {
                    connection.close();
                    connection = null;
                    statusLabel.setText(NOT_CONNECTED_STATUS);
                    statusLabel.setBackground(Color.RED);
                    JOptionPane.showMessageDialog(SQLManager.this, "Disconnected from the database.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(SQLManager.this, "Error disconnecting from the database: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(SQLManager.this, "No database connection to disconnect.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            System.err.println(e.toString());
        }
        SwingUtilities.invokeLater(SQLManager::new);
    }
}
