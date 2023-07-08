package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transaction;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransactionDao implements TransactionDao {
    private final JdbcTemplate jdbcTemplate;

    public JdbcTransactionDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    @Override
    public void transferMoney(int sendFrom, int sendTo, BigDecimal amount) {

        String userSQL = "SELECT balance FROM tenmo_user WHERE user_id = ?";


        String updateSQL = "UPDATE tenmo_user SET balance = ? WHERE user_id = ?";

        BigDecimal balanceUserFrom = new BigDecimal(jdbcTemplate.queryForObject(userSQL,Integer.class,sendFrom));
        BigDecimal balanceUserTo = new BigDecimal(jdbcTemplate.queryForObject(userSQL,Integer.class,sendTo));

        BigDecimal newUserFromBalance = balanceUserFrom.subtract(amount);
        BigDecimal newUserToBalance = balanceUserTo.add(amount);

        jdbcTemplate.update(updateSQL,newUserFromBalance,sendFrom);
        jdbcTemplate.update(updateSQL,newUserToBalance,sendTo);

    }
    public List<Transaction> listTransactionsByUserId(int id) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT transaction_id, user_from, user_to, amount, transfer_date,status " +
                "FROM user_transaction " +
                "WHERE user_to = ? OR user_from = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id,id);
        while (results.next()) {
            transactions.add(mapRowToTransaction(results));
        }
        return transactions;
    }

    public boolean enoughMoneyToCompleteTransaction(Transaction transaction) {
        String sql = "SELECT balance FROM tenmo_user WHERE user_id = ?";
        BigDecimal amount = new BigDecimal(jdbcTemplate.queryForObject(sql,Integer.class,transaction.getUserFrom()));
        if (amount.compareTo(transaction.getAmount()) >= 0) {
            return true;
        } else
            return false;
    }

    @ResponseStatus(HttpStatus.CREATED)
    public boolean createAndTransfer(Transaction transaction, Principal principal) {
        Integer newTransactionId = null;
        if (enoughMoneyToCompleteTransaction(transaction) && transaction.getUserFrom() != transaction.getUserTo()) {
            try {

                String username = getUsernameFromUserId(transaction.getUserFrom());

                if (principal.getName().equals(username)) {

                    transaction.setStatus("approved");

                    transferMoney(transaction.getUserFrom(), transaction.getUserTo(), transaction.getAmount());

                    String sql = "INSERT INTO user_transaction (user_from, user_to,amount,transfer_date,status) " +
                            " VALUES (?, ?, ?, ?, ?) RETURNING transaction_id";
                    newTransactionId = jdbcTemplate.queryForObject(sql, Integer.class, transaction.getUserFrom(), transaction.getUserTo(), transaction.getAmount(),
                            LocalDate.now(), transaction.getStatus());
                } else
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
            } catch (Exception e) {
                throw e;
            }
        } else throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        return (newTransactionId != null);
    }

    public String getUsernameFromUserId(int id) {

        String findUserSQL = "SELECT username FROM tenmo_user WHERE user_id=?";

        String username = jdbcTemplate.queryForObject(findUserSQL,String.class,id);

        return username;

    }

    public int getIdFromUsername(String username) {

        String findUserSQL = "SELECT user_id FROM tenmo_user WHERE username=?";

        int id = jdbcTemplate.queryForObject(findUserSQL,Integer.class,username);

        return id;

    }

    private Transaction mapRowToTransaction(SqlRowSet rowSet) {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(rowSet.getInt("transaction_id"));
        transaction.setUserTo(rowSet.getInt("user_to"));
        transaction.setUserFrom(rowSet.getInt("user_from"));
        transaction.setAmount(new BigDecimal(rowSet.getInt("amount")));
        transaction.setTransferDate(rowSet.getDate("transfer_date").toLocalDate());
        transaction.setStatus(rowSet.getString("status"));
        return transaction;
    }
}
