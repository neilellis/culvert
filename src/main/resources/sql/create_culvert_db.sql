CREATE DATABASE Culvert;
USE Culvert;
SET storage_engine=InnoDB;
CREATE TABLE core (uri_code SERIAL PRIMARY KEY, uri_root VARCHAR(4000), uri_detail VARCHAR(4000));
CREATE INDEX lookup ON core ( uri_root(100), uri_detail(100));