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
- [PCCA Verifier (Java)](https://github.com/ProofShow/PCCA-Verifier-Java)

### How to install
To install this library (without building the sample code) in the local repository, run the following:

```
mvn install -pl '!ProofShowVerifierExample'
```

### How to use
To use this library, study the sample code in `ProofShowVerifierExample` which can be built and run by the following:

```
mvn package
java -jar ProofShowVerifierExample/target/ProofShowVerifierExample.jar INPUT_PDF_PATH COURIER_ID TRACKING_NUMBER
```

Below is the list of `COURIER_ID` that ProofShow Verifier (Java) currently supports:
<table>
    <tr>
        <td>USPS</td>
        <td>0</td>
    </tr>
    <tr>
        <td>UPS</td>
        <td>1</td>
    </tr>
    <tr>
        <td>FedEx</td>
        <td>2</td>
    </tr>
    <tr>
        <td>DHL</td>
        <td>3</td>
    </tr>
    <tr>
        <td>新竹物流</td>
        <td>4</td>
    </tr>
    <tr>
        <td>嘉里大榮物流</td>
        <td>5</td>
    </tr>
    <tr>
        <td>黑貓宅急便</td>
        <td>6</td>
    </tr>
    <tr>
        <td>宅配通</td>
        <td>7</td>
    </tr>
    <tr>
        <td>網家速配</td>
        <td>8</td>
    </tr>
    <tr>
        <td>EMS</td>
        <td>9</td>
    </tr>
    <tr>
        <td>中華郵政</td>
        <td>10</td>
    </tr>
    <tr>
        <td>顺丰速运</td>
        <td>11</td>
    </tr>
</table>

### License
AGPL-3.0-or-later
