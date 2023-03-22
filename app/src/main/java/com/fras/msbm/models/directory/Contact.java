package com.fras.msbm.models.directory;

import com.orm.SugarRecord;

import java.util.Comparator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter //@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Contact extends SugarRecord {
    @NonNull private String name;
    @NonNull private String number;
    public String lastname;
    public String firstname;
    public String jobtitle;
    public String unit;
    public String phone;
    public String email;
    public String office;

//    public Contact(String lastname, String firstname, String jobtitle, String unit, String phone, String email){
//        this.lastname = "Default Value";
//        this.firstname = "Default Value";
//        this.jobtitle = "Default Value";
//        this.unit = "Default Value";
//        this.phone = "";
//        this.email = "Default Value";
//        this.office = "Default Value";
//    }

    public Contact(String name, String number){
        this.firstname = name;
        this.lastname = " ";
        this.phone = number;
        this.jobtitle = " ";
        this.unit = " ";
        this.email = " ";
        this.office = " ";
    }

    @Override
    public String toString() {
        return firstname + "  " + lastname;
    }

    public String getName() {
        return firstname + "  " + lastname;
    }

    public String getPhone(){
        return phone;
    }

    public static Comparator<Contact> NameComparator = new Comparator<Contact>() {
        @Override
        public int compare(Contact lhs, Contact rhs) {

            String name1 = lhs.lastname.toUpperCase();
            String name2 = rhs.lastname.toUpperCase();

            return name1.compareTo(name2);
        }
    };

}
