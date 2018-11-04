//----------------------------------------------------------------------
// CourseBasic.java
// Author: Osita Ighodaro Ben Musoke-Lubega
//----------------------------------------------------------------------

import java.io.Serializable;

class CourseBasic implements Serializable
{
    String dept;
    String coursenum;
    String area;
    String title;
    String classid;

    public CourseBasic(String dept, String coursenum, String area, String title, String classid)
    {
        this.classid = classid;
        this.dept = dept;
        this.coursenum = coursenum;
        this.area = area;
        this.title = title;
    }

    public String toString()
    {
        return classid + "\n" + title + "\n" + dept + " " + coursenum + "\n" + area;
    }

    public String getClassID()
    {
        return classid;
    }

    public String getDept()
    {
        return dept;
    }

    public String getCourseNum()
    {
        return coursenum;
    }

    public String getArea()
    {
        return area;
    }

    public String getTitle()
    {
        return title;
    }
}