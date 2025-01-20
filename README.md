# dynamic-transaction-routing-example
Learning how to dynamically route read-only and read-write transactions from a Spring Boot application to the intended database resource.

## Setup Primary and Secondary (Replica) Databases

1. Install PostgreSQL.
    1. [Postgres.app](https://postgresapp.com/)
        1. This is a full-featured PostgreSQL installation package provided as a standard Mac application.
    2. To simplify working with PostgreSQL in general it is **HIGHLY** recommended to enable CLI commands by updating your PATH variable.
       3. Instructions: https://postgresapp.com/documentation/cli-tools.html
1. Initialize a server if Postgres.app has not done so already.
1. Configure the PostgreSQL server: 
   1. `postgresql.conf` usually found `/Users/{user}/Library/Application Support/Postgres/{server-data-directory}/postgresql.conf`
       1. `wal_level = replica`
           1. Enables streaming replication by including the necessary information in the Write-Ahead Logs (WAL).
       1. `max_wal_senders = 1`
           1. Specifies the maximum number of replica databases that can connect to the primary database.
       1. `wal_sender_timeout = 60s`
           1. Defines how long the primary database waits for acknowledgments from the replica before timing out.
           1. A timeout of 60 seconds strikes a good balance between responsiveness and reliability.
       1. `listen_addresses = 'localhost'`
           1. Restricts incoming connections to the server to localhost.
           1. Since this project is an experiment conducted on a local machine, we limit access to localhost for security. In a production environment, this setting should carefully control and restrict communication to only the desired replica databases.
1. Verify your configuration values.
   1. `cat postgresql.conf | grep {configuration-key}`
1. Restart PostgreSQL instance (server):
   2. command line: `pg_ctl restart -D ~/Users/{user}/Library/Application\ Support/Postgres/{server-data-directory}`
   3. Postgres.app: Open the client, then stop (wait 30 seconds), then start the server again.
4. Verify the configuration updates are being correctly applied.
   5. Open and search the postgresql logs:
      6. Within a PostgreSQL shell run the command `SHOW {configuration-key}`
7. 