## ProofShow Verifier (Java)
[![License: AGPL v3](https://img.shields.io/badge/License-AGPL%20v3-blue.svg)](https://www.gnu.org/licenses/agpl-3.0)
[![Known Vulnerabilities](https://snyk.io//test/github/ProofShow/ProofShow-Verifier-Java/badge.svg?targetFile=ProofShowVerifier/pom.xml)](https://snyk.io//test/github/ProofShow/ProofShow-Verifier-Java?targetFile=ProofShowVerifier/pom.xml)
[![Build Status](https://travis-ci.com/ProofShow/ProofShow-Verifier-Java.svg?branch=master)](https://travis-ci.com/ProofShow/ProofShow-Verifier-Java)

ProofShow Verifier (Java) is a library for courier companies to verify a ProofShow return receipt (in PDF format) that is digitally signed. Specifically, given an input PDF file, it can

- Check if it is formatted according to the specification of ProofShow return receipt;
- Check if the [PCCA](https://pcca.proof.show) signing certificate within is valid, using [PCCA Verifier (Java)](https://github.com/ProofShow/PCCA-Verifier-Java);
- Check if the digital signature within meets EU's PAdES B-LTA standard, using EU's [Digital Signature Service](https://github.com/esig/dss) library;
- Output the check results (which, for a valid ProofShow return receipt, include the signing time and signer info).

### Requirement
- JDK 1.8 or higher
- Maven

### How to install
To install this library in the local repository, run the following:

```
mvn install -pl ProofShowVerifier
```

### How to use
To use this library, study the sample code in `ProofShowVerifierExample` which can be built and run by the following:

```
mvn package
java -jar ProofShowVerifierExample/target/ProofShowVerifierExample.jar INPUT_PATH COURIER_ID TRACKING_NUMBER
```

### License
AGPL-3.0-or-later
