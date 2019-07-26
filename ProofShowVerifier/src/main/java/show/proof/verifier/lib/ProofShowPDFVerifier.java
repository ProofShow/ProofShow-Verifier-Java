package show.proof.verifier.lib;

import java.security.InvalidParameterException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import eu.europa.esig.dss.DSSDocument;
import eu.europa.esig.dss.DSSUtils;
import eu.europa.esig.dss.InMemoryDocument;
import eu.europa.esig.dss.SignatureLevel;
import eu.europa.esig.dss.pades.validation.PDFDocumentValidator;
import eu.europa.esig.dss.validation.AdvancedSignature;
import eu.europa.esig.dss.validation.CertificateVerifier;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.validation.reports.Reports;
import eu.europa.esig.dss.validation.reports.SimpleReport;
import eu.europa.esig.dss.x509.CertificateToken;
import eu.europa.esig.dss.x509.CommonTrustedCertificateSource;

/**
 * Provides the logic of PDF verification.
 *
 */
public class ProofShowPDFVerifier {
    /**
     * the DSS document object.
     */
    private DSSDocument dssDocument = null;
    /**
     * the x509 certificate object of the signature signer.
     */
    private X509Certificate signerCert = null;

    /**
     * Constructor for ProofShowPDFVerifier
     * 
     * @param pdfBuffer the signed PDF buffer
     * @throws InvalidParameterException
     */
    public ProofShowPDFVerifier(byte[] pdfBuffer) throws InvalidParameterException {

        // check argument
        if (pdfBuffer == null)
            throw new InvalidParameterException("invalid PDF buffer");

        dssDocument = new InMemoryDocument(pdfBuffer);
    }

    /**
     * Perform the verification procedure.
     * 
     * @return the error code of verification.
     */
    public ProofShowErrors verify() {
        ProofShowErrors result = ProofShowErrors.UNKNOW;

        result = _checkSignature();

        return result;
    }

    /**
     * Get the certificate object of the signature signer.
     * 
     * @return the x509 certificate of the signature signer.
     */
    public X509Certificate getCertificate() {
        return signerCert;
    }

    /**
     * Retrieve all signer certificates from PDF document
     * 
     * @return the list of certificate token
     */
    private List<CertificateToken> _retrieveSignerCerts() {
        PDFDocumentValidator validator = new PDFDocumentValidator(dssDocument);
        CertificateVerifier certificateVerifier = new CommonCertificateVerifier();
        CommonTrustedCertificateSource trustCertSource = new CommonTrustedCertificateSource();

        certificateVerifier.setTrustedCertSource(trustCertSource);
        validator.setCertificateVerifier(certificateVerifier);

        List<CertificateToken> signerCertTokens = new ArrayList<>();
        List<AdvancedSignature> signatures = validator.getSignatures();

        for (AdvancedSignature signature : signatures) {
            CertificateToken signerToken = signature.getSigningCertificateToken();

            // check certificate issuer
            if (signerToken.getIssuerX500Principal().getName().equals("CN=PCCA Intermediate CA1,O=ProofShow Inc.,C=TW"))
                signerCertTokens.add(signerToken);
        }

        return signerCertTokens;
    }

    /**
     * Verify PDF signature with EU DSS.
     * 
     * @return the error code of verification
     */
    private ProofShowErrors _checkSignature() {
        List<CertificateToken> certs = _retrieveSignerCerts();

        if (certs.size() != 1)
            return ProofShowErrors.INVALID_SIGNATURE_NUM;
        else {
            PDFDocumentValidator validator = new PDFDocumentValidator(dssDocument);
            CertificateVerifier certificateVerifier = new CommonCertificateVerifier();
            CommonTrustedCertificateSource trustCertSource = new CommonTrustedCertificateSource();

            // add trust certificate
            trustCertSource.addCertificate(DSSUtils.loadCertificate("/EntrustRoot.pem"));
            trustCertSource.addCertificate(certs.get(0));

            certificateVerifier.setTrustedCertSource(trustCertSource);
            validator.setCertificateVerifier(certificateVerifier);

            Reports report = validator.validateDocument("/constraint.adoc");
            SimpleReport simpleReport = report.getSimpleReport();
            String signatureID = simpleReport.getFirstSignatureId();

            // check signature format
            if (!simpleReport.getSignatureFormat(signatureID).equals(SignatureLevel.PAdES_BASELINE_LTA.toString()))
                return ProofShowErrors.INVALID_SIGNATURE_FORMAT;

            // check signature indication
            if (!simpleReport.getIndication(signatureID).toString().equals("TOTAL_PASSED"))
                return ProofShowErrors.INVALID_SIGNATURE_INDICATION;

            signerCert = certs.get(0).getCertificate();

            return ProofShowErrors.SUCCESS;
        }
    }
}
