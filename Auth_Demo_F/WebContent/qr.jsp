<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>QR Code</title>
</head>
<body>
    <h1>QR Code for Authentication</h1>
    <p>Scan this QR code using your authenticator app:</p>
    <img src="qr-code?data=${totpUrl}" alt="QR Code">
    <p>If you cannot scan the QR code, use this key:</p>
    <p><strong>Secret Key:</strong> ${secretKey}</p>
</body>
</html>
