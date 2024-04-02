CREATE TABLE portfolio_instruments (
    id bigserial PRIMARY KEY,
    instrument_token BIGINT,
    exchange_token BIGINT,
    tradingsymbol VARCHAR(100),
    name VARCHAR(255),
    last_price NUMERIC(19, 2),
    expiry DATE,
    strike NUMERIC(19, 2),
    tick_size NUMERIC(19, 2),
    lot_size INTEGER,
    instrument_type VARCHAR(50),
    segment VARCHAR(50),
    exchange VARCHAR(50),
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE stocks_historical_data (
    id BIGSERIAL PRIMARY KEY,
    instrument_token BIGINT,
    timestamps TIMESTAMP,
    open_price NUMERIC(19, 2),
    high_price NUMERIC(19, 2),
    low_price NUMERIC(19, 2),
    close_price NUMERIC(19, 2),
    volume DOUBLE PRECISION,
    oi BIGINT,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE configuration (
    key VARCHAR(255) PRIMARY KEY,
    value VARCHAR(255)
);

INSERT INTO configuration (key, value) VALUES ('request_token', '5YuQo4XKu1AcsFJD94SXnWwlIFXSpjzU');
INSERT INTO configuration (key, value) VALUES ('apiKey', 'nhdemg5k6a43jioy');
INSERT INTO configuration (key, value) VALUES ('apiSecret', 'bb7j0vtjysu47mfd3ntbcc022wq3or6s');
INSERT INTO configuration (key, value) VALUES ('clientId', 'YP8452');
INSERT INTO configuration (key, value) VALUES ('appName', 'renil');
INSERT INTO configuration (key, value) VALUES ('access_token', 'dummy');
INSERT INTO configuration (key, value) VALUES ('public_token', 'dummy');
