package fi.foyt.hibernate.gae;

import java.io.IOException;
import java.io.PrintWriter;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import fi.foyt.hibernate.gae.persistence.Note;
import fi.foyt.hibernate.gae.utils.HibernateUtils;

@SuppressWarnings("serial")
public class TestServlet extends HttpServlet {
  
  protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    EntityManager entityManager = entityManagerFactory.createEntityManager();
    
    try {
      entityManager.getTransaction().begin();
      
      if ("POST".equals(req.getMethod())) {
        String text = req.getParameter("note");
        Note note = new Note();
        note.setText(text);
        entityManager.persist(note);
      }
      
      req.setAttribute("notes", entityManager.createQuery("from Note").getResultList());
      
      try {
        req.getRequestDispatcher("/notes.jsp").include(req, resp);
      } catch (ServletException e) {
        e.printStackTrace(new PrintWriter(resp.getOutputStream()));
      }
      resp.setContentType("text/html");
      
      entityManager.getTransaction().commit();
    } finally {
      if (entityManager.getTransaction().isActive())
        entityManager.getTransaction().rollback();
    }
  }
  
  private static EntityManagerFactory entityManagerFactory; 
  
  static {
    // Bootstrapping
    entityManagerFactory = HibernateUtils.getEntityManagerFactory();
  }
}
