SELECT DISTINCT f.origin_city AS 'city'
FROM flights AS f, (SELECT f1.origin_city as 'origin_city', 
MAX(f1.actual_time) AS 'actual_time'
FROM flights AS f1
GROUP BY f1.origin_city) AS maxf
WHERE f.origin_city =  maxf.origin_city AND
f.canceled = 0 AND
maxf.actual_time < 3 * 60
ORDER BY f.origin_city ASC;

/*
Rows: 109
Time: 72 seconds

Aberdeen SD
Abilene TX
Alpena MI
Ashland WV
Augusta GA
Barrow AK
Beaumont/Port Arthur TX
Bemidji MN
Bethel AK
Binghamton NY
Brainerd MN
Bristol/Johnson City/Kingsport TN
Butte MT
Carlsbad CA
Casper WY
Cedar City UT
Chico CA
College Station/Bryan TX
Columbia MO
Columbus GA
*/