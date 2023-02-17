package com.backend.bankingapp.models.accounts;

import com.backend.bankingapp.models.utils.HashCreator;
import com.backend.bankingapp.models.utils.Money;
import com.backend.bankingapp.models.utils.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CheckingAccount extends Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String accountKey = setAccountKey();
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="currency", column = @Column(name = "min_balance_currency")),
            @AttributeOverride(name="amount", column = @Column(name = "min_balance_amount")),
    })
    private Money minimumBalance = new Money(new BigDecimal("250"));
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="currency", column = @Column(name = "monthly_fee_currency")),
            @AttributeOverride(name="amount", column = @Column(name = "monthly_fee_amount")),
    })
    private Money monthlyFee = new Money(new BigDecimal("12"));
    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    @Override
    public void update() {
        Money currentBalance = getBalance();
        //verify if balance > minBalance before applying maintenance fees
        boolean beforeMonthlyUpdate = currentBalance.getAmount().compareTo(minimumBalance.getAmount())<0;

        //monthlyFee: apply every 30 days
        LocalDateTime monthAgo = LocalDateTime.now().minus(30, ChronoUnit.DAYS);
        if(getLastUpdated().isBefore(monthAgo)){
            //time elapsed without fee applied
            long daysElapsed = Duration.between(LocalDateTime.now(), getLastUpdated()).toDays();
            long monthsElapsed = Long.divideUnsigned(daysElapsed, 30L);
            //get fee for time elapsed (fee*months)
            BigDecimal monthFee = monthlyFee.getAmount().multiply(BigDecimal.valueOf(monthsElapsed));
            //subtract fee from balance
            BigDecimal updatedAmount = currentBalance.getAmount().subtract(monthFee);
            Money updatedBalance = new Money(updatedAmount, currentBalance.getCurrency());
            setBalance(updatedBalance);
        }

        Money newBalance = getBalance();
        //verify if balance > minBalance after applying maintenance fees
        boolean afterMonthlyUpdate = newBalance.getAmount().compareTo(minimumBalance.getAmount())<0;

        //penaltyFee: apply if monthlyFee application caused balance < minBalance
        if(!beforeMonthlyUpdate && afterMonthlyUpdate){
            applyPenaltyFee();
        }
        //update lastUpdated
        setLastUpdated();
    }

    public void applyPenaltyFee(){
        Money currentBalance = getBalance();
        BigDecimal balanceFee = getPenaltyFee().getAmount();
        BigDecimal newAmount = currentBalance.getAmount().subtract(balanceFee);
        Money lastBalance = new Money(newAmount, currentBalance.getCurrency());
        setBalance(lastBalance);
    }

    private String setAccountKey(){
        return HashCreator.createAccountKey();
    }
}
