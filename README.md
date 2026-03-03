# DPP Renderer BE

Backend application whose main functions are:
- DPP retrieval as JSON or as JSON-LD.
- DPP comparison: extraction of properties from at least two DPPs based on a custom property accessor format.
- DPP search over local storage.

© CIRPASS-2 Consortium, 2024-2027

<img width="832" height="128" alt="image" src="https://raw.githubusercontent.com/CIRPASS-2/assets/main/images/cc-commons.png" />

The CIRPASS-2 project receives funding under the European Union's DIGITAL EUROPE PROGRAMME under GA No 101158775.

> **Important disclaimer:**
> All software and artifacts produced by the CIRPASS-2 consortium are designed for exploration and are provided for information purposes only. They should not be interpreted as being complete, exhaustive, or normative. The CIRPASS-2 consortium partners are not liable for any damage that could result from making use of this information.
>
> Technical interpretations of the European Digital Product Passport system expressed in these artifacts are those of the author(s) only and do not necessarily reflect those of the European Union, European Commission, or the European Health and Digital Executive Agency (HADEA). Neither the European Union, the European Commission nor the granting authority can be held responsible for them. These interpretations should not be understood as reflecting those of CEN-CENELEC JTC 24.

## Overview

This application acts as a backend for:
- **DPP retrieval**: fetches a DPP from the decentralized repository and returns it as a JSON document or as expanded JSON-LD, performing on-the-fly conversion from other RDF serializations.
- **DPP comparison**: extracts properties from multiple DPPs using a custom property path format.
- **DPP search**: queries model-level data stored in the local database.

### Key Features

- **RESTful API** for searching, retrieving, and comparing DPPs.
- **Property extraction** for comparison purposes.
- **Multi-format DPP support**: JSON, JSON-LD, RDF-XML, Turtle, N3, N-Quads, N-Triples.
- **Multiple database backends**: PostgreSQL and MariaDB.
- **OpenID Connect authentication** with role-based access control.

## Table of Contents

- [Overview](#overview)
    - [Key Features](#key-features)
- [Quick Start](#quick-start)
    - [Build the Application](#build-the-application)
    - [Run the Application](#run-the-application)
    - [Using Docker](#using-docker)
- [Configuration](#configuration)
    - [Configuration Variables Reference](#configuration-variables-reference)
        - [Database](#database)
        - [OpenID Connect](#openid-connect)
        - [HTTP](#http)
        - [Configuration Notes](#configuration-notes)
    - [Configuration Examples](#configuration-examples)
- [REST API](#rest-api)
    - [Comparison Endpoints](#comparison-endpoints)
    - [Search Endpoints](#search-endpoints)
    - [Fetch Endpoints](#fetch-endpoints)
- [Authentication & Authorization](#authentication--authorization)
- [License](#license)
- [Contributing](#contributing)
- [Support](#support)

## Quick Start

The application provides two Maven profiles:
- `pgsql-oidc`: builds the application using PostgreSQL as the database and OIDC as the authentication method.
- `mariadb-oidc`: builds the application using MariaDB as the database and OIDC as the authentication method.

Pre-built artifacts and Docker images are available [here](https://github.com/cirpass-2/dpp-renderer-be/releases).

### Build the Application

```bash
mvn clean install -P pgsql-oidc
```
or
```bash
mvn clean install -P mariadb-oidc
```

### Run the Application

After building, run the application using the Quarkus runner. Create an `application.properties` file with your configuration and specify its location via the system property shown below.

**Run with PostgreSQL:**
```bash
java -Dquarkus.config.locations=file://path/to/application.properties \
     -Dquarkus.profile=pgsql,oidc \
     -jar target/quarkus-app/quarkus-run.jar
```

**Run with MariaDB:**
```bash
java -Dquarkus.config.locations=file://path/to/application.properties \
     -Dquarkus.profile=mariadb,oidc \
     -jar target/quarkus-app/quarkus-run.jar
```

Alternatively, configuration can be supplied entirely via environment variables:

```bash
QUARKUS_DATASOURCE_REACTIVE_URL=vertx-reactive:postgresql://localhost:5432/searches \
QUARKUS_DATASOURCE_USERNAME=db_user \
QUARKUS_DATASOURCE_PASSWORD=db_password \
QUARKUS_OIDC_AUTH_SERVER_URL=https://your-idp.com/realms/your-realm \
QUARKUS_OIDC_CLIENT_ID=your-client-id \
QUARKUS_OIDC_CREDENTIALS_SECRET=your-secret \
java -jar target/quarkus-app/quarkus-run.jar
```

### Using Docker

See the [Docker Compose examples](#configuration-examples) in the configuration section.

## Configuration

### Configuration Variables Reference

#### Database

| Variable                               | Environment Variable                   | Description                  | Default    |
|----------------------------------------|----------------------------------------|------------------------------|------------|
| `quarkus.datasource.reactive.url`      | `QUARKUS_DATASOURCE_REACTIVE_URL`      | Database reactive URL        | —          |
| `quarkus.datasource.username`          | `QUARKUS_DATASOURCE_USERNAME`          | Database username            | —          |
| `quarkus.datasource.password`          | `QUARKUS_DATASOURCE_PASSWORD`          | Database password            | —          |
| `quarkus.datasource.reactive.max-size` | `QUARKUS_DATASOURCE_REACTIVE_MAX_SIZE` | Maximum connection pool size | `16`       |

**PostgreSQL reactive URL format:**
```
vertx-reactive:postgresql://hostname:port/database_name
```
Example: `vertx-reactive:postgresql://localhost:5432/searches_db`

**MariaDB reactive URL format:**
```
vertx-reactive:mysql://hostname:port/database_name
```
Example: `vertx-reactive:mysql://localhost:3306/searches_db`

> **Note:** For MariaDB the reactive driver uses the `mysql` protocol identifier.

**PostgreSQL schema:**
```sql
CREATE TABLE IF NOT EXISTS dpp_data (
    id          BIGINT       PRIMARY KEY AUTO_INCREMENT,
    upi         VARCHAR(36)  UNIQUE NOT NULL,  -- Unique Product Identifier
    live_url    VARCHAR(1000),                 -- URL of the live DPP in the decentralized repository
    search_data JSON         NOT NULL          -- Extracted model-level fields, indexed for search
);
```

**MariaDB schema:**
```sql
CREATE TABLE IF NOT EXISTS dpp_data (
    id          BIGINT       PRIMARY KEY AUTO_INCREMENT,
    upi         VARCHAR(36)  UNIQUE NOT NULL,  -- Unique Product Identifier
    live_url    VARCHAR(1000),                 -- URL of the live DPP in the decentralized repository
    search_data JSON         NOT NULL          -- Extracted model-level fields, indexed for search
);
```

#### OpenID Connect

| Variable                             | Environment Variable                 | Description                              | Default  |
|--------------------------------------|--------------------------------------|------------------------------------------|----------|
| `quarkus.oidc.auth-server-url`       | `QUARKUS_OIDC_AUTH_SERVER_URL`       | OIDC server URL (realm URL for Keycloak) | —        |
| `quarkus.oidc.client-id`             | `QUARKUS_OIDC_CLIENT_ID`             | OIDC client ID                           | —        |
| `quarkus.oidc.credentials.secret`    | `QUARKUS_OIDC_CREDENTIALS_SECRET`    | OIDC client secret                       | —        |
| `quarkus.oidc.roles.role-claim-path` | `QUARKUS_OIDC_ROLES_ROLE_CLAIM_PATH` | Role claim path in the JWT token         | `groups` |

#### HTTP

| Variable            | Environment Variable | Description         | Default |
|---------------------|----------------------|---------------------|---------|
| `quarkus.http.port` | `QUARKUS_HTTP_PORT`  | HTTP listening port | `8080`  |

#### Configuration Notes

**OIDC role claim path**

Supports multiple comma-separated paths. The system searches for roles in the JWT token at each path in order.

Example: `group,realm_access.roles`

### Configuration Examples

#### Application Properties (PostgreSQL)

```properties
# Database
quarkus.datasource.reactive.url=vertx-reactive:postgresql://localhost:5432/searches_db
quarkus.datasource.username=dbuser
quarkus.datasource.password=dbpass
quarkus.datasource.reactive.max-size=20

# OIDC
quarkus.oidc.auth-server-url=https://keycloak.example.com/realms/myrealm
quarkus.oidc.client-id=my-client
quarkus.oidc.credentials.secret=my-secret
quarkus.oidc.roles.role-claim-path=group,realm_access.roles
```

#### Application Properties (MariaDB)

```properties
# Database
quarkus.datasource.reactive.url=vertx-reactive:mysql://localhost:3306/searches
quarkus.datasource.username=dbuser
quarkus.datasource.password=dbpass
quarkus.datasource.reactive.max-size=20

# OIDC
quarkus.oidc.auth-server-url=https://keycloak.example.com/realms/myrealm
quarkus.oidc.client-id=my-client
quarkus.oidc.credentials.secret=my-secret
quarkus.oidc.roles.role-claim-path=group,realm_access.roles
```

#### Docker Compose (PostgreSQL)

```yaml
version: '3.8'

services:
  extractor:
    image: ghcr.io/cirpass-2/dpp-data-extractor-pgsql-oidc:latest
    ports:
      - "8080:8080"
    environment:
      QUARKUS_DATASOURCE_REACTIVE_URL: vertx-reactive:postgresql://postgres:5432/searches_db
      QUARKUS_DATASOURCE_USERNAME: dbuser
      QUARKUS_DATASOURCE_PASSWORD: ${DB_PASSWORD}
      QUARKUS_DATASOURCE_REACTIVE_MAX_SIZE: 20
      QUARKUS_OIDC_AUTH_SERVER_URL: https://keycloak:8443/realms/myrealm
      QUARKUS_OIDC_CLIENT_ID: my-client
      QUARKUS_OIDC_CREDENTIALS_SECRET: ${OIDC_SECRET}
      QUARKUS_OIDC_ROLES_ROLE_CLAIM_PATH: group,realm_access.roles
    depends_on:
      - postgres

  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: searches_db
      POSTGRES_USER: dbuser
      POSTGRES_PASSWORD: ${DB_PASSWORD}
    volumes:
      - postgres-data:/var/lib/postgresql/data
    ports:
      - "5432:5432"

volumes:
  postgres-data:
```

#### Docker Compose (MariaDB)

```yaml
version: '3.8'

services:
  extractor:
    image: ghcr.io/cirpass-2/dpp-data-extractor-mariadb-oidc:latest
    ports:
      - "8080:8080"
    environment:
      QUARKUS_DATASOURCE_REACTIVE_URL: vertx-reactive:mysql://mariadb:3306/searches_db
      QUARKUS_DATASOURCE_USERNAME: dbuser
      QUARKUS_DATASOURCE_PASSWORD: ${DB_PASSWORD}
      QUARKUS_DATASOURCE_REACTIVE_MAX_SIZE: 20
      QUARKUS_OIDC_AUTH_SERVER_URL: https://keycloak:8443/realms/myrealm
      QUARKUS_OIDC_CLIENT_ID: my-client
      QUARKUS_OIDC_CREDENTIALS_SECRET: ${OIDC_SECRET}
      QUARKUS_OIDC_ROLES_ROLE_CLAIM_PATH: group,realm_access.roles
    depends_on:
      - mariadb

  mariadb:
    image: mariadb:11
    environment:
      MARIADB_DATABASE: searches_db
      MARIADB_USER: dbuser
      MARIADB_PASSWORD: ${DB_PASSWORD}
      MARIADB_ROOT_PASSWORD: ${DB_ROOT_PASSWORD}
    volumes:
      - mariadb-data:/var/lib/mysql
    ports:
      - "3306:3306"

volumes:
  mariadb-data:
```

#### Kubernetes Deployment

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: be-config
data:
  QUARKUS_DATASOURCE_REACTIVE_URL: "vertx-reactive:postgresql://postgres-service:5432/searches_db"
  QUARKUS_DATASOURCE_USERNAME: "dbuser"
  QUARKUS_DATASOURCE_REACTIVE_MAX_SIZE: "20"
  QUARKUS_OIDC_AUTH_SERVER_URL: "https://keycloak.example.com/realms/myrealm"
  QUARKUS_OIDC_CLIENT_ID: "my-client"
  QUARKUS_OIDC_ROLES_ROLE_CLAIM_PATH: "group,realm_access.roles"

---
apiVersion: v1
kind: Secret
metadata:
  name: be-secrets
type: Opaque
stringData:
  QUARKUS_DATASOURCE_PASSWORD: "dbpass"
  QUARKUS_OIDC_CREDENTIALS_SECRET: "my-secret"

---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: be-deployment
spec:
  replicas: 2
  selector:
    matchLabels:
      app: be
  template:
    metadata:
      labels:
        app: be
    spec:
      containers:
      - name: be
        image: ghcr.io/cirpass-2/dpp-data-extractor-pgsql-oidc:latest
        ports:
        - containerPort: 8080
        envFrom:
        - configMapRef:
            name: be-config
        - secretRef:
            name: be-secrets
        volumeMounts:
        - name: be-config-volume
          mountPath: /etc/be
          readOnly: true
      volumes:
      - name: be-config-volume
        configMap:
          name: be-json-config
```

## REST API

The application exposes four API groups:

1**Comparison API**: extracts attributes from two or more DPPs using configurable property paths, returning one result object per DPP.
2**Search API**: queries model-level DPP data stored in the local database with filtering and pagination support.
3**Fetch API**: retrieves a DPP from the decentralized repository, returning it as plain JSON or as expanded JSON-LD (with on-the-fly conversion from RDF serializations).

To obtain the full OpenAPI document, start the application and issue a `GET` request to `/q/openapi`, using the `Accept` header to negotiate the format (`application/json` or `application/yaml`).

### Comparison Endpoints

#### POST /comparison/v1

Extracts and compares properties from two or more DPPs. The request body must provide the list of DPP URIs and the set of properties to extract. Each property is identified by a logical name and one or more property path definitions, each tied to a vocabulary namespace. The system tries each path definition in order and uses the first one that resolves a value.

**Property path syntax:** `segment[*@type=TypeFilter].property`

| Part                  | Description                                                             |
|-----------------------|-------------------------------------------------------------------------|
| `rootProperty`        | RDF property name relative to the root subject (e.g. `hasProperty`)     |
| `[*@type=TypeFilter]` | Optional filter — selects only nodes whose `@type` matches `TypeFilter` |
| `.property`           | Property to read from the matched node (e.g. `numericalValue`)          |

For example, `hasProperty[*@type=RecyclingRate].numericalValue` navigates to the object linked via `hasProperty`, keeps only nodes typed as `RecyclingRate`, and reads their `numericalValue`.

> **Note:** This endpoint currently supports only RDF-compatible DPPs. Comparison of plain JSON DPPs is not supported.

**Example request:**

```http
POST /comparison/v1
Content-Type: application/json
```

```json
{
  "dppUrls": [
    "http://smartphone/json-ld",
    "http://smartphone/json-mod-ld",
    "http://laptop/rdf-mod-xml",
    "http://battery/rdf-ttl",
    "http://shoes/rdf-nt",
    "http://fridge/rdf-n3"
  ],
  "propertyPaths": {
    "productName": [
      { "namespace": "http://dpp.taltech.ee/MODDPP#", "path": "itemName" },
      { "namespace": "http://dpp.taltech.ee/EUDPP#",  "path": "productName" }
    ],
    "recycling Rate": [
      { "namespace": "http://dpp.taltech.ee/MODDPP#", "path": "hasAttribute[*@type=ReuseRate].quantity" },
      { "namespace": "http://dpp.taltech.ee/EUDPP#",  "path": "hasProperty[*@type=RecyclingRate].numericalValue" }
    ],
    "carbon Footprint": [
      { "namespace": "http://dpp.taltech.ee/MODDPP#", "path": "hasAttribute[*@type=GHGFootprint].quantity" },
      { "namespace": "http://dpp.taltech.ee/EUDPP#",  "path": "hasProperty[*@type=CarbonFootprint].numericalValue" }
    ],
    "energy Consumption": [
      { "namespace": "http://dpp.taltech.ee/MODDPP#", "path": "hasAttribute[*@type=PowerDraw].quantity" },
      { "namespace": "http://dpp.taltech.ee/EUDPP#",  "path": "hasProperty[*@type=EnergyConsumption].numericalValue" }
    ]
  }
}
```

### Search Endpoints

#### POST /search/v1

Queries the local storage for model-level DPP data. Supports pagination via `offset` and `limit`, and filtering via a `filters` array. Multiple filters are combined with a logical AND.

Each filter requires:
- `property`: the name of the field to test (must match a `fieldName` from [`/capabilities/v1`](#get-capabilitiesv1)).
- `op`: the comparison operator (see table below).
- `literal`: the value to compare against. **String values must be wrapped in single quotes** (e.g. `'sometext'`); numeric and boolean values must not (e.g. `20`, `false`).

**Supported filter operators:**

| Operator | SQL equivalent    |
|----------|-------------------|
| `EQ`     | `=`               |
| `LT`     | `<`               |
| `LTE`    | `<=`              |
| `GT`     | `>`               |
| `GTE`    | `>=`              |
| `LIKE`   | `LIKE` / contains |

**Example request:**

```http
POST /search/v1
Content-Type: application/json
```

```json
{
  "filters": [
    { "property": "textProperty",    "op": "LIKE", "literal": "'sometxt'" },
    { "property": "boolProperty",    "op": "EQ",   "literal": "false"     },
    { "property": "intProperty",     "op": "GT",   "literal": "20"        },
    { "property": "decimalProperty", "op": "LTE",  "literal": "11"        }
  ],
  "offset": 20,
  "limit": 10
}
```

### Fetch Endpoints

#### GET /fetch/v1

Retrieves a DPP from the decentralized repository by URL. The response format depends on what the upstream repository returns:

- **Plain JSON** — returned as-is.
- **Any RDF serialization** (JSON-LD, RDF-XML, Turtle, N3, N-Quads, N-Triples) — converted on the fly and returned as [expanded JSON-LD](https://www.w3.org/TR/json-ld11/#expanded-document-form), a flat array of nodes where all IRIs are fully qualified and no `@context` is required.

The `url` query parameter must be the full URL of the DPP in the decentralized repository.

**Example request:**

```http
GET /fetch/v1?url=http://dpp/jsonld
```

**Example response (abbreviated):**

```json
[
  {
    "@id": "http://example.com/property/carbon-footprint-001",
    "http://dpp.taltech.ee/EUDPP#numericalValue": [{ "@value": "45.8", "@type": "http://www.w3.org/2001/XMLSchema#decimal" }],
    "http://dpp.taltech.ee/EUDPP#hasMeasurementUnit": [{ "@id": "https://si-digital-framework.org/SI#kilogram-co2-equivalent" }],
    "http://dpp.taltech.ee/EUDPP#dictionaryReference": [{ "@value": "https://example.com/dict/carbon-footprint" }],
    "@type": ["http://dpp.taltech.ee/EUDPP#CarbonFootprint"]
  },
  {
    "@id": "http://example.com/product/smartphone-001",
    "http://dpp.taltech.ee/EUDPP#productName": [{ "@value": "EcoPhone X Pro" }],
    "http://dpp.taltech.ee/EUDPP#GTIN": [{ "@value": "8712345678901" }],
    "http://dpp.taltech.ee/EUDPP#uniqueProductID": [{ "@value": "urn:epc:id:sgtin:0614141.112345.001" }],
    "@type": ["http://dpp.taltech.ee/EUDPP#Product"]
  }
]
```

> The full response includes all linked nodes (packaging, properties, classification, DPP metadata, etc.). The example above is abbreviated for readability.

## Authentication & Authorization

The application uses OpenID Connect (OIDC) for authentication. All API endpoints require a valid Bearer token issued by the configured OIDC provider. Requests without a valid token will receive a `401 Unauthorized` response.

Include the token in the `Authorization` header of every request:

```http
Authorization: Bearer <access_token>
```

## License

This project is licensed under the Apache License 2.0.

```
Copyright 2024-2027 CIRPASS-2

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

## Contributing

Contributions are welcome. To contribute:

1. Open a Pull Request on GitHub with your changes.
2. Include tests for all modifications:
    - Bug fixes must include tests that verify the fix.
    - New features must include comprehensive test coverage.
    - Improvements should include tests where applicable.
3. Request a review from the maintainers.
4. Ensure all existing tests pass and that the code follows the project's coding standards.

All contributions will be reviewed before being merged.

## Support

For questions, issues, or support requests, please contact: **marco.volpini@extrared.it**