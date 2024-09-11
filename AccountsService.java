package com.dws.challenge.service;

import com.dws.challenge.domain.Account;
import com.dws.challenge.repository.AccountsRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountsService {

  @Getter
  private final AccountsRepository accountsRepository;
  
  private final NotificationService notificationService;

  @Autowired
  public AccountsService(AccountsRepository accountsRepository,NotificationService notificationService) {
	  this.notificationService = notificationService;
    this.accountsRepository = accountsRepository;
  }

  public void createAccount(Account account) {
    this.accountsRepository.createAccount(account);
  }

  public Account getAccount(String accountId) {
    return this.accountsRepository.getAccount(accountId);
  }
  
  public void transferMoney(Account accountFrom, Account accountTo, double amount) {
      if (amount <= 0) {
          throw new IllegalArgumentException("Transfer amount must be positive");
      }

      // Locks to avoid deadlock - always lock accounts in order based on their hashcode
      Account firstLock = accountFrom.hashCode() < accountTo.hashCode() ? accountFrom : accountTo;
      Account secondLock = accountFrom.hashCode() < accountTo.hashCode() ? accountTo : accountFrom;

      firstLock.getLock().lock();
      try {
          secondLock.getLock().lock();
          try {
              // Ensure there is enough balance in accountFrom
              if (accountFrom.getBalance() < amount) {
                  throw new IllegalArgumentException("Insufficient balance for transfer");
              }

              // Perform the transfer
              accountFrom.withdraw(amount);
              accountTo.deposit(amount);

              // Send notifications
              notificationService.notify(accountFrom.getAccountId(), 
                  "Transferred " + amount + " to account " + accountTo.getAccountId());
              notificationService.notify(accountTo.getAccountId(), 
                  "Received " + amount + " from account " + accountFrom.getAccountId());
          } finally {
              secondLock.getLock().unlock();
          }
      } finally {
          firstLock.getLock().unlock();
      }
  }
}
