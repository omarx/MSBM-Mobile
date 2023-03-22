package com.fras.msbm.models.directory;

import com.orm.SugarRecord;

import java.util.Comparator;

/**
 * Created by Antonio on 19/06/2016.
 */
public class DirectoryEntry extends SugarRecord {
    public String lastname;
    public String firstname;
    public String jobtitle;
    public String unit;
    public String phone;
    public String email;


    public DirectoryEntry(){
        this.lastname = "Default Value";
        this.firstname = "Default Value";
        this.jobtitle = "Default Value";
        this.unit = "Default Value";
        this.phone = "Default Value";
        this.email = "Default Value";
    }

    public DirectoryEntry(String lastname, String firstname, String jobtitle, String unit, String phone, String email) {
        this.lastname = lastname;
        this.firstname = firstname;
        this.jobtitle = jobtitle;
        this.unit = unit;
        this.phone = phone;
        this.email = email;
    }

    public DirectoryEntry(String lastname, String firstname, String phone) {
        this.lastname = lastname;
        this.firstname = firstname;
        this.phone = phone;
    }

    public String getNumber(){
        return this.phone;
    }

    @Override
    public String toString() {
        return firstname + "  " + lastname;
    }

    public static Comparator<DirectoryEntry> NameComparator = new Comparator<DirectoryEntry>() {
        @Override
        public int compare(DirectoryEntry lhs, DirectoryEntry rhs) {

            String name1 = lhs.lastname.toUpperCase();
            String name2 = rhs.lastname.toUpperCase();

            return name1.compareTo(name2);
        }
    };
}
