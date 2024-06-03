package com.example.start;
import javafx.util.Pair;

import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


public class DatabaseOperations {
    static private String url = "jdbc:mysql://localhost/";
    static private String urlWithDbName = "jdbc:mysql://localhost/";
    static private final String USER = "root";
    static private final String PASSWORD = "";

    static private void createDatabaseIfNotExist(String dbName) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, USER, PASSWORD);

            preparedStatement = connection.prepareStatement("CREATE DATABASE IF NOT EXISTS " + dbName);
            preparedStatement.executeUpdate();
            urlWithDbName += dbName;
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Error while creating database");
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.out.println("Error while creating database");
            }
        }
    }
    static public void increaseScoreInDb(String dbName, String tableName, String username) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        createDatabaseIfNotExist(dbName);

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Establish a connection
            connection = DriverManager.getConnection(urlWithDbName, USER, PASSWORD);

            // Prepare SQL query to check if the table exists
            String sql = "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = ? AND table_name = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, connection.getCatalog()); // Set the current database name
            preparedStatement.setString(2, tableName);

            // Execute the query
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                if (count > 0) {
                    System.out.println("Table " + tableName + " exists.");
                } else {
                    System.out.println("Table " + tableName + " does not exist.\nCreating table " + tableName);
                    sql = "CREATE TABLE " + tableName + "(" +
                            "username varchar(150) PRIMARY KEY not NULL," +
                            "score int(4) default 0 not NULL" +
                            ");";
                    preparedStatement.executeUpdate(sql);
                }
                updateScore(username, tableName, connection);
            }
        } catch (SQLException | ClassNotFoundException ex) {
            System.out.println("Error while creating a table");
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    static private void updateScore(String username, String tableName, Connection connection){
        PreparedStatement preparedStatement = null;
        try{
            String sql = "SELECT * FROM " + tableName + " WHERE username = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                sql = "UPDATE "+ tableName +" SET score = score + 1 WHERE username = ?";
                PreparedStatement update = connection.prepareStatement(sql);
                update.setString(1, username);
                update.executeUpdate();
                update.close();
                System.out.println("Score updated");
            }
            else{
                sql = "INSERT INTO "+tableName+" VALUES(?, 1)";
                PreparedStatement update = connection.prepareStatement(sql);
                update.setString(1, username);
                update.executeUpdate();
                update.close();
                System.out.println("new user was insert into a database");
            }
        }catch(SQLException e){
            System.out.println("Error while increasing score in Database");
        }finally {
            try{
                if(preparedStatement != null) {
                    preparedStatement.close();
                }
            }catch (SQLException e){
                System.out.println("Error while closing preparedStatement");
            }
        }
    }

    static public ObservableList<LeaderboardEntry> getLeaderboard(String dbName, String tableName) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url+dbName, USER, PASSWORD);

            String sql = "SELECT username, score FROM "+tableName+" ORDER BY score DESC LIMIT 11";
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            ObservableList<LeaderboardEntry> result = FXCollections.observableArrayList();

            while(resultSet.next()){
                result.add(new LeaderboardEntry(resultSet.getString(1), resultSet.getInt(2)));
            }
            return result;

        } catch (SQLException e) {
            System.out.println("Leaderboard is empty");
            return null;
        } catch (ClassNotFoundException e) {
            return null;
        }finally {
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
