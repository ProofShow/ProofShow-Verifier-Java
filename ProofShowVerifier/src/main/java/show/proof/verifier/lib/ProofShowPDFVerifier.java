package show.proof.verifier.lib;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.InvalidParameterException;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
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
import show.proof.pcca.verifier.PCCAVerifier;
import show.proof.pcca.verifier.lib.PCCAErrors;
import show.proof.pcca.verifier.lib.PCCAVerifierReport;

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
     * the courier'ID.
     */
    private String courierID = "";
    /**
     * the tracking number of receipt.
     */
    private String trackingNum = "";
    /**
     * the x509 certificate token object of the signature signer.
     */
    private CertificateToken signerCertToken = null;
    /**
     * the signer name of a valid signature
     */
    private String signerInfo = "";
    /**
     * the signing time of a valid signature
     */
    private Date signingTime = null;

    /**
     * Constructor for ProofShowPDFVerifier
     * 
     * @param pdfBuffer   the signed PDF buffer.
     * @param courierID   the courier's ID.
     * @param trackingNum the tracking number of receipt.
     * @throws InvalidParameterException
     */
    public ProofShowPDFVerifier(byte[] pdfBuffer, String courierID, String trackingNum)
            throws InvalidParameterException {

        // check argument
        if (pdfBuffer == null)
            throw new InvalidParameterException("invalid PDF buffer");

        if (courierID == null || courierID.isEmpty())
            throw new InvalidParameterException("invalid courier ID");

        if (trackingNum == null || trackingNum.isEmpty())
            throw new InvalidParameterException("invalid tracking number");

        this.dssDocument = new InMemoryDocument(pdfBuffer);
        this.courierID = courierID;
        this.trackingNum = trackingNum;
    }

    /**
     * Perform the verification procedure.
     * 
     * @return the error code of verification.
     */
    public ProofShowErrors verify() {
        ProofShowErrors result = ProofShowErrors.UNKNOW;

        if (!_checkSignatureNum())
            result = ProofShowErrors.INVALID_SIGNATURE_NUM;
        else if (!_checkOriginalPDF())
            result = ProofShowErrors.INVALID_ORIGINAL_PDF;
        else
            result = _checkSignature();

        return result;
    }

    /**
     * Get the signer information of valid signature
     * 
     * @return the signer information string
     */
    public String getSignerInfo() {
        return signerInfo;
    }

    /**
     * Get the signing time of valid signature
     * 
     * @return the signing time string
     */
    public String getSigningTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return dateFormat.format(signingTime);
    }

    /**
     * Read all string by the reader object.
     * 
     * @param rd the reader object
     * @return the complete string data
     * @throws IOException
     */
    private String _readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    /**
     * Retrieve the PSES data.
     * 
     * @return the JSON string of PSES data.
     * @throws MalformedURLException
     * @throws IOException
     */
    private String _retrievePSES() throws MalformedURLException, IOException {
        InputStream is = new URL("https://download.ca.proof.show/PSES.json").openStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
        String jsonText = _readAll(rd);

        return jsonText;
    }

    /**
     * Send the hash check request to server
     * 
     * @param hash the hash string
     * @return true if pass from server
     */
    private boolean _sendHashCheckRequest(String hash) {
        try {
            URL url = new URL("https://api.proof.show/v1/check-pdf-hash?courier=" + courierID + "&trackingNumber="
                    + trackingNum + "&originalHash=" + hash);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.connect();

            int code = connection.getResponseCode();

            return (code == 200);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check the signature numbers in the PDF
     * 
     * @return true if pass the checking
     */
    private boolean _checkSignatureNum() {
        PDFDocumentValidator validator = new PDFDocumentValidator(dssDocument);
        CertificateVerifier certificateVerifier = new CommonCertificateVerifier();
        CommonTrustedCertificateSource trustCertSource = new CommonTrustedCertificateSource();

        certificateVerifier.setTrustedCertSource(trustCertSource);
        validator.setCertificateVerifier(certificateVerifier);

        List<AdvancedSignature> signatures = validator.getSignatures();

        if (validator.getSignatures().size() != 1)
            return false;
        else {
            try {
                CertificateToken signerToken = signatures.get(0).getSigningCertificateToken();
                String psesDataString = _retrievePSES();
                PCCAVerifier verifier = new PCCAVerifier(
                        new ByteArrayInputStream(signerToken.getCertificate().getEncoded()), psesDataString);
                PCCAVerifierReport report = verifier.verify();

                if (report.retCode == PCCAErrors.SUCCESS) {
                    signerCertToken = signerToken;
                    return true;
                } else
                    return false;
            } catch (Exception e) {
                return false;
            }
        }
    }

    /**
     * Check the original PDF of a signed PDF
     * 
     * @return true if pass the testing
     */
    private boolean _checkOriginalPDF() {
        PDFDocumentValidator validator = new PDFDocumentValidator(dssDocument);
        CertificateVerifier certificateVerifier = new CommonCertificateVerifier();
        CommonTrustedCertificateSource trustCertSource = new CommonTrustedCertificateSource();

        certificateVerifier.setTrustedCertSource(trustCertSource);
        validator.setCertificateVerifier(certificateVerifier);

        List<AdvancedSignature> signatures = validator.getSignatures();
        List<DSSDocument> extractPDFList = validator.getOriginalDocuments(signatures.get(0).getId());

        if (extractPDFList.size() != 1)
            return false;
        else {
            try {
                ByteArrayOutputStream pdfOutStream = new ByteArrayOutputStream();
                extractPDFList.get(0).writeTo(pdfOutStream);

                byte lfByte = (byte) 0x0a;
                byte crByte = (byte) 0x0d;
                int trimLength = 0;
                byte[] pdfByteArray = pdfOutStream.toByteArray();

                // trim the PDF bytes
                for (int inverseIdx = pdfByteArray.length - 1; inverseIdx >= 0; inverseIdx--) {
                    byte tmpByte = pdfByteArray[inverseIdx];

                    if (tmpByte == lfByte || tmpByte == crByte)
                        trimLength++;
                    else
                        break;
                }

                // calculate the sha256 of PDF bytes
                byte[] trimePDFByteArray = Arrays.copyOfRange(pdfByteArray, 0, pdfByteArray.length - trimLength);
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                byte[] messageDigest = md.digest(trimePDFByteArray);
                BigInteger bigNO = new BigInteger(1, messageDigest);
                String hashText = bigNO.toString(16);

                while (hashText.length() < 64) {
                    hashText = "0" + hashText;
                }

                return _sendHashCheckRequest(hashText);
            } catch (Exception e) {
                return false;
            }
        }
    }

    /**
     * Verify PDF signature with EU DSS.
     * 
     * @return the error code of verification
     */
    private ProofShowErrors _checkSignature() {
        PDFDocumentValidator validator = new PDFDocumentValidator(dssDocument);
        CertificateVerifier certificateVerifier = new CommonCertificateVerifier();
        CommonTrustedCertificateSource trustCertSource = new CommonTrustedCertificateSource();

        // add trust certificate
        trustCertSource.addCertificate(DSSUtils.loadCertificate("/EntrustRoot.pem"));
        trustCertSource.addCertificate(DSSUtils.loadCertificate("/DigicertRoot.pem"));
        trustCertSource.addCertificate(signerCertToken);

        certificateVerifier.setTrustedCertSource(trustCertSource);
        validator.setCertificateVerifier(certificateVerifier);

        Reports report = validator.validateDocument("/constraint.adoc");
        SimpleReport simpleReport = report.getSimpleReport();
        String signatureID = simpleReport.getFirstSignatureId();

        // check signature format
        if (!simpleReport.getSignatureFormat(signatureID).equals(SignatureLevel.PAdES_BASELINE_LTA.toString()))
            return ProofShowErrors.INVALID_SIGNATURE_FORMAT;

        // check signature indication
        if (!simpleReport.getIndication(signatureID).toString().equals("TOTAL_PASSED")
                || simpleReport.getErrors(signatureID).size() != 0 || simpleReport.getWarnings(signatureID).size() != 0)
            return ProofShowErrors.INVALID_SIGNATURE_INDICATION;

        signerInfo = simpleReport.getSignedBy(signatureID);
        signingTime = simpleReport.getSigningTime(signatureID);

        return ProofShowErrors.SUCCESS;
    }
}
