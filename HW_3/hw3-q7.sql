SELECT c.name AS 'carrier'
FROM flights AS f, carriers AS c
WHERE c.cid = f.carrier_id AND 
f.origin_city = 'Seattle WA' AND f.dest_city = 'San Francisco CA'
GROUP BY c.name
ORDER BY c.name;

/*
Rows: 4
Time: 34 seconds

Alaska Airlines Inc.
SkyWest Airlines Inc.
United Air Lines Inc.
Virgin America
*/