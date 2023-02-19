package com.backend.bankingapp.models.accounts;

import com.backend.bankingapp.models.users.AccountHolder;
import com.backend.bankingapp.models.utils.Money;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CreditCard extends Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @DecimalMin(value="100")
    @DecimalMax(value="100000")
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="currency", column = @Column(name = "credit_limit_currency")),
            @AttributeOverride(name="amount", column = @Column(name = "credit_limit_amount")),
    })
    private Money creditLimit = new Money(new BigDecimal("100"));
    @DecimalMax(value="0.2")
    @DecimalMin(value = "0.1")
    private BigDecimal interestRate = new BigDecimal("0.2"); //annual

    public CreditCard(Money balance, AccountHolder primaryOwner){ super(balance, primaryOwner); }

    public void update() {
        //interestRate: apply monthly
        LocalDateTime monthAgo = LocalDateTime.now().minus(30, ChronoUnit.DAYS);
        if(getLastUpdated().isBefore(monthAgo)){
            //time elapsed without interest applied
            long daysElapsed = Duration.between(LocalDateTime.now(), getLastUpdated()).toDays();
            long monthsElapsed = Long.divideUnsigned(daysElapsed, 30L);
            //calculate rates and amount
            BigDecimal monthlyRate = interestRate.divide(new BigDecimal("12"), RoundingMode.HALF_EVEN);
            BigDecimal interestQuotient = monthlyRate.add(BigDecimal.valueOf(1));
            if(monthsElapsed>1){
                //calculate
                BigDecimal sum = getBalance().getAmount();
                for(int i = (int) monthsElapsed; i>0; i--){
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
        //update lastUpdated
        setLastUpdated();
    }

}
