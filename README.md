# davisbase

Implementation of a database engine that is loosely based on MySQL

Go to the src folder to see the source code.
cd src

To create an executable .class file type
javac DavisBasePrompt.java

The above steps will create an executable DavisBasePrompt.class file.
To run the project use the following command from the src folder:
java DavisBasePrompt

On running the above command successfully the following text should be seen:
davisql> 

We recommend trying out these commands to test the SQL engine:

1. show databases;

2. use catalog;

3. show tables;

4. help;

5. select * from davisbase_columns;

6. select * from davisbase_columns where table_name="davisbase_tables";

7. CREATE TABLE person (row_id INT PRIMARY KEY, fName TEXT NOT NULL);

8. insert into person values (1,'achyut');

9. update person set fname='bhandiwad' where row_id=1;

10. delete from person;

11. drop table person;

12. exit;
