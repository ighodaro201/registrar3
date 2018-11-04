//----------------------------------------------------------------------
// CourseInfo.java
// Author: Osita Ighodaro Ben Musoke-Lubega
//----------------------------------------------------------------------

import java.io.Serializable;

class CourseInfo implements Serializable
{
    String classid;
    String courseid;
    String days;
    String startTime;
    String endTime;
    String bldg;
    String roomNum;
    String area;
    String title;
    String descrip;
    String prereqs;
    String[] dept;
    String[] courseNum;
    String[] profName;

    public CourseInfo(String classid, String courseid, String days, String startTime, String endTime, 
    String bldg, String roomNum, String area, String title, String descrip, String prereqs,
    String[] dept, String[] courseNum, String[] profName)
    {
        this.classid = classid;
        this.courseid = courseid;
        this.days = days;
        this.startTime = startTime;
        this.endTime = endTime;
        this.bldg = bldg;
        this.roomNum = roomNum;
        this.area = area;
        this.title = title;
        this.descrip = descrip;
    
        this.dept = new String[dept.length];
        this.courseNum = new String[courseNum.length];
        this.profName = new String[profName.length];

        for (int i = 0; i < dept.length; i++)
            this.dept[i] = dept[i];

        for (int i = 0; i < courseNum.length; i++)
            this.courseNum[i] = courseNum[i];

        for (int i = 0; i < profName.length; i++)
            this.profName[i] = profName[i];
    }

    public String toString()
    {
        return classid + "\n" + title;
    }

    public String getClassID()
    {
        return classid;
    }

    public String getCourseID()
    {
        return courseid;
    }

    public String getDays()
    {
        return days;
    }

    public String getStartTime()
    {
        return startTime;
    }

    public String getEndTime()
    {
        return endTime;
    }

    public String getBldg()
    {
        return bldg;
    }

    public String getRoomNum()
    {
        return roomNum;
    }

    public String getArea()
    {
        return area;
    }

    public String getTitle()
    {
        return title;
    }

    public String getDescrip()
    {
        return descrip;
    }

    public String getPrereqs()
    {
        return prereqs;
    }

    public String[] getDept()
    {
        return dept;
    }

    public String[] getCourseNum()
    {
        return courseNum;
    }

    public String[] getProfNames()
    {
        return profName;
    }
}