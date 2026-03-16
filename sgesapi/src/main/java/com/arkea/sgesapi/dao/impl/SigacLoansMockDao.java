package com.arkea.sgesapi.dao.impl;

import com.arkea.sgesapi.dao.api.ISigacLoansDao;
import com.arkea.sgesapi.exception.DAOException;
import com.arkea.sgesapi.model.sigac.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.*;

/**
 * Implémentation Mock du client SIGAC Loans.
 * <p>
 * Simule les réponses du service REST SIGAC pour le développement.
 * Retourne des CommonLoan conformes au contrat OpenAPI sigac-prets.yaml.
 * <p>
 * En production, sera remplacé par une implémentation HTTP (RestTemplate/WebClient).
 */
@Repository
public class SigacLoansMockDao implements ISigacLoansDao {

    private static final Logger log = LoggerFactory.getLogger(SigacLoansMockDao.class);

    private final Map<String, CommonLoan> loans = new LinkedHashMap<>();

    @PostConstruct
    void initMockData() {
        log.info("Initialisation des données mock SIGAC Loans");

        // Prêt 1 — PAP en gestion, 2 participants
        CommonLoan loan1 = new CommonLoan();
        loan1.setId("PRT-2024-08-1547");
        loan1.setMasterContractId("PRJ-2024-08-1547");
        loan1.setDuration(240);
        loan1.setBorrowedAmount(BigDecimal.valueOf(250000.00));
        loan1.setRate(BigDecimal.valueOf(3.45));
        loan1.setAvailableAmount(BigDecimal.valueOf(0.00));
        loan1.setPeriodicity(CommonLoan.PeriodicityEnum.M);
        loan1.setLabel("Prêt à l'Accession à la Propriété");
        loan1.setTypeCode("PAP");

        LoanType type1 = new LoanType();
        type1.setCode("PAP");
        type1.setLabel("Prêt à l'Accession à la Propriété");
        loan1.setLoanType(type1);

        ObjectCode obj1 = new ObjectCode();
        obj1.setCode("01");
        obj1.setLabel("Acquisition ancien");
        loan1.setObjectCode(obj1);

        LoanState state1 = new LoanState();
        state1.setCode("40");
        state1.setLabel("En gestion");
        loan1.setLoanState(state1);

        Participant emp1 = new Participant();
        emp1.setPersonNumber("PP-001547-E");
        emp1.setPersonFederation("13807");
        emp1.setRoleCode("EMP");
        emp1.setLastName("MARTIN");
        emp1.setFirstName("Jean-Pierre");

        Participant coe1 = new Participant();
        coe1.setPersonNumber("PP-001547-C");
        coe1.setPersonFederation("13807");
        coe1.setRoleCode("COE");
        coe1.setLastName("MARTIN");
        coe1.setFirstName("Catherine");

        loan1.setParticipants(List.of(emp1, coe1));
        loans.put(loan1.getId(), loan1);

        // Prêt 2 — PAS en déblocage
        CommonLoan loan2 = new CommonLoan();
        loan2.setId("PRT-2024-10-2891");
        loan2.setMasterContractId("PRJ-2024-10-2891");
        loan2.setDuration(300);
        loan2.setBorrowedAmount(BigDecimal.valueOf(180000.00));
        loan2.setRate(BigDecimal.valueOf(2.85));
        loan2.setAvailableAmount(BigDecimal.valueOf(60000.00));
        loan2.setPeriodicity(CommonLoan.PeriodicityEnum.M);
        loan2.setLabel("Prêt d'Accession Sociale");
        loan2.setTypeCode("PAS");

        LoanType type2 = new LoanType();
        type2.setCode("PAS");
        type2.setLabel("Prêt d'Accession Sociale");
        loan2.setLoanType(type2);

        ObjectCode obj2 = new ObjectCode();
        obj2.setCode("02");
        obj2.setLabel("Construction");
        loan2.setObjectCode(obj2);

        LoanState state2 = new LoanState();
        state2.setCode("30");
        state2.setLabel("En déblocage");
        loan2.setLoanState(state2);

        Participant emp2 = new Participant();
        emp2.setPersonNumber("PP-002891-E");
        emp2.setPersonFederation("13808");
        emp2.setRoleCode("EMP");
        emp2.setLastName("DURAND");
        emp2.setFirstName("Marie");

        Participant coe2 = new Participant();
        coe2.setPersonNumber("PP-002891-C");
        coe2.setPersonFederation("13808");
        coe2.setRoleCode("COE");
        coe2.setLastName("DURAND");
        coe2.setFirstName("Philippe");

        loan2.setParticipants(List.of(emp2, coe2));
        loans.put(loan2.getId(), loan2);

        // Prêt 3 — PAP sans co-emprunteur
        CommonLoan loan3 = new CommonLoan();
        loan3.setId("PRT-2023-03-0412");
        loan3.setMasterContractId("PRJ-2023-03-0412");
        loan3.setDuration(180);
        loan3.setBorrowedAmount(BigDecimal.valueOf(75000.00));
        loan3.setRate(BigDecimal.valueOf(3.10));
        loan3.setAvailableAmount(BigDecimal.valueOf(0.00));
        loan3.setPeriodicity(CommonLoan.PeriodicityEnum.M);
        loan3.setLabel("Prêt à l'Accession à la Propriété");
        loan3.setTypeCode("PAP");

        LoanType type3 = new LoanType();
        type3.setCode("PAP");
        type3.setLabel("Prêt à l'Accession à la Propriété");
        loan3.setLoanType(type3);

        ObjectCode obj3 = new ObjectCode();
        obj3.setCode("03");
        obj3.setLabel("Travaux");
        loan3.setObjectCode(obj3);

        LoanState state3 = new LoanState();
        state3.setCode("40");
        state3.setLabel("En gestion");
        loan3.setLoanState(state3);

        Participant emp3 = new Participant();
        emp3.setPersonNumber("PP-000412-E");
        emp3.setPersonFederation("13807");
        emp3.setRoleCode("EMP");
        emp3.setLastName("BERNARD");
        emp3.setFirstName("Sophie");

        loan3.setParticipants(List.of(emp3));
        loans.put(loan3.getId(), loan3);

        log.info("{} prêts mock SIGAC initialisés", loans.size());
    }

    @Override
    public Optional<CommonLoan> getLoan(String contratId) throws DAOException {
        log.debug("SIGAC Mock — getLoan contratId={}", contratId);
        return Optional.ofNullable(loans.get(contratId));
    }
}
