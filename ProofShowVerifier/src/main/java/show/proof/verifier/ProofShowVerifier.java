package show.proof.verifier;

import java.security.InvalidParameterException;

import show.proof.verifier.lib.ProofShowErrors;
import show.proof.verifier.lib.ProofShowPDFVerifier;
import show.proof.verifier.lib.ProofShowVerifierReport;

/**
 * Provides the interface for verifying a signed PDF in PAdES Baseline LTA
 * format.
 *
 */
public class ProofShowVerifier {
    /**
     * the PDF verifier object.
     */
    ProofShowPDFVerifier pdfVerifier = null;

    /**
     * Constructor for ProofShowVerifier.
     * 
     * @param pdfBuffer   the signed PDF buffer.
     * @param courierID   the courier's ID
     * @param trackingNum the tracking number of receipt
     * @throws InvalidParameterException
     */
    public ProofShowVerifier(byte[] pdfBuffer, String courierID, String trackingNum) throws InvalidParameterException {
        pdfVerifier = new ProofShowPDFVerifier(pdfBuffer, courierID, trackingNum);
    }

    /**
     * Perform the verification procedure.
     * 
     * @return the report data of verification.
     */
    public ProofShowVerifierReport verify() {
        ProofShowVerifierReport report = new ProofShowVerifierReport();
        report.retCode = pdfVerifier.verify();

        if (report.retCode == ProofShowErrors.SUCCESS) {
            report.signerInfo = pdfVerifier.getSignerInfo();
            report.signingTime = pdfVerifier.getSigningTime();
        }

        return report;
    }
}
