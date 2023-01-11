-- 1.
CREATE TABLE frumble(
	name VARCHAR(10),
	discount INT,
	month VARCHAR(5),
	price INT
);

-- 2.
-- FD 1 -> 1
-- name -> price
SELECT COUNT(*)
FROM frumble AS f1, frumble AS f2
WHERE (f1.name = f2.name)
AND (f1.price != f2.price);

-- month -> discount
SELECT COUNT(*)
FROM frumble AS f1, frumble AS f2
WHERE (f1.month = f2.month)
AND (f1.discount != f2.discount);

-- name -> discount DOES NOT HOLD
SELECT COUNT(*)
FROM frumble AS f1, frumble AS f2
WHERE (f1.name = f2.name)
AND (f1.discount != f2.discount);

-- FD 1 -> 2
-- name -> price,month DOES NOT HOLD
SELECT COUNT(*)
FROM frumble AS f1, frumble AS f2
WHERE (f1.name = f2.name)
AND ((f1.price != f2.price)
OR (f1.month != f2. month));

-- 3.
CREATE TABLE nameMonth(
	name VARCHAR(10),
	month VARCHAR(5),
	PRIMARY KEY(name,month)
);

CREATE TABLE namePrice(
	name VARCHAR(10),
	price INT,
	FOREIGN KEY(name) REFERENCES nameMonth(name),
	PRIMARY KEY(name,price)
);

CREATE TABLE monthDiscount(
	month VARCHAR(5),
	discount INT,
	FOREIGN KEY(month) REFERENCES nameMonth(month),
	PRIMARY KEY(month,discount)
);

--4.
INSERT INTO namePrice  
SELECT DISTINCT name, price
FROM frumble;

-- Outputs 36 rows
SELECT COUNT(*)
FROM namePrice;


INSERT INTO monthDiscount 
SELECT DISTINCT month, discount
FROM frumble;

-- Outputs 12 rows
SELECT COUNT(*)
FROM monthDiscount;


INSERT INTO nameMonth 
SELECT DISTINCT name, month FROM frumble;

-- Outputs 426 rows
SELECT COUNT(*) 
FROM nameMonth;



