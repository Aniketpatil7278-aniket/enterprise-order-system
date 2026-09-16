
SHOW DATABASES;
-- use Db -- 
CREATE DATABASE user_db;
use  user_db;
select * from users;

-- Order DB
CREATE DATABASE order_db;
use order_db;
select * from orders;
select * from order_items;
select * from outbox_events;

-- payment DB --
CREATE DATABASE payment_db;
use payment_db;
select * from payments;
select * from outbox_events;

-- inventory_db--  
CREATE DATABASE inventory_db;
use inventory_db;
select * from inventory;
select * from inventory_reservations;
select * from outbox_events;
