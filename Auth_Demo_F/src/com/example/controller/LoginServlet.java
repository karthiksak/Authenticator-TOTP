package com.example.controller;

import com.example.controller.TOTPUtil;

import oracle.jdbc.pool.OracleDataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.servlet.*;
import javax.servlet.http.*;

import java.io.IOException;

public class LoginServlet extends HttpServlet {

	// @Autowired
	// private JdbcTemplate jdbcTemplate;

	private static final String DB_URL = "jdbc:oracle:thin:@10.22.66.75:1522/recondev";
	private static final String DB_USERNAME = "junit_user_pg";
	private static final String DB_PASSWORD = "junit_user_pg";

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// Forward to the login page
		request.getRequestDispatcher("/WEB-INF/login.jsp").forward(request,
				response);
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String username = request.getParameter("username");
		int otp = Integer.parseInt(request.getParameter("otp"));

		try {
			// Fetch the user's secret key from the database
			String sql = "SELECT secret_key FROM users WHERE username = ?";

			OracleDataSource dataSource = new OracleDataSource();

			// Set database connection parameters
			dataSource.setURL(DB_URL);
			dataSource.setUser(DB_USERNAME);
			dataSource.setPassword(DB_PASSWORD);

			JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

			String secretKey = jdbcTemplate.queryForObject(sql,
					new Object[] { username }, String.class);

			// Validate the OTP using the secret key
			boolean isValid = TOTPUtil.validateTOTP(secretKey, otp);

			// Redirect to validate.jsp with `isValid` as a query parameter
			response.sendRedirect("validate.jsp?isValid=" + isValid);

		} catch (Exception e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Error during login.");
		}
	}
}
