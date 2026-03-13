package com.cmsr.onebase.module.system.util.oauth2;

import org.springframework.beans.factory.annotation.Value;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class AesCfbCryptoUtil {

	private static final String AES = "AES";
	private static final String TRANSFORMATION = "AES/CFB/NoPadding";

//	@Value("${pig.encrypt.secret-key:tiangong,thanks_}")
	private static final String secretKey = "tiangong,thanks_";

	/**
	 * 加密（Java → 前端可解）
	 */
	public String encrypt(String plainText) {
		try {
			byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);

			SecretKeySpec keySpec = new SecretKeySpec(keyBytes, AES);
			IvParameterSpec ivSpec = new IvParameterSpec(keyBytes);

			Cipher cipher = Cipher.getInstance(TRANSFORMATION);
			cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

			byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

			return Base64.getEncoder().encodeToString(encrypted);
		} catch (Exception e) {
			throw new RuntimeException("AES encrypt error", e);
		}
	}

	/**
	 * 解密（前端 CryptoJS 加密 → Java 解）
	 */
	public static String decrypt(String cipherText) {
		try {
			byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);

			SecretKeySpec keySpec = new SecretKeySpec(keyBytes, AES);
			IvParameterSpec ivSpec = new IvParameterSpec(keyBytes);

			Cipher cipher = Cipher.getInstance(TRANSFORMATION);
			cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

			byte[] decoded = Base64.getDecoder().decode(cipherText);
			byte[] decrypted = cipher.doFinal(decoded);

			return new String(decrypted, StandardCharsets.UTF_8);
		} catch (Exception e) {
			throw new RuntimeException("AES decrypt error", e);
		}
	}


}