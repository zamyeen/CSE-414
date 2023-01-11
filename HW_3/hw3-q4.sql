SELECT ifs.dest_city AS 'city'
FROM flights AS f
JOIN (SELECT f2.origin_city, f2.dest_city FROM flights AS f2
WHERE f2.dest_city != 'Seattle WA'
AND f2.dest_city NOT IN 
(SELECT DISTINCT f3.dest_city FROM flights AS f3 WHERE f3.origin_city = 'Seattle WA')
GROUP BY f2.dest_city, f2.origin_city) AS ifs /*intermediate flights*/
ON ifs.origin_city = f.dest_city
WHERE f.origin_city = 'Seattle WA'
GROUP BY ifs.dest_city
ORDER BY ifs.dest_city ASC;

/*
Rows: 256
Time: 27 seconds

Aberdeen SD
Abilene TX
Adak Island AK
Aguadilla PR
Akron OH
Albany GA
Albany NY
Alexandria LA
Allentown/Bethlehem/Easton PA
Alpena MI
Amarillo TX
Appleton WI
Arcata/Eureka CA
Asheville NC
Ashland WV
Aspen CO
Atlantic City NJ
Augusta GA
Bakersfield CA
Bangor ME
*/