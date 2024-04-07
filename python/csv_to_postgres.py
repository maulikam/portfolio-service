# csv_to_postgres.py

import pandas as pd
from sqlalchemy import create_engine

def connect_to_database(username, password, host, port, database_name):
    print(f"Connecting to the PostgreSQL database at {host}:{port}...")
    connection_string = f"postgresql://{username}:{password}@{host}:{port}/{database_name}"
    engine = create_engine(connection_string)
    print("Connected to the database successfully.")
    return engine

def write_to_database(file_path, engine, table_name, database_name, batch_size=100000):
    """
    Writes data from a CSV file to a PostgreSQL table in batches.

    Parameters:
        file_path (str): The path to the CSV file.
        engine (sqlalchemy.engine.base.Engine): The SQL Alchemy engine connected to the database.
        table_name (str): The name of the table to write the DataFrame to.
        database_name (str): The name of the database where the table is.
        batch_size (int): Number of rows per batch to process.
    """
    print(f"Writing data from {file_path} to {table_name} table in batches of {batch_size}...")
    chunk_iter = pd.read_csv(file_path, chunksize=batch_size)
    first_chunk = True
    for chunk in chunk_iter:
        if first_chunk:
            chunk.to_sql(table_name, engine, if_exists='replace', index=False)
            first_chunk = False
        else:
            chunk.to_sql(table_name, engine, if_exists='append', index=False)
        print(f"Batch written to {table_name} table.")
    print(f"Data from {file_path} has been successfully written to the {table_name} table in the {database_name} database.")

def main():
    # Parameters
    csv_file_path = 'merged_df.csv'
    username = 'postgres'
    password = 'adminpassword'
    host = 'localhost'
    port = '5430'  # Ensure this is the correct port for your PostgreSQL instance
    database_name = 'renilalgo'
    table_name = 'merged_df'

    # Process
    engine = connect_to_database(username, password, host, port, database_name)
    write_to_database(csv_file_path, engine, table_name, database_name)
    
if __name__ == "__main__":
    main()
