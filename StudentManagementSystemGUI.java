import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class StudentManagementSystemGUI extends JFrame {
    // Components
    private JButton studentButton;
    private JButton branchButton;
    private JButton feePaymentButton;
    private JButton examResultButton;

    // Database connection
    private Connection conn;

    // Constructor
    public StudentManagementSystemGUI() {
        // Set frame properties
        setTitle("RUAS Student Management System");
        setSize(400, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        // Initialize components
        studentButton = new JButton("Student Details");
        branchButton = new JButton("Branch Details");
        feePaymentButton = new JButton("Fee Payment");
        examResultButton = new JButton("Exam Results");

        // Add components to the frame
        add(studentButton);
        add(branchButton);
        add(feePaymentButton);
        add(examResultButton);

        // Add action listeners to buttons
        studentButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayTableData("student");
            }
        });

        branchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayTableData("branch");
            }
        });

        feePaymentButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayTableData("feepayment");
            }
        });

        examResultButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayTableData("examresult");
            }
        });

        // Connect to the database
        connectToDatabase();
    }

    // Method to establish database connection
    private void connectToDatabase() {
        try {
            // Load the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Database connection parameters
            String url = "jdbc:mysql://localhost:3306/RUAS";
            String username = "root";
            String password = "root";

            // Connect to the database
            conn = DriverManager.getConnection(url, username, password);
            System.out.println("Database connection successful");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to connect to the database.");
            System.exit(0);
        }
    }

    // Method to display data from a specific table
    private void displayTableData(String tableName) {
        // Create a new JFrame for displaying table data
        JFrame tableFrame = new JFrame(tableName + " Details");
        tableFrame.setSize(600, 400);
        tableFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Create a table to display data
        JTable table = new JTable();
        JScrollPane scrollPane = new JScrollPane(table);

        // Fetch data from the specified table
        try {
            // Create a SQL statement
            Statement stmt = conn.createStatement();

            // Execute the query to fetch data from the specified table
            ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName);

            // Populate the table with data
            table.setModel(buildTableModel(rs));

            // Close the result set and statement
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to fetch data from the database.");
        }

        // Create a button to add data to the table
        JButton addButton = new JButton("Add Data");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addDataToTable(tableName);
            }
        });

        // Add components to the frame
        tableFrame.add(scrollPane, BorderLayout.CENTER);
        tableFrame.add(addButton, BorderLayout.SOUTH);

        // Set the table frame visible
        tableFrame.setVisible(true);
    }

    // Method to convert ResultSet to TableModel
    private static TableModel buildTableModel(ResultSet rs) throws SQLException {
        // Get metadata from the result set
        ResultSetMetaData metaData = rs.getMetaData();

        // Get column count
        int columnCount = metaData.getColumnCount();

        // Create a DefaultTableModel to hold the data
        DefaultTableModel tableModel = new DefaultTableModel();

        // Add columns to the table model
        for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
            tableModel.addColumn(metaData.getColumnLabel(columnIndex));
        }

        // Add rows to the table model
        while (rs.next()) {
            Object[] row = new Object[columnCount];
            for (int i = 0; i < columnCount; i++) {
                row[i] = rs.getObject(i + 1);
            }
            tableModel.addRow(row);
        }

        return tableModel;
    }

    // Method to add data to a specific table
    private void addDataToTable(String tableName) {
        // Prompt user for input data
        String input = JOptionPane.showInputDialog(this, "Enter data separated by commas:");

        if (input != null && !input.isEmpty()) {
            // Split input into values
            String[] values = input.split(",");

            // Construct SQL query to insert data
            StringBuilder sql = new StringBuilder("INSERT INTO ");
            sql.append(tableName).append(" VALUES (");
            for (int i = 0; i < values.length; i++) {
                sql.append("'").append(values[i]).append("'");
                if (i < values.length - 1) {
                    sql.append(", ");
                }
            }
            sql.append(")");

            try {
                // Create a SQL statement
                Statement stmt = conn.createStatement();

                // Execute the query to insert data into the specified table
                int rowsAffected = stmt.executeUpdate(sql.toString());

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Data added successfully.");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add data to the database.");
                }

                // Close the statement
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to add data to the database.");
            }
        }
    }

    // Main method to run the program
    public static void main(String[] args) {
        // Create and display the GUI
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                StudentManagementSystemGUI gui = new StudentManagementSystemGUI();
                gui.setVisible(true);
            }
        });
    }
}
