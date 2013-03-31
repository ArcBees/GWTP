package com.gwtplatform.carstore.server.servlet;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.gwtplatform.carstore.server.dao.CarDao;
import com.gwtplatform.carstore.shared.domain.Car;

/**
 * Sample servlet implementation to show how a simple servlet can be written to access
 * the back end data and render a JSP from it.
 */
@Singleton
public class ExampleServlet extends HttpServlet {

    private static final String OUTPUT_JSP = "/WEB-INF/jsp/exampleServlet.jsp";

    private final Logger logger;

    private final CarDao carDao;

    @Inject
    public ExampleServlet(final Logger logger, final CarDao carDao) {
        this.logger = logger;
        this.carDao = carDao;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        try {
            List<Car> cars = this.carDao.getAll();
            request.setAttribute("cars", cars);

            request.getRequestDispatcher(OUTPUT_JSP).forward(request, response);
        } catch (IOException e) {
            if (logger.isLoggable(Level.WARNING)) {
                logger.log(Level.WARNING, "IO exception while executing ExampleServlet: " + e.getMessage(), e);
            }
            throw e;
        } catch (ServletException e) {
            if (logger.isLoggable(Level.WARNING)) {
                logger.log(Level.WARNING, "Servlet exception while executing ExampleServlet: " + e.getMessage(), e);
            }
            throw e;
        } catch (RuntimeException e) {
            if (logger.isLoggable(Level.WARNING)) {
                logger.log(Level.WARNING, "Unexpected exception while executing ExampleServlet: " + e.getMessage(), e);
            }
            throw new ServletException(e);
        }
    }

}
