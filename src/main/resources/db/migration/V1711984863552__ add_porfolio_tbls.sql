CREATE TABLE option_portfolio_instruments (
    id bigserial PRIMARY KEY,
    instrument_token VARCHAR(100),
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

CREATE TABLE option_all_stocks_historical_data (
    id BIGSERIAL PRIMARY KEY,
    tradingsymbol VARCHAR(100),
    instrument_token VARCHAR(100),
    timestamps TIMESTAMP,
    open_price NUMERIC(19, 2),
    high_price NUMERIC(19, 2),
    low_price NUMERIC(19, 2),
    close_price NUMERIC(19, 2),
    volume DOUBLE PRECISION,
    oi BIGINT,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE option_stocks_historical_data (
    id BIGSERIAL PRIMARY KEY,
    instrument_token VARCHAR(100),
    timestamps TIMESTAMP,
    open_price NUMERIC(19, 2),
    high_price NUMERIC(19, 2),
    low_price NUMERIC(19, 2),
    close_price NUMERIC(19, 2),
    volume DOUBLE PRECISION,
    oi BIGINT,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE option_instrument_process_log (
    id SERIAL PRIMARY KEY,
    instrument_token VARCHAR(100),
    is_successfully_processed BOOLEAN,
    processed_count INTEGER,
    error_message TEXT
);
