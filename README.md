## ProofShow Verifier (Java)
[![License: AGPL v3](https://img.shields.io/badge/License-AGPL%20v3-blue.svg)](https://www.gnu.org/licenses/agpl-3.0)
[![Known Vulnerabilities](https://snyk.io//test/github/ProofShow/ProofShow-Verifier-Java/badge.svg?targetFile=ProofShowVerifier/pom.xml)](https://snyk.io//test/github/ProofShow/ProofShow-Verifier-Java?targetFile=ProofShowVerifier/pom.xml)
[![Build Status](https://travis-ci.com/ProofShow/ProofShow-Verifier-Java.svg?branch=master)](https://travis-ci.com/ProofShow/ProofShow-Verifier-Java)

ProofShow Verifier (Java) is a library for verifying a ProofShow return receipt that is digitally-signed. Specifically, it will

- Use EU's [Digital Signature Service](https://github.com/esig/dss) library to check if the digital signature meets EU's PAdES B-LTA standard;
- Output the signer's certificate for later check using [PCCA Verifier (Java)](https://github.com/ProofShow/PCCA-Verifier-Java).

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
java -jar ProofShowVerifierExample/target/ProofShowVerifierExample.jar PATH_OF_PDF OUT_PATH_OF_CERT
```

### License
AGPL-3.0-or-later
