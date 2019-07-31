package show.proof.verifier.lib;

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
     * the signer information of a valid signature
     */
    public String signerInfo = "";
    /**
     * the signing time of a valid signature
     */
    public String signingTime = "";
}