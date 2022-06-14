package net.flawe.practical.accounting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class AccountingApplication {

    public static void main(String[] args) {
        var context = SpringApplication.run(AccountingApplication.class, args);
    }

}
