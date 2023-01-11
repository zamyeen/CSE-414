SELECT DISTINCT c.name AS 'carrier'
FROM flights AS f, carriers AS c
WHERE EXISTS (SELECT * FROM flights as f2 WHERE 
f2.origin_city = 'Seattle WA' AND f2.dest_city = 'San Francisco CA' AND f.fid = f2.fid)
AND f.carrier_id = c.cid
ORDER BY c.name;
/*
Rows: 4
Time: 15 seconds

Alaska Airlines Inc.
SkyWest Airlines Inc.
United Air Lines Inc.
Virgin America
*/