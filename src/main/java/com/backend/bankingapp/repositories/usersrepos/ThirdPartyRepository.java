package com.backend.bankingapp.repositories.usersrepos;

import com.backend.bankingapp.models.users.ThirdParty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ThirdPartyRepository extends JpaRepository<ThirdParty, Long> {

    List<ThirdParty> findByName(String name);
    Optional<ThirdParty> findById(long id);

}
