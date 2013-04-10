<%@ page import="com.gwtplatform.carstore.shared.domain.Car" %>
<%@ page import="java.util.List" %>
<%
    List<Car> carList = (List<Car>)request.getAttribute("cars");
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
                <% for (Car car : carList) { %>
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
