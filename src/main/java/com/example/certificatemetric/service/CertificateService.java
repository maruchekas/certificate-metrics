package com.example.certificatemetric.service;

import com.example.certificatemetric.model.CertificateData;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

@Service
public class CertificateService {

    private final String keystorePassword = "changeit";

    public List<CertificateData> getCertificates() {
        String relativeCacertsPath = "/lib/security/cacerts".replace("/", File.separator);
        String filename = System.getProperty("java.home") + relativeCacertsPath;
        List<CertificateData> certificates = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filename)) {
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(fis, keystorePassword.toCharArray());

            Enumeration<String> aliases = ks.aliases();
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                Certificate cert = ks.getCertificate(alias);
                if (cert instanceof X509Certificate) {
                    CertificateData certificateData = new CertificateData()
                            .setName(alias)
                            .setCertificate(cert);
                    certificates.add(certificateData);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error loading certificates.", e);
        }

        return certificates;
    }
}
