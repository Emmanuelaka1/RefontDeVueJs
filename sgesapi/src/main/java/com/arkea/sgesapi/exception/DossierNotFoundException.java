package com.arkea.sgesapi.exception;

/**
 * Exception levée lorsqu'un dossier de prêt n'est pas trouvé.
 */
public class DossierNotFoundException extends RuntimeException {

    public DossierNotFoundException(String numeroPret) {
        super("Dossier non trouvé pour le numéro de prêt : " + numeroPret);
    }
}
