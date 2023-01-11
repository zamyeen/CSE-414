-- Entities
CREATE TABLE InsuranceCo(
	name VARCHAR(50),
	phone INT,
	PRIMARY KEY(name)
);

CREATE TABLE Person(
	ssn INT,
	name VARCHAR(20),
	PRIMARY KEY(ssn)
);

CREATE TABLE Driver(
	ssn INT,
	driverID INT,
	FOREIGN KEY(ssn) REFERENCES Person(ssn)
);

CREATE TABLE NonProfessionalDriver(
	ssn INT,
	FOREIGN KEY(ssn) REFERENCES Driver(ssn)
);

CREATE TABLE ProfessionalDriver(
		medicalHistory VARCHAR(1000),
		ssn INT,
		FOREIGN KEY(ssn) REFERENCES Driver(ssn)
);

CREATE TABLE Vehicle(
	iName VARCHAR(50),
	ssn INT,
	year INT,
	licensePlate VARCHAR(10),
	maxLiability FLOAT,
	FOREIGN KEY(ssn) REFERENCES Person(ssn),
	FOREIGN KEY(iName) REFERENCES InsuranceCo(name),
	PRIMARY KEY(licensePlate)
);

CREATE TABLE Truck(
	licensePlate VARCHAR(10),
	capacity INT,
	driverID INT,
	FOREIGN KEY(driverID) REFERENCES ProfessionalDriver(driverID),
	FOREIGN KEY(licensePlate) REFERENCES Vehicle(licensePlate),
	PRIMARY KEY(licensePlate)
);

CREATE TABLE Car(
	licensePlate VARCHAR(10),
	make INT,
	FOREIGN KEY(licensePlate) REFERENCES Vehicle(licensePlate),
	PRIMARY KEY(licensePlate)
);

-- Relations (Many to Many)
CREATE TABLE Drives(
	licensePlate VARCHAR(10),
	ssn INT,
	FOREIGN KEY(licensePlate) REFERENCES Car(licensePlate),
	FOREIGN KEY(ssn) REFERENCES NonProfessionalDriver(ssn)
);

/*
b.
The relation in the relational schema that represents insures is a
join on the table of InsuranceCo and Vehicle. Every vehicle has a
different max liability and thus the liability of a vehicle 
can be represented as an attribute. Additionally, every vehicle
has at most one insurance, so who is insuring what vehicle can be
a foreign key from the InsuranceCo table.

c.
The representation of drive in the schema is a table
whereas the representation of operates is imbued into other tables.
The reason is due to the fact that drive is a many to many relation.
For example, there can be multiple drivers to one car, and one driver
can have many cars. This relationship can only be contained in a table.
For a truck that is operated by a professional driver
each truck is only operated by one driver, so it makes sense that the
truck table contains information about the driver.
*/