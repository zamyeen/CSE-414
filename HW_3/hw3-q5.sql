SELECT DISTINCT f.dest_city AS 'city'
FROM flights AS f
WHERE f.dest_city != 'Seattle WA'
AND f.origin_city != 'Seattle WA'
AND f.dest_city NOT IN 

(SELECT f2.dest_city
FROM flights AS f1
JOIN flights AS f2
ON f1.dest_city = f2.origin_city
WHERE f1.origin_city = 'Seattle WA')

ORDER BY f.dest_city;

/*
Rows: 3
Time: 218 seconds

Devils Lake ND
Hattiesburg/Laurel MS
St. Augustine FL
*/
