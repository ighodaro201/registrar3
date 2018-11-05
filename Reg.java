//----------------------------------------------------------------------
// Reg.java
// Author: Osita Ighodaro Ben Musoke-Lubega
//----------------------------------------------------------------------

import spark.Spark;
import spark.Request;
import spark.Response;
import java.util.ArrayList;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;

public class Reg
{
    private static String index(Request req, Response res)
    {
        String errorMsg = req.queryParams("errorMsg");
        if (errorMsg == null) serrorMsg = "";
                      
        String html = ""; 
        html += "<!DOCTYPE html>";
        html += "<html>";
        html += "<head>";
         html += "<title>Registrar's Office Class Search</title>";
        html += "</head>";
        html += "<body>";
        html += "<h1>Registrar's Office</h1>";
        html += "<h2>Class Search</h2>";
        html += "<hr>";
        html += "<form action=\"index\" method=\"get\">";
        html += "<table>";

        html += "<tbody>";

        html += "<tr>";
        html += "<td>Dept:</td>";
        html += "<td>";
        html += "<input type=\"text\" name=\"dept\">";
        html += "<br>";
        html += "</td>";
        html += "</tr>";

        html += "<tr>";
        html += "<td>Course Num:</td>";
        html += "<td>";
        html += "<input type=\"text\" name=\"coursenum\">";
        html += "<br>";
        html += "</td>";
        html += "</tr>";

        html += "<tr>";
        html += "<td>Area:</td>";
        html += "<td>";
        html += "<input type=\"text\" name=\"area\">";
        html += "<br>";
        html += "</td>";
        html += "</tr>";

        html += "<tr>";
        html += "<td>Title:</td>";
        html += "<td>";
        html += "<input type=\"text\" name=\"title\">";
        html += "<br>";
        html += "</td>";
        html += "</tr>";

        html += "<tr>";
        html += "<td></td>";
        html += "<td>";
        html += "<input type=\"submit\" value=\"Submit\">";
        html += "<br>";
        html += "</td>";
        html += "</tr>";

        html += "</tbody>";
        html += "</table>";
        html += "</form>";
        html += "<hr>";

        html += "<table>";
        html += "<tr>";
        html += "<th> ClassId </th>";
        html += "<th> Dept </th>";
        html += "<th> Num </th>";
        html += "<th> Area </th>";
        html += "<th> Title </th>";
        html += "</tr>";  

        String dept = req.queryParams("dept");
        String coursenum = req.queryParams("coursenum");
        String area = req.queryParams("area");
        String title = req.queryParams("title");

        ArrayList<CourseBasic> courses = null;
        try
        {
            Database database = new Database();
            database.connect();
            courses = database.searchBasic(dept, coursenum, area, title);
            database.disconnect();
        }
        catch (Exception e)
        {
            return e.toString();
        }

        if (courses.size() == 0) html += "";
        else
        {
            for (CourseBasic course : courses)
            {
                html += "<tr>";
                html += "<td>" + "<a href=\"regdetails?classid=" + course.getClassId + "\">" + course.getClassId() + "</a>" + "</td>";
                html += "<td>" + course.getDept() + "</td>";
                html += "<td>" + course.getCourseNum() + "</td>";
                html += "<td>" + course.getArea() + "</td>";
                html += "<td>" + course.getTitle() + "</td>";
                html += "</tr>";  
                html += "<br>";
            }
        }

        html += "</table>";
        html += "</body>";
        html += "</html>";
        return html;    
   }

   private static String courseDetails(Request req, Response res) throws UnsupportedEncodingException
   {
        String classId = req.queryParams("classId");

        String html = ""; 
        html += "<!DOCTYPE html>";
        html += "<html>";
        html += "<head>";
         html += "<title>Registrar's Office Class Search</title>";
        html += "</head>";
        html += "<body>";
        html += "<h1>Registrar's Office</h1>";
        html += "<hr>";
        
        html += "<h2>";
        html += "Class Details (class id " + classId + ")";
        html += "</h2>";
        html += "<br>";

        CourseInfo info = null;

        try
        {
            Database database = new Database();
            database.connect();
            info = database.searchInfo(classId);
            database.disconnect();
        }
        catch (Exception e)
        {
            return e.toString();
        }

        html += "<hr>";
        html += "Click here to do ";
        html += "<a href=\"index\">another class search </a>.";
        html += "<hr>";

        html += "</body>";
        html += "</html>";

   }
   public static void main(String[] args) 
   {
      if (args.length != 1)
      {
         System.err.println("Usage: java Reg port");
         System.exit(1);
      } 

      Spark.port(Integer.parseInt(args[0]));
      
      Spark.get("/", 
         (req, res) -> index(req, res)
      );
      
      Spark.get("/index",
         (req, res) -> index(req, res)
      );

      Spark.get("/regdetails",
         (req, res) -> courseDetails(req, res)
      );
      
   }
}