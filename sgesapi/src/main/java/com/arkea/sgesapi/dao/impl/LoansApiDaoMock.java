package com.arkea.sgesapi.dao.impl;

import com.arkea.sgesapi.api.sigac.LoansApi;
import com.arkea.sgesapi.model.sigac.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.*;

/**
 * Implémentation Mock du client SIGAC Loans — profil DEV uniquement.
 * <p>
 * Simule les réponses du service REST SIGAC pour le développement.
 * Retourne des CommonLoan conformes au contrat OpenAPI sigac-prets.yaml.
 * <p>
 * En val/rec/hml/prod, c'est SigacLoansRestDao (@Profile "!dev") qui prend le relai
 * avec un vrai appel HTTP vers le service SIGAC.
 */
@Repository
@Profile("dev")
public class LoansApiDaoMock implements LoansApi {

    private static final Logger log = LoggerFactory.getLogger(LoansApiDaoMock.class);

    private final Map<String, CommonLoan> loans = new LinkedHashMap<>();

    @PostConstruct
    void initMockData() {
        log.info("Initialisation des données mock SIGAC Loans");

        // Prêt 1 — ALTIMMO en gestion normale, 2 participants (format réel SIGAC)
        CommonLoan loan1 = new CommonLoan();
        loan1.setId("DD04063627");
        loan1.setMasterContractId("DD04063627");
        loan1.setDuration(240);
        loan1.setBorrowedAmount(BigDecimal.valueOf(250000.00));
        loan1.setRate(BigDecimal.valueOf(3.45));
        loan1.setAvailableAmount(BigDecimal.valueOf(0.00));
        loan1.setPeriodicity(CommonLoan.PeriodicityEnum.M);
        loan1.setLabel(null);  // null dans le vrai SIGAC
        loan1.setTypeCode("10117");

        LoanType type1 = new LoanType();
        type1.setCode("110309");
        type1.setLabel("ALTIMMO FIXE");
        loan1.setLoanType(type1);

        ObjectCode obj1 = new ObjectCode();
        obj1.setCode("AA");
        obj1.setLabel("ACQUISITION ANCIEN");
        loan1.setObjectCode(obj1);

        LoanState state1 = new LoanState();
        state1.setCode("AA");
        state1.setLabel("EN COURS NORMALE");
        loan1.setLoanState(state1);

        Participant emp1 = new Participant();
        emp1.setPersonNumber("14336390");
        emp1.setPersonFederation("01");
        emp1.setRoleCode("EMP");

        Participant coe1 = new Participant();
        coe1.setPersonNumber("14336391");
        coe1.setPersonFederation("01");
        coe1.setRoleCode("COE");

        loan1.setParticipants(List.of(emp1, coe1));
        loans.put(loan1.getId(), loan1);

        // Prêt 2 — PAS en déblocage, 2 participants
        CommonLoan loan2 = new CommonLoan();
        loan2.setId("AX12457845");
        loan2.setMasterContractId("AX12457845");
        loan2.setDuration(300);
        loan2.setBorrowedAmount(BigDecimal.valueOf(180000.00));
        loan2.setRate(BigDecimal.valueOf(2.85));
        loan2.setAvailableAmount(BigDecimal.valueOf(60000.00));
        loan2.setPeriodicity(CommonLoan.PeriodicityEnum.M);
        loan2.setLabel(null);
        loan2.setTypeCode("10200");

        LoanType type2 = new LoanType();
        type2.setCode("200100");
        type2.setLabel("PRET ACCESSION SOCIALE");
        loan2.setLoanType(type2);

        ObjectCode obj2 = new ObjectCode();
        obj2.setCode("CN");
        obj2.setLabel("CONSTRUCTION");
        loan2.setObjectCode(obj2);

        LoanState state2 = new LoanState();
        state2.setCode("DB");
        state2.setLabel("EN DEBLOCAGE");
        loan2.setLoanState(state2);

        Participant emp2 = new Participant();
        emp2.setPersonNumber("15789012");
        emp2.setPersonFederation("03");
        emp2.setRoleCode("EMP");

        Participant coe2 = new Participant();
        coe2.setPersonNumber("15789013");
        coe2.setPersonFederation("03");
        coe2.setRoleCode("COE");

        loan2.setParticipants(List.of(emp2, coe2));
        loans.put(loan2.getId(), loan2);

        // Prêt 3 — PRET BIENVENUE sans co-emprunteur
        CommonLoan loan3 = new CommonLoan();
        loan3.setId("BZ98765432");
        loan3.setMasterContractId("BZ98765432");
        loan3.setDuration(180);
        loan3.setBorrowedAmount(BigDecimal.valueOf(75000.00));
        loan3.setRate(BigDecimal.valueOf(3.10));
        loan3.setAvailableAmount(BigDecimal.valueOf(0.00));
        loan3.setPeriodicity(CommonLoan.PeriodicityEnum.M);
        loan3.setLabel(null);
        loan3.setTypeCode("10050");

        LoanType type3 = new LoanType();
        type3.setCode("110309");
        type3.setLabel("PRET BIENVENUE");
        loan3.setLoanType(type3);

        ObjectCode obj3 = new ObjectCode();
        obj3.setCode("TV");
        obj3.setLabel("TRAVAUX");
        loan3.setObjectCode(obj3);

        LoanState state3 = new LoanState();
        state3.setCode("AA");
        state3.setLabel("EN COURS NORMALE");
        loan3.setLoanState(state3);

        Participant emp3 = new Participant();
        emp3.setPersonNumber("12004567");
        emp3.setPersonFederation("01");
        emp3.setRoleCode("EMP");

        loan3.setParticipants(List.of(emp3));
        loans.put(loan3.getId(), loan3);

        log.info("{} prêts mock SIGAC initialisés", loans.size());
    }

    @Override
    public ResponseEntity<CommonLoan> getLoan(String contratId) {
        log.debug("SIGAC Mock — getLoan contratId={}", contratId);
        CommonLoan loan = loans.get(contratId);
        if (loan == null) {
            log.warn("SIGAC Mock — Prêt non trouvé : {}", contratId);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(loan);
    }
}
