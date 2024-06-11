-- Insert authors
INSERT INTO author (name, birth_date, nationality) VALUES
  ('Stephen King', '1947-09-21', 'American'),
  ('J.K. Rowling', '1965-07-31', 'British'),
  ('Agatha Christie', '1890-09-15', 'British'),
  ('George Orwell', '1903-06-25', 'British'),
  ('Haruki Murakami', '1949-01-12', 'Japanese'),
  ('Jane Austen', '1775-12-16', 'British'),
  ('Leo Tolstoy', '1828-09-09', 'Russian'),
  ('Mark Twain', '1835-11-30', 'American'),
  ('J.R.R. Tolkien', '1892-01-03', 'British'),
  ('Gabriel Garcia Marquez', '1927-03-06', 'Colombian');

-- Insert books
INSERT INTO book (title, author_id, isbn, publication_date, genre, available) VALUES
  ('The Shining', 1, '9780385121681', '1977-01-28', 'Horror', true),
  ('Harry Potter and the Philosopher''s Stone', 2, '9780747532743', '1997-06-26', 'Fantasy', true),
  ('Murder on the Orient Express', 3, '9780007119318', '1934-01-01', 'Mystery', true),
  ('1984', 4, '9780451524935', '1949-06-08', 'Dystopian', true),
  ('Norwegian Wood', 5, '9780375704024', '1987-08-04', 'Fiction', true),
  ('Pride and Prejudice', 6, '9780141439518', '1813-01-28', 'Romance', true),
  ('War and Peace', 7, '9781421404452', '1869-01-01', 'Historical Fiction', true),
  ('The Adventures of Tom Sawyer', 8, '9780140620646', '1876-01-01', 'Adventure', true),
  ('The Hobbit', 9, '9780345339683', '1937-09-21', 'Fantasy', true),
  ('One Hundred Years of Solitude', 10, '9780060883287', '1967-05-30', 'Magical Realism', true);

-- Insert customers
INSERT INTO customer (name, email, address, phone_number, password) VALUES
  ('John Smith', 'john@example.com', '123 Main St, Anytown', '+1234567890', '$2a$10$EJv3JhOcNMyV/lnYH1LD/uZzJ7s32XzfkVweRdZSwqA9PR8WmNHQC'), -- Hashed password: password1
  ('Alice Johnson', 'alice@example.com', '456 Elm St, Othertown', '+0987654321', '$2a$10$EJv3JhOcNMyV/lnYH1LD/uZzJ7s32XzfkVweRdZSwqA9PR8WmNHQC'), -- Hashed password: password2
  ('Michael Brown', 'michael@example.com', '789 Oak St, Anycity', '+1357924680', '$2a$10$EJv3JhOcNMyV/lnYH1LD/uZzJ7s32XzfkVweRdZSwqA9PR8WmNHQC'), -- Hashed password: password3
  ('Emily Davis', 'emily@example.com', '1010 Pine St, Anothercity', '+2468135790', '$2a$10$EJv3JhOcNMyV/lnYH1LD/uZzJ7s32XzfkVweRdZSwqA9PR8WmNHQC'), -- Hashed password: password4
  ('James Wilson', 'james@example.com', '1212 Cedar St, Yetanothercity', '+3692581470', '$2a$10$EJv3JhOcNMyV/lnYH1LD/uZzJ7s32XzfkVweRdZSwqA9PR8WmNHQC'), -- Hashed password: password5
  ('Emma Martinez', 'emma@example.com', '1414 Maple St, Finalcity', '+7531908246', '$2a$10$EJv3JhOcNMyV/lnYH1LD/uZzJ7s32XzfkVweRdZSwqA9PR8WmNHQC'), -- Hashed password: password6
  ('David Jones', 'david@example.com', '1616 Birch St, Lastcity', '+9876543210', '$2a$10$EJv3JhOcNMyV/lnYH1LD/uZzJ7s32XzfkVweRdZSwqA9PR8WmNHQC'), -- Hashed password: password7
  ('Olivia Brown', 'olivia@example.com', '1818 Walnut St, Finaltown', '+1234567890', '$2a$10$EJv3JhOcNMyV/lnYH1LD/uZzJ7s32XzfkVweRdZSwqA9PR8WmNHQC'), -- Hashed password: password8
  ('William Taylor', 'william@example.com', '2020 Hickory St, Lasttown', '+9876543210', '$2a$10$EJv3JhOcNMyV/lnYH1LD/uZzJ7s32XzfkVweRdZSwqA9PR8WmNHQC'), -- Hashed password: password9
  ('Sophia Rodriguez', 'sophia@example.com', '2222 Cherry St, Endtown', '+1234567890', '$2a$10$EJv3JhOcNMyV/lnYH1LD/uZzJ7s32XzfkVweRdZSwqA9PR8WmNHQC'); -- Hashed password: password10

  -- Insert borrowing records
  INSERT INTO borrowing_record (customer_id, book_id, borrow_date, return_date) VALUES
    (1, 1, '2024-05-01', '2024-05-15'),
    (2, 2, '2024-05-02', '2024-05-16'),
    (3, 3, '2024-05-03', '2024-05-17'),
    (4, 4, '2024-05-04', '2024-05-18'),
    (5, 5, '2024-05-05', '2024-05-19'),
    (6, 6, '2024-05-06', '2024-05-20'),
    (7, 7, '2024-05-07', '2024-05-21'),
    (8, 8, '2024-05-08', '2024-05-22'),
    (9, 9, '2024-05-09', '2024-05-23'),
    (10, 10, '2024-05-10', '2024-05-24');
