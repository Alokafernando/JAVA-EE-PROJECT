package lk.ijse.pos;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.annotation.WebServlet;
import org.apache.commons.dbcp2.BasicDataSource;

@WebListener
public class DataSource implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        ds.setUrl("jdbc:mysql://localhost:3306/ems71");
        ds.setUsername("root");
        ds.setPassword("1234");
        ds.setInitialSize(5);
        ds.setMaxTotal(10);

        ServletContext context = sce.getServletContext();
        context.setAttribute("dataSource", ds);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        //close the datasource

        try{
            ServletContext context = sce.getServletContext();
            BasicDataSource ds = (BasicDataSource)context.getAttribute("dataSource");
            ds.close();
        } catch (Exception e){
            throw  new RuntimeException(e);
        }
    }
}
