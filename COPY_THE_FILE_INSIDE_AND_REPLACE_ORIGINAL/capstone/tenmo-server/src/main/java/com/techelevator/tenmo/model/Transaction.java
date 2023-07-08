package com.techelevator.tenmo.model;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;


public class Transaction {

    public Transaction() {

    }

    @NotNull
    private int transactionId;
    @NotNull
    private int userTo;
    @NotNull
    private int userFrom;
    @Min(1)
    private BigDecimal amount;

    private LocalDate transferDate;
    private String status;

    public Transaction(int transactionId, int userTo, int userFrom, BigDecimal amount, LocalDate transferDate, String status) {
        this.transactionId = transactionId;
        this.userTo = userTo;
        this.userFrom= userFrom;
        this.amount = amount;
        this.transferDate = transferDate;
        this.status = status;
    }

    public int getTransactionId(){
        return transactionId;
    }

    public void setTransactionId(int transferId){
        this.transactionId = transferId;
    }

    public int getUserTo(){
        return userTo;
    }

    public void setUserTo(int accountTo){
        this.userTo = accountTo;
    }

    public int getUserFrom(){
        return userFrom;
    }

    public void setUserFrom(int accountFrom){
        this.userFrom = accountFrom;
    }

    public BigDecimal getAmount(){
        return amount;
    }

    public void setAmount(BigDecimal amount){
        this.amount = amount;
    }

    public void setStatus(String msg) {
        this.status = msg;
    }
    public void setTransferDate(LocalDate date) {
        this.transferDate = transferDate;
    }
    public LocalDate getTransferDate() {
        return this.transferDate;
    }
    public String getStatus() {
        return this.status;
    }
}
