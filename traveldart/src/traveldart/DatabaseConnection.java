/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package traveldart;
import java.sql.*;
/**
 *
 * @author diego
 */
public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://ce0lkuo944ch99.cluster-czrs8kj4isg7.us-east-1.rds.amazonaws.com:5432/d9ed3kam9kvm1d";
    private static final String USER = "uatce3m0vi941q";
    private static final String PASSWORD = "p4de089d14c53298bd1044127e034bb2e622f2929793b8b2251b2a0385b7159ef";

    public static Connection connect() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to the database!");
        } catch (SQLException e) {
            System.err.println("Connection failed: " + e.getMessage());
        }
        return connection;
    }
    
    public static void printAllRowsForTable(String tableName) {
            try{
                 String query = "SELECT * FROM "+tableName; // Replace 'your_table_name' with the table you want to query
            
            Connection connection = connect();
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            

            // Get metadata to retrieve column information
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Print column names
            for (int i = 1; i <= columnCount; i++) {
                System.out.print(metaData.getColumnName(i) + "\t");
            }
            System.out.println();

            // Print rows
            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(resultSet.getString(i) + "\t");
                }
                System.out.println();
            }
            }catch(SQLException e){
                
                System.out.println(e);
            }
           
    }
    
     public static void printAllTableNames() {
        Connection connection = null;
        ResultSet resultSet = null;

        try {
            // Establish connection
            connection = connect();
            if (connection == null) {
                System.out.println("Failed to establish a database connection.");
                return;
            }

            // Get database metadata
            DatabaseMetaData metaData = connection.getMetaData();

            // Retrieve table names
            resultSet = metaData.getTables(null, null, "%", new String[]{"TABLE"});
            System.out.println("Tables in the database:");

            while (resultSet.next()) {
                String tableName = resultSet.getString("TABLE_NAME");
                System.out.println(tableName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close resources
            try {
                if (resultSet != null) resultSet.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
