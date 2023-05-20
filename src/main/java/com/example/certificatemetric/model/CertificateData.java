package com.example.certificatemetric.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.security.cert.Certificate;

@Data
@Accessors(chain = true)
public class CertificateData {

    private String name;
    private Certificate certificate;
}
