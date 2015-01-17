/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import entities.InvoiceFile;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import jpa.InvoiceFileJpaController;

/**
 *
 * @author asajenko
 */
@WebServlet(name = "DownloadFile", urlPatterns = {"/system/downloadFile.xhtml"})
public class DownloadFile extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        ServletOutputStream op = response.getOutputStream();
        String id = request.getParameter("id");
        InvoiceFileJpaController pjc = new InvoiceFileJpaController();
        InvoiceFile plik = pjc.findInvoiceFile(Integer.parseInt(id));
        File file = new File(plik.getPath());
        String fileName = plik.getName();
        if (plik.getName().endsWith(".msg")) {
            fileName = fileName.trim().replace("ą", "a")
                    .replace("ć", "c")
                    .replace("ę", "e")
                    .replace("ł", "l")
                    .replace("ń", "n")
                    .replace("ó", "o")
                    .replace("ś", "s")
                    .replace("ż", "z")
                    .replace("ź", "z")
                    .replace("Ą", "A")
                    .replace("Ć", "C")
                    .replace("Ę", "E")
                    .replace("Ł", "L")
                    .replace("Ń", "N")
                    .replace("Ó", "O")
                    .replace("Ś", "S")
                    .replace("Ż", "Z")
                    .replace("Ź", "Z")
                    .replace(" ", "_");
            response.setContentType("application/vnd.ms-outlook" + ";charset=utf-8");
            response.setHeader("Content-disposition", "attachment; filename=" + fileName);
        } else {
            response.setHeader("Content-disposition", "attachment; filename=" + fileName);
        }
        FileInputStream in = new FileInputStream(file);
        byte[] buffer = new byte[4096];
        int length;
        while ((length = in.read(buffer)) > 0) {
            op.write(buffer, 0, length);
        }
        in.close();
        op.flush();
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
