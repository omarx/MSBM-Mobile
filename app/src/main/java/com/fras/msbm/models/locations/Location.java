package com.fras.msbm.models.locations;

import com.mapbox.mapboxsdk.geometry.LatLng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Builder;

/**
 * Created by Shane on 7/27/2016.
 */
@Getter @Setter @ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Location implements Serializable {
    private String name;
    private String shortName;
    private String slogan;
    private String description;
    private Coordinates coordinates;

    public String getDisplayName() {
        final boolean hasShortName = getShortName() != null
                && ! getShortName().isEmpty();
        return (hasShortName) ? getShortName() : getName();
    }


    public static List<Location> getDiningLocations() {
        List<Location> locations = new ArrayList<>();

        locations.add(Location.builder().name("Mona School of Business and Management (North)").coordinates(new Coordinates(18.00788907060166,-76.74750619614109)).build());
        locations.add(Location.builder().name("Mona School of Business and Management (South)").coordinates(new Coordinates(18.006438, -76.748143)).build());

//        locations.add(Location.builder().name("").coordinates(new Coordinates(,)).build()); 18.006438, -76.748143
//        locations.add(Location.builder().name("").coordinates(new Coordinates(,)).build());
//        locations.add(Location.builder().name("").coordinates(new Coordinates(,)).build());
//        locations.add(Location.builder().name("").coordinates(new Coordinates(,)).build());
//        locations.add(Location.builder().name("").coordinates(new Coordinates(,)).build());
//        locations.add(Location.builder().name("").coordinates(new Coordinates(,)).build());
//        locations.add(Location.builder().name("").coordinates(new Coordinates(,)).build());

        return locations; //Faculty of Social Sciences
    }

    public static List<Location> getAllLocations() {
        List<Location> locations = new ArrayList<>();
        locations.add(Location.builder().name("Kentucky Fried Chicken").description("We do chicken right").shortName("KFC").coordinates(new Coordinates(18.006178, -76.744806)).build());
        locations.add(Location.builder().name("Juici Patties").description("Great breakfast, great variety").shortName("Juici").coordinates(new Coordinates(18.005058, -76.748503)).build());
        locations.add(Location.builder().name("Yoa's Chinese Restaurant").description("Asian Food").shortName("Yoa").coordinates(new Coordinates(18.000854, -76.743336)).build());
        locations.add(Location.builder().name("The Board Walk Cafe").shortName("Board Walk").coordinates(new Coordinates(18.007062, -76.747812)).build());
        locations.add(Location.builder().name("Bee Hive").description("Good food fast.").coordinates(new Coordinates(18.004521, -76.746361)).build());

        locations.add(Location.builder().name("Western Campus").coordinates(new Coordinates(18.495302, -77.914520)).build());


        locations.add(Location.builder().name("Senior Common Room Club and Restaurant").coordinates(new Coordinates(18.00168, -76.74790)).build());


        locations.add(Location.builder().name("Faculty of Humanities").coordinates(new Coordinates(18.005460, -76.746022)).build());
        locations.add(Location.builder().name("Faculty of Social Sciences").coordinates(new Coordinates(18.006549, -76.747827)).build());
        locations.add(Location.builder().name("Faculty Of Medical Science").coordinates(new Coordinates(18.009162, -76.746945)).build());
        locations.add(Location.builder().name("Mona School of Business and Management (North)").coordinates(new Coordinates(18.00788907060166,-76.74750619614109)).build());
        locations.add(Location.builder().name("Mona School of Business and Management (South)").coordinates(new Coordinates(18.006438, -76.748143)).build());
        locations.add(Location.builder().name("Faculty of Law").coordinates(new Coordinates(18.008178, -76.748322)).build());
        locations.add(Location.builder().name("Norman Manley Law School").coordinates(new Coordinates(18.00702015340222,-76.74653926108589)).build());
        locations.add(Location.builder().name("Faculty of Science and Technology").coordinates(new Coordinates(18.005167, -76.750208)).build());

        locations.add(Location.builder().name("Irvine Hall").coordinates(new Coordinates(18.00610521032412,-76.74274607401928)).build());
        locations.add(Location.builder().name("Chancellor Hall").coordinates(new Coordinates(18.006037, -76.744301)).build());
        locations.add(Location.builder().name("Taylor Hall").coordinates(new Coordinates(18.007506, -76.744823)).build());
        locations.add(Location.builder().name("Marlene Hamilton Hall").coordinates(new Coordinates(18.000526, -76.746096)).build());
        locations.add(Location.builder().name("AZ Preston Hall").coordinates(new Coordinates(18.000562, -76.742551)).build());
        locations.add(Location.builder().name("Mary Seacole Hall").coordinates(new Coordinates(18.004315, -76.745315)).build());
        locations.add(Location.builder().name("Elsa-Leo Rhynie Hall").coordinates(new Coordinates(18.007192, -76.743585)).build());
        locations.add(Location.builder().name("Rex Nettleford Hall").coordinates(new Coordinates(18.002897, -76.741939)).build());

        locations.add(Location.builder().name("Hi-Lo Food Store").description("Groceries, etc.").coordinates(new Coordinates(18.000921, -76.743154)).build());
        locations.add(Location.builder().name("UWI Student Union").description("Come for a good lime").coordinates(new Coordinates(18.000723, -76.743484)).build());
        locations.add(Location.builder().name("UWI Old Library").coordinates(new Coordinates(18.001016, -76.744802)).build());
        locations.add(Location.builder().name("The Spot").description("Where it's at").coordinates(new Coordinates(18.000791, -76.743411)).build());
        locations.add(Location.builder().name("Mona Information Technology Services").coordinates(new Coordinates(18.00168, -76.74790)).build());


        locations.add(Location.builder().name("EduCom Credit Union Branch").coordinates(new Coordinates(18.001879, -76.745599)).build());
//        locations.add(Location.builder().name("National Commercial Bank").coordinates(new Coordinates(18.004834, -76.747063)).build());
        locations.add(Location.builder().name("ScotiaBank UWI-Branch").coordinates(new Coordinates(18.005529, -76.744857)).build());
        locations.add(Location.builder().name("JNBS UWI-Branch").coordinates(new Coordinates(18.006308, -76.744955)).build());
        locations.add(Location.builder().name("NCB ATM").coordinates(new Coordinates(18.007199, -76.747681)).build());
        locations.add(Location.builder().name("NCB UWI-Branch").coordinates(new Coordinates(18.004823, -76.747073)).build());
//        locations.add(Location.builder().name("").coordinates(new Coordinates(,)).build());
//        locations.add(Location.builder().name("").coordinates(new Coordinates(,)).build());
//        locations.add(Location.builder().name("").coordinates(new Coordinates(,)).build());
//        locations.add(Location.builder().name("").coordinates(new Coordinates(,)).build());
//        locations.add(Location.builder().name("").coordinates(new Coordinates(,)).build());
//        locations.add(Location.builder().name("").coordinates(new Coordinates(,)).build());
//        locations.add(Location.builder().name("").coordinates(new Coordinates(,)).build());


        return locations; //Faculty of Social Sciences
    }

    public static List<Location> getHallLocations() {
        List<Location> locations = new ArrayList<>();
        locations.add(Location.builder().name("Irvine Hall").coordinates(new Coordinates(18.00610521032412,-76.74274607401928)).build());
        locations.add(Location.builder().name("Chancellor Hall").coordinates(new Coordinates(18.006037, -76.744301)).build());
        locations.add(Location.builder().name("Taylor Hall").coordinates(new Coordinates(18.007506, -76.744823)).build());
        locations.add(Location.builder().name("Marlene Hamilton Hall").coordinates(new Coordinates(18.000526, -76.746096)).build());
        locations.add(Location.builder().name("AZ Preston Hall").coordinates(new Coordinates(18.000562, -76.742551)).build());
        locations.add(Location.builder().name("Mary Seacole Hall").coordinates(new Coordinates(18.004315, -76.745315)).build());
        locations.add(Location.builder().name("Elsa-Leo Rhynie Hall").coordinates(new Coordinates(18.007192, -76.743585)).build());
        locations.add(Location.builder().name("Rex Nettleford Hall").coordinates(new Coordinates(18.002897, -76.741939)).build());

        return locations;
    }

    public static List<Location> getFacultyLocations() {
        List<Location> locations = new ArrayList<>();
        locations.add(Location.builder().name("Faculty of Humanities").coordinates(new Coordinates(18.005460, -76.746022)).build());
        locations.add(Location.builder().name("Faculty of Social Sciences").coordinates(new Coordinates(18.006549, -76.747827)).build());
        locations.add(Location.builder().name("Faculty Of Medical Science").coordinates(new Coordinates(18.009162, -76.746945)).build());
        locations.add(Location.builder().name("Mona School of Business and Management (North)").coordinates(new Coordinates(18.00788907060166,-76.74750619614109)).build());
        locations.add(Location.builder().name("Mona School of Business and Management (South)").coordinates(new Coordinates(18.006438, -76.748143)).build());
        locations.add(Location.builder().name("Faculty of Law").coordinates(new Coordinates(18.008178, -76.748322)).build());
        locations.add(Location.builder().name("Norman Manley Law School").coordinates(new Coordinates(18.00702015340222,-76.74653926108589)).build());
        locations.add(Location.builder().name("Faculty of Science and Technology").coordinates(new Coordinates(18.005167, -76.750208)).build());

        return locations;
    }

    public static List<Location> getFoodLocations() {
        List<Location> locations = new ArrayList<>();
        locations.add(Location.builder().name("Kentucky Fried Chicken").description("We do chicken right").shortName("KFC").coordinates(new Coordinates(18.006178, -76.744806)).build());
        locations.add(Location.builder().name("Juici Patties").description("Great breakfast, great variety.").shortName("Juici").coordinates(new Coordinates(18.005058, -76.748503)).build());
        locations.add(Location.builder().name("Yoa's Chinese Restaurant").description("Asian Food.").shortName("Yoa").coordinates(new Coordinates(18.000854, -76.743336)).build());
        locations.add(Location.builder().name("The Board Walk Cafe").description("Chill atmosphere, good food.").shortName("Board Walk").coordinates(new Coordinates(18.007062, -76.747812)).build());
        locations.add(Location.builder().name("Bee Hive").description("Good food fast.").coordinates(new Coordinates(18.004521, -76.746361)).build());
        locations.add(Location.builder().name("Mary Seacole Cafeteria").coordinates(new Coordinates(18.004834, -76.744665)).build());
        locations.add(Location.builder().name("Hi-Lo Food Store").description("Groceries, etc.").coordinates(new Coordinates(18.000921, -76.743154)).build());
        locations.add(Location.builder().name("The Spot").description("Where it's at").coordinates(new Coordinates(18.000791, -76.743411)).build());
        locations.add(Location.builder().name("Senior Common Room Club and Restaurant").coordinates(new Coordinates(18.00168, -76.74790)).build());

        return locations;
    }

    public static List<Location> getDrinkLocations() {
        List<Location> locations = new ArrayList<>();
        locations.add(Location.builder().name("Senior Common Room Club and Restaurant").coordinates(new Coordinates(18.00168, -76.74790)).build());
        locations.add(Location.builder().name("The Spot").description("Where it's at").coordinates(new Coordinates(18.000791, -76.743411)).build());

        return locations;
    }

    public static List<Location> getBankLocations() {
        List<Location> locations = new ArrayList<>();
        locations.add(Location.builder().name("EduCom Credit Union Branch").coordinates(new Coordinates(18.001879, -76.745599)).build());
//        locations.add(Location.builder().name("National Commercial Bank").coordinates(new Coordinates(18.004834, -76.747063)).build());
        locations.add(Location.builder().name("ScotiaBank UWI-Branch").coordinates(new Coordinates(18.005529, -76.744857)).build());
        locations.add(Location.builder().name("JNBS UWI-Branch").coordinates(new Coordinates(18.006308, -76.744955)).build());
        locations.add(Location.builder().name("NCB ATM").coordinates(new Coordinates(18.007199, -76.747681)).build());
        locations.add(Location.builder().name("NCB UWI-Branch").coordinates(new Coordinates(18.004823, -76.747073)).build());

        return locations;
    }

    public static List<Location> getWesternLocations(){
        List<Location> locations = new ArrayList<>();
        locations.add(Location.builder().name("Western Campus").description("").coordinates(new Coordinates(18.495302, -77.914520)).build());
        locations.add(Location.builder().name("Msbm Office").description("Msbm Office").coordinates(new Coordinates(18.49455, -77.91514)).build());
        locations.add(Location.builder().name("OSSD").description("OSSD").coordinates(new Coordinates(18.49512, -77.91474)).build());
        locations.add(Location.builder().name("Main office").description("Main office").coordinates(new Coordinates(18.49501, -77.91464)).build());
        locations.add(Location.builder().name("Credit Union").description("Credit Union").coordinates(new Coordinates(18.49501, -77.91467)).build());
        locations.add(Location.builder().name("ATM").description("ATM").coordinates(new Coordinates(18.4948, -77.9147)).build());
        locations.add(Location.builder().name("Bookshop").description("Bookshop").coordinates(new Coordinates(18.49521, -77.91465)).build());
        locations.add(Location.builder().name("W103 Classroom").description("W103 Classroom").coordinates(new Coordinates(18.49537, -77.91535)).build());
        locations.add(Location.builder().name("E202 Classroom").description("E202 Classroom").coordinates(new Coordinates(18.49528, -77.91429)).build());
        locations.add(Location.builder().name("E203 Classroom").description("E203 Classroom").coordinates(new Coordinates(18.49506, -77.91464)).build());
        locations.add(Location.builder().name("W202 Classroom").description("W202 Classroom").coordinates(new Coordinates(18.4948, -77.91497)).build());
        locations.add(Location.builder().name("Cafeteria").description("Cafeteria").coordinates(new Coordinates(18.49477, -77.91492)).build());
        locations.add(Location.builder().name("Sick bay").description("Sick bay").coordinates(new Coordinates(18.49486, -77.91479)).build());
        locations.add(Location.builder().name("IT Office").description("IT Office").coordinates(new Coordinates(18.495012, -77.914615)).build());
        locations.add(Location.builder().name("Computer Lab").description("Computer Lab").coordinates(new Coordinates(18.49519, -77.91438)).build());
        locations.add(Location.builder().name("Library").description("Library").coordinates(new Coordinates(18.49528, -77.91429)).build());
//        locations.add(Location.builder().name("").description("").coordinates(new Coordinates(, )).build());
//        locations.add(Location.builder().name("").description("").coordinates(new Coordinates(, )).build());

        return locations;
    }

    public static List<Location> getWesternClasses(){
        List<Location> locations = new ArrayList<>();
        locations.add(Location.builder().name("W103 Classroom").description("W103 Classroom").coordinates(new Coordinates(18.49537, -77.91535)).build());
        locations.add(Location.builder().name("E202 Classroom").description("E202 Classroom").coordinates(new Coordinates(18.49528, -77.91429)).build());
        locations.add(Location.builder().name("E203 Classroom").description("E203 Classroom").coordinates(new Coordinates(18.49506, -77.91464)).build());
        locations.add(Location.builder().name("W202 Classroom").description("W202 Classroom").coordinates(new Coordinates(18.4948, -77.91497)).build());

        return locations;
    }

    public static List<Location> getWesternFaculties() {
        List<Location> locations = new ArrayList<>();
//        locations.add(Location.builder().name("Western Campus").description("").coordinates(new Coordinates(18.495302, -77.914520)).build());
        locations.add(Location.builder().name("Msbm Office").description("Msbm Office").coordinates(new Coordinates(18.49455, -77.91514)).build());
        locations.add(Location.builder().name("OSSD").description("OSSD").coordinates(new Coordinates(18.49512, -77.91474)).build());
        locations.add(Location.builder().name("Main office").description("Main office").coordinates(new Coordinates(18.49501, -77.91464)).build());
        locations.add(Location.builder().name("Bookshop").description("Bookshop").coordinates(new Coordinates(18.49521, -77.91465)).build());
        locations.add(Location.builder().name("IT Office").description("IT Office").coordinates(new Coordinates(18.495012, -77.914615)).build());
        locations.add(Location.builder().name("Computer Lab").description("Computer Lab").coordinates(new Coordinates(18.49519, -77.91438)).build());
        locations.add(Location.builder().name("Library").description("Library").coordinates(new Coordinates(18.49528, -77.91429)).build());

        return locations;
    }

    public static List<Location> getWesternBanks() {
        List<Location> locations = new ArrayList<>();
        locations.add(Location.builder().name("Credit Union").description("Credit Union").coordinates(new Coordinates(18.49501, -77.91467)).build());
        locations.add(Location.builder().name("ATM").description("ATM").coordinates(new Coordinates(18.4948, -77.9147)).build());

        return locations;
    }

    public static List<Location> getMonaClassLocations(){
        List<Location> locations = new ArrayList<>();
        locations.add(Location.builder().name("GLT1/D101").description("Graduate Lecture Theatre 1 - Building D - Sir Alister McIntyre Complex").coordinates(new Coordinates(18.007968, -76.747381)).build());
        locations.add(Location.builder().name("GLT2/I101").description("Graduate Lecture Theatre 2 - Building I - Sir Alister McIntyre Complex").coordinates(new Coordinates(18.007839, -76.747797)).build());
        locations.add(Location.builder().name("GLT3/I102").description("Graduate Lecture Theatre 3 - Building I - Sir Alister McIntyre Complex").coordinates(new Coordinates(18.007907, -76.747839)).build());
        locations.add(Location.builder().name("Pop Lab").description("Population Computer Lab - Building E - Sir Alister McIntyre Complex").coordinates(new Coordinates(18.007831, -76.747404)).build());
        locations.add(Location.builder().name("SR4").description("Seminar Room 4   - Faculty of Social Sciences Office Building - Faculty of Social Sciences ").coordinates(new Coordinates(18.007146, -76.747169)).build());
        locations.add(Location.builder().name("SR5").description("Seminar Room 5   - Faculty of Social Sciences Office Building - Faculty of Social Sciences ").coordinates(new Coordinates(18.007146, -76.747169)).build());
        locations.add(Location.builder().name("SR6").description("Seminar Room 6   - Faculty of Social Sciences Office Building - Faculty of Social Sciences ").coordinates(new Coordinates(18.007146, -76.747169)).build());
        locations.add(Location.builder().name("SR8").description("Seminar Room 8   - Faculty of Social Sciences Office Building - Faculty of Social Sciences ").coordinates(new Coordinates(18.007146, -76.747169)).build());
        locations.add(Location.builder().name("SSLT").description("Social Science Lecture Theatre -  - Faculty of Social Sciences ").coordinates(new Coordinates(18.006712, -76.747388)).build());
        locations.add(Location.builder().name("SR10").description("Seminar Room 10 - Downstairs - Mona School of Business and Management South Building").coordinates(new Coordinates(18.006496, -76.748149)).build());
        locations.add(Location.builder().name("SR11").description("Seminar Room 11 - Downstairs - Mona School of Business and Management South Building").coordinates(new Coordinates(18.006337, -76.748199)).build());
        locations.add(Location.builder().name("SR12").description("Seminar Room 12 - Downstairs - Mona School of Business and Management South Building").coordinates(new Coordinates(18.006355, -76.748277)).build());
        locations.add(Location.builder().name("SR14").description("Seminar Room 14 - Downstairs - Mona School of Business and Management South Building").coordinates(new Coordinates(18.006579, -76.748148)).build());
        locations.add(Location.builder().name("SR15").description("Seminar Room 15 - Downstairs - Mona School of Business and Management South Building").coordinates(new Coordinates(18.006051, -76.747946)).build());
        locations.add(Location.builder().name("SR16").description("Seminar Room 16 - Downstairs - Mona School of Business and Management South Building").coordinates(new Coordinates(18.006051, -76.747946)).build());
        locations.add(Location.builder().name("SR22").description("Seminar Room 22 - Downstairs - Mona School of Business and Management South Building").coordinates(new Coordinates(18.006497, -76.748231)).build());
        locations.add(Location.builder().name("SR23").description("Seminar Room 23 - Upstairs - Mona School of Business and Management South Building").coordinates(new Coordinates(18.006497, -76.748231)).build());
        locations.add(Location.builder().name("DOMS Lab/DOMS Main").description("MSBM South Computer lab - Upstairs - Mona School of Business and Management South Building").coordinates(new Coordinates(18.006497, -76.748231)).build());
        locations.add(Location.builder().name("TR11").description("Tutorial Room 11 -  - Department of Economics").coordinates(new Coordinates(18.00695, -76.747384)).build());
        locations.add(Location.builder().name("TR12").description("Tutorial Room 12 -  - Department of Economics").coordinates(new Coordinates(18.00695, -76.747384)).build());
        locations.add(Location.builder().name("TR20").description("Tutorial Room 20 -  - Department of Government").coordinates(new Coordinates(18.006987, -76.747394)).build());
        locations.add(Location.builder().name("SALISES").description(" ").coordinates(new Coordinates(18.006761, -76.747694)).build());
        locations.add(Location.builder().name("Grad lab A").description("Graduate Lab A -  - SALISES").coordinates(new Coordinates(18.006666, -76.747684)).build());
        locations.add(Location.builder().name("Grad lab B").description("Graduate Lab B -  - SALISES").coordinates(new Coordinates(18.006666, -76.747684)).build());
        locations.add(Location.builder().name("BLT").description("Biology Lecture Theatre -  - Faculty of Science and Technology").coordinates(new Coordinates(18.006263, -76.750482)).build());
        locations.add(Location.builder().name("CHEM/Phys").description("Chemistry/Physic Lecture Theatre -  - Faculty of Science and Technology").coordinates(new Coordinates(18.004436, -76.749179)).build());
        locations.add(Location.builder().name("CHEM2 / C2").description("Chemistry 2 -  - Faculty of Science and Technology").coordinates(new Coordinates(18.004343, -76.749704)).build());
        locations.add(Location.builder().name("CHEM3 /C3").description("Chemistry 3 -  - Faculty of Science and Technology").coordinates(new Coordinates(18.004343, -76.749704)).build());
//        locations.add(Location.builder().name("CHEM4 /C4").description("Chemistry 4 -  - Faculty of Science and Technology").coordinates(new Coordinates(, )).build());
        locations.add(Location.builder().name("CHEM5 /C5").description("Chemistry 5 -  - Faculty of Science and Technology").coordinates(new Coordinates(18.004439, -76.749992)).build());
        locations.add(Location.builder().name("CHEM6/C6").description("Chemistry 6 -  - Faculty of Science and Technology").coordinates(new Coordinates(18.004578, -76.749936)).build());
        locations.add(Location.builder().name("CHEM7 /C7").description("Chemistry 7 -  - Faculty of Science and Technology").coordinates(new Coordinates(18.004543, -76.749897)).build());
        locations.add(Location.builder().name("IFLT").description("Inter-Faculty Lecture Theatre -  - Faculty of Science and Technology").coordinates(new Coordinates(18.00556, -76.748784)).build());
        locations.add(Location.builder().name("IFSR1").description("Inter-Faculty Seminar room 1 -  - Faculty of Science and Technology").coordinates(new Coordinates(18.005647, -76.748792)).build());
        locations.add(Location.builder().name("IFSR2").description("Inter-Faculty Seminar room 2 -  - Faculty of Science and Technology").coordinates(new Coordinates(18.005647, -76.748792)).build());
        locations.add(Location.builder().name("PCLT/SLT2").description("PreClinical Lecture Theatre -  - Faculty of Science and Technology").coordinates(new Coordinates(18.005433, -76.74934)).build());
        locations.add(Location.builder().name("PLT/SLT3").description("Physiology Lecture Theatre -  - Faculty of Science and Technology").coordinates(new Coordinates(18.005385, -76.750061)).build());
        locations.add(Location.builder().name("SLT/SLT1").description("Science Lecture Theatre -  - Faculty of Science and Technology").coordinates(new Coordinates(18.005162, -76.749817)).build());
        locations.add(Location.builder().name("Ashcroft").description("Ashcroft Computer Lab -  - Faculty of Humanities and Education").coordinates(new Coordinates(18.005569, -76.745657)).build());
        locations.add(Location.builder().name("GCR").description("Graduate Conference room -  - Faculty of Humanities and Education").coordinates(new Coordinates(18.005569, -76.745657)).build());
        locations.add(Location.builder().name("LangLab1").description("Language lab  1 -  - Faculty of Humanities and Education").coordinates(new Coordinates(18.005569, -76.745657)).build());
        locations.add(Location.builder().name("LangLab2").description("Language lab  2 -  - Faculty of Humanities and Education").coordinates(new Coordinates(18.005569, -76.745657)).build());
        locations.add(Location.builder().name("LangLab3").description("Language lab  3 -  - Faculty of Humanities and Education").coordinates(new Coordinates(18.005569, -76.745657)).build());
        locations.add(Location.builder().name("LLSRM").description("Language lab Seminar Room -  - Faculty of Humanities and Education").coordinates(new Coordinates(18.005569, -76.745657)).build());
        locations.add(Location.builder().name("N1").description("Room N1 -  - Faculty of Humanities and Education").coordinates(new Coordinates(18.005196, -76.746081)).build());
        locations.add(Location.builder().name("N2").description("Room N2 -  - Faculty of Humanities and Education").coordinates(new Coordinates(18.005196, -76.746081)).build());
        locations.add(Location.builder().name("N3").description("Room N3 -  - Faculty of Humanities and Education").coordinates(new Coordinates(18.005196, -76.746081)).build());
        locations.add(Location.builder().name("N4").description("Room N4 -  - Faculty of Humanities and Education").coordinates(new Coordinates(18.005196, -76.746081)).build());
        locations.add(Location.builder().name("N5").description("Room N5 -  - Faculty of Humanities and Education").coordinates(new Coordinates(18.005196, -76.746081)).build());
        locations.add(Location.builder().name("NELT").description("New Education Lecture Threatre -  - Faculty of Humanities and Education").coordinates(new Coordinates(18.004978, -76.7461)).build());
        locations.add(Location.builder().name("OELT").description("Old Education Lecture Threatre -  - Faculty of Humanities and Education").coordinates(new Coordinates(18.005871, -76.746192)).build());
//        locations.add(Location.builder().name("").coordinates(new Coordinates(, )).build());
//        locations.add(Location.builder().name("").coordinates(new Coordinates(, )).build());
//        locations.add(Location.builder().name("").coordinates(new Coordinates(, )).build());

        return locations;
    }

    public LatLng convertCoordinatesToLatLng() {
        return new LatLng(this.coordinates.getLatitude(), this.coordinates.getLongitude());
    }

}
