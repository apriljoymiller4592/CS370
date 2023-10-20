public class databaseHandler {
    private static final String dbClassname = "com.mysql.cj.jdbc.Driver";    //assume JDBC driver is installed
    public boolean databaseInitialConnection(String CONNECTION, Properties properties)
    {
        try {
            Connection connection = DriverManager.getConnection(CONNECTION, properties);
            return true;
        }
        catch (Exception e)
        {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }
    
    private void promptDBLogin(Stage primaryStage) {
        primaryStage.setTitle("JavaFX Login");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(5);
        grid.setHgap(5);

        Label nameLabel = new Label("Username:");
        TextField nameInput = new TextField();
        nameInput.setText("root"); // Set default username
        GridPane.setConstraints(nameLabel, 0, 0);
        GridPane.setConstraints(nameInput, 1, 0);

        Label passLabel = new Label("Password:");
        PasswordField passInput = new PasswordField();
        passInput.setPromptText("Enter your password");
        GridPane.setConstraints(passLabel, 0, 1);
        GridPane.setConstraints(passInput, 1, 1);

        Button submitButton = new Button("Submit");
        GridPane.setConstraints(submitButton, 1, 2);
        
        submitButton.setOnAction(e -> {
            System.out.println("Username: " + nameInput.getText());
            System.out.println("Password: " + passInput.getText());
            
            // Add your SQL server connection logic here
            String CONNECTION = ""; // Your connection string
            Properties properties = new Properties();
            properties.setProperty("user", nameInput.getText());
            properties.setProperty("password", passInput.getText());

            if(databaseInitialConnection(CONNECTION, properties)) {
                System.out.println("Successfully connected to the database!");
            } else {
                System.out.println("Failed to connect to the database.");
            }
        });

        grid.getChildren().addAll(nameLabel, nameInput, passLabel, passInput, submitButton);

        Scene scene = new Scene(grid, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
