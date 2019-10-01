# gsp-core

Core schema and data model for GSP.

| Artifact           | Description                    |
|--------------------|--------------------------------|
| `gsp-core-model`   | GSP core data model.           |
| `gsp-core-testkit` | GSP core laws and arbitraries. |
| `gsp-core-db`      | GSP core data access objects.  |

Most downstream libraries and applications will only require `gsp-core-model` and possibly `gsp-core-testkit`. The observing database will also require `gsp-core-db`. The model and schema are defined and released together in order to guarantee consistency between lookup tables and enumerated types (which are generated from the schema).

## Setting Up a Local Database

In order to build and run tests you will need a Postgres database running locally. The recommended way to do this is via the included `docker-compose` file. In the project root run:

```
docker-compose up -d
```

This will start a new Postgres database initialized with Schema defined in the `schema` project, configured as follows.

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





