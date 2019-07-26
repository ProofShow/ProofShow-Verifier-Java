package show.proof.verifier.lib;

import java.security.cert.X509Certificate;

/**
 * Provides the report data for verification.
 *
 */
public class ProofShowVerifierReport {
    /**
     * the error code of verification result.
     */
    public ProofShowErrors retCode = ProofShowErrors.UNKNOW;
    /**
     * the signer certificate of a valid signature.
     */
    public X509Certificate signerCert = null;
}