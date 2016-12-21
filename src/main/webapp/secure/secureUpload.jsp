<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<!-- taglib does not work in Jetty -->
<html>
<head>
<title>Upload Payment Report Request Page</title>
</head>
<body>
    This is a secure page. <br/> <br/>
    <form method="POST" action="uploadPaymentReport" enctype="multipart/form-data">
        Payment report to upload: <input type="file" name="file"><br /> 
        <br /> 
        <input type="submit" value="Upload"> Press here to upload the payment report!
    </form>

</body>
</html>