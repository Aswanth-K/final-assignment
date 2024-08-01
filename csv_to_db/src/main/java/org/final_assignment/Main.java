package org.final_assignment;

import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

public class Main {
    public static void main(String[] args) {
        String jdbcURL = "jdbc:postgresql://localhost:5432/customer_db";
        String username = "postgres";
        String password = "123456";

        String csvFilePath = "C:\\Users\\Lenovo\\IdeaProjects\\csv_to_db\\customers.csv";

        Connection connection = null;

        try {
            connection = DriverManager.getConnection(jdbcURL, username, password);
            connection.setAutoCommit(false);

            CSVReader reader = new CSVReaderBuilder(new FileReader(csvFilePath)).withSkipLines(1).build();
            String[] nextLine;

            while ((nextLine = reader.readNext()) != null) {
                String customerId = nextLine[1];
                String firstName = nextLine[2].toUpperCase();
                String lastName = nextLine[3].toUpperCase();
                String city = nextLine[5];
                String country = nextLine[6];
                String personalPhnum = nextLine[7];
                String workPhnum = nextLine[8];
                String personalEmail = nextLine[9].toLowerCase();
                String subscriptionDate = nextLine[10];
                String website = nextLine[11];

                // Insert into customer_info
                String insertCustomerInfoSQL = "INSERT INTO customer_info (customer_id, f_name, l_name, city, country, personal_phnum, personal_email) VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement pstmt = connection.prepareStatement(insertCustomerInfoSQL);
                pstmt.setString(1, customerId);
                pstmt.setString(2, firstName);
                pstmt.setString(3, lastName);
                pstmt.setString(4, city);
                pstmt.setString(5, country);
                pstmt.setString(6, personalPhnum);
                pstmt.setString(7, personalEmail);

                int customerInfoRows = pstmt.executeUpdate();
                System.out.println("Inserted into customer_info: " + customerInfoRows + " rows.");

                String fullName = firstName + " " + lastName;
                String workEmail = firstName.toLowerCase() + "_" + lastName.toLowerCase() + "@sample.com";

                String insertCustomerWorkInfoSQL = "INSERT INTO customer_work_info (customer_id, full_name, office_loc, subscription_date, website, work_phnum, work_email) VALUES (?, ?, 'Bangalore', ?, ?, ?, ?)";
                PreparedStatement pstmtWork = connection.prepareStatement(insertCustomerWorkInfoSQL);
                pstmtWork.setString(1, customerId);
                pstmtWork.setString(2, fullName);
                pstmtWork.setDate(3, java.sql.Date.valueOf(subscriptionDate));
                pstmtWork.setString(4, website);
                pstmtWork.setString(5, workPhnum);
                pstmtWork.setString(6, workEmail);

                int customerWorkInfoRows = pstmtWork.executeUpdate();
                System.out.println("Inserted into customer_work_info: " + customerWorkInfoRows + " rows.");
            }

            // Commit transaction
            connection.commit();
            System.out.println("Transaction committed successfully.");

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
            if (connection != null) {
                try {
                    connection.rollback();
                    System.out.println("Transaction rolled back.");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
