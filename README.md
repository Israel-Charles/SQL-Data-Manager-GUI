# SQL Data Manager - GUI

SQL Data Manager is a Java-based GUI application that allows users to manage SQL databases. Users can connect to a MySQL database by providing the database URL, username, and password directly in the GUI. The application supports executing SQL commands and displays the results in a table. It also allows users to export result as CSV file.

#### Demo > https://codepad.site/pad/98t70yjd

## Features

- **Dynamic Database Connection:** Connect to any MySQL database by entering the database URL, username, and password directly.
- **SQL Command Execution:** Execute SQL commands and view the results directly within the GUI.
- **Results Management:** Clear results and export them to CSV files for further analysis.
- **Command History:** Clear previous commands or export them to text files.

## Prerequisites

Before you begin, ensure you have installed:
- **Java Development Kit (JDK)**: Version 8 or higher.
- **MySQL Server**: Ensure MySQL server is running and accessible.

## Setup and Installation
**When running or compiling the program, make sure to include the external library found in the 'lib' folder**

## How to Use

### Connect to Database
1. Enter the database URL, username, and password in the respective fields.
2. Click the Connect to Database button to establish a connection.

If successful, the status will display as "CONNECTED".

### Execute SQL Commands
1. Type your SQL command in the command input area.
2. Press the Execute Command button to run the command.

Results will appear in the table, or an update count will be shown for non-query SQL commands.

### Manage Commands and Results
- Use the Clear Command button to reset the command input area.
- Use the Clear Result button to empty the results table.
- Use the Export Command or Export Result buttons to save data to files.

### Disconnect from the Database
- Click the Disconnect from Database button to terminate the connection.
