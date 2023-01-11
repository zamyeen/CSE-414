SELECT f.origin_city AS 'origin_city', 
ISNULL(under3f * 100.0/count(f.fid),0) AS 'percentage'
FROM flights AS f
LEFT JOIN (SELECT origin_city, COUNT(fid) AS under3f
FROM flights 
WHERE actual_time < 180.0
GROUP BY origin_city) AS under3
ON f.origin_city = under3.origin_city
WHERE f.canceled = 0
GROUP BY f.origin_city, under3f
ORDER BY percentage, origin_city ASC;

/*
Rows: 327
Time: 21 seconds
Guam TT	0

Pago Pago TT	0
Aguadilla PR	29.43396226
Anchorage AK	32.1460374
San Juan PR	33.89036071
Charlotte Amalie VI	40
Ponce PR	41.93548387
Fairbanks AK	50.69124424
Kahului HI	53.66499853
Honolulu HI	54.90880869
San Francisco CA	56.30765683
Los Angeles CA	56.60410765
Seattle WA	57.75541655
Long Beach CA	62.45411641
Kona HI	63.28210757
New York NY	63.48151977
Las Vegas NV	65.16300929
Christiansted VI	65.33333333
Newark NJ	67.13735558
Worcester MA	67.74193548
*/