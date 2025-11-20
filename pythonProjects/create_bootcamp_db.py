import psycopg2
from psycopg2.errors import DuplicateDatabase

# Your create_database_if_not_exists function (as provided)
def create_database_if_not_exists(host, port, user, password, db_name):
    """
    Connects to the default 'postgres' database and creates the specified
    database if it does not already exist.
    """
    conn = None
    try:
        # Connect to the default 'postgres' database
        conn = psycopg2.connect(
            host=host,
            port=port,
            user=user,
            password=password,
            database="postgres" # Connect to the default database
        )
        conn.autocommit = True

        cursor = conn.cursor()

        # Check if the database already exists
        cursor.execute(f"SELECT 1 FROM pg_database WHERE datname = '{db_name}'")
        exists = cursor.fetchone()

        if not exists:
            print(f"Creating database '{db_name}' on {host}:{port}...")
            cursor.execute(f"CREATE DATABASE {db_name}")
            print(f"Database '{db_name}' created successfully on {host}:{port}.")
        else:
            print(f"Database '{db_name}' already exists on {host}:{port}.")

    except DuplicateDatabase:
        print(f"Database '{db_name}' already exists (caught DuplicateDatabase error) on {host}:{port}.")
    except psycopg2.Error as e:
        print(f"Error creating database on {host}:{port}: {e}")
    finally:
        if conn:
            conn.close()

if __name__ == "__main__":
    # --- Configuration for localhost:5432 (system-installed PostgreSQL) ---
    DB_HOST_5432 = "localhost"
    DB_PORT_5432 = "5432"
    DB_USER_5432 = "postgres"
    DB_PASSWORD_5432 = "admin1234" # Replace with your actual password for 5432
    TARGET_DATABASE_NAME = "bootcamp_2504"

    print("--- Attempting to create database on localhost:5432 ---")
    create_database_if_not_exists(
        DB_HOST_5432,
        DB_PORT_5432,
        DB_USER_5432,
        DB_PASSWORD_5432,
        TARGET_DATABASE_NAME
    )
    # Optional: Test connection after creation
    try:
        app_conn_5432 = psycopg2.connect(
            host=DB_HOST_5432,
            port=DB_PORT_5432,
            user=DB_USER_5432,
            password=DB_PASSWORD_5432,
            database=TARGET_DATABASE_NAME
        )
        print(f"Successfully connected to {TARGET_DATABASE_NAME} on {DB_HOST_5432}:{DB_PORT_5432}.")
        app_conn_5432.close()
    except psycopg2.Error as e:
        print(f"Failed to connect to {TARGET_DATABASE_NAME} on {DB_HOST_5432}:{DB_PORT_5432}: {e}")


    print("\n--- Attempting to create database on localhost:5532 (Docker) ---")
    # --- Configuration for localhost:5532 (Docker PostgreSQL) ---
    DB_HOST_5532 = "localhost"
    DB_PORT_5532 = "5532"
    DB_USER_5532 = "postgres"
    DB_PASSWORD_5532 = "admin1234" # Replace with your actual password for 5532

    create_database_if_not_exists(
        DB_HOST_5532,
        DB_PORT_5532,
        DB_USER_5532,
        DB_PASSWORD_5532,
        TARGET_DATABASE_NAME
    )
    # Optional: Test connection after creation
    try:
        app_conn_5532 = psycopg2.connect(
            host=DB_HOST_5532,
            port=DB_PORT_5532,
            user=DB_USER_5532,
            password=DB_PASSWORD_5532,
            database=TARGET_DATABASE_NAME
        )
        print(f"Successfully connected to {TARGET_DATABASE_NAME} on {DB_HOST_5532}:{DB_PORT_5532}.")
        app_conn_5532.close()
    except psycopg2.Error as e:
        print(f"Failed to connect to {TARGET_DATABASE_NAME} on {DB_HOST_5532}:{DB_PORT_5532}: {e}")
