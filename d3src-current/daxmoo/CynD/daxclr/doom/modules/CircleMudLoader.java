package daxclr.doom.modules;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import org.opencyc.cycobject.CycFort;
import org.opencyc.cycobject.CycList;
import daxclr.inference.CycAPI;

public class CircleMudLoader {
    public static PrintWriter cycfile = null;
    static public File loadDir = null;
    static public CycAPI cyc = null;
    static public CycFort mudMt = null;

    static public void main(String arg[]) {
        int argmnt = 0;
        CircleMudLoader cml = new CircleMudLoader();
        cml.cycfile = new PrintWriter(System.out);

        if (arg[argmnt].equals("-o")) {
            argmnt++;
            try {
                cml.cycfile = new PrintWriter(new FileWriter(arg[argmnt++]));
            } catch (Exception e) {
                debugln(e);
            }
        }

        if (arg[argmnt].equals("-cyc")) {
            cml.isAssertCyc = true;
            argmnt++;
            try {
                cml.mudMt = cml.cyc.makeCycConstant(arg[argmnt]);
            } catch (Exception e) {
                debugln(e);
            }
            argmnt++;
        }

        introConstant("DoomCurrentStateMt");
        assertDataString("(cyc-assert '(#$isa #$DoomCurrentStateMt #$DataMicrotheory) #$UniversalVocabularyMt)");
        introConstant("BoundsOfDirectionFn");
        assertDataString("(cyc-assert '(#$isa #$BoundsOfDirectionFn #$BinaryFunction) #$UniversalVocabularyMt)");
        assertDataString("(cyc-assert '(#$isa #$BoundsOfDirectionFn #$ReifiableFunction) #$UniversalVocabularyMt)");
//        assertDataString("(cyc-assert '(#$arg1Isa #$BoundsOfDirectionFn #$ThreeDimensionalGeometricThing) #$UniversalVocabularyMt)");
//        assertDataString("(cyc-assert '(#$arg1Isa #$Place #$ThreeDimensionalGeometricThing) #$UniversalVocabularyMt)");
//        assertDataString("(cyc-assert '(#$arg2Isa #$Direction #$Direction) #$UniversalVocabularyMt)");
  //      assertDataString("(cyc-assert '(#$resultIsa #$BoundsOfDirectionFn #$Boundary-Underspecified) #$UniversalVocabularyMt)");
//        assertDataString("(cyc-assert '(#$resultIsa #$BoundsOfDirectionFn #$GeographicalThing) #$UniversalVocabularyMt)");
        assertDataString("(cyc-assert '(#$resultIsa #$BoundsOfDirectionFn (#$CollectionIntersection2Fn #$GeographicalThing #$Boundary-Underspecified)) #$UniversalVocabularyMt)");


//        assertDataString("(cyc-assert '(#$implies (#$and (#$isa ?X #$BPVAgent) (#$isa ?X ?Type) (#$relationAllInstance ?Pred ?Type ?Instance)) (?Pred ?X ?Instance)) '#$DoomCurrentStateMt '(:DIRECTION :FORWARD))");
///        assertDataString("(cyc-assert '(#$implies (#$and (#$isa ?X #$BPVItem) (#$isa ?X ?Type) (#$relationAllInstance ?Pred ?Type ?Instance)) (?Pred ?X ?Instance)) '#$DoomCurrentStateMt '(:DIRECTION :FORWARD))");

//        assertDataString("(cyc-assert '(#$implies (#$and (#$pathBetween (#$BoundsOfDirectionFn ?P1 ?DIR1) ?P1 ?P2) (#$oppositeDirection-Interval ?DIR1 ?DIR2)) (#$equals (#$BoundsOfDirectionFn ?P1 ?DIR1) (#$BoundsOfDirectionFn ?P2 ?DIR2))) '#$DoomCurrentStateMt '(:DIRECTION :FORWARD))");


        for (int i=argmnt; i < arg.length; i++) {
            try {
                cml.loadFSO(new File(arg[i]));
            } catch (Exception e) {
                debugln(e);
            }
        }
        cml.cycfile.flush();
        System.exit(0);
        cml.cycfile.close();
    }

    static public void debugln(Throwable e) {
        e.printStackTrace(System.err);
    }

    public CircleMudLoader() {
        try {
            //   cyc = daxclr.inference.CycAPI.current();
        } catch (Exception e) {
            debugln(e);
        }

    }

    public void loadFSO(File inputFile)  throws Exception {
        if (inputFile.isDirectory()) {
            loadDir(inputFile);
        } else {
            loadFile(inputFile);
        }
    }

    public void loadDir(File dir) throws Exception {
        File[] file = dir.listFiles();
        for (int i = 0; i < file.length ; i++) {
            loadFSO(file[i]);
        }
    }

    public void loadFile(File inputFile)  throws Exception { 
        String fn = inputFile.getName();
        assertData(";load file '" + fn + "'");

        if (fn.endsWith(".zon"))
            loadZonFile(inputFile);
        else if (fn.endsWith(".obj"))
            loadObjFile(inputFile);
        else if (fn.endsWith(".mob"))
            loadMobFile(inputFile);
        else if (fn.endsWith(".wld"))
            loadWorldFile(inputFile);
    }



    public void loadWorldFile(File file) throws Exception {
        assertData("");
        assertData(";; Sourcing " + file.getAbsoluteFile());
        BufferedReader br = new BufferedReader( new FileReader(file));
        try {
            while (br.ready()) parseRoom(br);
        } catch (Exception e) {
            debugln(e);
        }
        br.close();
    }

    static public String getDirectionWards(char num) {
        return getDirection(num)+"-Generally";
    }



    static public String getDirection(char num) {
        switch (num) {
            case 'n':
                return "North";
            case 'e':
                return "East";
            case 's':
                return "South";
            case 'w':
                return "West";
            case 'u':
                return  "Up";
            case 'd':
                return "Down";
        }
        return "North";
    }

    static public String getDirectionState(String num) {
        if (num.startsWith("D")) num = num.substring(1);
        switch (num.charAt(0)) {
            case '0':
                return "#$OpenPortal";
            case '1':
                return "#$ClosedPortal";
            case '2':
                return "#$PathBlocked";
        }
        return "#$OpenPortal";
    }


    public boolean isKeyword(String str) {
        if (str.length()<3) return false;
        if (str.equalsIgnoreCase("the")) return false;
        return true;
    }

    static String STATIC_neswud = "neswud";

    public void parseRoom(BufferedReader br)  throws Exception {

        String line = br.readLine(); 

        String neswud = STATIC_neswud;

        if (line.startsWith("$")) return;
        if (line.length()<1) return;

        // Read #<virtual number>
        String vnum = getAsArea( line.substring(1).trim());
        assertData("");
        //introConstant(vnum.substring(2));
        // Read <room name>~
        String roomname = readUpTilde(br);
        String splitter[] = roomname.split(" ");

        if (roomname.length()>0) {
            assertData("(#$nameString " + vnum + " " + quotedString(roomname) + ")");
            //assertData("(properNameStrings " + vnum + " " + quotedString(roomname) + ")");
            for (int i=0;i<splitter.length;i++) {
                if (isKeyword(splitter[i])) assertData("(#$nicknames "+vnum+" "+quotedString(splitter[i])+")");
            }
        }
        // Read <Room Description>~
        String desc = readUpTilde(br);
        splitter = desc.split("\\.");
        if (desc.length()>0) {
            for (int i=0;i<splitter.length;i++) {
                if (isKeyword(splitter[i])) assertData("(#$definiteDescriptions "+vnum+" "+quotedString(splitter[i])+")");
            }

        }
        // Read <zone number> <room bitvector> <sector type>
        splitter = br.readLine().trim().split(" ");
        if (splitter.length==3) {
            getBitvectors(vnum,splitter[1]);
            getBittypes(vnum,splitter[2]);
        } else {
            System.out.println(vnum + " error " + splitter);
        }

        // Read {Zero or more Direction Fields and/or Extra Descriptions}
        while (!((line=br.readLine()).startsWith("S"))) {
            if (line.startsWith("E")) {
                // Read <keyword list>~
                splitter = readUpTilde(br).split(" ");
                // Read <description text>~   
                desc = readUpTilde(br);
                String OBJ = vnum+"-Object"+ ((int)(objnumint++));
                introConstant(OBJ);
                assertData("(#$isa "+OBJ+" #$SpatialThing-Localized)");
                assertData("(#$definiteDescriptions "+OBJ+" "+quotedString(desc)+")");
                assertData("(#$in-ContCompletely "+OBJ+" "+vnum+")");
                for (int i =0 ; i < splitter.length;i++) {
                    assertData("(#$nicknames "+OBJ+" "+quotedString(splitter[i])+")");
                }
            } else {

                if (line.startsWith("*")) line = line.substring(1).trim();
                if (line.startsWith("D")) line = line.substring(1).trim();
                //System.err.println(vnum+ ":" + line);

                char dchar = neswud.charAt(new Integer(line.substring(0,1)).intValue());

                neswud = neswud.replace(""+dchar,"x");
                //System.err.println(" NESWUD "+vnum+": "+neswud+ " "+ dchar);

                // Read D<direction number>
                String direction = "#$"+getDirectionWards(dchar);
                // Read <general description>~
                desc = readUpTilde(br);
                // Read <keyword list>~
                String[] kws = readUpTilde(br).split(" ");
                // Read <doorType flag> <key number> <room linked>
                splitter = readUpLine(br).split(" ");
                String doorType = "#$OpenPortal";

                switch (splitter[0].charAt(0)) {
                    case '0':
                        doorType = "#$OpenPortal";
                        break;
                    case '1':
                        doorType = "#$Doorway";
                        break;
                    case '2':
                        doorType = "#$ClosedPortal";//ClosedPortal //SecurityDoorPortal
                        break;
                }
                String lock = null;
                if (!splitter[1].equals("-1") && !splitter[1].equals("0")) lock = "#$ArtifactCol"+splitter[1];

                String destination = getAsArea(splitter[2]);
                if (splitter[2].equals("-1")) destination =  vnum;


                String exitToFn = "(#$BoundsOfDirectionFn " + vnum + " " + direction + " )"; 
                assertData("(#$isa " + exitToFn + " " + doorType + " )");
                assertData("(#$pathBetween " + exitToFn +  " "+ vnum + " "+destination+")");


                if (desc.length()>0)
                    assertData("(#$nameString " + exitToFn + " " + quotedString(desc)  + " )");

                if (lock!=null) assertData("(#$controls " + lock + " " + exitToFn + " )");

                for (int i =0 ; i < kws.length;i++) {
                    try {
                        if (kws[i].length()>0) assertData("(#$nicknames " + exitToFn + " " + quotedString(kws[i]) + " )");
                    } catch (Exception e) {
                        e.printStackTrace();       //
                    }
                }

            }
        }
        neswud = neswud.replace("x","");
        for (int i=0; i<neswud.length(); i++) {
            String direction = "#$"+getDirection(neswud.charAt(i))+"-Generally";
            String doorType = "#$Wall-GenericBarrier";
            String exitToFn = "(#$BoundsOfDirectionFn " + vnum + " " + direction + " )"; 
            assertData("(#$isa " + exitToFn + " " + doorType + " )");
//            assertDataString("(cyc-unassert (#$cavityHasWall " + exitToFn + " " + vnum + " )  #$DoomCurrentStateMt)");
//            assertData("(#$instantiatesBorderPart " + exitToFn + "  (#$BorderFn " + vnum + "))");
        }

    }

    static public boolean isAssertCyc = false;

    static public void assertDataString(String form) {
        cycfile.println(form);
    }

    static public void assertData(String form) {
        form = form.trim();
        form = form.replace("  "," ");
        form = form.replace("  "," ");
        form = form.replace("  "," ");
        form = form.replace("  "," ");
        form = form.replace(" )",")");
        form = form.replace(" (","(");
        if (form.length()<1) return;
        if (form.startsWith(";")) {
            cycfile.println(form);
            return;
        }
        //CycList cyclist = cyclifyString(form);
        assertDataString("(cyc-assert '"+form+ " #$DoomCurrentStateMt '(:DIRECTION :FORWARD :STRENGTH :MONOTONIC) )");
        if (!isAssertCyc) return;
        try {
            cyc.converseVoid(
                            "(clet ((*the-cyclist* #$CycAdministrator))\n" +
                            "   (without-wff-semantics (cyc-assert\n" +
                            "    '" + ((CycList)cyc.cyclify(form)).cyclify() + "\n" +
                            "    " + mudMt.cyclify() + " '(:DIRECTION :FORWARD :STRENGTH :MONOTONIC) )))");

        } catch (Exception e) {
            debugln(e);
            System.out.println(form);
        }
    }


    public String getAsItemType(String vnum) {
        vnum = vnum.trim();
        if (!vnum.startsWith("#$")) {
            vnum = "#$ArtifactCol" + vnum;
        }
        assertDataString("(find-or-create-constant \""+vnum.substring(2)+"\")");
        assertData("(#$isa " + vnum + " #$ArtifactTypeByGenericCategory)");
        assertData("(#$isa " + vnum + " #$BPVItemType)");        
        return vnum;
    }

    public String getAsArea(String vnum) {
        vnum = vnum.trim();
        if (!vnum.startsWith("#$")) {
            vnum = "#$Area"+vnum;
        }
        assertDataString("(find-or-create-constant \""+vnum.substring(2)+"\")");
        assertDataString("(cyc-assert '(#$isa " + vnum + " #$BPVLocation) #$UniversalVocabularyMt '(:DIRECTION :FORWARD))");
        //assertDataString("(cyc-assert '(#$isa " + vnum + " #$BPVLocation) #$UniversalVocabularyMt '(:DIRECTION :FORWARD))");
        return vnum;
    }

    static public String getAsNpcType(String vnum) {
        vnum = vnum.trim();
        if (!vnum.startsWith("#$")) {
            vnum = "#$NpcCol"+vnum;
        }
        assertDataString("(find-or-create-constant \""+vnum.substring(2)+"\")");
        assertDataString("(cyc-assert '(#$isa " + vnum + " #$BPVAgentType) #$UniversalVocabularyMt '(:DIRECTION :FORWARD))");
        assertDataString("(cyc-assert '(#$genls " + vnum + " #$Agent-Generic) #$UniversalVocabularyMt '(:DIRECTION :FORWARD))");
        return vnum;
    }

    public void getBitvectors(String vnum,String flags)  throws Exception  {
        try {
            int bits = Integer.parseInt(flags);
            if (bits>0) {
                for (int i = 0; i<16;i++) {
                    getBitvector(vnum,(new Double(Math.pow(2,i)).intValue()) & bits);
                }
            }
        } catch (Exception e) {
            char[] ca = flags.trim().toLowerCase().toCharArray();
            for (int i = 0; i<ca.length;i++) {
                getBitvector(vnum,ca[i]);
            }
        }

    }

    /*
    1) DARK       - self explanatory
2) DEATH      - room is a deathtrap - a forced quit
3) NO_MOB     - mobs will not enter this room
4) INDOORS    - weather has no effect
5) PEACEFUL   - no violence will work here 
6) SOUNDPROOF - tell, gossip, shout, holler will not be heard here.
7) NO_TRACK   - track will never find a path through this room
8) NO_MAGIC   - no magic will work here.
9) TUNNEL     - only one person allowed at one time
10) PRIVATE   - cannot enter if more than two persons there
11) GODROOM   - only for GODS of level 33 or above

    */

    public void getBitvector(String vnum,int alphanum)  throws Exception  {
        switch (alphanum) {
            case 0:
                break;
            case 1:
            case 'a':
                assertData("(#$isa "+vnum+ " #$Darkroom)");
                break;
            case 2:
            case 'b':
                assertData("(#$isa "+vnum+ " #$BattleZone)");
                break;
            case 4:
            case 'c':
                assertData("(#$isa "+vnum+ " NoMobRegion)");
                break;
            case 8:
            case 'd':
                assertData("(#$isa "+vnum+ " #$Indoors-IsolatedFromOutside)");
                break;
            case 16:
            case 'e':
                assertData("(#$isa "+vnum+ " #$FriendlyRegion)"); //PeacefullRegion
                break;
            case 32:
            case 'f':
                assertData("(#$isa "+vnum+ " SoundProofRegion)");
                break;
            case 64:
            case 'g':
                assertData("(#$isa "+vnum+ " #$FlankingPosition)");//NoTrackRegion
                break;
            case 128:
            case 'h':
                assertData("(#$isa "+vnum+ " NoMagicRegion)");
                break;
            case 256:
            case 'i':
                assertData("(#$isa "+vnum+ " #$CavityInteriorRegion)");
                break;
            case 512:
            case 'j':
                assertData("(#$isa "+vnum+ " #$MilitaryPost)"); //PrivateRegion
                break;
            case 1024:
            case 'k':
                assertData("(#$isa "+vnum+ " #$Airhead)"); //GodOnlyRegion
                break;
            case 2048:
            case 'l':
                assertData("(#$isa "+vnum+ " #$ModernShelterConstruction)");
                break;
            case 4096:
            case 'm':
                assertData("(#$isa "+vnum+ " #$SpaceInAHOC)");                
                // assertData("(#$isa "+vnum+ " HouseCrashRegion)");
                break;
            case 8192:
            case 'n':
                assertData("(#$isa "+vnum+ " #$Doorway)");
                assertData("(#$isa "+vnum+ " #$SpaceInAHOC)");                
                break;
            case 16384:
            case 'o':
                assertData("(#$isa "+vnum+ " OLCRegion)");
                break;
            case 32768:
            case 'p':
                assertData("(#$isa "+vnum+ " BFSMarkRegion)");
                break;
        }
    }

    public void getBittypes(String vnum,String flags) throws Exception {
        int flag = Integer.parseInt(flags);
        switch (flag) {
            case 0:
                assertData("(#$isa "+vnum+ " #$SpaceInAHOC)");
                break;
            case 1:
                assertData("(#$isa "+vnum+ " #$Street-Generic)");
                assertData("(#$isa "+vnum+ " #$OutdoorRegion)");
                assertData("(#$isa "+vnum+ " #$Outdoors-ExposedToWeather)");
                break;
            case 2:
                assertData("(#$isa "+vnum+ " #$Plain-Topographical)");
                assertData("(#$isa "+vnum+ " #$OutdoorRegion)");
                break;
            case 3:
                assertData("(#$isa "+vnum+ " #$Forest)");
                assertData("(#$isa "+vnum+ " #$OutdoorRegion)");
                assertData("(#$isa "+vnum+ " #$EcologicalRegion)");
                break;
            case 4:
                assertData("(#$isa "+vnum+ " #$Plain-Topographical)");
                assertData("(#$isa "+vnum+ " #$EcologicalRegion)");
                assertData("(#$isa "+vnum+ " #$OutdoorRegion)");
                assertData("(#$isa "+vnum+ " #$MountainRange)");
                break;
            case 5:
                assertData("(#$isa "+vnum+ " EcologicalRegion)");
                assertData("(#$isa "+vnum+ " OutdoorRegion)");
                assertData("(#$isa "+vnum+ " MountainRange)");
                break;
            case 6:
            case 7:
            case 8:
                assertData("(#$isa "+vnum+ " #$BodyOfWater)");
                assertData("(#$isa "+vnum+ " #$EcologicalRegion)");
                if (flag==7) {
                    assertData("(#$not (#$isa "+vnum+ " #$SwimmingRegion))");
                } else {
                    // 6,8
                    assertData("(#$isa "+vnum+ " #$SwimmingRegion)");   
                    if (flag==8) {
                        assertData("(#$isa "+vnum+ " #$UnderwaterRegion)");
                    }
                }
                break;
            case 9:
                assertData("(#$isa "+vnum+ " #$FreeSpaceContent)");
                //assertData("(#$isa "+vnum+ " #$AirspaceRegion)");   
                break;
            default:
                break;
        }
    }

    static public String quotedString(String stuff) {
        return "\""+stuff.trim().replace("\"","'")+"\"";
    }

    static public String readUpLine(BufferedReader br) {
        try {

            String ln = "";
            ln = ln.trim();
            while (ln.length()<1) {
                ln = br.readLine();
                ln = ln.trim();
            }
            return ln;
        } catch (Exception e) {
            debugln(e);
            return " " + e;
        }
    }

    static public String readUpTilde(BufferedReader br) throws Exception {
        String line = br.readLine().trim();
        if (line.endsWith("~")) {
            if (line.length()==1) return "";
            return line.substring(0,line.length()-1);
        }
        StringBuffer desc = new StringBuffer(line);
        while (!(line =br.readLine().trim()).endsWith("~")) {
            desc.append(" <br>" + line);
        }
        line = desc.toString();
        if (line.endsWith("~")) {
            return line.substring(0,line.length()-1);
        } else return line;
    }

    public void loadObjFile(File file) throws Exception{
        BufferedReader br = new BufferedReader( new FileReader(file));
        while (br.ready()) {
            try {
                parseObjects(br);
            } catch (Exception e) {
                debugln(e);
            }
        }
        br.close();
    }


    public void parseObjects(BufferedReader br) throws Exception {

        String line = br.readLine();
        if (line.startsWith("$")) return;

        // Read #<virtual number>
        parseObject(getAsItemType(line.substring(1).trim()),  br) ;

    }

    public void assertDataX(String vnum,String formula) {
        assertData("(#$implies (#$isa "+vnum+" " + vnum +  ") " + formula + " )");
    }

    public void  parseObject(String vnum, BufferedReader br) throws Exception {
        String line = null;
        // Read <alias list>~
        String[] kws = readUpTilde(br).split(" ");
        for (int i = 0 ; i < kws.length; i++) {
            assertData("(#$relationAllInstance #$nicknames "+vnum+" " + quotedString(kws[i]) + ")");
        }
        // Read <short description>~
        String shortDesc = readUpTilde(br);
        if (shortDesc.length()>0) assertData("(#$relationAllInstance #$termStrings "+vnum+" " + quotedString(shortDesc) + ")");
        // Read <long description>~
        String longDesc = readUpTilde(br);
        String [] splitter = longDesc.split("\\.");
        for (int i=0;i<splitter.length;i++) {
            if (isKeyword(splitter[i])) assertData("(#$relationAllInstance #$comment "+vnum+" "+quotedString(splitter[i])+")");
        }
        // Read <action description>~
        String actionDesc = readUpTilde(br);
        if (actionDesc.length()>0) {
            splitter = actionDesc.split("\\.");
            for (int i=0;i<splitter.length;i++) {
                if (isKeyword(splitter[i])) assertData("(#$relationAllInstance #$comment "+vnum+" "+quotedString(splitter[i])+")");
            }
        }
        // Read <type flag> <extra (effects) bitvector> <wear bitvector>
        String[] flags = readUpLine(br).split(" ");
        String[] values = readUpLine(br).split(" ");
        parseObjectType(vnum,Integer.parseInt(flags[0]),values);
        parseObjectAffectsWear(vnum,flags);
        // Read <weight> <cost> <rent per day>
        String[] wcr = readUpLine(br).split(" ");
        assertData("(#$relationAllInstance #$massOfObject "+vnum+" (#$Kilogram " + wcr[0] + "))");
        if (!wcr[1].equals("0")) assertData("(#$relationAllInstance #$cost "+vnum+" (#$Dollar-UnitedStates " + wcr[1] + "))");
        //    if (!wcr[2].equals("0")) assertData("(#$relationAllInstance #$rentalRate "+vnum+" (#$DollarsPerDay " + wcr[2] + " ))");
        if (!wcr[2].equals("0")) assertData("(#$relationAllInstance #$cost "+vnum+" (#$DollarsPerDay " + wcr[2] + " ))");

        try {
            while (!(line =  readUpLine(br).trim()).startsWith("$")) {
                if (line.startsWith("#")) {
                    vnum = getAsItemType(line.substring(1));
                    parseObject(vnum, br);
                    return;
                } else if (line.startsWith("E")) {
                    //Read <keyword list>~
                    String pkws[] = readUpTilde(br).split(" ");
                    //Read <description text>~
                    String kwDesc = readUpTilde(br);
                    assertData("(#$relationAllInstance #$comment "+vnum+" "+ quotedString(kwDesc) +")");
                    for (int i = 0; i < pkws.length; i++) {
                        //                        assertData("(#$implies (#$isa "+vnum+" " + vnum + ")(#$thereExists ?Obj (#$and (#$parts "+vnum+" ?Obj)(#$comment ?Obj "+ quotedString(kwDesc) +")(#$nicknames ?Obj "+  quotedString(pkws[i]) +" ))))");
                        assertData("(#$relationAllInstance #$nicknames "+vnum+" "+  quotedString(pkws[i]) +")");
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    /*
    
    
    */


    public void parseObjectType(String vnum, int flag0, String[]values ) {
        switch (flag0) {
            case 0:
                break;
            case 1:
                assertData("(#$relationAllInstance #$comment " + vnum+ " \"maybe a  #$LightingDevice\")");
                assertData("(#$relationAllInstance #$duration "+vnum+" "+values[2]+")");
                break;
            case 2:
                assertData("(#$genls "+vnum+ " #$Scroll)");
            case 10:
                // Potion
                if (flag0 == 2) {
                    assertData("(#$genls "+vnum+ "  #$InformationBearingObject)");
                    assertData("(#$genls "+vnum+ "  #$Paper)");
                } else {
                    assertData("(#$genls "+vnum+ " #$DrugSubstance)");
                }
                assertData("(#$relationAllInstance #$comment "+vnum+" \"mudLevelOf: "+values[0] + "\")");
                if (!values[1].equals("0")) assertData("(#$relationAllInstance #$comment "+vnum+" \""+values[1] + "\")");
                if (!values[2].equals("0")) assertData("(#$relationAllInstance #$comment "+vnum+" \""+values[2] + "\")");
                if (!values[3].equals("0")) assertData("(#$relationAllInstance #$comment "+vnum+" \""+values[3] + "\")");
                break;
            case 3:
                //Wand 
                assertData("(#$genls "+vnum+ " #$RodShapedObject)");
                assertData("(#$genls "+vnum+ " #$ControlDevice)");
                assertData("(#$genls "+vnum+ " #$HandTool)");
            case 4:
                //Staff 
                assertData("(#$genls "+vnum+ " #$RodShapedObject)");
                assertData("(#$genls "+vnum+ " #$ControlDevice)");
                if (flag0==4) assertData("(#$genls "+vnum+ " Weapon)");
                assertData("(#$relationAllInstance #$comment "+vnum+" \"mudLevelOf: "+values[0] + "\")");
                assertData("(#$relationAllInstance #$comment "+vnum+" \"chargeCapacity: "+values[1] + "\")");
                assertData("(#$relationAllInstance #$comment "+vnum+" \"chargeRemaining: "+values[2] + "\")");
                if (!values[3].equals("0")) assertData("(#$relationAllInstance #$comment "+vnum+" \""+values[3] + "\")");
                break;
            case 5:
                // Weapon
                assertData("(#$genls "+vnum+ " #$Weapon)");
            case 6:
                // FireWeapon
                assertData("(#$genls "+vnum+ " #$Weapon)");
            case 7:
                // MissileWeapon
                assertData("(#$genls "+vnum+ " #$Weapon)");

                if (flag0==6) {
                    assertData("(#$relationAllInstance #$comment " + vnum+ " \""+"FLAME"+"\")");
                    //    assertData("(#$genls "+vnum+ " MudFireWeapon)");
                    assertData("(#$genls "+vnum+ " #$ProjectileLauncher)");
                }
                if (flag0==7) {
                    assertData("(#$genls "+vnum+ " #$MissileLauncher-Direct)");
                    assertData("(#$genls "+vnum+ " #$ProjectileLauncher)");
                }
                assertData("(#$comment  "+vnum+ " "+quotedString("damageNumberDice "+ values[1]) + ")");
                assertData("(#$comment  "+vnum+ " "+quotedString("damageSizeDice "+ values[2]) + ")");
                parseObjectWeaponMsg(vnum, Integer.parseInt( values[3]));
                break;
            case 8:
                // TreasureObject
                //assertData("(#$isa "+vnum+ " TreasureObject)");
                assertData("(#$genls "+vnum+ " #$TenderObject)");
                break;
            case 9:
                // Armor
                assertData("(#$genls "+vnum+ " #$ProtectiveAttire)");
                assertData("(#$relationAllInstance #$comment " + vnum+ " \"armorLevel: "+values[0] + "\")");
                break;
            case 11:
                // SomethingToWear
                assertData("(#$genls "+vnum+ " #$SomethingToWear)");
                break;
            case 12:
                // Unkonwn
                assertData("(#$genls "+vnum+ " #$Artifact-Generic)");
                break;
            case 13:
                // Garbage
                assertData("(#$genls "+vnum+ " #$Garbage-Generic)");
                assertData("(#$relationAllInstance #$cost "+vnum+" (#$Dollar-UnitedStates 0))");
                break;
            case 14:
                // Trap
                assertData("(#$genls "+vnum+ " #$TrapDevice)");
                break;
            case 15:
                // Container
                assertData("(#$genls "+vnum+ " #$Container)");
                assertData("(#$relationAllInstance #$volumeContained "+vnum+" "+values[0] + ")");
                if (Integer.parseInt( values[2])>0)
                    assertData("(#$relationAllInstance #$controls "+ getAsItemType(values[2]) + " ?X)");
                break;
            case 16:
                // Note
                assertData("(#$genls "+vnum+ "  #$InformationBearingObject)");
                assertData("(#$genls "+vnum+ " #$Paper)");
                assertData("(#$relationAllInstance #$nativeLanguage "+vnum+" #$"+values[0] + ")");
                break;
            case 17:
                // Flask
                assertData("(#$genls "+vnum+ " #$Flask-LabGlassware)");
            case 23:
                // Bottle
                assertData("(#$genls "+vnum+ " #$Bottle)");
                assertData("(#$genls "+vnum+ " #$FluidReservoir)");
                assertData("(#$genls "+vnum+ " #$Container)");
                if (flag0==23) assertData("(#$genls "+vnum+ " #$BodyOfWater)");
                assertData("(#$relationAllInstance #$volumeOfObject "+vnum+" (#$Liter  "+values[0] + "))");
                assertData("(#$relationAllInstance #$volumeContained "+vnum+" (#$Liter  "+values[0] + "))");
                assertData("(#$genls "+vnum+ " #$Portal)");
                parseContainerState(vnum, Integer.parseInt( values[1] ));
                if (Integer.parseInt(values[3])>0) assertData("(#$relationAllInstance #$comment " + vnum+ " \"poisioned\")");
                String contents =  parseFluid(Integer.parseInt(values[2])); 
                assertData("(#$genls #$" + contents + " #$LiquidTangibleThing)");
                // TODO assertData("(#$relationAllInstance #$containedObject "+vnum+" (Liter " +contents + " " + values[1] + ")");
                break;
            case 18:
                // KEY
                assertData("(#$genls "+vnum+ " #$ControlDevice)");
                break;
            case 19:
                // FOOD
                assertData("(#$genls "+vnum+ " #$Food)");
                break;
            case 20:
                // GOLD
                assertData("(#$genls "+vnum+ " #$Currency)");
                break;
            case 21:
                // PENCIL
                assertData("(#$genls "+vnum+ " #$WritingDevice)");
                break;
            case 22:
                // BOAT
                assertData("(#$genls "+vnum+ " #$Watercraft)");
                break;
            default:
                break;
        }
    }


    public void parseContainerState(String vnum, int bit) {
        switch (bit) {
            case 1:
                assertData("(#$relationAllInstance #$portalState "+vnum+" #$OpenPortal )");
                assertData("(#$genls "+vnum+ " #$Device-Unlocked)");
                break;
            case 2:
                assertData("(#$genls "+vnum+ " #$LimitedAccess)"); //ContainerLocked-Pickproof
                assertData("(#$typeBehaviorIncapable "+vnum+ "#$UnlockingALock #$objectOfStateChange)");
                break;
            case 4:
                assertData("(#$relationAllInstance #$portalState "+vnum+" #$ClosedPortal )");
                break;
            case 8:
                assertData("(#$genls "+vnum+ " #$LimitedAccess)"); //ContainerLocked
                //assertData("(#$typeBehaviorCapable-DeviceUsed "+vnum+ "#$UnlockingALock)");
                break;
        }
    }


    /* 
    Type nr. Effect of Liquid on:   Drunkness Fullness Thirst 
    LIQ_WATER 0 0 1 10 
    LIQ_BEER 1 3 2 5 
    LIQ_WINE 2 5 2 5 
    LIQ_ALE 3 2 2 5 
    LIQ_DARKALE 4 1 2 5 
    LIQ_WHISKEY 5 6 1 4 
    LIQ_LEMONADE 6 0 1 8 
    LIQ_FIREBRT 7 10 0 0 
    LIQ_LOCALSPC 8 3 3 3 
    LIQ_SLIME 9 0 4 -8 
    LIQ_MILK 10 0 3 6 
    LIQ_TEA 11 0 1 6 
    LIQ_COFFEE 12 0 1 6 
    LIQ_BLOOD 13 0 2 -1 
    LIQ_SALTWATER 14 0 1 -2 
    LIQ_CLEARWATER 15 0 0 13 
    */
    public String parseFluid(int bit) {
        String  contents = "Oil";
        switch (bit) {
            case 0:
                contents = "Water-Fresh";
                break;
            case 1:
                contents = "Beer";
                break;
            case 2:
                contents = "Wine";
                break;
            case 3:
                contents = "AleBeer";
                break;
            case 4:
                contents = "LagerBeer";
                break;
            case 5:
                contents = "Whisky";
                break;
            case 6:
                contents = "Lemonade";
                break;
            case 7:
                contents = "FireBrt";
                break;
            case 8:
                contents = "LocalSpc";
                break;
            case 9:
                contents = "SlimeLayer-Bacterium";
                break;
            case 10:
                contents = "Milk";
                break;
            case 11:
                contents = "Tea-Beverage";
                break;
            case 12:
                contents = "Coffee-Beverage";
                break;
            case 13:
                contents = "Blood";
                break;
            case 14:
                contents = "SeaWater";
                break;
            case 15:
                contents = "Water-Fresh";
                break;
        }
        return contents;
        //        return "Fluid"+contents;
    }

    public void parseObjectAffectsWear(String vnum, String[] flags) {
        try {
            int bits = Integer.parseInt(flags[1]);
            if (bits>0) {
                for (int i = 0; i<9;i++) {
                    parseObjectAffectBit1(vnum,(new Double(Math.pow(2,i)).intValue()) & bits);
                }
            }
        } catch (Exception e) {
            char[] ca = flags[1].trim().toLowerCase().toCharArray();
            for (int i = 0; i<ca.length;i++) {
                parseObjectAffectBit1(vnum,ca[i]);
            }
        }

        try {
            int bits = Integer.parseInt(flags[2]);
            if (bits>0) {
                for (int i = 0; i<14;i++) {
                    parseObjectAffectBit2(vnum,(new Double(Math.pow(2,i)).intValue()) & bits);
                }
            }
        } catch (Exception e) {
            char[] ca = flags[2].trim().toLowerCase().toCharArray();
            for (int i = 0; i<ca.length; i++) {
                parseObjectAffectBit2(vnum,ca[i]);
            }
        }

    }

    public void parseObjectAffectBit1(String vnum, int bit) {
        switch (bit) {
            case 0:
                break;
            case 1:
            case 'a':
                //assertData("(#$isa "+vnum+ " MudGlow)");
                assertData("(#$relationAllInstance #$stateOfDevice "+vnum+" #$DeviceOn)");
                assertData("(#$genls "+vnum+ " #$LightingDevice)");
                break;
            case 2:
            case 'b':
                //                assertData("(#$isa "+vnum+ " MudHum)");
                assertData("(#$relationAllInstance #$stateOfDevice "+vnum+" #$DeviceOn)");
                assertData("(#$genls "+vnum+ " #$InformationStore)");
                break;
            case 4:
            case 'c':
                assertFlag(vnum,"MudNoRent");
                break;
            case 8:
            case 'd':
                assertFlag(vnum,"MudNoDonate");
                break;
            case 16:
            case 'e':
                assertData("(#$genls "+vnum+ " #$Opaque)");
                break;
            case 32:
            case 'f':
                assertData("(#$genls "+vnum+ " #$Transparent)");
                break;
            case 64:
            case 'g':
                assertFlag(vnum,"MudMagic");
                break;
            case 128:
            case 'h':
                assertFlag(vnum,"MudNoDrop");
                break;
            case 256:
            case 'i':
                assertFlag(vnum,"MudBlessed");
                break;
            default:
                break;
        }

    }
    /*
     0 light        7  legs        14 left_wrist
 1 left_finger  8  feet        15 right_wrist
 2 right_finger 9  hands       16 wield*
 3 neck_1       10 arms        17 hold
 4 neck_2       11 shield      18 float
 5 body         12 waist
 6 head         13 about_body
    */

    public String bodyPlace(String arg) {
        switch (Integer.parseInt(arg)) {
            case 0:
                return "#$TopOfHead";
            case 1:
                return "(#$RightObjectOfPairFn #$RingFinger)";
            case 2:
                return "(#$LeftObjectOfPairFn #$RingFinger)";
            case 3:
                return "#$Neck-AnimalBodyPart";
            case 4:
                return "#$Neck-AnimalBodyPart";
            case 5:
                return "#$Chest-BodyPart";
            case 6:
                return "#$Head-AnimalBodyPart";
            case 7:
                return "#$Leg";
            case 8:
                return "#$Foot-AnimalBodyPart";
            case 9:
                return "(#$RightObjectOfPairFn #$Hand)";
            case 10:
                return "(#$LeftObjectOfPairFn #$Arm)";
            case 11:
                return "(#$LeftObjectOfPairFn #$Hand)";
                //                return "AsSheild";
            case 12:
                return "#$Torso";
            case 13:
                return "#$Waist";
            case 14:
                return "(#$RightObjectOfPairFn #$Wrist)";
            case 15:
                return "(#$LeftObjectOfPairFn #$Wrist)";
            case 16:
                return "#$Trunk-BodyCore";
            case 17:
                return "#$Hand";
            case 18:
                return "Above";
        }
        return "AsHeld";
    }


    public void  parseObjectAffectBit2(String vnum, int bit) {
        switch (bit) {
            case 0:       //bodyPlace
                break;
            case 1:
            case 'a':
                assertData("(#$genls "+vnum+ " #$PortableObject)");
                break;
            case 2:
            case 'b':
                assertData("(#$wornOn-TypeType "+vnum+"  #$Finger)");
                assertData("(#$genls "+vnum+ " #$SomethingToWear)");
                break;
            case 4:
            case 'c':
                //assertData("(#$wornOn-TypeType "+vnum+" #$Neck)");
                assertData("(#$genls "+vnum+ " #$Necklace)");
                break;
            case 8:
            case 'd':
                assertData("(#$wornOn-TypeType "+vnum+" #$Torso)");
                assertData("(#$genls "+vnum+ " #$SomethingToWear)");
                break;
            case 16:
            case 'e':
                assertData("(#$relationAllInstance wornOn "+vnum+"  #$Head-AnimalBodyPart)");
                assertData("(#$genls "+vnum+ " #$SomethingToWear)");
                break;
            case 32:
            case 'f':
                assertData("(#$wornOn-TypeType "+vnum+"  #$Leg)");
                assertData("(#$genls "+vnum+ " #$SomethingToWear)");
                break;
            case 64:
            case 'g':
                assertData("(#$wornOn-TypeType "+vnum+"  #$Foot-AnimalBodyPart)");
                assertData("(#$genls "+vnum+ " #$SomethingToWear)");
                break;
            case 128:
            case 'h':
                assertData("(#$wornOn-TypeType "+vnum+"  #$Hand)");
                assertData("(#$genls "+vnum+ " #$SomethingToWear)");
                break;
            case 256:
            case 'i':
                assertData("(#$wornOn-TypeType "+vnum+"  #$Arm)");
                assertData("(#$genls "+vnum+ " #$SomethingToWear)");
                break;
            case 512:
            case 'j':
                assertData("(#$genls "+vnum+ " #$BodyArmor)");
                assertData("(#$genls "+vnum+ " #$SomethingToWear)");
                break;
            case 1024:
            case 'k':
                assertData("(#$wornOn-TypeType "+vnum+" #$Trunk-BodyCore)");
                assertData("(#$genls "+vnum+ " #$SomethingToWear)");
                break;
            case 2048:
            case 'l':
                assertData("(#$genls "+vnum+ " #$Belt-Clothing)");
                assertData("(#$genls "+vnum+ " #$SomethingToWear)");
                break;
            case 4096:
            case 'm':
                assertData("(#$genls "+vnum+ " #$Bracelet)");
                assertData("(#$genls "+vnum+ " #$SomethingToWear)");
                break;
            case 8192:
            case 'n':
                assertData("(#$genls "+vnum+ " #$Device-SingleUser)");
                assertData("(#$genls "+vnum+ " #$SomethingToWear)");
                break;
            case 16384:
            case 'o':
                assertData("(#$genls "+vnum+ " #$HandTool)"); //Device-SingleUser
                assertData("(#$genls "+vnum+ " #$PortableObject)");
                break;
            default:
                break;
        }
    }




    public void  parseObjectWeaponMsg(String vnum, int bit) {
        switch (bit) {
            case 0:
                assertFlag(vnum, "WeaponHitting");
                break;
            case 1:
                assertFlag(vnum, "StingingByAnimal");
                break;
            case 2:
                assertFlag(vnum, "Whipping");
                break;
            case 3:
                assertFlag(vnum, "WeaponSlashing");
                break;
            case 4:
                assertFlag(vnum, "WeaponBiting");
                break;
            case 5:
                assertFlag(vnum, "WeaponBludgeon");
                break;
            case 6:
                assertFlag(vnum, "WeaponCrushing");
                break;
            case 7:
                assertFlag(vnum, "WeaponPounding");
                break;
            case 8:
                assertFlag(vnum, "WeaponClawing");
                break;
            case 9:
                assertFlag(vnum, "WeaponMauling");
                break;
            case 10:
                assertFlag(vnum, "WeaponThrashing");
                break;
            case 11:
                assertFlag(vnum, "WeaponPeircing");
                break;
            case 12:
                assertFlag(vnum, "WeaponBlasting");
                break;
            case 13:
                assertFlag(vnum, "WeaponPunching");
                break;
            case 14:
                assertFlag(vnum, "WeaponStabing");
                break;
        }

    }


    public void loadMobFile(File file) throws Exception {
        assertData("");
        assertData(";; Sourcing Mob File " + file.getAbsoluteFile());
        BufferedReader br = new BufferedReader( new FileReader(file));
        try {
            while (br.ready()) parseMobs(br);
        } catch (Exception e) {
            debugln(e);
        }
        br.close();
    }



    public void parseMobs(BufferedReader br) throws Exception {

        String line = br.readLine();
        if (line.startsWith("$")) return;

        // Read #<virtual number>
        line = ""+line;
        assertData(";; mob " + line);
        if (line.length()<1) return;
        line = line.substring(1);
        parseMob( getAsNpcType(line.trim()),  br) ;
    }

    static public String introConstant(String vnum) {
        if (vnum.startsWith("#$")) {
            vnum = vnum.substring(2);
        }
        assertDataString("(find-or-create-constant \""+vnum+"\")" );
        return "#$"+vnum;
    }
    public static String amountString(double range, String value) {
        return amountString(range,Double.parseDouble(value));
    }
    public static String amountString(double range, double value) {
        value = value/range*100;
        if (value<1) return "NoAmount";
        if (value<10) return "VeryLowAmount";
        if (value<20) return "VeryLowToLowAmount";
        if (value<30) return "LowAmount";
        if (value<60) return "MediumAmount";
        if (value<70) return "MediumToVeryHighAmount";
        if (value<80) return "HighAmount";
        if (value<90) return "HighToVeryHighAmount";
        return "VeryHighAmount";

    }

    public void  parseMob(String vnum, BufferedReader br) throws Exception {
        String splitter[] = "".split(" ");
        String line = null;
        // Read <alias list>~
        String[] kws = readUpTilde(br).split(" ");
        for (int i = 0 ; i < kws.length; i++) {
            assertData("(#$nicknames "+vnum+" "+  quotedString(kws[i]) + ")");
        }
        // Read <short description>~
        String shortDesc = readUpTilde(br);
        if (shortDesc.length()>0) assertData("(#$relationAllInstance #$nameString "+vnum+" " + quotedString(shortDesc) + ")");
        //shortDesc = "StarTrek-"+shortDesc.replace(" ","").replace("'","");
        //introConstant(shortDesc);
        //assertData("(#$isa #$"+shortDesc+" "+vnum+")");

        // Read <long description>~
        String longDesc = readUpTilde(br);                     //mudAreaView
        splitter = longDesc.split("\\.");
        for (int i=0;i<splitter.length;i++) {
            if (isKeyword(splitter[i])) assertData("(#$relationAllInstance #$definiteDescriptions "+vnum+" "+quotedString(splitter[i])+")");
        }
        // Read <detailed description>~       // like LOOK
        String actionDesc = readUpTilde(br);
        if (actionDesc.length()>0) {
            splitter = actionDesc.split("\\.");
            for (int i=0;i<splitter.length;i++) {
                if (isKeyword(splitter[i])) assertData("(#$relationAllInstance #$personalIndentifyingCharacteristic "+vnum+" "+quotedString(splitter[i])+")");
            }
        }
        // Read <action bitvector> <affection bitvector> <alignment> <type flag>
        String[] flags = readUpLine(br).split(" ");
        try {
            int bits = Integer.parseInt(flags[0]);
            if (bits>0) {
                for (int i = 0; i<17;i++) {
                    parseMobBitV0(vnum,(new Double(Math.pow(2,i)).intValue()) & bits);
                }
            }
        } catch (Exception e) {
            char[] ca = flags[0].trim().toLowerCase().toCharArray();
            for (int i = 0; i<ca.length;i++) {
                parseMobBitV0(vnum,ca[i]);
            }
        }
        try {
            int bits = Integer.parseInt(flags[1]);
            if (bits>0) {
                for (int i = 0; i<17;i++) {
                    parseMobBitV1(vnum,(new Double(Math.pow(2,i)).intValue()) & bits);
                }
            }
        } catch (Exception e) {
            char[] ca = flags[1].trim().toLowerCase().toCharArray();
            for (int i = 0; i<ca.length;i++) {
                parseMobBitV1(vnum,ca[i]);
            }
        }
        //(#$and (#$isa #$netWorth #$QuantitySlot)(#$implies (#$isa "+vnum+" #$NpcCol1000)(#$netWorth "+vnum+" (#$USDollarFn 75000))))
        // Alignment flags[2]
        int allignment = Integer.parseInt(flags[2]);
        ///             -1000...-350   Evil
        //      -349...349    Neutral
        //       350...1000   Good
        String alignDir = "Goodness-Moral";
        if (allignment<0) alignDir ="Badness-Moral";
        String alignScale = amountString(900,Math.abs(allignment));


        //        assertData("(#$relationAllInstance #$moralCharacter "+vnum+" (#$"+alignScale+"Fn #$"+alignDir+"))");
        assertData("(#$relationAllInstance #$moralCharacter "+vnum+" (#$"+alignScale+"Fn #$"+alignDir+"))");

        // Read <level> <thac0> <armor class> <max hit points> <bare hand damage>          //46 3 -7 12d12+3200 9d9+42
        String[] ltamb = readUpLine(br).split(" ");
        String lvl = amountString(50,ltamb[0]);
        lvl = lvl.substring(0,1).toLowerCase()+lvl.substring(1);
        assertData("(#$relationAllInstance #$skillLevel  "+vnum+" #$SingleDoerAction #$Effectiveness #$"+lvl+"Of)");
        assertData("(#$comment  " + vnum + " \"+mudToHitArmorClass0: "+ltamb[1]+"\")");
        assertData("(#$relationAllInstance #$toughnessOfObject " + vnum + " #$"+amountString(-10,ltamb[2])+"Fn)"); //redForceStrengthInTask 
        assertData("(#$comment  " + vnum + " "+quotedString("mudMaxHitPoints: "+ltamb[3])+")");
        //assertData("(skillLevel "+vnum+" PunchingSomething performedBy Effectiveness highAmountOf))
        assertData("(#$comment  " + vnum + " "+quotedString("#$PunchingSomething mudBareHandDamage: "+ltamb[4])+")");
        // Read <gold> <experience points>
        String[] ge = readUpLine(br).split(" ");      
        assertData("(#$relationAllInstance #$netWorth "+vnum+" (#$USDollarFn "+ge[0]+"))");
        String exp = amountString(800000,ge[1]); //mudExperience
        exp = exp.substring(0,1).toLowerCase()+exp.substring(1);
        assertData("(#$relationAllInstance #$skillLevel  "+vnum+" #$IntentionalMentalEvent #$Effectiveness #$"+exp+"Of)");
        // Read <load position> <default position> <sex>
        String[] lps = readUpLine(br).split(" ");
        assertData("(#$possible (#$relationAllInstance #$postureOfAnimal "+vnum+" #$"+ getPosture(lps[0])+"))");
        //assertData("(#$relationExistsInstance #$postureOfAnimal " + vnum + " #$"+getPosture(lps[1])+")");
        assertData("(#$genls " + vnum + " #$" + getGender(lps[2])+")");
        // Type flags[3]
        if (flags[3].startsWith("S")) return;
        while (!(line =  readUpLine(br).trim()).startsWith("$")) {
            if (line.startsWith("E")) return;
        }
    }

    public String getGender(String flag) {
        switch (flag.charAt(0)) {
            case '0':
                return "NeuterObject";
            case '1':
                return "MaleAnimal";
            case '2':
                return "FemaleAnimal";
        }
        return "NeuterObject";
    }

    public String getPosture(String flag) {
        switch (flag.charAt(0)) {
            case '0':
                return "Deceased";
            case '1':
                return "Injured";
            case '2':
                return "Incapacitated";
            case '3':
                return "Shock-PhysiologicalCondition";
            case '4':
                return "Sleeping";
            case '5':
                return "RecliningPosture";
            case '6':
                return "SittingPosture";
            case '7':
                return "Battle";
            case '8':
                return "UprightPosture";
        }
        return "UprightPosture";
    }

    public void assertFlag(String vnum,String flagname) {
        //        assertData("(#$relationAllInstance #$programStrings  "+vnum+  " \""+flagname+"\")" );  
        //assertData("(#$programStrings  "+vnum+  " \""+flagname+"\")" );  
        assertData("(#$comment  "+vnum+  " \""+flagname+"\")" );  
    }


    public void parseMobBitV0(String vnum, int flag) {
        switch (flag) {
            case 0:
                break;
            case 1:
            case 'a':
                assertFlag(vnum,"ACT_SPEC");
                assertFlag(vnum,"the mobile has a special procedure (referred to as a spec) programmed in C connected to it.");  
                break;
            case 2:
            case 'b':
                assertFlag(vnum,"ACT_SENTINEL");   
                assertData("(#$frequencyOfActionType #$LeavingAPlace "+vnum+" #$bodilyDoer #$Never)");  //SENTINEL
                break;
            case 4:
            case 'c':
                assertFlag(vnum,"ACT_SCAVENGER");   
                assertData("(#$genls "+vnum+ " #$Scavenger)");    //The mob should pick up valuables it finds on the ground.  More expensive items will be taken first.
                assertData("(#$frequencyOfActionType #$Scavenging-Recon "+vnum+" #$bodilyDoer #$Often)");   //SCAVENGER
                break;
            case 8:
            case 'd':
                assertFlag(vnum,"ACT_ISNPC");   
                break;
            case 16:
            case 'e':
                assertFlag(vnum,"ACT_NICE_THIEF");
                assertFlag(vnum,"AWARE");
                assertFlag(vnum,"NOBACKSTAB");
                //                AWARE          Set for mobs which cannot be backstabbed. 
                //               Replaces the ACT_NICE_THIEF bit from Diku Gamma.   A mobile with this bit set will not attack a thief which has been caught in the act of stealing.
                break;
            case 32:
            case 'f':   
                assertFlag(vnum,"AGRESSIVE");
                assertData("(#$genls "+vnum+ " #$Vicious)");
                break;
            case 64:
            case 'g':
                assertFlag(vnum,"ACT_STAY_ZONE");
                break;
            case 128:
            case 'h':
                assertFlag(vnum,"ACT_WIMPY");// (MediumToVeryHighAmountFn Fear)
                assertFlag(vnum,"wimpy mobile will try to flee when it gets low on hit points. A mobile which is both aggressive and wimpy will not attack a player that is awake.");
                break;
            case 256:
            case 'i':
                //ACT_SWITCH A mobile with this bit set will switch target during fight.
                assertFlag(vnum,"ACT_SWITCH");
                assertFlag(vnum,"AGGR_EVIL");//   (VeryHighAmountFn Goodness-Moral)
                break;
            case 512:
            case 'j':  //Anyone with this bit set can only be statted by an implementor. This can be useful if you want to hide the internal stats of a mobile for lower level immortals. Note: this flag can also be set for players.
                assertFlag(vnum,"MOB_PRIVATE");
                assertFlag(vnum,"AGGR_GOOD");//   (VeryHighAmountFn Badness-Moral)
                break;
            case 1024:             //ACT_DEATHLOG
            case 'k':
                assertFlag(vnum,"AGGR_NEUTRAL");
                assertFlag(vnum,"ACT_DEATHLOG");
                //                assertData("(#$isa "+vnum+ " NPCAgroNeutral)"); //Terrorist
                assertData("(#$genls "+vnum+ " #$SerialKiller)"); //Terrorist
                break;
            case 2048:
            case 'l':
                assertFlag(vnum,"MEMORY");
                assertData("(#$frequencyOfActionType #$RevengeAction "+vnum+" #$bodilyDoer #$Often)");
                break;
            case 4096:
            case 'm': //NPCHelper
                assertFlag(vnum,"HELPER"); 
                assertFlag(vnum,"ACT_FRIEND"); //
                assertData("(#$typeBehaviorCapable-PerformedBy "+vnum+ " #$HelpingAnAgent)"); //HelpingAnAgent
                assertData("(#$frequencyOfActionType #$ProtectingSomething "+vnum+" #$bodilyDoer #$Often)");
                break;
            case 8192:
            case 'n': //NPCNoCharmed
                assertFlag(vnum,"NOCHARM"); 
                assertData("(#$typeBehaviorIncapable "+vnum+ " #$InfluencingAnAgent #$recipientOfInfo)");
                break;
            case 16384:
            case 'o':           // recipientOfInfo
                assertFlag(vnum,"NOSUMMON"); //
                assertData("(#$typeBehaviorIncapable "+vnum+ " #$CoercingAnAgentToMove #$objectMoving)");
                //                assertData("(#$isa "+vnum+ " NPCNoSummoned)");
                break;
            case 32768:
            case 'p':  // NPC No sleep
                assertFlag(vnum,"NOSLEEP"); //
                assertData("(#$typeBehaviorIncapable "+vnum+ "  #$Sleeping #$bodilyDoer)");
                break;
            case 65536:
            case 'q':  // Large mobs such as trees that cannot be bashed. NoBASH
                assertFlag(vnum,"NOBASH"); //
                assertData("(#$typeBehaviorIncapable "+vnum+ " #$IncurringPhysicalDamage #$damages)");
                break;
            case 131072:
            case 'r':
                assertFlag(vnum,"NOBLIND"); //
                //assertData("(#$not (#$genls "+vnum+ " #$Blind))");
                //     (anatomicallyCapableOf "+vnum+" VisualPerception doneBy)) 
                //                assertData("(#$typeBehaviorIncapable "+vnum+ " IncurringPhysicalDamage damages)");
                break;
            default:
                break;
        }
    }

    public void parseMobBitV1(String vnum, int flag) {
        switch (flag) {
            case 0:
                break;
            case 1:
            case 'a':
                assertFlag(vnum,"NPC_BLIND");
                assertData("(#$genls "+vnum+" #$BlindAnimal)");
                break;
            case 2:
            case 'b':
                assertFlag(vnum,"NPC_INVISIBLE");
                assertData("(#$genls "+vnum+" #$Transparent)");
                break;
            case 4:
            case 'c':
                assertFlag(vnum,"NPC_DETECT_ALIGN");
                break;
            case 8:
            case 'd':
                assertFlag(vnum,"NPC_DETECT_INVIS");
                break;
            case 16:
            case 'e':
                assertFlag(vnum,"NPC_DETECT_MAGIC");
                break;
            case 32:
            case 'f':
                assertFlag(vnum,"NPC_SENSE_LIFE");
                break;
            case 64:
            case 'g':
                //                assertData("(behaviorCapable "+vnum+ " NPC_WATERWALK)");
                //                assertData("(#$typeBehaviorCapable "+vnum+ " ?Role ?Event))");
                assertFlag(vnum,"NPC_WATERWALK");
                break;
            case 128:
            case 'h':
                assertFlag(vnum,"NPC_SANCTUARY");
                break;
            case 256:
            case 'i':
                assertFlag(vnum,"NPC_GROUP");
                assertData("(#$genls "+vnum+ " (GroupFn Agent-Generic))");
                break;
            case 512:
            case 'j':
                assertFlag(vnum,"NPC_CURSE");
                break;
            case 1024:
            case 'k':
                assertFlag(vnum,"NPC_INFRAVISION");
                break;
            case 2048:
            case 'l':
                assertFlag(vnum,"NPC_POISON");
                break;
            case 4096:
            case 'm':
                assertFlag(vnum,"NPC_PROTECT_EVIL");
                break;
            case 8192:
            case 'n':
                assertFlag(vnum,"NPC_PROTECT_GOOD");
                break;
            case 16384:
            case 'o':
                assertFlag(vnum,"NPC_SLEEP");
                break;
            case 32768:
            case 'p':
                assertFlag(vnum,"NPC_NOTRACK");
                break;
            case 65536:
            case 'q':
                assertFlag(vnum,"NPC_UNUSED16");
                break;
            case 131072:
            case 'r':
                assertFlag(vnum,"NPC_UNUSED17");
                break;
            case 262144:
            case 's':
                assertFlag(vnum,"NPC_SNEAK");
                break;
            case 524288:
            case 't':
                assertFlag(vnum,"NPC_HIDE");
                break;
            case 1048576:
            case 'u':
                assertFlag(vnum,"NPC_UNUSED20");
                break;
            case 2097152:
            case 'v':
                assertFlag(vnum,"NPC_CHARM");
                break;
            default:
                break;
        }
    }

    public void assertData(String pred,String vnum,String classed,String value,String Desc) {
        assertData("(" + pred + " " + vnum + " " + classed + "_" + value + ")");
    }

    public void loadZonFile(File file) throws Exception {
        assertData("");
        assertData(";; Sourcing " + file.getAbsoluteFile());
        BufferedReader br = new BufferedReader( new FileReader(file));
        try {
            while (br.ready()) parseZone(br);
        } catch (Exception e) {
            debugln(e);
        }
        br.close();
    }



    public void parseZone(BufferedReader br) throws Exception {
        // Read #<virtual number>
        String line = br.readLine();
        if (line.startsWith("$")) return;
        if (line.length()<2) return;
        // Read <zone name>~
        readUpTilde(br);
        // Read <top room number> <lifespan> <reset mode>
        String timer = readUpLine(br).split("0")[1];
        parseZoneParts(timer,br) ;
    }

    static int objnumint = 666;
    public static String newObject(String name, String type) {
        name = name.trim();
        String tthisobj = type+"-"+name.replace(" ","-").replace(".","").replace("'","").replace("\"","").replace("(","").replace(")","")+((int)objnumint++);
        tthisobj = tthisobj.replace("--","-");
        tthisobj = tthisobj.replace("--","-");
        introConstant(tthisobj);
        introConstant(type);
        assertData("(#$isa "+tthisobj + " " + type+")");
        assertData("(#$properNameStrings "+tthisobj+ " " + quotedString(name)+ ")");
        return tthisobj;
    }

    static String npc = null;
    static String thisnpc = null;
    static String thisobj = null;
    static String objtype = null;
    public void parseZoneParts(String timer, BufferedReader br) throws Exception {
        String line = null;
        String thisname = null;
        int start = 0;
        while (!(line =  readUpLine(br).trim()).startsWith("$")) {
            line = line.replace("\t"," ");
            line = line.replace("  "," ");
            String[] arg = line.split(" ");
            switch (line.charAt(0)) {
                case '*':
                    break;
                case 'S':
                    return;
                case 'M':
                    npc = getAsNpcType(arg[2]);
                    start = line.indexOf(arg[5]);
                    thisname = line.substring(start);
                    thisnpc = newObject(thisname,npc);
                    assertData("(#$in-ContCompletely "+thisnpc+" "+ getAsArea( arg[4]) +")");
                    break;
                case 'E':     
                    //Format: E <if-flag> <obj vnum> <max existing> <equipment position>
                    //E 1 1005 30 8			Boots
                    objtype = getAsItemType(arg[2]);
                    start = line.indexOf(arg[4]);
                    thisname = line.substring(start+2);
                    thisobj = newObject(thisname,objtype);
                    assertData("(#$wornOn "+thisobj+" (#$The (#$BodyPartCollectionFn " + thisnpc +" "+bodyPlace(arg[4])+ ")) )");

                    break;
                case 'O':
                    // 
                    objtype = getAsItemType(arg[2]);
                    start = line.indexOf(arg[5]);
                    thisname = line.substring(start);
                    thisobj = newObject(thisname,objtype);
                    assertData("(#$in-ContCompletely "+thisobj+" "+ getAsArea( arg[4]) +")");
                    break;
                case 'G':
                    objtype = getAsItemType(arg[2]);
                    start = line.indexOf(arg[3]);
                    thisname = line.substring(start);
                    thisobj = newObject(thisname,objtype);
                    assertData("(#$possesses "+thisnpc+"  " +  thisobj+ ") ");
                    break;
                case 'P':
                    assertData("(#$relationExistsInstance #$in-ContCompletely "+ getAsItemType(arg[2]) + "  "+getAsItemType(arg[4]) + " )");
                    break;
                case 'D':  //Format: D <if-flag> <room vnum> <exit num> <state>
                    //                    assertData("(#$mudExitState "+ getAsArea(arg[2]) + "  #$" + getDirectionWards(arg[3]) + " #$Portal"+getDirectionState( arg[4]) + " )");
                    assertData("(#$isa (#$BoundsOfDirectionFn "+ getAsArea(arg[2]) + "  #$" + getDirectionWards(STATIC_neswud.charAt(new Integer(arg[3].substring(0,1)).intValue())) + ") "+getDirectionState( arg[4]) + " )");
                    break;
                case 'R':
                    break;
                default:
                    break;
            }
        }
    }
}




