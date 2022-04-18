This user interface allows the user to do one of three options to a database of tweets regarding the 2016 presidential election. 
Insert a new tweet into the database providing the user, tweet text, day, month, year, and number of retweets the tweet has. The second option
is to delete a user completely from the database. Providing the username removes it from the mentions table, hashtags table, url table, any 
tweets made by the user, and then the user from the user table. The third option is to perform a stored procedure that returns the number of tweets and
from the user containing a given hashtag from a given month and year and the user from a given state. The java file contains an easy to use user interface
the allows the user to perform one of the three options and then perform the query using JDBC calls to the stored procedures or just a simple query. 
This program using set Transaction Isolation Level TRANSACTION_SERIALIZABLE to prevent Phantom reads. 