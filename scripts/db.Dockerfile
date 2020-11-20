FROM postgres

ENV POSTGRES_PASSWORD postgres

COPY initdb.sql /docker-entrypoint-initdb.d/