package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transaction;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

public interface TransactionDao {

    void transferMoney(int sendFrom, int sendTo, BigDecimal amount);

    List<Transaction> listTransactionsByUserId(int id);

    boolean createAndTransfer(Transaction transaction, Principal principal);

}
