package com.codingreflex.renilalgo.fno.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "option_merged_df")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OptionMergedDf {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "index")
    private Long index;

    @Column(name = "instrument_token")
    private Long instrumentToken;


    @Column(name = "tradingsymbol")
    private String tradingSymbol;

    @Column(name = "timestamps")
    private String timestamps;

    @Column(name = "open_price")
    private Double openPrice;

    @Column(name = "high_price")
    private Double highPrice;

    @Column(name = "low_price")
    private Double lowPrice;

    @Column(name = "close_price")
    private Double closePrice;

    @Column(name = "volume")
    private Long volume;

    @Column(name = "INDIA VIX")
    private Double indiaVix;

    @Column(name = "INDIA VIX close")
    private Double indiaVixClose;

    @Column(name = "NIFTY 50")
    private Double nifty50;

    @Column(name = "NIFTY 50 close")
    private Double nifty50Close;

    @Column(name = "NIFTY 500")
    private Double nifty500;

    @Column(name = "NIFTY 500 close")
    private Double nifty500Close;

    @Column(name = "NIFTY AUTO")
    private Double niftyAuto;

    @Column(name = "NIFTY AUTO close")
    private Double niftyAutoClose;

    @Column(name = "NIFTY BANK")
    private Double niftyBank;

    @Column(name = "NIFTY BANK close")
    private Double niftyBankClose;

    @Column(name = "NIFTY COMMODITIES")
    private Double niftyCommodities;

    @Column(name = "NIFTY COMMODITIES close")
    private Double niftyCommoditiesClose;

    @Column(name = "NIFTY CONSR DURBL")
    private Double niftyConsuDurbl;

    @Column(name = "NIFTY CONSR DURBL close")
    private Double niftyConsuDurblClose;

    @Column(name = "NIFTY CONSUMPTION")
    private Double niftyConsumption;

    @Column(name = "NIFTY CONSUMPTION close")
    private Double niftyConsumptionClose;

    @Column(name = "NIFTY CPSE")
    private Double niftyCpse;

    @Column(name = "NIFTY CPSE close")
    private Double niftyCpseClose;

    @Column(name = "NIFTY ENERGY")
    private Double niftyEnergy;

    @Column(name = "NIFTY ENERGY close")
    private Double niftyEnergyClose;

    @Column(name = "NIFTY FMCG")
    private Double niftyFmcg;

    @Column(name = "NIFTY FMCG close")
    private Double niftyFmcgClose;

    @Column(name = "NIFTY HEALTHCARE")
    private Double niftyHealthcare;

    @Column(name = "NIFTY HEALTHCARE close")
    private Double niftyHealthcareClose;

    @Column(name = "NIFTY INDIA MFG")
    private Double niftyIndiaMfg;

    @Column(name = "NIFTY INDIA MFG close")
    private Double niftyIndiaMfgClose;

    @Column(name = "NIFTY IT")
    private Double niftyIt;

    @Column(name = "NIFTY IT close")
    private Double niftyItClose;

    @Column(name = "NIFTY MEDIA")
    private Double niftyMedia;

    @Column(name = "NIFTY MEDIA close")
    private Double niftyMediaClose;

    @Column(name = "NIFTY METAL")
    private Double niftyMetal;

    @Column(name = "NIFTY METAL close")
    private Double niftyMetalClose;

    @Column(name = "NIFTY MICROCAP250")
    private Double niftyMicrocap250;

    @Column(name = "NIFTY MICROCAP250 close")
    private Double niftyMicrocap250Close;

    @Column(name = "NIFTY PHARMA")
    private Double niftyPharma;

    @Column(name = "NIFTY PHARMA close")
    private Double niftyPharmaClose;

    @Column(name = "NIFTY PSE")
    private Double niftyPse;

    @Column(name = "NIFTY PSE close")
    private Double niftyPseClose;

    @Column(name = "NIFTY PSU BANK")
    private Double niftyPsuBank;

    @Column(name = "NIFTY PSU BANK close")
    private Double niftyPsuBankClose;

    @Column(name = "NIFTY PVT BANK")
    private Double niftyPvtBank;

    @Column(name = "NIFTY PVT BANK close")
    private Double niftyPvtBankClose;

    @Column(name = "NIFTY REALTY")
    private Double niftyRealty;

    @Column(name = "NIFTY REALTY close")
    private Double niftyRealtyClose;

    @Column(name = "NIFTY SMLCAP 100")
    private Double niftySmlcap100;

    @Column(name = "NIFTY SMLCAP 100 close")
    private Double niftySmlcap100Close;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // Getters and Setters
}

