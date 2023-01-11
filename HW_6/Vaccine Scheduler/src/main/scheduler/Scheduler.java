package scheduler;

import scheduler.db.ConnectionManager;
import scheduler.model.Caregiver;
import scheduler.model.Patient;
import scheduler.model.Vaccine;
import scheduler.util.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Scheduler {

    // objects to keep track of the currently logged-in user
    // Note: it is always true that at most one of currentCaregiver and currentPatient is not null
    //       since only one user can be logged-in at a time
    private static Caregiver currentCaregiver = null;
    private static Patient currentPatient = null;

    public static void main(String[] args) {
        // printing greetings text
        System.out.println();
        System.out.println("Welcome to the COVID-19 Vaccine Reservation Scheduling Application!");
        System.out.println("*** Please enter one of the following commands ***");
        System.out.println("> create_patient <username> <password>");  //TODO: implement create_patient (Part 1)
        System.out.println("> create_caregiver <username> <password>");
        System.out.println("> login_patient <username> <password>");  // TODO: implement login_patient (Part 1)
        System.out.println("> login_caregiver <username> <password>");
        System.out.println("> search_caregiver_schedule <date>");  // TODO: implement search_caregiver_schedule (Part 2)
        System.out.println("> reserve <date> <vaccine>");  // TODO: implement reserve (Part 2)
        System.out.println("> upload_availability <date>");
        //System.out.println("> cancel <appointment_id>");  // TODO: implement cancel (extra credit)
        System.out.println("> add_doses <vaccine> <number>");
        System.out.println("> show_appointments");  // TODO: implement show_appointments (Part 2)
        System.out.println("> logout");  // TODO: implement logout (Part 2)
        System.out.println("> quit");
        System.out.println();

        // read input from user
        BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.print("> ");
            String response = "";
            try {
                response = r.readLine();
            } catch (IOException e) {
                System.out.println("Please try again!");
            }
            // split the user input by spaces
            String[] tokens = response.split(" ");
            // check if input exists
            if (tokens.length == 0) {
                System.out.println("Please try again!");
                continue;
            }
            // determine which operation to perform
            String operation = tokens[0];
            if (operation.equals("create_patient")) {
                createPatient(tokens);
            } else if (operation.equals("create_caregiver")) {
                createCaregiver(tokens);
            } else if (operation.equals("login_patient")) {
                loginPatient(tokens);
            } else if (operation.equals("login_caregiver")) {
                loginCaregiver(tokens);
            } else if (operation.equals("search_caregiver_schedule")) {
                searchCaregiverSchedule(tokens);
            } else if (operation.equals("reserve")) {
                reserve(tokens);
            } else if (operation.equals("upload_availability")) {
                uploadAvailability(tokens);
            } else if (operation.equals("add_doses")) {
                addDoses(tokens);
            } else if (operation.equals("show_appointments")) {
                showAppointments(tokens);
            } else if (operation.equals("logout")) {
                logout(tokens);
            } else if (operation.equals("print")) {
                print();
            } else if (operation.equals("quit")) {
                System.out.println("Bye!");
                return;
            } else {
                System.out.println("Invalid operation name!");
            }
        }
    }

    private static void print(){
        if (currentCaregiver == null && currentPatient == null){
            System.out.println("No one is logged in.");
        } else {
            System.out.print("Logged in: ");
            if (currentCaregiver != null) {
                System.out.println(currentCaregiver.getUsername());
            } else if (currentPatient != null) {
                System.out.println(currentPatient.getUsername());
            }
        }
        ConnectionManager cm = new ConnectionManager();
        Connection con = cm.createConnection();

        String printCaregivers = "SELECT C.Username FROM Caregivers AS C";
        String printPatients = "SELECT P.Username FROM Patients AS P";
        String printVaccines = "SELECT * FROM Vaccines";
        String printAvailabilities = "SELECT * FROM Availabilities";
        String selectAppointments = "SELECT AppointmentID, VaccineName, Time, PatientUsername, CaregiverUsername FROM Appointments ORDER BY AppointmentID";
        try {
            Statement caregiverStatement = con.createStatement();
            ResultSet rs1 = caregiverStatement.executeQuery(printCaregivers);
            System.out.println("Caregivers");
            while (rs1.next()){
                System.out.println(rs1.getString(1));
            }

            Statement patientStatement = con.createStatement();
            ResultSet rs2 = patientStatement.executeQuery(printPatients);
            System.out.println("\nPatients");
            while (rs2.next()){
                System.out.println(rs2.getString(1));
            }

            Statement vaccineStatement = con.createStatement();
            ResultSet rs3 = vaccineStatement.executeQuery(printVaccines);
            System.out.println("\nVaccines\nName Doses");
            while (rs3.next()){
                System.out.println(rs3.getString(1) + " " + rs3.getString(2));
            }

            Statement availabilitiesStatement = con.createStatement();
            ResultSet rs4 = availabilitiesStatement.executeQuery(printAvailabilities);
            System.out.println("\nAvailabilities\nUsername Date");
            while (rs4.next()){
                System.out.println(rs4.getString(1) + " " + rs4.getString(2));
            }

            Statement appointmentsStatement = con.createStatement();
            ResultSet rs5 = appointmentsStatement.executeQuery(selectAppointments);
            System.out.println("Appointments");
            System.out.printf("%-20s|%-15s|%-15s|%-20s|%-20s\n","AppointmentID","Vaccine Name","Date","Patient Username","Caregiver Username");
            while (rs5.next()){
                System.out.printf("%-20s|%-15s|%-15s|%-20s|%-20s\n",rs5.getString(1),rs5.getString(2),rs5.getString(3),rs5.getString(4),rs5.getString(5));
            }
        } catch (SQLException e) {
            System.out.println("Error occurred when printing usernames");
            e.printStackTrace();
        } finally {
            cm.closeConnection();
        }
    }

    private static void createPatient(String[] tokens) {
        // TODO: Part 1
        // create_caregiver <username> <password>
        // check 1: the length for tokens need to be exactly 3 to include all information (with the operation name)
        if (tokens.length != 3) {
            System.out.println("Failed to create user.");
            return;
        }
        String username = tokens[1];
        String password = tokens[2];
        // check 2: check if the username has been taken already
        if (usernameExistsPatient(username)) {
            System.out.println("Username taken, try again!");
            return;
        }
        if (!checkStrongPassword(password)){
            System.out.println("Password is not a strong password. It must include the following:");
            System.out.println("At least 8 characters");
            System.out.println("A mixture of both uppercase and lowercase letters");
            System.out.println("A mixture of letters and numbers");
            System.out.println("Inclusion of a special character.");
            return;
        }
        byte[] salt = Util.generateSalt();
        byte[] hash = Util.generateHash(password, salt);
        // create the caregiver
        try {
            currentPatient = new Patient.PatientBuilder(username,salt,hash).build();
            // save to caregiver information to our database
            currentPatient.saveToDB();
            System.out.println("Created user " + username);
        } catch (SQLException e) {
            System.out.println("Failed to create user.");
            e.printStackTrace();
        }
    }

    private static void createCaregiver(String[] tokens) {
        // create_caregiver <username> <password>
        // check 1: the length for tokens need to be exactly 3 to include all information (with the operation name)
        if (tokens.length != 3) {
            System.out.println("Failed to create user.");
            return;
        }
        String username = tokens[1];
        String password = tokens[2];
        // check 2: check if the username has been taken already
        if (usernameExistsCaregiver(username)) {
            System.out.println("Username taken, try again!");
            return;
        }
        if (!checkStrongPassword(password)){
            System.out.println("Password is not a strong password. It must include the following:");
            System.out.println("At least 8 characters");
            System.out.println("A mixture of both uppercase and lowercase letters");
            System.out.println("A mixture of letters and numbers");
            System.out.println("Inclusion of a special character.");
            return;
        }
        byte[] salt = Util.generateSalt();
        byte[] hash = Util.generateHash(password, salt);
        // create the caregiver
        try {
            currentCaregiver = new Caregiver.CaregiverBuilder(username, salt, hash).build();
            // save to caregiver information to our database
            currentCaregiver.saveToDB();
            System.out.println("Created user " + username);
        } catch (SQLException e) {
            System.out.println("Failed to create user.");
            e.printStackTrace();
        }
    }

    private static boolean usernameExistsCaregiver(String username) {
        ConnectionManager cm = new ConnectionManager();
        Connection con = cm.createConnection();

        String selectUsername = "SELECT * FROM Caregivers WHERE Username = ?";
        try {
            PreparedStatement statement = con.prepareStatement(selectUsername);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            // returns false if the cursor is not before the first record or if there are no rows in the ResultSet.
            return resultSet.isBeforeFirst();
        } catch (SQLException e) {
            System.out.println("Error occurred when checking username");
            e.printStackTrace();
        } finally {
            cm.closeConnection();
        }
        return true;
    }

    private static boolean usernameExistsPatient(String username) {
        ConnectionManager cm = new ConnectionManager();
        Connection con = cm.createConnection();

        String selectUsername = "SELECT * FROM Patients WHERE Username = ?";
        try {
            PreparedStatement statement = con.prepareStatement(selectUsername);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            // returns false if the cursor is not before the first record or if there are no rows in the ResultSet.
            return resultSet.isBeforeFirst();
        } catch (SQLException e) {
            System.out.println("Error occurred when checking username");
            e.printStackTrace();
        } finally {
            cm.closeConnection();
        }
        return true;
    }

    private static void loginPatient(String[] tokens) {
        // TODO: Part 1
        // login_patient <username> <password>
        // check 1: if someone's already logged-in, they need to log out first
        if (currentCaregiver != null || currentPatient != null) {
            System.out.println("User already logged in.");
            return;
        }
        // check 2: the length for tokens need to be exactly 3 to include all information (with the operation name)
        if (tokens.length != 3) {
            System.out.println("Login failed.");
            return;
        }
        String username = tokens[1];
        String password = tokens[2];

        Patient patient = null;
        try {
            patient = new Patient.PatientGetter(username,password).get();
        } catch (SQLException e) {
            System.out.println("Login failed.");
            e.printStackTrace();
        }
        // check if the login was successful
        if (patient == null) {
            System.out.println("Login failed.");
        } else {
            System.out.println("Logged in as: " + username);
            currentPatient = patient;
        }
    }

    private static void loginCaregiver(String[] tokens) {
        // login_caregiver <username> <password>
        // check 1: if someone's already logged-in, they need to log out first
        if (currentCaregiver != null || currentPatient != null) {
            System.out.println("User already logged in.");
            return;
        }
        // check 2: the length for tokens need to be exactly 3 to include all information (with the operation name)
        if (tokens.length != 3) {
            System.out.println("Login failed.");
            return;
        }
        String username = tokens[1];
        String password = tokens[2];

        Caregiver caregiver = null;
        try {
            caregiver = new Caregiver.CaregiverGetter(username, password).get();
        } catch (SQLException e) {
            System.out.println("Login failed.");
            e.printStackTrace();
        }
        // check if the login was successful
        if (caregiver == null) {
            System.out.println("Login failed.");
        } else {
            System.out.println("Logged in as: " + username);
            currentCaregiver = caregiver;
        }
    }

    private static void searchCaregiverSchedule(String[] tokens) {
        // TODO: Part 2
        // check 1: if no one's logged in, someone needs to login
        if (currentCaregiver == null && currentPatient == null) {
            System.out.println("Please login first!");
            return;
        }
        if (tokens.length != 2) {
            System.out.println("Please try again.");
            return;
        }

        String date = tokens[1];
        ConnectionManager cm = new ConnectionManager();
        Connection con = cm.createConnection();

        String selectNameAndVaccine = "SELECT A.Username, V.Name, V.Doses FROM Availabilities AS A, Vaccines AS V " +
                "WHERE A.Time = ? ORDER BY A.Username";
        try {
            PreparedStatement statement = con.prepareStatement(selectNameAndVaccine);
            statement.setString(1, date);
            ResultSet resultSet = statement.executeQuery();
            System.out.println("Available Caregivers on " + date + ":");
            System.out.printf("%-20s|%-15s|%-15s\n","Caregiver Username","Vaccine Name","Doses Remaining");
            while (resultSet.next()){
                System.out.printf("%-20s|%-15s|%-15s\n",resultSet.getString(1),resultSet.getString(2),resultSet.getString(3));
            }
        } catch (SQLException e) {
            System.out.println("Error occurred when checking username");
            e.printStackTrace();
        } finally {
            cm.closeConnection();
        }
    }

    private static void reserve(String[] tokens) {
        // TODO: Part 2
        if (currentPatient == null && currentCaregiver == null){
            System.out.println("Please login first!");
            return;
        }
        if (currentPatient == null) {
            System.out.println("Please login as a patient!");
            return;
        }
        if (tokens.length != 3) {
            System.out.println("Please try again!");
            return;
        }
        String date = tokens[1];
        String vaccine = tokens[2];
        if (!availableCaregivers(date)){
            System.out.println("There are no available caregivers on that day!");
            return;
        }
        if (!availableVaccines(vaccine)){
            System.out.println("Not enough available doses!");
            return;
        }

        ConnectionManager cm = new ConnectionManager();
        Connection con = cm.createConnection();

        String chooseCaregiver = "SELECT A.Username FROM Availabilities AS A WHERE Time = ? ORDER BY A.Username";
        try {
            PreparedStatement statement = con.prepareStatement(chooseCaregiver);
            statement.setString(1, date);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            String caregiverUsername = resultSet.getString(1);
            String appointment_id = caregiverUsername + "" + date;
            System.out.println("Appointment ID: {" + appointment_id + "}, Caregiver username: {" + caregiverUsername + "}");

            removeAvailabilityFromCaregiver(caregiverUsername, date);
            removeVaccine(vaccine);
            addToAppointmentTable(currentPatient.getUsername(),caregiverUsername,vaccine,appointment_id,date);
        } catch (SQLException e) {
            System.out.println("Error occurred when printing usernames");
            //e.printStackTrace();
        } finally {
            cm.closeConnection();
        }
    }

    private static void removeAvailabilityFromCaregiver(String caregiverUsername, String date){
        ConnectionManager cm = new ConnectionManager();
        Connection con = cm.createConnection();

        String removeAvailability = "DELETE FROM Availabilities WHERE Username = ? AND Time = ?";
        //Appointments (AppointmentID, PatientUsername, CaregiverUsername, VaccineName, Time)
        //Availabilities (Username, Time);
        try {
            PreparedStatement statement = con.prepareStatement(removeAvailability);
            statement.setString(1, caregiverUsername);
            statement.setString(2, date);
            statement.executeUpdate();
            //System.out.println("Removed availability of: " + caregiverUsername + " on : " + date);
        } catch (SQLException e) {
            System.out.println("Error occurred when printing usernames");
            //e.printStackTrace();
        } finally {
            cm.closeConnection();
        }
    }

    private static void removeVaccine(String vaccineName){
        ConnectionManager cm = new ConnectionManager();
        Connection con = cm.createConnection();

        Vaccine vaccine = null;
        try {
            vaccine = new Vaccine.VaccineGetter(vaccineName).get();
        } catch (SQLException e) {
            System.out.println("Error occurred when removing doses");
            //e.printStackTrace();
        }

        try {
            vaccine.decreaseAvailableDoses(1);
            //System.out.println("Removed 1 dose from :" + vaccineName);
        } catch (SQLException e) {
            System.out.println("Error occurred when removing doses");
            //e.printStackTrace();
        }
    }

    private static void addToAppointmentTable(String patientUsername, String caregiverUsername, String vaccineName,
                                                String appointment_id, String date){
        ConnectionManager cm = new ConnectionManager();
        Connection con = cm.createConnection();

        String addToAppointment = "INSERT INTO Appointments (AppointmentID, PatientUsername, CaregiverUsername, VaccineName, Time) VALUES (?,?,?,?,?)";
        try {
            PreparedStatement statement = con.prepareStatement(addToAppointment);
            statement.setString(1, appointment_id);
            statement.setString(2, patientUsername);
            statement.setString(3, caregiverUsername);
            statement.setString(4,vaccineName);
            statement.setString(5,date);
            statement.executeUpdate();
            //System.out.println("Added patient " + patientUsername + " and caregiver " + caregiverUsername + " into appointments table.");
        } catch (SQLException e) {
            System.out.println("Error occurred when printing usernames");
            //e.printStackTrace();
        } finally {
            cm.closeConnection();
        }
    }
    private static boolean availableCaregivers(String date){
        ConnectionManager cm = new ConnectionManager();
        Connection con = cm.createConnection();

        String selectUsername = "SELECT * FROM Availabilities WHERE Time = ?";
        try {
            PreparedStatement statement = con.prepareStatement(selectUsername);
            statement.setString(1, date);
            ResultSet resultSet = statement.executeQuery();
            // returns false if the cursor is not before the first record or if there are no rows in the ResultSet.
            return resultSet.isBeforeFirst();
        } catch (SQLException e) {
            System.out.println("Error occurred when checking availabilities");
            //e.printStackTrace();
        } finally {
            cm.closeConnection();
        }
        return true;
    }

    private static boolean availableVaccines(String vaccine){
        ConnectionManager cm = new ConnectionManager();
        Connection con = cm.createConnection();

        String selectVaccines = "SELECT * FROM Vaccines WHERE Name = ?";
        try {
            PreparedStatement statement = con.prepareStatement(selectVaccines);
            statement.setString(1, vaccine);
            ResultSet resultSet = statement.executeQuery();
            // returns false if the cursor is not before the first record or if there are no rows in the ResultSet.
            return resultSet.isBeforeFirst();
        } catch (SQLException e) {
            System.out.println("Error occurred when checking vaccine");
            //e.printStackTrace();
        } finally {
            cm.closeConnection();
        }
        return true;
    }

    private static void uploadAvailability(String[] tokens) {
        // upload_availability <date>
        // check 1: check if the current logged-in user is a caregiver
        if (currentCaregiver == null) {
            System.out.println("Please login as a caregiver first!");
            return;
        }
        // check 2: the length for tokens need to be exactly 2 to include all information (with the operation name)
        if (tokens.length != 2) {
            System.out.println("Please try again!");
            return;
        }
        String date = tokens[1];
        try {
            Date d = Date.valueOf(date);
            currentCaregiver.uploadAvailability(d);
            System.out.println("Availability uploaded!");
        } catch (IllegalArgumentException e) {
            System.out.println("Please enter a valid date!");
        } catch (SQLException e) {
            System.out.println("Error occurred when uploading availability");
            //e.printStackTrace();
        }
    }

    private static void addDoses(String[] tokens) {
        // add_doses <vaccine> <number>
        // check 1: check if the current logged-in user is a caregiver
        if (currentCaregiver == null) {
            System.out.println("Please login as a caregiver first!");
            return;
        }
        // check 2: the length for tokens need to be exactly 3 to include all information (with the operation name)
        if (tokens.length != 3) {
            System.out.println("Please try again!");
            return;
        }
        String vaccineName = tokens[1];
        int doses = Integer.parseInt(tokens[2]);
        Vaccine vaccine = null;
        try {
            vaccine = new Vaccine.VaccineGetter(vaccineName).get();
        } catch (SQLException e) {
            System.out.println("Error occurred when adding doses");
            //e.printStackTrace();
        }
        // check 3: if getter returns null, it means that we need to create the vaccine and insert it into the Vaccines
        //          table
        if (vaccine == null) {
            try {
                vaccine = new Vaccine.VaccineBuilder(vaccineName, doses).build();
                vaccine.saveToDB();
            } catch (SQLException e) {
                System.out.println("Error occurred when adding doses");
                //e.printStackTrace();
            }
        } else {
            // if the vaccine is not null, meaning that the vaccine already exists in our table
            try {
                vaccine.increaseAvailableDoses(doses);
            } catch (SQLException e) {
                System.out.println("Error occurred when adding doses");
                //e.printStackTrace();
            }
        }
        System.out.println("Doses updated!");
    }

    private static void showAppointments(String[] tokens) {
        // TODO: Part 2
        if (currentPatient == null && currentCaregiver == null){
            System.out.println("Please login first!");
            return;
        }
        if (tokens.length != 1){
            System.out.println("Please try again!");
            return;
        }

        ConnectionManager cm = new ConnectionManager();
        Connection con = cm.createConnection();

        //Caregiver Route
        if (currentCaregiver != null){
            String selectAppointments = "SELECT AppointmentID, VaccineName, Time, PatientUsername FROM Appointments WHERE CaregiverUsername = ? ORDER BY AppointmentID";
            try {
                PreparedStatement statement = con.prepareStatement(selectAppointments);
                statement.setString(1, currentCaregiver.getUsername());
                ResultSet resultSet = statement.executeQuery();
                System.out.println("Appointments for you:");
                System.out.printf("%-25s|%-15s|%-10s|%-20s\n","AppointmentID","Vaccine Name","Date","Username of Patient");
                while (resultSet.next()){
                    System.out.printf("%-25s|%-15s|%-10s|%-20s\n",resultSet.getString(1),resultSet.getString(2),resultSet.getString(3),resultSet.getString(4));
                }
            } catch (SQLException e) {
                System.out.println("Error occurred when checking appointments");
                //e.printStackTrace();
            } finally {
                cm.closeConnection();
            }
        }
        //Patient Route
        else if (currentPatient != null){
            String selectAppointments = "SELECT AppointmentID, VaccineName, Time, CaregiverUsername FROM Appointments WHERE PatientUsername = ? ORDER BY AppointmentID";
            try {
                PreparedStatement statement = con.prepareStatement(selectAppointments);
                statement.setString(1, currentPatient.getUsername());
                ResultSet resultSet = statement.executeQuery();
                System.out.println("Appointments for you:");
                System.out.printf("%-25s|%-15s|%-10s|%-20s\n","AppointmentID","Vaccine Name","Date","Username of Caregiver");
                while (resultSet.next()){
                    System.out.printf("%-25s|%-15s|%-10s|%-20s\n",resultSet.getString(1),resultSet.getString(2),resultSet.getString(3),resultSet.getString(4));
                }
            } catch (SQLException e) {
                System.out.println("Error occurred when checking appointments");
                //e.printStackTrace();
            } finally {
                cm.closeConnection();
            }
        }
    }

    private static void logout(String[] tokens) {
        if (tokens.length != 1){
            System.out.println("Please try again!");
            return;
        }
        // TODO: Part 2
        currentPatient = null;
        currentCaregiver = null;
        System.out.println("Successfully logged out!");
    }

    private static boolean checkStrongPassword(String password) {
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$?]).+$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(password);
        if (password.length() <= 8 || !m.matches()) {
            return false;
        }
        return true;
    }
}
