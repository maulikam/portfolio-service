-- Table: public.all_stocks_historical_data

-- DROP TABLE IF EXISTS public.all_stocks_historical_data;

CREATE TABLE IF NOT EXISTS public.all_stocks_historical_data
(
    id bigint NOT NULL DEFAULT nextval('all_stocks_historical_data_id_seq'::regclass),
    instrument_token bigint,
    timestamps timestamp without time zone,
    open_price numeric(19,2),
    high_price numeric(19,2),
    low_price numeric(19,2),
    close_price numeric(19,2),
    volume double precision,
    oi bigint,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    india_vix numeric(19,2),
    nifty_100 numeric(19,2),
    nifty_200 numeric(19,2),
    nifty_50 numeric(19,2),
    nifty_500 numeric(19,2),
    nifty_alpha_50 numeric(19,2),
    nifty_alphalowvol numeric(19,2),
    nifty_auto numeric(19,2),
    nifty_bank numeric(19,2),
    nifty_commodities numeric(19,2),
    nifty_consr_durbl numeric(19,2),
    nifty_consumption numeric(19,2),
    nifty_cpse numeric(19,2),
    nifty_div_opps_50 numeric(19,2),
    nifty_energy numeric(19,2),
    nifty_fin_service numeric(19,2),
    nifty_finsrv25_50 numeric(19,2),
    nifty_fmcg numeric(19,2),
    nifty_growsect_15 numeric(19,2),
    nifty_healthcare numeric(19,2),
    nifty_ind_digital numeric(19,2),
    nifty_india_mfg numeric(19,2),
    nifty_infra numeric(19,2),
    nifty_it numeric(19,2),
    nifty_largemid250 numeric(19,2),
    nifty_m150_qlty50 numeric(19,2),
    nifty_media numeric(19,2),
    nifty_metal numeric(19,2),
    nifty_microcap250 numeric(19,2),
    nifty_mid_select numeric(19,2),
    nifty_midcap_100 numeric(19,2),
    nifty_midcap_150 numeric(19,2),
    nifty_midcap_50 numeric(19,2),
    nifty_midsml_400 numeric(19,2),
    nifty_mnc numeric(19,2),
    nifty_next_50 numeric(19,2),
    nifty_oil_and_gas numeric(19,2),
    nifty_pharma numeric(19,2),
    nifty_pse numeric(19,2),
    nifty_psu_bank numeric(19,2),
    nifty_pvt_bank numeric(19,2),
    nifty_realty numeric(19,2),
    nifty_serv_sector numeric(19,2),
    nifty_smlcap_100 numeric(19,2),
    nifty_smlcap_250 numeric(19,2),
    nifty_smlcap_50 numeric(19,2),
    nifty_total_mkt numeric(19,2),
    nifty100_eql_wgt numeric(19,2),
    nifty100_esg numeric(19,2),
    nifty100_liq_15 numeric(19,2),
    nifty100_lowvol30 numeric(19,2),
    nifty100_qualty30 numeric(19,2),
    nifty100esgsecldr numeric(19,2),
    nifty200_qualty30 numeric(19,2),
    nifty200momentm30 numeric(19,2),
    nifty50_div_point numeric(19,2),
    nifty50_eql_wgt numeric(19,2),
    nifty50_value_20 numeric(19,2),
    nifty500_multicap numeric(19,2),
    CONSTRAINT all_stocks_historical_data_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.all_stocks_historical_data
    OWNER to postgres;
-- Index: idx_instrument_token

-- DROP INDEX IF EXISTS public.idx_instrument_token;

CREATE INDEX IF NOT EXISTS idx_instrument_token
    ON public.all_stocks_historical_data USING btree
    (instrument_token ASC NULLS LAST)
    TABLESPACE pg_default;
-- Index: idx_timestamps

-- DROP INDEX IF EXISTS public.idx_timestamps;

CREATE INDEX IF NOT EXISTS idx_timestamps
    ON public.all_stocks_historical_data USING btree
    (timestamps ASC NULLS LAST)
    TABLESPACE pg_default;


-- create function:

-- FUNCTION: public.update_index_data()

-- DROP FUNCTION IF EXISTS public.update_index_data();

CREATE OR REPLACE FUNCTION public.update_index_data(
	)
    RETURNS void
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE PARALLEL UNSAFE
AS $BODY$
DECLARE
    r RECORD;
BEGIN
    FOR r IN SELECT * FROM (VALUES
    ('INDIA VIX', 'india_vix'),
    ('NIFTY 100', 'nifty_100'),
    ('NIFTY 200', 'nifty_200'),
    ('NIFTY 50', 'nifty_50'),
    ('NIFTY 500', 'nifty_500'),
    ('NIFTY ALPHA 50', 'nifty_alpha_50'),
    ('NIFTY ALPHALOWVOL', 'nifty_alphalowvol'),
    ('NIFTY AUTO', 'nifty_auto'),
    ('NIFTY BANK', 'nifty_bank'),
    ('NIFTY COMMODITIES', 'nifty_commodities'),
    ('NIFTY CONSR DURBL', 'nifty_consr_durbl'),
    ('NIFTY CONSUMPTION', 'nifty_consumption'),
    ('NIFTY CPSE', 'nifty_cpse'),
    ('NIFTY DIV OPPS 50', 'nifty_div_opps_50'),
    ('NIFTY ENERGY', 'nifty_energy'),
    ('NIFTY FIN SERVICE', 'nifty_fin_service'),
    ('NIFTY FINSRV25 50', 'nifty_finsrv25_50'),
    ('NIFTY FMCG', 'nifty_fmcg'),
    ('NIFTY GROWSECT 15', 'nifty_growsect_15'),
    ('NIFTY HEALTHCARE', 'nifty_healthcare'),
    ('NIFTY IND DIGITAL', 'nifty_ind_digital'),
    ('NIFTY INDIA MFG', 'nifty_india_mfg'),
    ('NIFTY INFRA', 'nifty_infra'),
    ('NIFTY IT', 'nifty_it'),
    ('NIFTY LARGEMID250', 'nifty_largemid250'),
    ('NIFTY M150 QLTY50', 'nifty_m150_qlty50'),
    ('NIFTY MEDIA', 'nifty_media'),
    ('NIFTY METAL', 'nifty_metal'),
    ('NIFTY MICROCAP250', 'nifty_microcap250'),
    ('NIFTY MID SELECT', 'nifty_mid_select'),
    ('NIFTY MIDCAP 100', 'nifty_midcap_100'),
    ('NIFTY MIDCAP 150', 'nifty_midcap_150'),
    ('NIFTY MIDCAP 50', 'nifty_midcap_50'),
    ('NIFTY MIDSML 400', 'nifty_midsml_400'),
    ('NIFTY MNC', 'nifty_mnc'),
    ('NIFTY NEXT 50', 'nifty_next_50'),
    ('NIFTY OIL AND GAS', 'nifty_oil_and_gas'),
    ('NIFTY PHARMA', 'nifty_pharma'),
    ('NIFTY PSE', 'nifty_pse'),
    ('NIFTY PSU BANK', 'nifty_psu_bank'),
    ('NIFTY PVT BANK', 'nifty_pvt_bank'),
    ('NIFTY REALTY', 'nifty_realty'),
    ('NIFTY SERV SECTOR', 'nifty_serv_sector'),
    ('NIFTY SMLCAP 100', 'nifty_smlcap_100'),
    ('NIFTY SMLCAP 250', 'nifty_smlcap_250'),
    ('NIFTY SMLCAP 50', 'nifty_smlcap_50'),
    ('NIFTY TOTAL MKT', 'nifty_total_mkt'),
    ('NIFTY100 EQL WGT', 'nifty100_eql_wgt'),
    ('NIFTY100 ESG', 'nifty100_esg'),
    ('NIFTY100 LIQ 15', 'nifty100_liq_15'),
    ('NIFTY100 LOWVOL30', 'nifty100_lowvol30'),
    ('NIFTY100 QUALTY30', 'nifty100_qualty30'),
    ('NIFTY100ESGSECLDR', 'nifty100esgsecldr'),
    ('NIFTY200 QUALTY30', 'nifty200_qualty30'),
    ('NIFTY200MOMENTM30', 'nifty200momentm30'),
    ('NIFTY50 DIV POINT', 'nifty50_div_point'),
    ('NIFTY50 EQL WGT', 'nifty50_eql_wgt'),
    ('NIFTY50 VALUE 20', 'nifty50_value_20'),
    ('NIFTY500 MULTICAP', 'nifty500_multicap')) AS t(tradingsymbol, column_name)
    LOOP
        EXECUTE format('
            UPDATE public.all_stocks_historical_data AS ash
            SET %I = subquery.close_price
            FROM (
                SELECT timestamps, close_price
                FROM public.all_stocks_historical_data
                WHERE instrument_token = (
                    SELECT instrument_token
                    FROM public.portfolio_instruments
                    WHERE tradingsymbol = $1
                )
            ) AS subquery
            WHERE ash.timestamps = subquery.timestamps
        ', r.column_name) USING r.tradingsymbol;
    END LOOP;
END;
$BODY$;

ALTER FUNCTION public.update_index_data()
    OWNER TO postgres;



CREATE OR REPLACE FUNCTION public.update_index_data(
	)
    RETURNS void
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE PARALLEL UNSAFE
AS $BODY$
BEGIN
    WITH index_data AS (
        SELECT
            pi.tradingsymbol,
            pi.instrument_token,
            ash.timestamps,
            ash.close_price
        FROM
            public.portfolio_instruments pi
            JOIN public.all_stocks_historical_data ash ON pi.instrument_token = ash.instrument_token
    )
    UPDATE
        public.all_stocks_historical_data ash
    SET
	    india_vix = CASE WHEN tradingsymbol = 'INDIA VIX' THEN index_data.close_price ELSE india_vix END,
	    nifty_100 = CASE WHEN tradingsymbol = 'NIFTY 100' THEN index_data.close_price ELSE nifty_100 END,
	    nifty_200 = CASE WHEN tradingsymbol = 'NIFTY 200' THEN index_data.close_price ELSE nifty_200 END,
	    nifty_50 = CASE WHEN tradingsymbol = 'NIFTY 50' THEN index_data.close_price ELSE nifty_50 END,
	    nifty_500 = CASE WHEN tradingsymbol = 'NIFTY 500' THEN index_data.close_price ELSE nifty_500 END,
	    nifty_alpha_50 = CASE WHEN tradingsymbol = 'NIFTY ALPHA 50' THEN index_data.close_price ELSE nifty_alpha_50 END,
	    nifty_alphalowvol = CASE WHEN tradingsymbol = 'NIFTY ALPHALOWVOL' THEN index_data.close_price ELSE nifty_alphalowvol END,
	    nifty_auto = CASE WHEN tradingsymbol = 'NIFTY AUTO' THEN index_data.close_price ELSE nifty_auto END,
	    nifty_bank = CASE WHEN tradingsymbol = 'NIFTY BANK' THEN index_data.close_price ELSE nifty_bank END,
	    nifty_commodities = CASE WHEN tradingsymbol = 'NIFTY COMMODITIES' THEN index_data.close_price ELSE nifty_commodities END,
	    nifty_consr_durbl = CASE WHEN tradingsymbol = 'NIFTY CONSR DURBL' THEN index_data.close_price ELSE nifty_consr_durbl END,
	    nifty_consumption = CASE WHEN tradingsymbol = 'NIFTY CONSUMPTION' THEN index_data.close_price ELSE nifty_consumption END,
	    nifty_cpse = CASE WHEN tradingsymbol = 'NIFTY CPSE' THEN index_data.close_price ELSE nifty_cpse END,
	    nifty_div_opps_50 = CASE WHEN tradingsymbol = 'NIFTY DIV OPPS 50' THEN index_data.close_price ELSE nifty_div_opps_50 END,
	    nifty_energy = CASE WHEN tradingsymbol = 'NIFTY ENERGY' THEN index_data.close_price ELSE nifty_energy END,
	    nifty_fin_service = CASE WHEN tradingsymbol = 'NIFTY FIN SERVICE' THEN index_data.close_price ELSE nifty_fin_service END,
	    nifty_finsrv25_50 = CASE WHEN tradingsymbol = 'NIFTY FINSRV25 50' THEN index_data.close_price ELSE nifty_finsrv25_50 END,
	    nifty_fmcg = CASE WHEN tradingsymbol = 'NIFTY FMCG' THEN index_data.close_price ELSE nifty_fmcg END,
	    nifty_growsect_15 = CASE WHEN tradingsymbol = 'NIFTY GROWSECT 15' THEN index_data.close_price ELSE nifty_growsect_15 END,
	    nifty_healthcare = CASE WHEN tradingsymbol = 'NIFTY HEALTHCARE' THEN index_data.close_price ELSE nifty_healthcare END,
	    nifty_ind_digital = CASE WHEN tradingsymbol = 'NIFTY IND DIGITAL' THEN index_data.close_price ELSE nifty_ind_digital END,
	    nifty_india_mfg = CASE WHEN tradingsymbol = 'NIFTY INDIA MFG' THEN index_data.close_price ELSE nifty_india_mfg END,
	    nifty_infra = CASE WHEN tradingsymbol = 'NIFTY INFRA' THEN index_data.close_price ELSE nifty_infra END,
	    nifty_it = CASE WHEN tradingsymbol = 'NIFTY IT' THEN index_data.close_price ELSE nifty_it END,
	    nifty_largemid250 = CASE WHEN tradingsymbol = 'NIFTY LARGEMID250' THEN index_data.close_price ELSE nifty_largemid250 END,
	    nifty_m150_qlty50 = CASE WHEN tradingsymbol = 'NIFTY M150 QLTY50' THEN index_data.close_price ELSE nifty_m150_qlty50 END,
	    nifty_media = CASE WHEN tradingsymbol = 'NIFTY MEDIA' THEN index_data.close_price ELSE nifty_media END,
	    nifty_metal = CASE WHEN tradingsymbol = 'NIFTY METAL' THEN index_data.close_price ELSE nifty_metal END,
	    nifty_microcap250 = CASE WHEN tradingsymbol = 'NIFTY MICROCAP250' THEN index_data.close_price ELSE nifty_microcap250 END,
	    nifty_mid_select = CASE WHEN tradingsymbol = 'NIFTY MID SELECT' THEN index_data.close_price ELSE nifty_mid_select END,
	    nifty_midcap_100 = CASE WHEN tradingsymbol = 'NIFTY MIDCAP 100' THEN index_data.close_price ELSE nifty_midcap_100 END,
	    nifty_midcap_150 = CASE WHEN tradingsymbol = 'NIFTY MIDCAP 150' THEN index_data.close_price ELSE nifty_midcap_150 END,
	    nifty_midcap_50 = CASE WHEN tradingsymbol = 'NIFTY MIDCAP 50' THEN index_data.close_price ELSE nifty_midcap_50 END,
	    nifty_midsml_400 = CASE WHEN tradingsymbol = 'NIFTY MIDSML 400' THEN index_data.close_price ELSE nifty_midsml_400 END,
	    nifty_mnc = CASE WHEN tradingsymbol = 'NIFTY MNC' THEN index_data.close_price ELSE nifty_mnc END,
	    nifty_next_50 = CASE WHEN tradingsymbol = 'NIFTY NEXT 50' THEN index_data.close_price ELSE nifty_next_50 END,
	    nifty_oil_and_gas = CASE WHEN tradingsymbol = 'NIFTY OIL AND GAS' THEN index_data.close_price ELSE nifty_oil_and_gas END,
	    nifty_pharma = CASE WHEN tradingsymbol = 'NIFTY PHARMA' THEN index_data.close_price ELSE nifty_pharma END,
	    nifty_pse = CASE WHEN tradingsymbol = 'NIFTY PSE' THEN index_data.close_price ELSE nifty_pse END,
	    nifty_psu_bank = CASE WHEN tradingsymbol = 'NIFTY PSU BANK' THEN index_data.close_price ELSE nifty_psu_bank END,
	    nifty_pvt_bank = CASE WHEN tradingsymbol = 'NIFTY PVT BANK' THEN index_data.close_price ELSE nifty_pvt_bank END,
	    nifty_realty = CASE WHEN tradingsymbol = 'NIFTY REALTY' THEN index_data.close_price ELSE nifty_realty END,
	    nifty_serv_sector = CASE WHEN tradingsymbol = 'NIFTY SERV SECTOR' THEN index_data.close_price ELSE nifty_serv_sector END,
	    nifty_smlcap_100 = CASE WHEN tradingsymbol = 'NIFTY SMLCAP 100' THEN index_data.close_price ELSE nifty_smlcap_100 END,
	    nifty_smlcap_250 = CASE WHEN tradingsymbol = 'NIFTY SMLCAP 250' THEN index_data.close_price ELSE nifty_smlcap_250 END,
	    nifty_smlcap_50 = CASE WHEN tradingsymbol = 'NIFTY SMLCAP 50' THEN index_data.close_price ELSE nifty_smlcap_50 END,
	    nifty_total_mkt = CASE WHEN tradingsymbol = 'NIFTY TOTAL MKT' THEN index_data.close_price ELSE nifty_total_mkt END,
	    nifty100_eql_wgt = CASE WHEN tradingsymbol = 'NIFTY100 EQL WGT' THEN index_data.close_price ELSE nifty100_eql_wgt END,
	    nifty100_esg = CASE WHEN tradingsymbol = 'NIFTY100 ESG' THEN index_data.close_price ELSE nifty100_esg END,
	    nifty100_liq_15 = CASE WHEN tradingsymbol = 'NIFTY100 LIQ 15' THEN index_data.close_price ELSE nifty100_liq_15 END,
	    nifty100_lowvol30 = CASE WHEN tradingsymbol = 'NIFTY100 LOWVOL30' THEN index_data.close_price ELSE nifty100_lowvol30 END,
	    nifty100_qualty30 = CASE WHEN tradingsymbol = 'NIFTY100 QUALTY30' THEN index_data.close_price ELSE nifty100_qualty30 END,
	    nifty100esgsecldr = CASE WHEN tradingsymbol = 'NIFTY100ESGSECLDR' THEN index_data.close_price ELSE nifty100esgsecldr END,
	    nifty200_qualty30 = CASE WHEN tradingsymbol = 'NIFTY200 QUALTY30' THEN index_data.close_price ELSE nifty200_qualty30 END,
	    nifty200momentm30 = CASE WHEN tradingsymbol = 'NIFTY200MOMENTM30' THEN index_data.close_price ELSE nifty200momentm30 END,
	    nifty50_div_point = CASE WHEN tradingsymbol = 'NIFTY50 DIV POINT' THEN index_data.close_price ELSE nifty50_div_point END,
	    nifty50_eql_wgt = CASE WHEN tradingsymbol = 'NIFTY50 EQL WGT' THEN index_data.close_price ELSE nifty50_eql_wgt END,
	    nifty50_value_20 = CASE WHEN tradingsymbol = 'NIFTY50 VALUE 20' THEN index_data.close_price ELSE nifty50_value_20 END,
	    nifty500_multicap = CASE WHEN tradingsymbol = 'NIFTY500 MULTICAP' THEN index_data.close_price ELSE nifty500_multicap END
	FROM

        index_data
    WHERE
        ash.timestamps = index_data.timestamps;
END;
$BODY$;

ALTER FUNCTION public.update_index_data()
    OWNER TO postgres;
