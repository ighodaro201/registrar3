//----------------------------------------------------------------------
// Database.java
// Author: Bob Dondero
//----------------------------------------------------------------------

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.io.File;

public class Database
{
   private static final String DATABASE_NAME = "reg.sqlite";

   private Connection connection;

   public Database() {}

   public void connect() throws Exception
   {
      File databaseFile = new File(DATABASE_NAME);
      if (! databaseFile.isFile())
         throw new Exception("Database connection failed");
      connection =
         DriverManager.getConnection("jdbc:sqlite:" + DATABASE_NAME);
   }

   public void disconnect() throws Exception
   {
      connection.close();
   }

  /* public ArrayList<CourseBasic> searchBasic(String author) throws Exception
   {
      ArrayList<Book> result = new ArrayList<Book>();
  
      PreparedStatement statement = connection.prepareStatement(
         "select author, title, price from books where author like ?");
      statement.setString(1, author + "%");
      ResultSet resultSet = statement.executeQuery();

      while (resultSet.next())
      {  
         String foundAuthor = resultSet.getString("author");
         String title = resultSet.getString("title");
         double price = resultSet.getDouble("price");
         Book book = new Book(foundAuthor, title, price);
         result.add(book);
      }

      return result;
   } */

   public CourseInfo searchDetails(String classid) throws Exception
   {
        try
        {
            CourseInfo output;
            ArrayList<String> deptList = new ArrayList<String>();
            ArrayList<String> courseNumList = new ArrayList<String>();
            ArrayList<String> profNameList = new ArrayList<String>();

            if (!Pattern.matches("[0-9]*", classid))
            {
                throw new Exception("regdetails: classid is not an integer");
            }

            String stmtStr = "SELECT courses.courseid, days, starttime, endtime, bldg, roomnum, area, title, " +
                "descrip, prereqs " + 
                "FROM courses, classes " +
                "WHERE classes.courseid = courses.courseid " +
                "AND classid = ?";

            String stmtStr2 = "SELECT dept, coursenum FROM crosslistings, classes " + 
                "WHERE crosslistings.courseid = classes.courseid " +
                "AND classid = ? ORDER BY dept, coursenum";

            String stmtStr3 = "SELECT profname FROM profs, coursesprofs, classes " + 
                "WHERE coursesprofs.courseid = classes.courseid " +
                "AND coursesprofs.profid = profs.profid AND classid = ? " + 
                "ORDER BY profname";

            // Create a prepared statement and substitute values.
            PreparedStatement statement = 
                connection.prepareStatement(stmtStr);
            statement.setString(1, classid);

            PreparedStatement statement2 = 
                connection.prepareStatement(stmtStr2);
            statement2.setString(1, classid);

            PreparedStatement statement3 = 
                connection.prepareStatement(stmtStr3);
            statement3.setString(1, classid);

            ResultSet resultSet = statement.executeQuery();
            ResultSet resultSet2 = statement2.executeQuery();
            ResultSet resultSet3 = statement3.executeQuery();

            if (resultSet.isClosed())
            {
                throw new Exception("regdetails: classid does not exist");
            }

            String courseID = resultSet.getString("courseid");
            String days = resultSet.getString("days");
            String startTime = resultSet.getString("starttime");
            String endTime = resultSet.getString("endtime");
            String bldg = resultSet.getString("bldg");
            String roomNum = resultSet.getString("roomnum");
            String area = resultSet.getString("area");
            String title = resultSet.getString("title");
            String descrip = resultSet.getString("descrip");
            String prereqs = resultSet.getString("prereqs");
            int count = 0;
            String[] dept;
            String[] courseNum;
            String[] profName;
            

            
            while(resultSet2.next())
            {
                deptList.add(resultSet2.getString("dept"));
                courseNumList.add(resultSet2.getString("coursenum"));
            }
            
            while(resultSet3.next())
            {
                profNameList.add(resultSet3.getString("profname"));
            }

            dept = new String[deptList.size()];
            
            for (String s: deptList)
            {
                dept[count] = s;
                count++;
            }

            count = 0;

            courseNum = new String[courseNumList.size()];

            for (String s: courseNumList)
            {
                courseNum[count] = s;
                count++;
            }

            count = 0;

            profName = new String[profNameList.size()];

            for (String s: profNameList)
            {
                profName[count] = s;
                count++;
            }

            count = 0;

            output = new CourseInfo(classid, courseID, days, startTime, endTime,
            bldg, roomNum, area, title, descrip, prereqs, dept, courseNum, profName);

            return output;
        }
        catch (Exception e) 
        {
            System.err.println(e);
        }
        return null;
   }

   // For testing:

   public static void main(String[] args) throws Exception
   {
      Database database = new Database();
      database.connect();
      CourseInfo test = database.searchDetails("8361");
      System.out.println(test);
      database.disconnect();
   }
}