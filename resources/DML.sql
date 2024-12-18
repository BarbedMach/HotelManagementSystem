-- This file is not used in the application. It is only created for project purposes.

-- Admin Panel SQL Queries
-- Insert a new hotel into the database.
INSERT INTO hotel (h_name, h_zip, h_street, h_building_no, h_phone_no)
VALUES (?, ?, ?, ?, ?);

-- Retrieve all hotel details from the database.
SELECT h_name, h_zip, h_street, h_building_no, h_phone_no
FROM hotel;

-- Delete a hotel from the database based on its name.
DELETE FROM hotel
WHERE h_name = ?;

-- Insert a new user into the database.
INSERT INTO user (u_name, u_phone_no, u_password)
VALUES (?, ?, ?);

-- Assign a user as a receptionist by selecting their user ID.
INSERT INTO receptionist
SELECT u_id
FROM user
WHERE u_name = ? AND u_password = ?;

-- Assign a user as a housekeeper by selecting their user ID.
INSERT INTO housekeeper
SELECT u_id
FROM user
WHERE u_name = ? AND u_password = ?;

-- Retrieve guest details including name and phone number.
SELECT u_name, u_phone_no
FROM user
         JOIN guest ON user.u_id = guest.g_id;

-- Retrieve receptionist details including name, phone number, and password.
SELECT u_name, u_phone_no, u_password
FROM user
         JOIN receptionist ON user.u_id = receptionist.r_id;

-- Retrieve housekeeper details including name, phone number, and password.
SELECT u_name, u_phone_no, u_password
FROM user
         JOIN housekeeper ON user.u_id = housekeeper.hk_id;

-- Delete a user if they are not an administrator.
DELETE FROM user
WHERE u_name = ? AND NOT EXISTS (
    SELECT 1
    FROM administrator
    WHERE user.u_id = administrator.a_id
);

-- Insert a new room type into the database.
INSERT INTO roomtype (r_type, capacity)
VALUES (?, ?);

-- Retrieve all room types and their capacities.
SELECT r_type, capacity
FROM roomtype;

-- Delete a specific room type from the database.
DELETE FROM roomtype
WHERE r_type = ?;

-- Insert a new room into the database linked to a hotel.
INSERT INTO room (r_id, h_id, r_type, r_status)
SELECT ?, h_id, ?, ?
FROM hotel
WHERE hotel.h_name = ?;

-- Retrieve details of all rooms, including type, capacity, and status.
SELECT hotel.h_name, room.r_id, room.r_type, roomtype.capacity, room.r_status
FROM room
         JOIN roomtype ON room.r_type = roomtype.r_type
         JOIN hotel ON hotel.h_id = room.h_id;

-- Delete a specific room from a hotel based on its ID and name.
DELETE r
FROM room r
         JOIN hotel h ON r.h_id = h.h_id
WHERE h.h_name = ? AND r.r_id = ?;

-- Update the status of a specific room in a hotel.
UPDATE room r
    JOIN hotel h ON r.h_id = h.h_id
SET r.r_status = ?
WHERE h.h_name = ? AND r.r_id = ?;

-- Retrieve booking details including hotel name, guest name, and booking status.
SELECT h_name, user.u_name, total_guests, check_in_date, check_out_date, status
FROM booking
         JOIN user ON u_id = booking.g_id;

-- Retrieve the top 5 most booked room types based on booking count.
SELECT room.r_type AS RoomType, COUNT(*) AS BookingCount
FROM reservations
         JOIN room ON reservations.r_id = room.r_id
GROUP BY room.r_type
ORDER BY COUNT(*) DESC
LIMIT 5;

-- Retrieve housekeeping schedules and their associated details.
SELECT t_start_date AS Start,
       t_end_date AS End,
       status AS Status,
       h_name AS Hotel,
       housekeeping_rooms.r_id AS Room,
       u_name AS Staff
FROM housekeeping_schedule
         LEFT JOIN housekeeping_rooms ON housekeeping_schedule.t_id = housekeeping_rooms.t_id
         LEFT JOIN housekeeping_staff ON housekeeping_schedule.t_id = housekeeping_staff.t_id
         JOIN user ON housekeeping_staff.hk_id = user.u_id
         JOIN room ON room.r_id = housekeeping_rooms.r_id
         JOIN hotel ON hotel.h_id = room.h_id;

-- Retrieve revenue reports for hotels, showing total revenue and unique booking count.
SELECT hotel.h_name, SUM(payments.amount), COUNT(DISTINCT booking.b_id)
FROM payments
         JOIN booking ON payments.b_id = booking.b_id
         JOIN hotel ON booking.h_name = hotel.h_name
GROUP BY hotel.h_name
ORDER BY SUM(payments.amount) DESC;

-- Guest Panel SQL Queries
-- Insert a new booking for a guest.
INSERT INTO booking (g_id, h_name, total_guests, check_in_date, check_out_date, status)
SELECT u_id, ?, ?, ?, ?, ?
FROM user
WHERE u_name = ?;

-- Retrieve all available rooms for booking, including room type and capacity.
SELECT
    roomtype.r_type AS Room_Type,
    roomtype.capacity AS Capacity,
    hotel.h_name AS Hotel_Name
FROM
    room
        JOIN roomtype ON room.r_type = roomtype.r_type
        JOIN hotel ON room.h_id = hotel.h_id
WHERE
    room.r_status = 'available';

-- Retrieve all bookings made by a specific guest.
SELECT
    booking.b_id AS Booking_ID,
    hotel.h_name AS Hotel_Name,
    booking.total_guests AS Total_Guests,
    booking.check_in_date AS Check_In_Date,
    booking.check_out_date AS Check_Out_Date,
    booking.status AS Status
FROM
    booking
        JOIN hotel ON booking.h_name = hotel.h_name
        JOIN user ON booking.g_id = user.u_id
WHERE
    user.u_name = ?;

-- Check the status of a specific booking.
SELECT b_id, status
FROM booking
WHERE b_id = ? AND g_id = (
    SELECT u_id FROM user WHERE u_name = ?
);

-- Receptionist Panel SQL Queries

-- Insert a new reservation for a hotel.
INSERT INTO reservations
SELECT h_id, ?
FROM hotel
WHERE h_name = ?;

-- Retrieve all bookings and reservations, grouped by hotel and guest details.
SELECT h_name, u_name, total_guests, r_id, status
FROM booking
         LEFT JOIN reservations ON booking.b_id = reservations.b_id
         JOIN user ON user.u_id = booking.g_id
ORDER BY h_name, u_name, total_guests, r_id;

-- Delete a reservation based on the reservation ID and hotel name.
DELETE FROM reservations
WHERE reservations.r_id = ? AND reservations.b_id = (
    SELECT b_id
    FROM booking
    WHERE h_name = ?
);

-- Insert a new housekeeping task into the schedule with a default status of 'waiting.'
INSERT INTO housekeeping_schedule (t_start_date, t_end_date, status)
VALUES (?, ?, 'waiting');

-- Retrieve all housekeeping tasks, including their status and dates.
SELECT t_id, t_start_date, t_end_date, status
FROM housekeeping_schedule;

-- Assign a housekeeping task to a staff member based on their username.
INSERT INTO housekeeping_staff (hk_id, t_id)
SELECT hk_id, ?
FROM housekeeper
         JOIN user ON housekeeper.hk_id = user.u_id
WHERE user.u_name = ?;

-- Assign a housekeeping task to a room in a hotel.
INSERT INTO housekeeping_rooms (t_id, r_id, h_id, status)
SELECT ?, ?, h.h_id, 'dirty'
FROM hotel h
WHERE h.h_name = ? AND EXISTS (
    SELECT 1
    FROM room
    WHERE room.h_id = h.h_id AND room.r_id = ?
);

-- Retrieve a count of tasks assigned to each housekeeper, sorted by task count.
SELECT user.u_name, COUNT(housekeeping_staff.t_id)
FROM housekeeper
         JOIN user ON housekeeper.hk_id = user.u_id
         LEFT JOIN housekeeping_staff ON housekeeper.hk_id = housekeeping_staff.hk_id
GROUP BY user.u_name
ORDER BY COUNT(housekeeping_staff.t_id);

-- Retrieve pending bookings for a guest based on their username.
SELECT b_id, total_guests, check_in_date, check_out_date, status
FROM booking
         JOIN user ON booking.g_id = user.u_id
WHERE user.u_name = ? AND booking.status = 'p_pending';

-- Record a payment for a booking and mark it as paid.
INSERT INTO payments (b_id, status, amount, payment_date)
VALUES (?, 'paid', ?, CURRENT_DATE);

-- Update the booking status to 'booked' after payment is processed.
UPDATE booking
SET status = 'booked'
WHERE b_id = ?;

-- Housekeeping Panel SQL Queries

-- Retrieve all rooms marked as 'dirty' for cleaning tasks.
SELECT
    housekeeping_rooms.r_id AS Room_ID,
    h_id AS Hotel_ID,
    h_name AS Hotel_Name
FROM
    housekeeping_rooms
        JOIN room r ON housekeeping_rooms.r_id = r.r_id
        JOIN hotel h ON r.h_id = h.h_id
WHERE
    housekeeping_rooms.status = 'dirty';

-- Retrieve all rooms marked as 'clean' for inspection or use.
SELECT
    housekeeping_rooms.r_id AS Room_ID,
    h_id AS Hotel_ID,
    h_name AS Hotel_Name
FROM
    housekeeping_rooms
        JOIN room r ON housekeeping_rooms.r_id = r.r_id
        JOIN hotel h ON r.h_id = h.h_id
WHERE
    housekeeping_rooms.status = 'clean';

-- Update the status of a room to 'clean' after housekeeping tasks are completed.
UPDATE housekeeping_rooms
SET housekeeping_rooms.status = 'clean'
WHERE r_id = ? AND h_id = (
    SELECT h_id
    FROM hotel
    WHERE h_name = ?
);

-- Retrieve the schedule and status of rooms assigned to a housekeeper.
SELECT
    housekeeping_rooms.r_id AS Room_ID,
    housekeeping_rooms.h_id AS Hotel_ID,
    hotel.h_name AS Hotel_Name,
    housekeeping_rooms.status AS Room_Status
FROM user
         JOIN housekeeping_staff ON user.u_id = housekeeping_staff.hk_id
         JOIN housekeeping_schedule ON housekeeping_staff.t_id = housekeeping_schedule.t_id
         JOIN housekeeping_rooms ON housekeeping_schedule.t_id = housekeeping_rooms.t_id
         JOIN hotel ON hotel.h_id = housekeeping_rooms.h_id
WHERE user.u_name = ?;

-- Login Page SQL Queries

-- Verify if a user is an administrator by matching their username and password.
SELECT COUNT(*)
FROM administrator
         JOIN user ON administrator.a_id = user.u_id
WHERE user.u_name = ? AND user.u_password = ?;

-- Verify if a user is a guest by matching their username and password.
SELECT COUNT(*)
FROM guest
         JOIN user ON guest.g_id = user.u_id
WHERE user.u_name = ? AND user.u_password = ?;

-- Verify if a user is a housekeeper by matching their username and password.
SELECT COUNT(*)
FROM housekeeper
         JOIN user ON housekeeper.hk_id = user.u_id
WHERE user.u_name = ? AND user.u_password = ?;

-- Verify if a user is a receptionist by matching their username and password.
SELECT COUNT(*)
FROM receptionist
         JOIN user ON receptionist.r_id = user.u_id
WHERE user.u_name = ? AND user.u_password = ?;

-- Sign-Up Page SQL Queries

-- Insert a new user into the database with a username, phone number, and password.
INSERT INTO user (u_name, u_phone_no, u_password)
VALUES (?, ?, ?);

-- Assign the newly created user as a guest by their user ID.
INSERT INTO guest (g_id)
SELECT u_id
FROM user
WHERE user.u_name = ? AND user.u_password = ?;
