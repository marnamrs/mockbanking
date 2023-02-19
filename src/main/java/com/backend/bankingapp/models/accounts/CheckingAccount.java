package com.backend.bankingapp.models.accounts;

import com.backend.bankingapp.models.users.AccountHolder;
import com.backend.bankingapp.models.utils.HashCreator;
import com.backend.bankingapp.models.utils.Money;
import com.backend.bankingapp.models.utils.Status;
import com.backend.bankingapp.models.utils.Type;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Getter
@Setter
@ToString
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


    public CheckingAccount(Money balance, AccountHolder primaryOwner, Type type) {
        super(balance, primaryOwner, type);
    }
    public CheckingAccount(Money balance, AccountHolder primaryOwner, AccountHolder secondaryOwner, Type type) {
        super(balance, primaryOwner, secondaryOwner, type);
    }


    //update should be called before each operation or balance check
    @Override
    public void update() {
        Money currentBalance = getBalance();

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

        //penaltyFee: will apply if monthlyFee application caused balance < minBalance
        verifyPenaltyFee(currentBalance.getAmount(), newBalance.getAmount());

        //update lastUpdated
        setLastUpdated();
    }

    //verifyPenaltyFee should be called after balance change
    public void verifyPenaltyFee(BigDecimal preBalance, BigDecimal postBalance){
        //check if balance before and after operation < minimumBalance
        boolean preChange = preBalance.compareTo(minimumBalance.getAmount())<0;
        boolean postChange = postBalance.compareTo(minimumBalance.getAmount())<0;
        //apply penalty if minBalance infringement was caused by operation
        if(!preChange && postChange){
            Money currentBalance = getBalance();
            BigDecimal balanceFee = getPenaltyFee().getAmount();
            BigDecimal newAmount = currentBalance.getAmount().subtract(balanceFee);
            Money lastBalance = new Money(newAmount, currentBalance.getCurrency());
            setBalance(lastBalance);
        }
    }

    private String setAccountKey(){
        return HashCreator.createAccountKey();
    }
}
