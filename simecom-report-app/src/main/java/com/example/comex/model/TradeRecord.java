package com.example.comex.model;

import java.math.BigDecimal;

public class TradeRecord {
    private int coAno;
    private int coMes;
    private String sh4;
    private String coPais;
    private String sgUfMun;
    private String coMun;
    private BigDecimal kgLiquido;
    private BigDecimal vlFob;
    private TradeType tradeType;

    public int getCoAno() { return coAno; }
    public void setCoAno(int coAno) { this.coAno = coAno; }
    public int getCoMes() { return coMes; }
    public void setCoMes(int coMes) { this.coMes = coMes; }
    public String getSh4() { return sh4; }
    public void setSh4(String sh4) { this.sh4 = sh4; }
    public String getCoPais() { return coPais; }
    public void setCoPais(String coPais) { this.coPais = coPais; }
    public String getSgUfMun() { return sgUfMun; }
    public void setSgUfMun(String sgUfMun) { this.sgUfMun = sgUfMun; }
    public String getCoMun() { return coMun; }
    public void setCoMun(String coMun) { this.coMun = coMun; }
    public BigDecimal getKgLiquido() { return kgLiquido; }
    public void setKgLiquido(BigDecimal kgLiquido) { this.kgLiquido = kgLiquido; }
    public BigDecimal getVlFob() { return vlFob; }
    public void setVlFob(BigDecimal vlFob) { this.vlFob = vlFob; }
    public TradeType getTradeType() { return tradeType; }
    public void setTradeType(TradeType tradeType) { this.tradeType = tradeType; }
}
