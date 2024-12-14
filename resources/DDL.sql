CREATE TABLE IF NOT EXISTS hotel (
                                     h_id INT AUTO_INCREMENT PRIMARY KEY,
                                     h_name VARCHAR(50) NOT NULL UNIQUE,
    h_zip VARCHAR(10),
    h_street VARCHAR(50),
    h_building_no VARCHAR(10),
    h_phone_no VARCHAR(11)
    );

CREATE TABLE IF NOT EXISTS roomtype (
                                        r_type VARCHAR(50) PRIMARY KEY,
    capacity INT NOT NULL
    );

CREATE TABLE IF NOT EXISTS room (
                                    r_id INT,
                                    h_id INT,
                                    r_type VARCHAR(50),
    r_status VARCHAR(10),
    PRIMARY KEY (r_id, h_id),
    FOREIGN KEY (r_type) REFERENCES roomtype(r_type) ON DELETE SET NULL,
    FOREIGN KEY (h_id) REFERENCES hotel(h_id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS user (
                                    u_id INT AUTO_INCREMENT PRIMARY KEY,
                                    u_name VARCHAR(50) NOT NULL UNIQUE,
    u_phone_no VARCHAR(11),
    u_password VARCHAR(50) NOT NULL
    );

CREATE TABLE IF NOT EXISTS guest (
                                     g_id INT PRIMARY KEY,
                                     FOREIGN KEY (g_id) REFERENCES user(u_id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS administrator (
                                             a_id INT PRIMARY KEY,
                                             FOREIGN KEY (a_id) REFERENCES user(u_id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS receptionist (
                                            r_id INT PRIMARY KEY,
                                            FOREIGN KEY (r_id) REFERENCES user(u_id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS housekeeper (
                                           hk_id INT PRIMARY KEY,
                                           FOREIGN KEY (hk_id) REFERENCES user(u_id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS housekeeping_schedule (
                                                     t_id INT PRIMARY KEY,
                                                     t_start_date DATE NOT NULL,
                                                     t_end_time TIME NOT NULL,
                                                     status VARCHAR(10),
    hk_id INT,
    FOREIGN KEY (hk_id) REFERENCES housekeeper(hk_id) ON DELETE SET NULL
    );

CREATE TABLE IF NOT EXISTS housekeeping_staff (
                                                  hk_id INT,
                                                  t_id INT,
                                                  PRIMARY KEY (hk_id, t_id),
    FOREIGN KEY (hk_id) REFERENCES housekeeper(hk_id) ON DELETE CASCADE,
    FOREIGN KEY (t_id) REFERENCES housekeeping_schedule(t_id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS housekeeping_rooms (
                                                  t_id INT,
                                                  r_id INT,
                                                  PRIMARY KEY (t_id, r_id),
    FOREIGN KEY (t_id) REFERENCES housekeeping_schedule(t_id) ON DELETE CASCADE,
    FOREIGN KEY (r_id) REFERENCES room(r_id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS booking (
                                       b_id INT AUTO_INCREMENT PRIMARY KEY,
                                       g_id INT NOT NULL,
                                       h_name VARCHAR(50) NOT NULL,
                                       r_id INT,
                                       total_guests INT,
                                       check_in_date DATE,
                                       check_out_date DATE,
                                       status VARCHAR(10),
    FOREIGN KEY (g_id) REFERENCES guest(g_id) ON DELETE SET NULL,
    FOREIGN KEY (r_id) REFERENCES room(r_id) ON DELETE SET NULL,
    FOREIGN KEY (h_name) REFERENCES hotel(h_id) ON DELETE SET NULL
    );

CREATE TABLE IF NOT EXISTS reservations (
                                            b_id INT,
                                            r_id INT,
                                            PRIMARY KEY (b_id, r_id),
    FOREIGN KEY (b_id) REFERENCES booking(b_id) ON DELETE CASCADE,
    FOREIGN KEY (r_id) REFERENCES room(r_id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS payments (
                                        p_id INT PRIMARY KEY,
                                        b_id INT NOT NULL,
                                        status VARCHAR(10),
    amount DECIMAL(10,2) NOT NULL,
    payment_date DATE,
    FOREIGN KEY (b_id) REFERENCES booking(b_id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS booking_payments (
                                                b_id INT,
                                                p_id INT,
                                                PRIMARY KEY (b_id, p_id),
    FOREIGN KEY (b_id) REFERENCES booking(b_id) ON DELETE CASCADE,
    FOREIGN KEY (p_id) REFERENCES payments(p_id) ON DELETE CASCADE
    );