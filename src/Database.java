import java.sql.*;

public class Database {

    public static void createNewDatabase(String fileName) {

        String url = "jdbc:sqlite:" + fileName;

        try {
            Connection conn = DriverManager.getConnection(url);
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private Connection connect() {
        // SQLite connection string
        String url = "jdbc:sqlite:Testrunner.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public static void createNewTable() {
        // SQLite connection string
        String url = "jdbc:sqlite:Testrunner.db";

        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS Results (\n"
                + " start varchar not null ,\n"
                + " finish Varchar NOT NULL,\n"
                + " tests integer not null, \n"
                + " success integer not null, \n"
                + " fail integer not null \n"
                + ");";

        try{
            Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void insert(String start, String finish, Integer tests, Integer success, Integer fail) {
        String sql = "INSERT INTO Results VALUES(?,?,?,?,?)";

        try{
            Connection conn = this.connect();
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, start);
            preparedStatement.setString(2, finish);
            preparedStatement.setInt(3,tests);
            preparedStatement.setInt(4, success);
            preparedStatement.setInt(5, fail);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

}
