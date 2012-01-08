<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Notes</title>
</head>
<body>

  <h1>Leave a note</h1>
  
  <form action="test" method="POST">
    <div>
      <textarea name="note">Insert note content</textarea>
    </div>
    <div>
      <input type="submit" value="Save"/>
    </div>
  </form>
  
  <h2>Notes</h2>
  
  <%
    @SuppressWarnings("unchecked")
    java.util.List<fi.foyt.hibernate.gae.persistence.Note> notes = (java.util.List<fi.foyt.hibernate.gae.persistence.Note>) request.getAttribute("notes");
    for (fi.foyt.hibernate.gae.persistence.Note note : notes) {
      out.println("<p>" + note.getText() + "</p><hr/>");
    }
  %>

</body>
</html>