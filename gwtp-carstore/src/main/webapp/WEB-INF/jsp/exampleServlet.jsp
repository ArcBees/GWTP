<%@ page import="java.util.List" %>
<%@ page import="com.gwtplatform.carstore.shared.dto.CarDto" %>
<%
    List<CarDto> carList = (List<CarDto>)request.getAttribute("cars");
%>
<!DOCTYPE html>
<html>
    <head>
        <title>Example Servlet</title>
    </head>
    <body>
        <h3>List of Cars</h3>
        <table>
            <thead>
                <tr>
                    <th>Id</th>
                    <th>Manufacturer</th>
                    <th>Model</th>
                </tr>
            </thead>
            <tbody>
                <% for (CarDto car : carList) { %>
                    <tr>
                        <td><%= car.getId() %></td>
                        <td><%= car.getManufacturer().getName() %></td>
                        <td><%= car.getModel() %></td>
                    </tr>
                <% } %>
            </tbody>
        </table>
    </body>
</html>
