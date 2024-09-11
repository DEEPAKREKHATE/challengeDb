package com.dws.challenge.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Test
    void testTransferSuccess() {
        NotificationService notificationService = Mockito.mock(NotificationService.class);
        AccountService accountService = new AccountService(notificationService);

        Account accountFrom = new Account("1", 500);
        Account accountTo = new Account("2", 300);

        accountService.transferMoney(accountFrom, accountTo, 200);

        // Verify that the balances are updated
        assert accountFrom.getBalance() == 300;
        assert accountTo.getBalance() == 500;

        // Verify notifications are sent
        Mockito.verify(notificationService).notify("1", "Transferred 200.0 to account 2");
        Mockito.verify(notificationService).notify("2", "Received 200.0 from account 1");
    }

    @Test
    void testInsufficientBalance() {
        NotificationService notificationService = Mockito.mock(NotificationService.class);
        AccountService accountService = new AccountService(notificationService);

        Account accountFrom = new Account("1", 100);
        Account accountTo = new Account("2", 300);

        // Should throw an exception due to insufficient balance
        assertThrows(IllegalArgumentException.class, () -> accountService.transferMoney(accountFrom, accountTo, 200));
    }

    @Test
    void testNegativeAmountTransfer() {
        NotificationService notificationService = Mockito.mock(NotificationService.class);
        AccountService accountService = new AccountService(notificationService);

        Account accountFrom = new Account("1", 100);
        Account accountTo = new Account("2", 300);

        // Should throw an exception due to negative transfer amount
        assertThrows(IllegalArgumentException.class, () -> accountService.transferMoney(accountFrom, accountTo, -50));
    }
}