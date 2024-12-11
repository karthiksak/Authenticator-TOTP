package com.example.controller;

import com.example.controller.TOTPUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class RegisterServlet extends HttpServlet {

	private JdbcTemplate jdbcTemplate;

	@Override
	public void init() throws ServletException {
		// Configure the DataSource manually using the provided DB credentials
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("oracle.jdbc.OracleDriver");
		dataSource.setUrl("jdbc:oracle:thin:@10.22.66.75:1522/recondev");
		dataSource.setUsername("junit_user_pg");
		dataSource.setPassword("junit_user_pg");

		// Initialize JdbcTemplate with the configured DataSource
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// Forward to the registration page
		request.getRequestDispatcher("/register.jsp")
				.forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String username = request.getParameter("username");

		try {
			// Generate a secret key for the user
			String secretKey = TOTPUtil.generateSecretKey();

			// Insert the user and secret key into the database
			String sql = "INSERT INTO users (username, secret_key) VALUES (?, ?)";
			jdbcTemplate.update(sql, username, secretKey);

			// Generate TOTP-compliant URL
			String issuer = "BankApp"; // Replace with your app name
			String accountName = URLEncoder.encode(username,
					StandardCharsets.UTF_8.name());

			String totpUrl = String.format(
					"otpauth://totp/%s:%s?secret=%s&issuer=%s", issuer,
					accountName, secretKey,
					URLEncoder.encode(issuer, StandardCharsets.UTF_8.name()));

			// Set attributes for the username, secretKey, and TOTP URL
			request.setAttribute("secretKey", secretKey);
			request.setAttribute("username", username);
			request.setAttribute("totpUrl", totpUrl);

			// Forward to the QR code page
			request.getRequestDispatcher("/qr.jsp").forward(request, response);

		} catch (Exception e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Error during registration.");
		}
	}
}
