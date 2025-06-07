package lk.ijse.pos;

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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@WebServlet("/api/v1/employee")
public class EmployeeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        resp.setContentType("application/json");

        ServletContext sc = req.getServletContext();
        BasicDataSource dataSource = (BasicDataSource) sc.getAttribute("dataSource");

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement pstm = connection.prepareStatement("select * from employee");
            ResultSet rs = pstm.executeQuery();

            List<Map<String, String>> employeeList = new ArrayList<>();

            while (rs.next()) {

                Map<String, String> emp = Map.of(
                        "eid", rs.getString("eid"),
                        "ename", rs.getString("ename"),
                        "eaddrss", rs.getString("eaddress"),
                        "eemail", rs.getString("eemail")
                );
                employeeList.add(emp);

            }

            PrintWriter out = resp.getWriter();
            resp.setStatus(HttpServletResponse.SC_OK);
            mapper.writeValue(out, Map.of(
                    "code", "200",
                    "status", "success",
                    "data", employeeList
            ));
        connection.close();
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            mapper.writeValue(resp.getWriter(), Map.of(
                    "code", "500",
                    "status", "error",
                    "message", "Internal server error!"
            ));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            //read json obj and write in to hashmap
            ObjectMapper mapper = new ObjectMapper();
            Map<String,String> employee = mapper.readValue(req.getInputStream(), Map.class);

            //save user in database
            ServletContext sc = req.getServletContext();
            BasicDataSource dataSource= (BasicDataSource) sc.getAttribute("dataSource");

            Connection connection=dataSource.getConnection();
            PreparedStatement pstm=
                    connection.prepareStatement("INSERT INTO employee" +
                            "(eid,ename,eaddress,eemail) Values (?,?,?,?)");

            pstm.setString(1, UUID.randomUUID().toString());
            pstm.setString(2, employee.get("ename"));
            pstm.setString(3,employee.get("eaddress"));
            pstm.setString(4,employee.get("eemail"));

            int executed=pstm.executeUpdate();
            PrintWriter out=resp.getWriter();
            resp.setContentType("application/json");
            if(executed>0){
                resp.setStatus(HttpServletResponse.SC_CREATED);
                mapper.writeValue(out,Map.of(
                        "code","201",
                        "status","success",
                        "message","Employee saved successfully"
                ));
            }else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                mapper.writeValue(out,Map.of(
                        "code","400",
                        "status","error",
                        "message","Bad Request"
                ));
            }
            connection.close();
        } catch (Exception e) {
            ObjectMapper mapper = new ObjectMapper();
            PrintWriter out=resp.getWriter();
            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            mapper.writeValue(out,Map.of(
                    "code","500",
                    "status","error",
                    "message","Internal Server Error"
            ));
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String,String> employee = mapper.readValue(req.getInputStream(), Map.class);

        ServletContext sc = req.getServletContext();
        BasicDataSource dataSource= (BasicDataSource) sc.getAttribute("dataSource");

        try {
            Connection connection=dataSource.getConnection();
            PreparedStatement pstm = connection.prepareStatement(
                    "update employee set ename=?,eaddress=?,eemail=? where eid=?"
            );

            pstm.setString(1, employee.get("ename"));
            pstm.setString(2,employee.get("eaddress"));
            pstm.setString(3,employee.get("eemail"));
            pstm.setString(4, employee.get("eid"));

            int executed = pstm.executeUpdate();
            PrintWriter out=resp.getWriter();
            if(executed>0){
                resp.setStatus(HttpServletResponse.SC_CREATED);
                mapper.writeValue(out,Map.of(
                        "code","201",
                        "status","success",
                        "message","Employee updated successfully"
                ));
            }else{
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                mapper.writeValue(out,Map.of(
                        "code","400",
                        "status","error",
                        "message","Bad Request"
                ));
            }
            connection.close();
        } catch (SQLException e) {
            PrintWriter out=resp.getWriter();
            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            mapper.writeValue(out,Map.of(
                    "code","500",
                    "status","error",
                    "message","Internal Server Error"
            ));
            throw new RuntimeException(e);
        }

    }

}