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

-- FD 1 -> 2
-- name -> price,month
SELECT COUNT(*)
FROM frumble AS f1, frumble AS f2
WHERE (f1.name = f2.name)
AND (f1.price != f2.price)
AND (f1.month != f2. month);

-- name -> price,discount
SELECT COUNT(*)
FROM frumble AS f1, frumble AS f2
WHERE (f1.name = f2.name)
AND (f1.price != f2.price)
AND (f1.discount != f2. discount);

-- month -> discount,name
SELECT COUNT(*)
FROM frumble AS f1, frumble AS f2
WHERE (f1.month = f2.month)
AND (f1.discount != f2.discount)
AND (f1.name != f2. name);

-- month -> discount,price
SELECT COUNT(*)
FROM frumble AS f1, frumble AS f2
WHERE (f1.month = f2.month)
AND (f1.discount != f2.discount)
AND (f1.price != f2. price);

-- 1 -> 3
-- name -> price,month,discount
SELECT COUNT(*)
FROM frumble AS f1, frumble AS f2
WHERE (f1.name = f2.name)
AND (f1.price != f2.price)
AND (f1.month != f2. month)
AND (f1.discount != f2. discount);

-- month -> discount,name,price
SELECT COUNT(*)
FROM frumble AS f1, frumble AS f2
WHERE (f1.month = f2.month)
AND (f1.discount != f2.discount)
AND (f1.name != f2. name)
AND (f1.price != f2. price);

-- 2 -> 2
-- name,month -> discount,price
SELECT COUNT(*)
FROM frumble AS f1, frumble AS f2
WHERE (f1.name = f2.name)
AND (f1.month = f2.month)
AND (f1.discount != f2. discount)
AND (f1.price != f2. price);