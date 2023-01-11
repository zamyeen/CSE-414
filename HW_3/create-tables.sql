CREATE TABLE CARRIERS (cid varchar(7) PRIMARY KEY, name varchar(83));

CREATE TABLE MONTHS (mid int PRIMARY KEY, month varchar(9));

CREATE TABLE WEEKDAYS (did int PRIMARY KEY, day_of_week varchar(9));

CREATE TABLE FLIGHTS (fid int PRIMARY KEY, 
         month_id int REFERENCES MONTHS(mid), 
         day_of_month int,   
         day_of_week_id int REFERENCES WEEKDAYS(did),
         carrier_id varchar(7) REFERENCES CARRIERS(cid), 
         flight_num int,
         origin_city varchar(34), 
         origin_state varchar(47), 
         dest_city varchar(34), 
         dest_state varchar(46), 
         departure_delay int, -- in mins
         taxi_out int,        -- in mins
         arrival_delay int,   -- in mins
         canceled int,        -- 1 means canceled
         actual_time int,     -- in mins
         distance int,        -- in miles
         capacity int, 
         price int            -- in $             
         );


