INSERT INTO tenmo_user (username, password_hash)
VALUES ('Eli', 'a309jfdzio@*'),
('Billy', 'e30JRFKJ039&#S'),
('Samuel', 'zje*#WfdjOIQWKnd');

INSERT INTO account (user_id, balance) 
VALUES ('1001', '1000.00'),
('1002', '1000.00'),
('1003', '1000.00');


INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount)
VALUES ('1', '1', '2001', '2003', '230.00'),
('2', '2', '2002', '2001', '250.00'),
('2', '2', '2003', '2001', '150.00'),
('1', '3', '2002', '2003', '2000.00');