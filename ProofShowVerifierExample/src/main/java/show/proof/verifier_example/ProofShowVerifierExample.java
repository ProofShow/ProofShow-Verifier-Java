package show.proof.verifier_example;

import java.io.File;
import java.nio.file.Files;

import show.proof.verifier.ProofShowVerifier;
import show.proof.verifier.lib.ProofShowErrors;
import show.proof.verifier.lib.ProofShowVerifierReport;

/**
 * Provides the example code for using ProofShowVerifier.
 *
 */
public class ProofShowVerifierExample {
    /**
     * Main method of example code.
     * 
     * @param args program arguments
     */
    public static void main(String[] args) {
        System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");
        System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "ERROR");
        java.util.logging.Logger.getLogger("org.apache.pdfbox").setLevel(java.util.logging.Level.SEVERE);

        if (args.length == 3) {
            try {
                // load PDF and certificate path arguments
                String pdfPath = args[0];
                String courierID = args[1];
                String trackingNum = args[2];

                // read PDF to buffer
                File pdfFile = new File(pdfPath);
                byte[] pdfBuffer = Files.readAllBytes(pdfFile.toPath());

                // perform validation
                ProofShowVerifier verifier = new ProofShowVerifier(pdfBuffer, courierID, trackingNum);
                ProofShowVerifierReport report = verifier.verify();

                // print the result message
                System.out.println("ProofShowVerifier Version "
                        + ProofShowVerifierExample.class.getPackage().getImplementationVersion() + " - ProofShow Inc.");
                System.out.println("");

                if (report.retCode == ProofShowErrors.SUCCESS) {
                    System.out.println(
                            "The input is a valid ProofShow return receipt meeting EU’s PAdES B-LTA standard.");
                    System.out.println("Signing Time: " + report.signingTime);
                    System.out.println("Signer Info : " + report.signerInfo);
                } else {
                    System.out.println("The input is NOT a valid ProofShow return receipt.");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else
            System.out.println("Invalid argument");
    }
}
