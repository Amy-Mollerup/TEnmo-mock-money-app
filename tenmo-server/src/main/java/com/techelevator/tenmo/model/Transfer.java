package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class Transfer {

    private Long id;
    private int typeId;
    private int transferStatusId;
    private Long fromAccountId;
    private Long toAccountId;
    private BigDecimal amount;

    public Transfer() {
    }

    public Transfer(Long id, int typeId, int transferStatusId, Long fromAccountId, Long toAccountId, BigDecimal amount) {
        this.id = id;
        this.typeId = typeId;
        this.transferStatusId = transferStatusId;
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public int getTransferStatusId() {
        return transferStatusId;
    }

    public void setTransferStatusId(int transferStatusId) {
        this.transferStatusId = transferStatusId;
    }

    public Long getFromAccountId() {
        return fromAccountId;
    }

    public void setFromAccountId(Long fromAccountId) {
        this.fromAccountId = fromAccountId;
    }

    public Long getToAccountId() {
        return toAccountId;
    }

    public void setToAccountId(Long toAccountId) {
        this.toAccountId = toAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
