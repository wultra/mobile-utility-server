/*
 * Wultra Mobile Utility Server
 * Copyright (C) 2026 Wultra s.r.o.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.wultra.app.mobileutilityserver.utils;


/**
 * Utility class containing various certificates in PEM format to be used in tests.
 *
 * @author Pavel Sindelar, pavel.sindelar@wultra.com
 */
public final class Certificates {

    private Certificates() {
        throw new UnsupportedOperationException("Instantiation not allowed.");
    }

    public static final String GENERIC_CERT = """
            -----BEGIN CERTIFICATE-----
            MIIEQTCCAymgAwIBAgIBATANBgkqhkiG9w0BAQUFADCBkzEaMBgGA1UEAxMRTW9u
            a2V5IE1hY2hpbmUgQ0ExCzAJBgNVBAYTAlVLMREwDwYDVQQIEwhTY290bGFuZDEQ
            MA4GA1UEBxMHR2xhc2dvdzEcMBoGA1UEChMTbW9ua2V5bWFjaGluZS5jby51azEl
            MCMGCSqGSIb3DQEJARYWY2FAbW9ua2V5bWFjaGluZS5jby51azAeFw0wNTAzMDYy
            MzI4MjJaFw0wNjAzMDYyMzI4MjJaMIGvMQswCQYDVQQGEwJVSzERMA8GA1UECBMI
            U2NvdGxhbmQxEDAOBgNVBAcTB0dsYXNnb3cxGzAZBgNVBAoTEk1vbmtleSBNYWNo
            aW5lIEx0ZDElMCMGA1UECxMcT3BlbiBTb3VyY2UgRGV2ZWxvcG1lbnQgTGFiLjEU
            MBIGA1UEAxMLTHVrZSBUYXlsb3IxITAfBgkqhkiG9w0BCQEWEmx1a2VAbW9ua2V5
            bWFjaGluZTBcMA0GCSqGSIb3DQEBAQUAA0sAMEgCQQDItxZr07mm65ttYH7RMaVo
            VeMCq4ptfn+GFFEk4+54OkDuh1CHlk87gEc1jx3ZpQPJRTJx31z3YkiAcP+RDzxr
            AgMBAAGjggFIMIIBRDAJBgNVHRMEAjAAMBEGCWCGSAGG+EIBAQQEAwIHgDALBgNV
            HQ8EBAMCBeAwHQYDVR0OBBYEFG7mW1czzw4vFcL03+wUvvvPVFY8MIHABgNVHSME
            gbgwgbWAFKt47K8QG4qbH8exJY8WKPIXmq02oYGZpIGWMIGTMRowGAYDVQQDExFN
            b25rZXkgTWFjaGluZSBDQTELMAkGA1UEBhMCVUsxETAPBgNVBAgTCFNjb3RsYW5k
            MRAwDgYDVQQHEwdHbGFzZ293MRwwGgYDVQQKExNtb25rZXltYWNoaW5lLmNvLnVr
            MSUwIwYJKoZIhvcNAQkBFhZjYUBtb25rZXltYWNoaW5lLmNvLnVrggEAMDUGCWCG
            SAGG+EIBBAQoFiZodHRwczovL21vbmtleW1hY2hpbmUuY28udWsvY2EtY3JsLnBl
            bTANBgkqhkiG9w0BAQUFAAOCAQEAZ961bEgm2rOq6QajRLeoljwXDnt0S9BGEWL4
            PMU2FXDog9aaPwfmZ5fwKaSebwH4HckTp11xwe/D9uBZJQ74Uf80UL9z2eo0GaSR
            nRB3QPZfRvop0I4oPvwViKt3puLsi9XSSJ1w9yswnIf89iONT7ZyssPg48Bojo8q
            lcKwXuDRBWciODK/xWhvQbaegGJ1BtXcEHtvNjrUJLwSMDSr+U5oUYdMohG0h1iJ
            R+JQc49I33o2cTc77wfEWLtVdXAyYY4GSJR6VfgvV40x85ItaNS3HHfT/aXU1x4m
            W9YQkWlA6t0blGlC+ghTOY1JbgWnEfXMmVgg9a9cWaYQ+NQwqA==
            -----END CERTIFICATE-----
            """;

    // CN=domain.com
    public static final String CN_ONLY_CERT = """
            -----BEGIN CERTIFICATE-----
            MIIDOTCCAiGgAwIBAgIUJOrFTBc/1vCL99sM2kNdP90fC/wwDQYJKoZIhvcNAQEL
            BQAwRTELMAkGA1UEBhMCQ1oxDzANBgNVBAcMBlByYWd1ZTEQMA4GA1UECgwHVGVz
            dE9yZzETMBEGA1UEAwwKZG9tYWluLmNvbTAeFw0yNjAxMjAxMTAzMTBaFw0yNzAx
            MjAxMTAzMTBaMEUxCzAJBgNVBAYTAkNaMQ8wDQYDVQQHDAZQcmFndWUxEDAOBgNV
            BAoMB1Rlc3RPcmcxEzARBgNVBAMMCmRvbWFpbi5jb20wggEiMA0GCSqGSIb3DQEB
            AQUAA4IBDwAwggEKAoIBAQDrwZCL3/ianzBD2A6gDVpAAsLW2eolYFQhWBYfZOPg
            SsFE692kH0FKXVCWAtuuMH0C4axXRsGtB7xbrrDandf75Yhx17DF44eFR8/tTSVY
            UIVh5urnj0V0X9r+aa/iJWq2bak5hUmWMI3xU2JNUROn+O8sNsW0KtGS0KGnzGVv
            s7DvvAEnDc8AgKVbTrDmzm08bEsxObJ7FM8tQlnhRupz/mOVyePavzY7F5j4u6P3
            OQzVNK7tpMLOiy0/c7gzZ6q9KLtx3jdQdO3k8u+0X74j9pUGRTpUHCQf6/s6bEQw
            4xIb9IQKrXbPOILbDd0oPP/zTTrL+a3RBovTwP9O3KRnAgMBAAGjITAfMB0GA1Ud
            DgQWBBTvqwNjmkfT4hK053P/2z7lD7vf6zANBgkqhkiG9w0BAQsFAAOCAQEAOaMY
            +y0arl+LDVuIg0Fo35iA6kVANtB5WD9rEwtT3jVmmPHkJlpUApO5F8zko/9VmQg4
            CH/do0OTFBgOWEHLUqT9U3qNfzU31jcr0JgEpsWQ/CpTN9ZdgprHLfpzVomcUrvV
            yVRwAXC4HsP4T4tzH49JzQl1uY6BqVpPfBx2AkPJTOkNeD4Y+U/OuYAyndmqSKux
            E4LYZot8cvdKbkP+haHrPFo92mE/EGIivSlV/6lepv4ZXE6L7c9rtLZLslLPrWN+
            Z0yiOcZmb8bZNaEOL4QU9Y9rXdlrqGwMpGZxZsCMH8peU+tYcLB+AoHNbJsBhvnV
            uA5CUYyv7LnkeE09YA==
            -----END CERTIFICATE-----
            """;

    // CN=*.domain.com
    public static final String WILDCARD_CN_ONLY_CERT = """
            -----BEGIN CERTIFICATE-----
            MIIDPTCCAiWgAwIBAgIULwc4yhvzwgwJj1pVTiLjUDVqcMgwDQYJKoZIhvcNAQEL
            BQAwRzELMAkGA1UEBhMCQ1oxDzANBgNVBAcMBlByYWd1ZTEQMA4GA1UECgwHVGVz
            dE9yZzEVMBMGA1UEAwwMKi5kb21haW4uY29tMB4XDTI2MDEyMDExMDM1OFoXDTI3
            MDEyMDExMDM1OFowRzELMAkGA1UEBhMCQ1oxDzANBgNVBAcMBlByYWd1ZTEQMA4G
            A1UECgwHVGVzdE9yZzEVMBMGA1UEAwwMKi5kb21haW4uY29tMIIBIjANBgkqhkiG
            9w0BAQEFAAOCAQ8AMIIBCgKCAQEAu8KJbvyYZmaoGWfA1XnnQNd8e38TvO9xtR0m
            hrXfgKMenFTMX4jcT0akYHYbJ7a1EZ5OtjZ5Fh4p5qMEyfh+FFbQej09Ifc1kI6a
            75O1+4PE/MwudaHXuJm4S15hT077AAsTt4jG+8z7C9m1iBYwX4NRJwMGi5+hsg+g
            oAG12tJQF+vDPZz5CHWOwSc4qcFlvMQ9M0AiFFsIkPRfU4C7hiS5GOY6RnwbGhYB
            WYXvdH5XxAC3ERxMPK2+q2KE3+D7nKqYkn7KFcoHjBEYATG1qTO/GqRSTZWxuqOJ
            +m1P0BqE5LC8Zton2QAl2leBLeJeBNp9U30WsP3kdU0HCXVJ5QIDAQABoyEwHzAd
            BgNVHQ4EFgQUircnwfdRnA/MaL3maU9/5s5IpwIwDQYJKoZIhvcNAQELBQADggEB
            AJnJwjlJQEPsJm7Ceft6LOwv6ELaJiQPc1pCYbQ8PZABcA+CffgvDwFlSiTcZHH9
            AJcNqA5p/VZtAqNFib+9SPqmUM9ofl9h3GjEePznF9SQrz0Nh2zZDvI6MfZoLCz0
            D8x5qpNfKugwGRle7WSZWMYwcbRjI+wd7Smxgz51SBptbgxSfaWbOoMx+EInBBkE
            VvkexV12ZspNCLO+X9PyIW3kbHzrSkV0Kv2g2Km3QpL2WDplv53PDzbbm6B4s3mh
            D3naokaGFlyRuTdJIBxHmS/NkINdgcPfVL8xLiJaa76sgEk83R/2EbAf73xXTMuR
            XjPTydlPhfL1Fm9WnXMlvTY=
            -----END CERTIFICATE-----
            """;

    // CN=domain.com, SAN=DNS:domain.com
    public static final String SINGLE_SAN_CERT = """
            -----BEGIN CERTIFICATE-----
            MIIDUDCCAjigAwIBAgIUKp4M4EQM2eO8dim6t36MHnotOcMwDQYJKoZIhvcNAQEL
            BQAwRTELMAkGA1UEBhMCQ1oxDzANBgNVBAcMBlByYWd1ZTEQMA4GA1UECgwHVGVz
            dE9yZzETMBEGA1UEAwwKZG9tYWluLmNvbTAeFw0yNjAxMjIxMjE3MDJaFw0yNzAx
            MjIxMjE3MDJaMEUxCzAJBgNVBAYTAkNaMQ8wDQYDVQQHDAZQcmFndWUxEDAOBgNV
            BAoMB1Rlc3RPcmcxEzARBgNVBAMMCmRvbWFpbi5jb20wggEiMA0GCSqGSIb3DQEB
            AQUAA4IBDwAwggEKAoIBAQDRCqK0yvRFcbX1jaF8PE3Sfi7S6Mnbw8K1/pROyVp1
            ZFLmQ//Gcc7+apMnVlqFuWJ8d7g0pCi7rL0KjjM+ssJ+YkaKw48iH7EIYZO87R08
            MSokdfBDAuRCjAzQOdZPfkNj4m65MddA+e3ZxYYCpHD7eudoGC6GpDCOC9w74114
            Vja5GaGS+ZqwtBNIod7f4z68zmIAVfVja0/IVbhxkQmWsLDmxd8QuO6ebCTOrb+R
            zH6cPtNV2tV2EeFELdcIMC9d+65800PLm8UL1lx1PCPEPqpJBlm6FTdrzKRo51/4
            NMg4PmNhBoAOiSTpPPgZoCEN67FLe+YVRxulNGT6DkChAgMBAAGjODA2MBUGA1Ud
            EQQOMAyCCmRvbWFpbi5jb20wHQYDVR0OBBYEFKfFHZcN/FEDZhtkg7Rr49sswbDu
            MA0GCSqGSIb3DQEBCwUAA4IBAQBfdwNJIUY8dKAoAVASdbtMImvCII32QwlM4MsW
            XoovMZxCj+jqwIMb+NhaWnDBzrVLmDKjxDNUZSFm4QvrSngx8HuyIcL0RdIhbqSS
            N3RJ1qKT6icrkHw91+DZCtbzLOxjlM5KJWx4ZG/gHdadweuvkWx4rg0KD5pci0qx
            JaS50azDLGgylh/Ro2CCJxH9thuTFF1WVEQzo4R4NsTn3mYMjLBFT5S2O8Ka8xwx
            Buh7JktXwoenzwAU7SAyXAwXIi5qRVNVF/AaMvMgxuDVeiNLaZiaSaYNEA/Ue4I9
            gW8AomqECK18S5lF4fDGRmsgF6ML+L4TRPsObKP+3yCjPPPk
            -----END CERTIFICATE-----
            """;

    // CN=domain1.com, SAN=DNS:domain1.com,DNS=domain2.com,DNS=domain3.com
    public static final String MULTIPLE_SAN_CERT = """
            -----BEGIN CERTIFICATE-----
            MIIDbTCCAlWgAwIBAgIUAt77jznl6e4Nq5TeUndFQDiyxp0wDQYJKoZIhvcNAQEL
            BQAwRjELMAkGA1UEBhMCQ1oxDzANBgNVBAcMBlByYWd1ZTEQMA4GA1UECgwHVGVz
            dE9yZzEUMBIGA1UEAwwLZG9tYWluMS5jb20wHhcNMjYwMTIyMTIxOTAwWhcNMjcw
            MTIyMTIxOTAwWjBGMQswCQYDVQQGEwJDWjEPMA0GA1UEBwwGUHJhZ3VlMRAwDgYD
            VQQKDAdUZXN0T3JnMRQwEgYDVQQDDAtkb21haW4xLmNvbTCCASIwDQYJKoZIhvcN
            AQEBBQADggEPADCCAQoCggEBAM9QlwvmBDQc9LiYpTdBPHPAIRVAwTewaGEWYERz
            +ttuANoST5hhoAotpqigchGfnAoxrauHdq6F7HuLdpK5f0kDBqbZfZcXoFl3xDYD
            yzzk3daCzHE7oBMK+s1ATxKqNxkR3hfmo7La28u1u1I6pMXjd4hpjsSERDlFLrUq
            zi1XUX8+H8CGWd9AJALntjfY7PNQc/uwEddf42u0y/zJ2tmWM8yKJBCm3bLwlqgF
            fUA5FRSnaUyDXO3tLJ/Le0QjX65BXLFw5fdK/sWyVZe31iIb/KbmcdoB8w2L7vB0
            Ulbmm9AukJWBcY1hbYZ6LzopytarUUn5BaFoWcDTrzBZFkMCAwEAAaNTMFEwMAYD
            VR0RBCkwJ4ILZG9tYWluMS5jb22CC2RvbWFpbjIuY29tggtkb21haW4zLmNvbTAd
            BgNVHQ4EFgQUgh8Kj5LnspXL6PEduMmDxtPeqacwDQYJKoZIhvcNAQELBQADggEB
            AChlHT+AYAqs9CyTWMV4PmVEkDuNZRkmpSk2hMj/C15umDDSuEUnKtscNI9l2otd
            tIsroyACxDlDKj4hYPXP5dABjzoPR0CG869r1IB1jIM8q9k5h6NNmyo/NcbjkwuO
            z1JGIQ309yTwayd6aRFW/KM4LaYP3j6uhXmEy8N4c7A9CnwmMi7SGCk9x0f1Itb9
            TSUS99fRNJXH+L2pnKK5Wu6eeV3EDzhMGObOnpoafzMEUaYpOcyB77eg3o4j4Kis
            Ud6eEJy6DmoAimf6kHSz7IMwgabITg89QAb4Jwl+W+FJvvwMTzgj4xWTnjMxQcQ4
            8/aprIlvFWC3G2iiAuC2ypQ=
            -----END CERTIFICATE-----
            """;

    // CN=*.domain.com, SAN=DNS:*.domain.com
    public static final String WILDCARD_SINGLE_SAN_CERT = """
            -----BEGIN CERTIFICATE-----
            MIIDVjCCAj6gAwIBAgIUKeTsVClsUv6OxKHcykqgj34RvGcwDQYJKoZIhvcNAQEL
            BQAwRzELMAkGA1UEBhMCQ1oxDzANBgNVBAcMBlByYWd1ZTEQMA4GA1UECgwHVGVz
            dE9yZzEVMBMGA1UEAwwMKi5kb21haW4uY29tMB4XDTI2MDEyMjEyMjA1NVoXDTI3
            MDEyMjEyMjA1NVowRzELMAkGA1UEBhMCQ1oxDzANBgNVBAcMBlByYWd1ZTEQMA4G
            A1UECgwHVGVzdE9yZzEVMBMGA1UEAwwMKi5kb21haW4uY29tMIIBIjANBgkqhkiG
            9w0BAQEFAAOCAQ8AMIIBCgKCAQEApYgifUcZLhOIoVA0A9bCU++96QwVLl/hktpx
            S7nxciz9jqxIwi9pNlMANmvuV+qALZVB/p1T6HhkkKvXYUwzJBXEQKhqwcQCPsj8
            yTy8Dgx3lgi6l+u6VNNvYenycMrXMLodSY5jlSyg/ZC/rYW831tMsG7LymyG1JM9
            WDANIaz2D3pBB3kkbQaBvQ1SQ2ZkN5WqG4pT03YKxbubetEHhGo6kR6VRZrJ7L0O
            aru4wMmLAYOU2F2F8vaN4fIL5D8euULNT1e78suqsTVF7S7xODBMjMfxEpNMNpnf
            aWEt2ED39jK/zefokXn4P4ogHKhp/wZ6WPWJjjOFzjjtbPVYFwIDAQABozowODAX
            BgNVHREEEDAOggwqLmRvbWFpbi5jb20wHQYDVR0OBBYEFIb7kZVJtbPuHWtxPSTL
            zJ52BdmkMA0GCSqGSIb3DQEBCwUAA4IBAQB5ZzhP5C3sJH+4Kkl1yFcF1J8bhEzO
            ifHNE7v1ntKRmWH29E41yQcfTjMeAL7sTpkyyRHPXKlhCkmmE66LXufSOMjK3CjL
            lP13Gja12JhiEMzuGGR6EYNu2kAXyU3fsWEkWqJrNFzMbyqgbcltTn+6M4cNrZpi
            7zYHAP9Y5u3rJGhNrzTjONWmcIkY/pOmp+Op+wv48GIbck5TNmCrlv/P4CGcBAnX
            Hauml6mkuEVX4UV1q/igfj1v6FBRlwFRpifoZKHK65Yxr2OGD8KU/sZCyx7l6Jn8
            QGm90pCSWNdjpeFE9mA5RSorC/JWhZQrco2gvXRaH2I66BHlOzX71wqZ
            -----END CERTIFICATE-----
            """;

    // CN=*.domain1.com, SAN=DNS:*.domain1.com,DNS=*.domain2.com,DNS=domain3.com
    public static final String WILDCARD_MULTIPLE_SAN_CERT = """
            -----BEGIN CERTIFICATE-----
            MIIDdTCCAl2gAwIBAgIUazd6OlZ2PmDr1HicdqvnZCZYyNowDQYJKoZIhvcNAQEL
            BQAwSDELMAkGA1UEBhMCQ1oxDzANBgNVBAcMBlByYWd1ZTEQMA4GA1UECgwHVGVz
            dE9yZzEWMBQGA1UEAwwNKi5kb21haW4xLmNvbTAeFw0yNjAxMjIxMjI2NDFaFw0y
            NzAxMjIxMjI2NDFaMEgxCzAJBgNVBAYTAkNaMQ8wDQYDVQQHDAZQcmFndWUxEDAO
            BgNVBAoMB1Rlc3RPcmcxFjAUBgNVBAMMDSouZG9tYWluMS5jb20wggEiMA0GCSqG
            SIb3DQEBAQUAA4IBDwAwggEKAoIBAQDADh6/+FpmrtAdTKOfF4XjSo/41ZYD38Tv
            VK1wylGoznVMVjQ50MtLIawSgNrw2HzxRZBoWsATB8YERQfbSG/8p25o1exzi4U+
            ITvr9WWfPGif9Y5wcmFnQFVNw1ctmYkpfPDEYkbIXn1KRkT0+dRNAOpia2176pJp
            fQ6G2/lE0Xd+tsPBitR2UDCETvfQuB6LzE6cA9QAzP46C8Z4d8Y746IYkDsAtu3G
            pMnlysJaSc6PkSbBfLSasoSs5ZeXYmoPFUxacXgcwWBCCbipg3Kcala2BWl6JJAc
            9U7WjkyJvGaxYGod18BuTmHi6CjVxP72yh1l/X1tqFrgKtN5vpkHAgMBAAGjVzBV
            MDQGA1UdEQQtMCuCDSouZG9tYWluMS5jb22CDSouZG9tYWluMi5jb22CC2RvbWFp
            bjMuY29tMB0GA1UdDgQWBBQhKMDdB3uZHY5xLihc9dJlKBzb1zANBgkqhkiG9w0B
            AQsFAAOCAQEAtf3s0zFcsimdXEPy/dRa4+psl/P9cx+tGjORk6Lgsz7dL1CrJYeV
            4r4QiynhKBdWv7gnqVrEE73lKM47ijboXlHhpvNyYxShd2LeiShBFH2ZVRuBJiqI
            NtG4v602Yh5/jRQWK33NAe/Gm5Qdqap5w//C7lAVppzzDQZ7rqkUbOtnQax4m7Ax
            APfV+7ByM/+UZSorIisV6BF3jjXIJABxBNy+nEFT8p3CBJ5VWbdn4B4y9ezwh/S5
            M309t3aLGDdHAN15807+CQNUe1fpUNO7WeV+mpCG763PcA4X95UnYMw7xJvM9JEY
            hPHCzZhATFOgkP28geyBNTGMj5uAgWtBJw==
            -----END CERTIFICATE-----
            """;

    // SAN=DNS:domain.com
    public static final String SAN_ONLY_CERT = """
            -----BEGIN CERTIFICATE-----
            MIIDJjCCAg6gAwIBAgIUZKA6gKoi8XlM57+NPnmPV8ffgDEwDQYJKoZIhvcNAQEL
            BQAwMDELMAkGA1UEBhMCQ1oxDzANBgNVBAcMBlByYWd1ZTEQMA4GA1UECgwHVGVz
            dE9yZzAeFw0yNjAxMjIxMjI3NDZaFw0yNzAxMjIxMjI3NDZaMDAxCzAJBgNVBAYT
            AkNaMQ8wDQYDVQQHDAZQcmFndWUxEDAOBgNVBAoMB1Rlc3RPcmcwggEiMA0GCSqG
            SIb3DQEBAQUAA4IBDwAwggEKAoIBAQC4bcRQgZk6QfXUHtgBB5+/v/x92q2Cr9g5
            zVbCfZK51Ns4R2ZowGNbsvEToFB7oicXJpQyR+mSMHjTn+Sfh8WukqvW/VlRD7h6
            cGbKwREgcFcZ/JU3S5jkExdFeMJWK9Sb/eRsMIr7cbSbS/a9BIrGRpD8QVoUL0W/
            Wzx2Ka6SAO7K5h/0lEtb+rM20xt0MsbjCzVE69bz+z36he4kLZN+70k1LvGri9gX
            6u9/i/af7sRpEXiNl9e5OS2QgIZXap/tecmi9s9wTXWGMF7TGnyxrlcV8gY/G17J
            7Y96qFF21y9n04sRoNoy3f+7DOa7TqdTCA44KiH3LCTtWdxsYHeLAgMBAAGjODA2
            MBUGA1UdEQQOMAyCCmRvbWFpbi5jb20wHQYDVR0OBBYEFIfFYIU6A2A1FNYqd5x3
            X0T4TFNxMA0GCSqGSIb3DQEBCwUAA4IBAQAAXgUgcLhPAQgwy1QA9Kh619hU/80g
            J+1zmyC6Lfzn+3EQeO73VtEuAvIK9XC9+8i4doKMDJHgS/LL+Bf90oUyLJnmtLLP
            KVM5XNrnhKdjhOlHwT5zHlzzEA6JkLYzhsFaKEvvHIv1GokSfKYzxLaMaLl94vgl
            wpS7amOQPBlRxjV5PVfN2GV/Yx3vpSVOPcWNRe2A9nv7gpPDvXy1v5hAQyv1W5Vh
            A9v8OH6gM867bJWYYTx9KnzlthQaqC2z2S0TuNNPLsroPTRqJH2h0s52rbq9YRjV
            +QQ/1XSGMEXZyhKUKctrmq7F++x8rBk9UJ1ZfbtJ93ftGvgT9VZqJdpi
            -----END CERTIFICATE-----
            """;
}
