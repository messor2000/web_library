# Web Library 
***
## What is the library used for
Web library that uses in **КІОНС** department in KPI.
This service gives the user options to watch the books that physically 
located in the university library by several variants. All site visitors can 
open in browser and download book in pdf format(if it exists).
User after registration has ability to book a book to pick up its 
physical copy from the library in the future. 
Admin has a lot of opportunities to add new book, add new user, edit book/user,
add many book using Excel file. Also, the main admin task is to control who and 
how many books, he can accept or reject booked book, change book status after
he gives a physical copy and return book into the library.
---
## Technologies that used in this service
This service is using **PostgreSQL** database that placed in **Docker**.
For storing images for user's and for book covers server is using **S3** 
bucket which is part of **Amazon Web Services**.
## How deploy locally library in your computer
1. Clone repository 
2. Install Docker in your computer
3. Run command ```docker-compose up``` in your project directory
4. Go to **localhost:5050/** and you can pg4admin program
5. Enter credentials in login panel
6. Create new connection 
7. Create new database with name **web_library**
8. Go to **localhost:8080/** and you can see empty library

How create admin to get full access to the service
1. Register new user in library
2. Go to the pg4admin in your browser
3. Find your created user entity in the library_user table, remember his id
4. Find role id in the table users_role with associated with user id
5. File role by role id in the role table and change role from the **ROLE_USER**
to the **ROLE_ADMIN**
6. Recompile your project, login by your user credentials and get full access 
to the library 

How add images to the book cover and pdf files for book
1. Create account in the aws
2. Create new bucket in the S3 service with unique name
3. Create IAM user and add to him full access to the S3 service. Save this 
IAM user credentials 
4. Go to the application.properties file in the app and put all the credentials their
5. Recompile your app, add cover to the book and you will see image on the book

---
If you have any question you can write to me by my email: **ovcharenko.messor@gmail.com**