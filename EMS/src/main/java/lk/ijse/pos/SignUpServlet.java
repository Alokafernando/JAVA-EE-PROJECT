package lk.ijse.pos;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.dbcp2.BasicDataSource;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

@WebServlet("/api/v1/signup")
public class SignUpServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {
            ObjectMapper mapper = new ObjectMapper(); ///////read json object
            Map<String, String> user = mapper.readValue(req.getInputStream(), Map.class);

            ServletContext sc = getServletContext();
            BasicDataSource dataSource = (BasicDataSource) sc.getAttribute("dataSource");
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement("insert into user" +
                    "(uid, uname, uemail, upassword) values (?, ?, ?, ?)");

            statement.setString(1, UUID.randomUUID().toString());
//            statement.setString(1, user.get("uid"));
            statement.setString(2, user.get("uname"));  ////set values
            statement.setString(3, user.get("uemail"));
            statement.setString(4, user.get("upassword"));
            int rows = statement.executeUpdate();

            PrintWriter out = resp.getWriter();
            resp.setContentType("application/json");

            if (rows > 0) {
                resp.setStatus(HttpServletResponse.SC_CREATED);
                mapper.writeValue(out, Map.of(
                        "code", "201",
                        "status", "success",
                        "message", "User Sign up Successful"));
            }else{
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                mapper.writeValue(out, Map.of(
                        "code", "400",
                        "status", "error",
                        "message", "User Sign up Failed"));
            }

            connection.close();
        } catch (SQLException e) {
            ObjectMapper mapper = new ObjectMapper();
            PrintWriter out = resp.getWriter();
            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            mapper.writeValue(out, Map.of(
                    "code", "500",
                    "status", "error",
                    "message", "Internal Server Error"
            ));
        }

    }
}
