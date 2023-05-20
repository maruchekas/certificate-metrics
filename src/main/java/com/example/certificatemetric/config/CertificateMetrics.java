package com.example.certificatemetric.config;

import com.example.certificatemetric.model.CertificateData;
import com.example.certificatemetric.service.CertificateService;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.MeterBinder;
import org.springframework.stereotype.Component;

import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Component
public class CertificateMetrics implements MeterBinder {
    private final CertificateService certificateService;

    public CertificateMetrics(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @Override
    public void bindTo(MeterRegistry meterRegistry) {
        List<CertificateData> certificates = certificateService.getCertificates();
        for (CertificateData cert : certificates) {
            X509Certificate x509cert = (X509Certificate) cert.getCertificate();
            String certName = cert.getName() + ": " + x509cert.getSubjectDN().getName();
            long daysToExpiration = getDaysToExpiration(x509cert);

//            meterRegistry.gauge("certificate.life_metrics",
//                    Tags.of("certs info", certName + " : " + daysToExpiration),
//                    daysToExpiration);
            Gauge.builder("certificate.life_metrics", cert.getCertificate(), c -> daysToExpiration)
                    .tag("certs info", certName + " Days to expire: " + daysToExpiration)
                    .description("Days to expire for the certificate")
                    .register(meterRegistry);
        }
    }

    private long getDaysToExpiration(X509Certificate x509cert) {
        Instant now = Instant.now();
        Instant expiration = x509cert.getNotAfter().toInstant();
        Duration duration = Duration.between(now, expiration);
        return duration.toDays();
    }
}
