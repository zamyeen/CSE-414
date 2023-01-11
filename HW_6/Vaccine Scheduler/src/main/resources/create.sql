CREATE TABLE Caregivers (
    Username VARCHAR(255),
    Salt BINARY(16),
    Hash BINARY(16),
    PRIMARY KEY (Username)
);

CREATE TABLE Availabilities (
	Username VARCHAR(255),
    Time DATE,
    FOREIGN KEY (Username) REFERENCES Caregivers,
    PRIMARY KEY (Time, Username)
);

CREATE TABLE Vaccines (
    Name VARCHAR(255),
    Doses INT,
    PRIMARY KEY (Name)
);

CREATE TABLE Patients (
    Username VARCHAR(255),
    Salt BINARY(16),
    Hash BINARY(16),
    PRIMARY KEY (Username)
);

CREATE TABLE Appointments (
	AppointmentID VARCHAR(255),
	PatientUsername VARCHAR(255),
	CaregiverUsername VARCHAR(255),
	VaccineName VARCHAR(255),
    Time DATE,
    FOREIGN KEY (PatientUsername) REFERENCES Patients(Username),
    FOREIGN KEY (CaregiverUsername) REFERENCES Caregivers(Username),
    FOREIGN KEY (VaccineName) REFERENCES Vaccines(Name),
    PRIMARY KEY (AppointmentID)
);

