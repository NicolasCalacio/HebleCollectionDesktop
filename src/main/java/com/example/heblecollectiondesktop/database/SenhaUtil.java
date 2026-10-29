package com.example.heblecollectiondesktop.database;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public final class SenhaUtil {

    private static final String PREFIXO = "pbkdf2$";
    private static final String ALGORITMO = "PBKDF2WithHmacSHA256";
    private static final int ITERACOES = 600_000;
    private static final int TAMANHO_SALT_BYTES = 16;
    private static final int TAMANHO_HASH_BITS = 256;
    private static final SecureRandom RANDOM = new SecureRandom();

    private SenhaUtil() {
    }

    public static boolean estaEmHash(String valor) {
        return valor != null && valor.startsWith(PREFIXO);
    }

    public static String gerarHash(String senha) {
        byte[] salt = new byte[TAMANHO_SALT_BYTES];
        RANDOM.nextBytes(salt);
        byte[] hash = derivar(senha.toCharArray(), salt, ITERACOES);
        return PREFIXO + ITERACOES + "$"
                + Base64.getEncoder().encodeToString(salt) + "$"
                + Base64.getEncoder().encodeToString(hash);
    }

    public static boolean verificar(String senhaDigitada, String armazenado) {
        if (senhaDigitada == null || armazenado == null) {
            return false;
        }

        if (!estaEmHash(armazenado)) {
            return MessageDigest.isEqual(
                    senhaDigitada.getBytes(StandardCharsets.UTF_8),
                    armazenado.getBytes(StandardCharsets.UTF_8));
        }

        String[] partes = armazenado.split("\\$");
        if (partes.length != 4) {
            return false;
        }

        try {
            int iteracoes = Integer.parseInt(partes[1]);
            byte[] salt = Base64.getDecoder().decode(partes[2]);
            byte[] esperado = Base64.getDecoder().decode(partes[3]);
            byte[] calculado = derivar(senhaDigitada.toCharArray(), salt, iteracoes);
            return MessageDigest.isEqual(esperado, calculado);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static String prepararParaGravar(String senha) {
        if (senha == null) {
            return null;
        }
        return estaEmHash(senha) ? senha : gerarHash(senha);
    }

    private static byte[] derivar(char[] senha, byte[] salt, int iteracoes) {
        try {
            PBEKeySpec spec = new PBEKeySpec(senha, salt, iteracoes, TAMANHO_HASH_BITS);
            return SecretKeyFactory.getInstance(ALGORITMO).generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Não foi possível calcular o hash da senha", e);
        }
    }
}