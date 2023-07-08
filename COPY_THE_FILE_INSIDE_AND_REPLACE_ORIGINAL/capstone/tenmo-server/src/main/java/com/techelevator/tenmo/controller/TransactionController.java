package com.techelevator.tenmo.controller;
import com.techelevator.tenmo.dao.JdbcTransactionDao;
import com.techelevator.tenmo.model.Transaction;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.security.Principal;
import java.util.*;

@RestController
public class TransactionController {

    JdbcTransactionDao transactionDao;
    public TransactionController(JdbcTransactionDao transactionDao) {
        this.transactionDao = transactionDao;
    }
    @GetMapping(path = "/transactions")
    public Transaction getTransactionById(int id) {

        return null;

    }

    @GetMapping(path = "/transactions/all")
    public List<Transaction> getTransactionById(Principal principal) {
        int id = transactionDao.getIdFromUsername(principal.getName());
        return transactionDao.listTransactionsByUserId(id);
    }


    @PostMapping(path = "transactions/send") // CHANGE THE NAME
    public void createTransaction(@Valid @RequestBody Transaction transaction, Principal principal) {

        transactionDao.createAndTransfer(transaction, principal);

    }

}
