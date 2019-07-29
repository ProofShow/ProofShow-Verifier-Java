package show.proof.verifier_example;

import java.io.File;
import java.io.FileWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.security.cert.X509Certificate;

import org.bouncycastle.openssl.jcajce.JcaPEMWriter;

import show.proof.verifier.ProofShowVerifier;
import show.proof.verifier.lib.ProofShowErrors;
import show.proof.verifier.lib.ProofShowVerifierReport;

/**
 * Provides the example code for using ProofShowVerifier.
 *
 */
public class ProofShowVerifierExample {
    /**
     * the main error message.
     */
    final private static String errMsg = "The input PDF does not carry a valid signature";
    /**
     * the result messages.
     */
    final private static String[] resultMessages = new String[] {
            "The input PDF is correctly signed in PAdES Baseline LTA format.", errMsg + " (Invalid signature number)",
            errMsg + " (Invalid signature format)", errMsg + " (Invalid signature indication)" };

    /**
     * Main method of example code.
     * 
     * @param args program arguments
     */
    public static void main(String[] args) {
        System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");
        System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "ERROR");
        java.util.logging.Logger.getLogger("org.apache.pdfbox").setLevel(java.util.logging.Level.SEVERE);

        if (args.length == 2) {
            try {
                // load PDF and certificate path arguments
                String pdfPath = args[0];
                String certPath = args[1];

                // read PDF to buffer
                File pdfFile = new File(pdfPath);
                byte[] pdfBuffer = Files.readAllBytes(pdfFile.toPath());

                // perform validation
                ProofShowVerifier verifier = new ProofShowVerifier(pdfBuffer);
                ProofShowVerifierReport report = verifier.verify();

                // print the result message
                System.out.println("ProofShowVerifier Version "
                        + ProofShowVerifierExample.class.getPackage().getImplementationVersion() + " - ProofShow Inc.");
                System.out.println("");
                System.out.println(resultMessages[report.retCode.ordinal()]);

                // save signer certificate
                if (report.retCode == ProofShowErrors.SUCCESS)
                    _saveCert(certPath, report.signerCert);

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else
            System.out.println("Invalid argument");
    }

    /**
     * Save certificate to the file path in PEM format.
     * 
     * @param certPath the file path of output certificate
     * @param cert     the certificate object
     */
    private static void _saveCert(String certPath, X509Certificate cert) {
        try {
            // format to PEM string
            final StringWriter writer = new StringWriter();
            final JcaPEMWriter pemWriter = new JcaPEMWriter(writer);
            pemWriter.writeObject(cert);
            pemWriter.flush();
            pemWriter.close();

            // save PEM string to file
            FileWriter fw = new FileWriter(certPath);
            fw.write(writer.toString());
            fw.close();

            System.out.println("The signer certificate is saved to: " + certPath);
        } catch (Exception e) {
            System.out.println("Failed to save signer certificate");
        }
    }
}
