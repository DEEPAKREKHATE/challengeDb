package com.dws.challenge.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class Account {

  @NonNull
  @NotEmpty
  private final String accountId;

  @NotNull
  @Min(value = 0, message = "Initial balance must be positive.")
  private double balance;

  private final Lock lock = new ReentrantLock(); // Lock for thread safety 
  
  public Account(String accountId) {
    this.accountId = accountId;
    this.balance = 0.0;
  }

  @JsonCreator
  public Account(@JsonProperty("accountId") String accountId,
    @JsonProperty("balance") double balance) {
    this.accountId = accountId;
    this.balance = balance;
  }
  
  public String getAccountId() {
      return accountId;
  }

  public double getBalance() {
      return balance;
  }

  // Withdraw money from account
  public void withdraw(double amount) {
      if (amount > balance) {
          throw new IllegalArgumentException("Insufficient balance");
      }
      balance -= amount;
  }

  // Deposit money to account
  public void deposit(double amount) {
      balance += amount;
  }

  // Get the lock for thread-safety
  public Lock getLock() {
      return lock;
  }
  
}
