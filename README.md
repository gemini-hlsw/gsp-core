# gsp-core

Core schema and data model for GSP.

| Artifact           | Description                    |
|--------------------|--------------------------------|
| `gsp-core-model`   | GSP core data model.           |
| `gsp-core-testkit` | GSP core laws and arbitraries. |
| `gsp-core-db`      | GSP core data access objects.  |

Most downstream libraries and applications will only require `gsp-core-model` and possibly `gsp-core-testkit`. The observing database will also require `gsp-core-db`. The model and schema are defined and released together in order to guarantee consistency between lookup tables and enumerated types (which are generated from the schema).

## Setting Up a Local Database

In order to build and run tests you will need a Postgres database running locally. There are two ways to do this.

### Option 1: Postgres in Docker

This option is what's used in CI and is what you want if you're ok starting over with an empty database when things change. It does not require you to install or administer your own Postgres instance. Make sure you have [Docker](https://www.docker.com) installed, then you can use the included `docker-compose` file. In the project root run:

```
docker-compose up -d
```

This will start a new Postgres database initialized with the schema defined in the `schema` project, configured as follows.

| Parameter | Value      | Notes                      |
|-----------|------------|----------------------------|
| Port      | `5432`     | This is the standard port. |
| Database  | `gem`      |                            |
| User      | `postgres` |                            |
| Password  | (none)     |                            |

If you have `psql` installed locally you can connect to the database thus:

```
psql -h localhost -U postgres -d gem
```

Otherwise you can run it in a container:

```
docker-compose exec db psql -U postgres -d gem
```

To stop and delete the database:

```
docker-compose down
```

### Option 2: Local Postgres Installation with Flyway

If you want to maintain a database full of data and peform migrations instead of starting with a fresh database every time, this option is probably what you want. Install [Postgres.app](https://postgresapp.com) and add its binaries to your path, something along the lines of

```
export PATH=$PATH:/Applications/Postgres.app/Contents/Versions/latest/bin
```

Next you can run the following to create the `postgres` user and `gem` database.

```
psql -c 'create user postgres createdb'
psql -c 'create database gem' -U postgres
```

Initialize the database by running the migration scripts.

```
sbt sql/flywayMigrate
```

If you ever want to wipe out the database and start over, you can do

```
psql -c 'drop database gem' -U postgres
```

And then redo the steps above starting from `create database`.

You can do

```
psql -U postgres -d gem
```

to poke around with the database on the commandline.

## Generating Enumerated Types

There are many enumerated types in the database, represented by tables named `e_whatever`. The Scala equivalents are generated *on demand* by queries, then checked into source control like normal source files. This is only needed if you update the contents of an enum in the schema, or add/modify a the generation
code in the `gen` project. In any case, you can [re]-generate the enumerated types thus:

```
sbt genEnums
```

The source files appear in `modules/model/shared/src/main/scala/gem/enum`.

