## ProofShow Verifier Java
[![License: AGPL v3](https://img.shields.io/badge/License-AGPL%20v3-blue.svg)](https://www.gnu.org/licenses/agpl-3.0)
[![Known Vulnerabilities](https://snyk.io//test/github/ProofShow/ProofShow-Verifier-Java/badge.svg?targetFile=ProofShowVerifier/pom.xml)](https://snyk.io//test/github/ProofShow/ProofShow-Verifier-Java?targetFile=ProofShowVerifier/pom.xml)
[![Build Status](https://travis-ci.com/ProofShow/ProofShow-Verifier-Java.svg?branch=master)](https://travis-ci.com/ProofShow/ProofShow-Verifier-Java)

ProofShow Verifier Java is a Java library for verifying a signed PDF. The verification is done by

- Using [Digital Signature Service](https://github.com/esig/dss) to check if the input PDF is signed in PAdES Baseline LTA format

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