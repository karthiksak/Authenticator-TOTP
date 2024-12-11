package com.example.controller;


import com.example.controller.TOTPUtil;

import javax.imageio.ImageIO;
import javax.servlet.*;
import javax.servlet.http.*;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

public class QRCodeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String secretKey = request.getParameter("secretKey");
        String username = request.getParameter("username");

        try {
            // Generate the QR code for the user using the secret key
            BufferedImage qrImage = TOTPUtil.generateQRCode(secretKey, username, "BankApp");
            
            // Set response content type as PNG for the QR code
            response.setContentType("image/png");
            
            // Output the QR code image to the response
            try (OutputStream outputStream = response.getOutputStream()) {
                ImageIO.write(qrImage, "PNG", outputStream);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error generating QR code.");
        }
    }
}
