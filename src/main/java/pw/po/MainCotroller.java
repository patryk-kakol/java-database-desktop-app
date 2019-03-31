package pw.po;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MainCotroller {

    //FXML View objects
    @FXML
    private Button refreshCityListButton;
    @FXML
    private Label statusLabel;
    @FXML
    private TableView<CityModel> cityTableView;
    @FXML
    private TableColumn<CityModel, String> idColumn;
    @FXML
    private TableColumn<CityModel, String> nameColumn;
    @FXML
    private TableColumn<CityModel, String> countryCodeColumn;
    @FXML
    private TableColumn<CityModel, String> districtColumn;
    @FXML
    private TableColumn<CityModel, String> populationColumn;
    @FXML
    private TextField addCityNameTextField;
    @FXML
    private TextField addCityDistrictTextField;
    @FXML
    private TextField addCityPopulationTextField;
    @FXML
    private Button addCityButton;
    @FXML
    private TextField updateCityIDTextField;
    @FXML
    private TextField updateCityPopulationTextField;
    @FXML
    private Button updateCityButton;
    @FXML
    private Button deleteCityButton;
    @FXML
    private TextField deleteCityIDTextField;
    @FXML
    private ComboBox<String> addCityCountryCodeComboBox;

    //Observable database data lists
    private ObservableList<CityModel> cityModelObservableList = FXCollections.observableArrayList();
    private ObservableList<String> addCityCountryCodeObservableList = FXCollections.observableArrayList();

    //Database queries
    private final String insertCityQuery = "INSERT INTO city (name, countrycode, district, population) VALUES (?, ?, ?, ?)";
    private final String updateCityPopulationQuery = "UPDATE city SET population = ? where id = ?";
    private final String deleteCityQuery = "DELETE FROM city WHERE id = ?";

    public void initialize() {

        // initial status
        statusLabel.setText("Running...");

        // tableView initial setup setup
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        countryCodeColumn.setCellValueFactory(new PropertyValueFactory<>("countryCode"));
        districtColumn.setCellValueFactory(new PropertyValueFactory<>("district"));
        populationColumn.setCellValueFactory(new PropertyValueFactory<>("population"));

        // tableView initial load
        loadCitiesFromDatabase();

        // addCityCountryCodeComboBox (dropdown list) initial load
        loadCountryCodesToComboBox();


        /* GUI Action Events */

        // try to load country codes if failed to load initially
        addCityCountryCodeComboBox.setOnMousePressed(mouseEvent -> {
            if (addCityCountryCodeObservableList.isEmpty()) {
                loadCountryCodesToComboBox();
            }
        });

        // adding new City - data verification
        addCityButton.setOnAction(actionEvent -> {
            if (StringUtils.isEmpty(addCityNameTextField.getText())) {
                statusLabel.setText("Insert city name (all fields are necessary).");
            } else if (StringUtils.isEmpty(addCityCountryCodeComboBox.getSelectionModel().getSelectedItem())) {
                statusLabel.setText("Choose city country code (all fields are necessary).");
            } else if (StringUtils.isEmpty(addCityDistrictTextField.getText())) {
                statusLabel.setText("Insert city district (all fields are necessary).");
            } else if (!isValidNumeric(addCityPopulationTextField.getText())) {
                statusLabel.setText("Insert correct population (must be higher than 0).");
            } else {
                //adding new City - database insert
                try (Connection connection = DBConnector.getConnection();
                    PreparedStatement insertStatement = connection.prepareStatement(insertCityQuery)) {
                    insertStatement.setString(1, addCityNameTextField.getText());
                    insertStatement.setString(2, addCityCountryCodeComboBox.getSelectionModel().getSelectedItem());
                    insertStatement.setString(3, addCityDistrictTextField.getText());
                    insertStatement.setString(4, addCityPopulationTextField.getText());
                    insertStatement.executeUpdate();
                    statusLabel.setText("City: " + addCityNameTextField.getText() + " has been written.");
                    reloadCitiesList();
                } catch (SQLException e) {
                    statusLabel.setText("Error occurred.");
                    e.printStackTrace();
                }
            }
        });

        // updating City population
        updateCityButton.setOnAction(actionEvent -> {
            if (!isValidCityID(updateCityIDTextField.getText())) {
                statusLabel.setText("Insert city ID.");
            } else if (!isValidNumeric(updateCityPopulationTextField.getText())) {
                statusLabel.setText("Insert correct population (must be higher than 0).");
            } else {
                try (Connection connection = DBConnector.getConnection();
                     PreparedStatement updateStatement = connection.prepareStatement(updateCityPopulationQuery)) {
                    updateStatement.setString(1, updateCityPopulationTextField.getText());
                    updateStatement.setString(2, updateCityIDTextField.getText());
                    updateStatement.executeUpdate();
                    statusLabel.setText("City population for city ID: " + updateCityIDTextField.getText()
                            + " has been updated. New population is: " + updateCityPopulationTextField.getText() + ".");
                    reloadCitiesList();
                } catch (SQLException e) {
                    statusLabel.setText("Error occurred.");
                    e.printStackTrace();
                }
            }
        });

        // delete City
        deleteCityButton.setOnAction(actionEvent -> {
            if (!isValidCityID(deleteCityIDTextField.getText())) {
                statusLabel.setText("Insert city ID.");
            } else {
                try (Connection connection = DBConnector.getConnection();
                     PreparedStatement deleteStatement = connection.prepareStatement(deleteCityQuery)) {
                    deleteStatement.setString(1, deleteCityIDTextField.getText());
                    deleteStatement.executeUpdate();
                    statusLabel.setText("City ID: " + deleteCityIDTextField.getText() + " has been deleted.");
                    reloadCitiesList();
                } catch (SQLException e) {
                    statusLabel.setText("Error occurred.");
                    e.printStackTrace();
                }
            }
        });

        // refresh City list
        refreshCityListButton.setOnAction(actionEvent -> {
            statusLabel.setText("City list has been reloaded.");
            reloadCitiesList();
        });

    }

    // load City table values to Observable list
    private void loadCitiesFromDatabase() {
        try (Connection connection = DBConnector.getConnection()) {
            ResultSet resultSet = connection.createStatement().executeQuery("select * from city");
            while(resultSet.next()){
                cityModelObservableList.add(
                        new CityModel(resultSet.getString("id"),
                                resultSet.getString("name"),
                                resultSet.getString("countryCode"),
                                resultSet.getString("district"),
                                resultSet.getString("population"))
                );
            }
            cityTableView.setItems(cityModelObservableList);
        } catch (SQLException e) {
            statusLabel.setText("Error occurred when connecting to the database.");
            e.printStackTrace();
        }
    }

    // clears Observable list and reload values from database
    private void reloadCitiesList() {
        cityModelObservableList.clear();
        loadCitiesFromDatabase();
    }

    // addCityCountryCodeComboBox (dropdown list) load
    private void loadCountryCodesToComboBox() {
        try (Connection connection = DBConnector.getConnection()) {
            ResultSet resultSet = connection.createStatement().executeQuery("select code from country");
            while(resultSet.next()){
                addCityCountryCodeObservableList.add(resultSet.getString("code"));
            }
            addCityCountryCodeComboBox.setItems(addCityCountryCodeObservableList);
        } catch (SQLException e) {
            statusLabel.setText("Error occurred when connecting to the database.");
            e.printStackTrace();
        }
    }

    // basic validation of input values (must be not empty, number > 0)
    private boolean isValidNumeric(String givenNumeric) {
        return !StringUtils.isEmpty(givenNumeric)
                && StringUtils.isNumeric(givenNumeric)
                && (Integer.parseInt(givenNumeric) > 0);
    }

    // basic validation of ID (must be present on list)
    private boolean isValidCityID(String givenID) {
        if (isValidNumeric(givenID)) {
            for (CityModel cm : cityModelObservableList) {
                if(cm.getId().equals(givenID)) return true;
            }
        }
        return false;
    }
}

