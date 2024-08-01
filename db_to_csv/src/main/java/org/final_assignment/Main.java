package org.final_assignment;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String jdbcURL = "jdbc:postgresql://localhost:5432/customer_db";
        String username = "postgres";
        String password = "123456";

        Scanner scanner = new Scanner(System.in);
        System.out.print("ENTER CUSTOME NAME:: ");
        String userInput = scanner.nextLine().toUpperCase() + "%";

        String csvFilePath = "customer_details.csv";
        try (Connection connection = DriverManager.getConnection(jdbcURL, username, password);
             BufferedWriter fileWriter = new BufferedWriter(new FileWriter(csvFilePath))) {

            String query = "SELECT ci.customer_id, "
                    + "cw.full_name, "
                    + "ci.city, "
                    + "ci.personal_phnum, "
                    + "ci.personal_email, "
                    + "cw.office_loc, "
                    + "cw.subscription_date, "
                    + "cw.work_phnum, "
                    + "cw.work_email "
                    + "FROM customer_info ci "
                    + "JOIN customer_work_info cw ON ci.customer_id = cw.customer_id "
                    + "WHERE UPPER(cw.full_name) LIKE ?";

            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, userInput);

            ResultSet resultSet = pstmt.executeQuery();

            // Write header
            String header = "customer_id,full_name,city,personal_phnum,personal_email,office_loc,subscription_date,pending_days,work_phnum,work_email";
            fileWriter.write(header);
            fileWriter.newLine();

            while (resultSet.next()) {
                String customerId = resultSet.getString("customer_id");
                String fullName = resultSet.getString("full_name");
                String city = resultSet.getString("city");
                String personalPhnum = resultSet.getString("personal_phnum");
                String personalEmail = resultSet.getString("personal_email");
                String officeLoc = resultSet.getString("office_loc");
                LocalDate subscriptionDate = resultSet.getDate("subscription_date").toLocalDate();
                long pendingDays = ChronoUnit.DAYS.between(subscriptionDate, LocalDate.now());
                String workPhnum = resultSet.getString("work_phnum");
                String workEmail = resultSet.getString("work_email");

                // Format row
                String row = String.join(",",
                        customerId,
                        fullName,
                        city,
                        personalPhnum,
                        personalEmail,
                        officeLoc,
                        subscriptionDate.toString(),
                        String.valueOf(pendingDays),
                        workPhnum,
                        workEmail
                );
                fileWriter.write(row);
                fileWriter.newLine();
            }

            System.out.println("CSV file created successfully: " + csvFilePath);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}
