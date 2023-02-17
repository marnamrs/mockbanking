package com.backend.bankingapp.models.accounts;

import com.backend.bankingapp.models.users.AccountHolder;
import com.backend.bankingapp.models.utils.HashCreator;
import com.backend.bankingapp.models.utils.Money;
import com.backend.bankingapp.models.utils.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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
public class SavingsAccount extends Account{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String accountKey = setAccountKey();
    @DecimalMin(value="100")
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="currency", column = @Column(name = "min_balance_currency")),
            @AttributeOverride(name="amount", column = @Column(name = "min_balance_amount")),
    })
    private Money minimumBalance = new Money(new BigDecimal("1000"));
    @DecimalMax(value="0.5")
    @PositiveOrZero
    private BigDecimal interestRate = new BigDecimal("0.0025");
    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    public SavingsAccount(Money balance, AccountHolder primaryOwner) {
        super(balance, primaryOwner);
    }
    public SavingsAccount(Money balance, AccountHolder primaryOwner, AccountHolder secondaryOwner) {
        super(balance, primaryOwner, secondaryOwner);
    }
    public SavingsAccount(Money balance, AccountHolder primaryOwner, Money minimumBalance) {
        super(balance, primaryOwner);
        setBalance(minimumBalance);
    }
    public SavingsAccount(Money balance, AccountHolder primaryOwner, AccountHolder secondaryOwner, Money minimumBalance) {
        super(balance, primaryOwner, secondaryOwner);
        setBalance(minimumBalance);
    }
    public SavingsAccount(Money balance, AccountHolder primaryOwner, BigDecimal interestRate) {
        super(balance, primaryOwner);
        setInterestRate(interestRate);
    }
    public SavingsAccount(Money balance, AccountHolder primaryOwner, AccountHolder secondaryOwner, BigDecimal interestRate) {
        super(balance, primaryOwner, secondaryOwner);
        setInterestRate(interestRate);
    }
    public SavingsAccount(Money balance, AccountHolder primaryOwner, Money minimumBalance, BigDecimal interestRate) {
        super(balance, primaryOwner);
        setBalance(minimumBalance);
        setInterestRate(interestRate);
    }
    public SavingsAccount(Money balance, AccountHolder primaryOwner, AccountHolder secondaryOwner, Money minimumBalance, BigDecimal interestRate) {
        super(balance, primaryOwner, secondaryOwner);
        setBalance(minimumBalance);
        setInterestRate(interestRate);
    }

    @Override
    public void update() {
        //interestRate: apply every 365 days
        LocalDateTime yearAgo = LocalDateTime.now().minus(365, ChronoUnit.DAYS);
        if(getLastUpdated().isBefore(yearAgo)){
            //time elapsed without interest applied
            long daysElapsed = Duration.between(LocalDateTime.now(), getLastUpdated()).toDays();
            long yearsElapsed = Long.divideUnsigned(daysElapsed, 365L);
            //calculate rates and amount
            BigDecimal interestQuotient = interestRate.add(BigDecimal.valueOf(1));
            if(yearsElapsed>1){
                //calculate
                BigDecimal sum = getBalance().getAmount();
                for(int i = (int) yearsElapsed; i>0; i--){
                    sum = sum.add(sum.multiply(interestQuotient));
                }
                //set new balance
                setBalance(new Money(sum));
            } else {
                //calculate
                BigDecimal updatedBalance = getBalance().getAmount().multiply(interestQuotient);
                //set new balance
                setBalance(new Money(updatedBalance));
            }
        }
        //application of interests can only be positive
        //does not trigger a verifyPenaltyFee (applies if balance dips below min)

        //update lastUpdated
        setLastUpdated();
    }

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
