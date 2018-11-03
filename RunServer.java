//----------------------------------------------------------------------
// RunServer.java
// Author: Osita Ighodaro Ben Musoke-Lubega
//----------------------------------------------------------------------
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.File;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

//----------------------------------------------------------------------
class CourseStuff implements Serializable
{
    String classid;
    String classData;

    public CourseStuff(String classID, String classData)
    {
        this.classid = classID;
        this.classData = classData;
    }

    public String toString()
    {
        return classid + "\n" + classData + "\n";
    }

    public String getClassID()
    {
        return classid;
    }

    public String getCourseData()
    {
        return classData;
    }
}

class ServerThread extends Thread
{
    private Socket socket;
    private SocketAddress clientAddr;
    private static final String DATABASE_NAME = "reg.sqlite";
    private static final String DEPT = "dept";
    private static final String COURSENUM = "coursenum";
    private static final String AREA = "area";
    private static final String TITLE = "title";

   public ServerThread(Socket socket, SocketAddress clientAddr)
   {
        this.socket = socket;
        this.clientAddr = clientAddr;
   }

   public String courseInfo(String classID)
   {
       try
       {
        StringBuilder output = new StringBuilder();

        File databaseFile = new File(DATABASE_NAME);
            if (! databaseFile.isFile())
                System.err.println("regdetails: Database connection failed");

        if (!Pattern.matches("[0-9]*", classID))
        {
            System.err.println("regdetails: classid is not an integer");
        }

        Connection connection =
                DriverManager.getConnection("jdbc:sqlite:" + DATABASE_NAME);

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
            statement.setString(1, classID);

            PreparedStatement statement2 = 
                connection.prepareStatement(stmtStr2);
            statement2.setString(1, classID);

            PreparedStatement statement3 = 
                connection.prepareStatement(stmtStr3);
            statement3.setString(1, classID);

            ResultSet resultSet = statement.executeQuery();
            ResultSet resultSet2 = statement2.executeQuery();
            ResultSet resultSet3 = statement3.executeQuery();

            if (resultSet.isClosed())
            {
                System.err.println("regdetails: classid does not exist");
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

            output.append("\nCourse ID: " + courseID + "\n");    
            output.append("\nDays: " + days + "\n");
            output.append("\nStart Time: " + startTime + "\n");
            output.append("\nEnd Time: " + endTime + "\n");
            output.append("\nBuilding: " + bldg + "\n");
            output.append("\nRoom Number: " + roomNum + "\n\n");

            
            while(resultSet2.next())
            {
                String dept = resultSet2.getString("dept");
                String courseNum = resultSet2.getString("coursenum");

                output.append("Department and Course Number: ");
                output.append(dept + " ");
                output.append(courseNum + "\n");
            }
            
            output.append("\nDistribution Area (if applicable): " + area + "\n");
            output.append("\nTitle: " + title + "\n");
            output.append("\nDescription: " + descrip + "\n");
            output.append("\nPrerequisites: " + prereqs + "\n");
            output.append("\nProfessor Name(s):");
            while(resultSet3.next())
            {
                String profName = resultSet3.getString("profname");
                output.append(" " + profName + ",");
            }

            output.deleteCharAt(output.length() - 1);
            connection.close();
            return output.toString();
        }
        catch (Exception e) 
        {
            System.err.println(e);
            System.err.println("regdetails: database reg.sqlite not found");
        }
        return null;
   }

   public ArrayList<CourseStuff> getCourseBasic(String[] inputs)
   {
       ArrayList<CourseStuff> output = new ArrayList<CourseStuff>();

       HashMap<String, Integer> map = new HashMap<String, Integer>();
       map.put(DEPT, 0);
       map.put(COURSENUM, 0);
       map.put(AREA, 0);
       map.put(TITLE, 0);

       // Identifies search queries
       String whereString = new String();
       
       // Read in queries
       for (int i = 0; i < inputs.length; i++)
       {
           String key = inputs[i].substring(1);
           
           // check validity of key
           if (map.containsKey(key))
           {
               if (map.get(key) == 0) map.put(key, 1);
               else 
               {
                   System.err.println("reg: duplicate key");
                   System.exit(1);
               }
           }
           else
           {
               System.err.println("reg: invalid key");
               System.exit(1);
           }
           i++;

           // checks for missing value
           if (i >= inputs.length) 
           {
               System.err.println("reg: missing value");
               System.exit(1);
           }

           String value = inputs[i];

           if (key.equals(DEPT)) 
           {
               value = value.toUpperCase();
               value = "\"" + value + "\"";
               whereString += " AND " + key + " = " + value;
           }
           else if (key.equals(COURSENUM))
           {
               whereString += " AND " + key + " = " + value;
           }
           else if (key.equals(AREA))
           {
               value = value.toUpperCase();
               value = "\"" + value + "\"";
               whereString += " AND " + key + " = " + value;
           }
           else if (key.equals(TITLE))
           {
               value = "%" + value + "%";
               value = "\"" + value + "\"";
               whereString += " AND " + key + " LIKE " + value;
           }
       }

       try
       {
           File databaseFile = new File(DATABASE_NAME);
           if (!databaseFile.isFile())
               throw new Exception("Database connection failed");

           Connection connection = DriverManager.getConnection("jdbc:sqlite:" + DATABASE_NAME);

           String stmtStr = "SELECT classid, dept, coursenum, area, title  " + 
           "FROM courses, classes, crosslistings " +
           "WHERE courses.courseid = crosslistings.courseid " +
           "AND classes.courseid = courses.courseid " +
           "AND classes.courseid = crosslistings.courseid " +
           whereString.toString() +
           " ORDER BY dept, coursenum, classid;";

           PreparedStatement statement = connection.prepareStatement(stmtStr);

           ResultSet resultSet = statement.executeQuery();

           while (resultSet.next())
           {
               CourseStuff dataStructure;
               String lineOfOutput;
               String classid = resultSet.getString("classid");
               String dept = resultSet.getString("dept");
               String coursenum = resultSet.getString("coursenum");
               String area = resultSet.getString("area");
               String title = resultSet.getString("title");

               lineOfOutput = String.format("%-5s\t%-5s\t%-5s\t%-5s\t%-5s", classid, dept, coursenum, 
               area, title);

               dataStructure = new CourseStuff(classid, lineOfOutput);
               output.add(dataStructure);
           }

           connection.close();
           return output;
       }
       catch (Exception e) 
       { 
           System.err.println(e);
           System.err.println("regdetails: database reg.sqlite not found");
       }
   
       return null;
   }

   public void run()
   {
        try
        {  
            String[] inputs;
            ArrayList<String> list = new ArrayList<String>();
            String classID = "";
            HashMap<String, ArrayList<Character>> disgusting = new HashMap<String, ArrayList<Character>>();

            System.out.println("Spawned thread for " + clientAddr);

            InputStream inputStream = socket.getInputStream();
            ObjectInputStream ois = new ObjectInputStream(inputStream);

            OutputStream os = socket.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(os);

            Object stuff = ois.readObject();

            if (stuff instanceof String)
            {
                classID = (String) stuff;
                oos.writeObject(courseInfo(classID));
                oos.flush();
                System.out.println("Wrote Detailed Course Info to " + clientAddr);
            }
            
            else
            {
                disgusting = (HashMap<String, ArrayList<Character>>) stuff;

                for (Map.Entry<String, ArrayList<Character>> entry : disgusting.entrySet())
                {
                    String key = entry.getKey();
                    ArrayList<Character> value = entry.getValue();
                    char[] charValue = new char[value.toArray().length];
                    int count = 0;

                    for (Character c : value)
                    {
                        charValue[count] = c.charValue();
                        count++;
                    }

                    if (!value.isEmpty())
                    {                        
                        list.add(key);
                        list.add(new String(charValue));
                    }
                }

                inputs = new String[list.size()];
                for (int i = 0; i < list.size(); i++)
                {
                    inputs[i] = list.get(i);
                }
                oos.writeObject(getCourseBasic(inputs));
                oos.flush();
                System.out.println("Wrote Basic Course Info to " + clientAddr);
            }

            socket.close();
            System.out.println("Closed socket for " + clientAddr);
            System.out.println("Exiting thread for " + clientAddr);
            System.out.println();
        }
        catch (Exception e) { System.err.println(e); }
   }
}

public class RunServer
{
   public static void main(String[] args)
   {
        if (args.length != 1)
        {  
            System.err.println("Usage: java RunServer port");
            System.exit(1);
        }
      
        if (!Pattern.matches("[0-9]*", args[0]))
        {
            System.err.println("runserver: port is not an integer");
            System.exit(1);
        }

        try
        {  
            int port = Integer.parseInt(args[0]);

            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Opened server socket");
            
            while (true)
            {  
                Socket socket = serverSocket.accept();
                SocketAddress clientAddr = socket.getRemoteSocketAddress();

                System.out.println("Accepted connection for " + clientAddr);
                System.out.println("Opened socket for " + clientAddr);
                ServerThread serverThread = new ServerThread(socket, clientAddr);
                serverThread.start();
            }
        }
        catch (Exception e) { System.err.println(e); }
   }
}
