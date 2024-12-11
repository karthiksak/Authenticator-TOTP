package com.example.controller;

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;

import org.apache.commons.codec.binary.Base32;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import java.awt.image.BufferedImage;
import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class TOTPUtil {

	public static String generateSecretKey() throws Exception {
		KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA1");
		keyGenerator.init(160);
		SecretKey secretKey = keyGenerator.generateKey();
		Base32 base32 = new Base32();
		return base32.encodeToString(secretKey.getEncoded());
	}

	public static BufferedImage generateQRCode(String secret, String user,
			String issuer) throws Exception {
		String uri = String.format("otpauth://totp/%s?secret=%s&issuer=%s",
				user, secret, issuer);
		QRCodeWriter qrCodeWriter = new QRCodeWriter();
		return MatrixToImageWriter.toBufferedImage(qrCodeWriter.encode(uri,
				BarcodeFormat.QR_CODE, 300, 300));
	}

	public static boolean validateTOTP(String secret, int otp) throws Exception {
		// Decode the Base32 secret key
		byte[] decodedKey = new Base32().decode(secret);
		Key key = new SecretKeySpec(decodedKey, "HmacSHA1");

		// Create a TOTP generator with a default time step (30 seconds)
		TimeBasedOneTimePasswordGenerator totpGenerator = new TimeBasedOneTimePasswordGenerator();
		Duration timeStep = totpGenerator.getTimeStep();

		// Get the current time in UTC
		Instant currentTime = Instant.now();

		// Define tolerance for ±1 time step
		Instant[] timeWindows = { currentTime.minus(timeStep), // Previous time
																// window
				currentTime, // Current time window
				currentTime.plus(timeStep) // Next time window
		};

		// Validate the OTP against all valid time windows
		for (Instant timeWindow : timeWindows) {
			int generatedOTP = totpGenerator.generateOneTimePassword(key,
					timeWindow);
			System.out.println("Generated OTP for " + timeWindow + ": "
					+ generatedOTP);

			if (generatedOTP == otp) {
				return true; // OTP is valid
			}
		}

		return false; // OTP is invalid
	}

}
