#!/usr/bin/env bash

/opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P ${SA_PASSWORD} -Q "CREATE DATABASE [onebase_v3];
GO"
/opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P ${SA_PASSWORD} -d 'onebase_v3' -i /tmp/schema.sql
