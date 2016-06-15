/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 C. Ian Short
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*
* This version - molecular line lists - adapted from LineListServer.java for the NIST atomic line list
*  So far (June 2016) this is focused specifically on the 48TiO line list from Bertrand Plez
 */
package linelistserver;

//import java.nio.file;
//import java.io.BufferedWriter;
import java.io.BufferedReader;
//import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
//import java.nio.file.Files;  //java.nio.file not available in Java 6
//import java.nio.file.Paths;  //java.nio.file not available in Java 6
//import java.nio.file.Path;  //java.nio.file not available in Java 6
import java.io.File;  
import java.nio.charset.Charset;
import java.text.*;
import java.text.DecimalFormat;

/**
 *
 * @author Ian
 */
public class MolListServer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        // Argument 0: Name of ascii input line list
        // Argument 1: Name of byte data output line list
        String asciiListStr = args[0];
        String byteListStr = args[1];

//
////
////Abundance table adapted from PHOENIX V. 15 input bash file
////Solar abundances:
//// c='abundances, Anders & Grevesse',
//

  double logE = Math.log10(Math.E); // for debug output
  double logE10 = Math.log(10.0); //natural log of 10

// These atomic and molecular data are really just here for the human reader's
// reference - they're not actually used by the code:
  int nelemAbnd = 40;
  int[] nome = new int[nelemAbnd];
  String[] cname = new String[nelemAbnd];
//nome is the Kurucz code - in case it's ever useful
  nome[0]=   100;
  nome[1]=   200;
  nome[2]=   300;
  nome[3]=   400;
  nome[4]=   500;
  nome[5]=   600;
  nome[6]=   700;
  nome[7]=   800;
  nome[8]=   900;
  nome[9]=  1000;
  nome[10]=  1100;
  nome[11]=  1200;
  nome[12]=  1300;
  nome[13]=  1400;
  nome[14]=  1500;
  nome[15]=  1600;
  nome[16]=  1700;
  nome[17]=  1800;
  nome[18]=  1900;
  nome[19]=  2000;
  nome[20]=  2100;
  nome[21]=  2200;
  nome[22]=  2300;
  nome[23]=  2400;
  nome[24]=  2500;
  nome[25]=  2600;
  nome[26]=  2700;
  nome[27]=  2800;
  nome[28]=  2900;
  nome[29]=  3000;
  nome[30]=  3100;
  nome[31]=  3600;
  nome[32]=  3700;
  nome[33]=  3800;
  nome[34]=  3900;
  nome[35]=  4000;
  nome[36]=  4100;
  nome[37]=  5600;
  nome[38]=  5700;
  nome[39]=  5500;

  cname[0]="H";
  cname[1]="He";
  cname[2]="Li";
  cname[3]="Be";
  cname[4]="B";
  cname[5]="C";
  cname[6]="N";
  cname[7]="O";
  cname[8]="F";
  cname[9]="Ne";
  cname[10]="Na";
  cname[11]="Mg";
  cname[12]="Al";
  cname[13]="Si";
  cname[14]="P";
  cname[15]="S";
  cname[16]="Cl";
  cname[17]="Ar";
  cname[18]="K";
  cname[19]="Ca";
  cname[20]="Sc";
  cname[21]="Ti";
  cname[22]="V";
  cname[23]="Cr";
  cname[24]="Mn";
  cname[25]="Fe";
  cname[26]="Co";
  cname[27]="Ni";
  cname[28]="Cu";
  cname[29]="Zn";
  cname[30]="Ga";
  cname[31]="Kr";
  cname[32]="Rb";
  cname[33]="Sr";
  cname[34]="Y";
  cname[35]="Zr";
  cname[36]="Nb";
  cname[37]="Ba";
  cname[38]="La";
  cname[39]="Cs";

//Associate diatomic molecules with each element that forms significant molecules:
//Initialize arrays:
  int numAssocMols = 4; //max number of associated molecules
  String[][] cnameMols = new String[nelemAbnd][numAssocMols];
  for (int iElem = 0; iElem < nelemAbnd; iElem++){
     for (int iMol = 0; iMol < numAssocMols; iMol++){
         cnameMols[iElem][iMol] = "None";
     }  //iMol loop
  } //iElem loop
//CAUTION: cnameMols names should match mnames names in general list of molecules blow
//List the four molecular species most likely to deplete the atomic species A
  cname[0]="H";
  cnameMols[0][0] = "H2";
  cnameMols[0][1] = "H2+";
  cnameMols[0][2] = "CH";
  cnameMols[0][3] = "OH";
  cname[1]="He";
  cname[2]="Li";
  cname[3]="Be";
  cname[4]="B";
  cname[5]="C";
  cnameMols[5][0] = "CH";
  cnameMols[5][1] = "CO";
  cnameMols[5][2] = "CN";
  cnameMols[5][3] = "C2";
  cname[6]="N";
  cnameMols[6][0] = "NH";
  cnameMols[6][1] = "NO";
  cnameMols[6][2] = "CN";
  cnameMols[6][3] = "N2";
  cname[7]="O";
  cnameMols[7][0] = "OH";
  cnameMols[7][1] = "CO";
  cnameMols[7][2] = "NO";
  cnameMols[7][3] = "O2";
  cname[8]="F";
  cname[9]="Ne";
  cname[10]="Na";
  cname[11]="Mg";
  cnameMols[11][0] = "MgH";
  cname[12]="Al";
  cname[13]="Si";
  cnameMols[13][0] = "SiO";
  cname[14]="P";
  cname[15]="S";
  cname[16]="Cl";
  cname[17]="Ar";
  cname[18]="K";
  cname[19]="Ca";
  cnameMols[19][0] = "CaH";
  cnameMols[19][1] = "CaO";
  cname[20]="Sc";
  cname[21]="Ti";
  cnameMols[21][0] = "TiO";
  cname[22]="V";
  cnameMols[22][0] = "VO";
  cname[23]="Cr";
  cname[24]="Mn";
  cname[25]="Fe";
  cnameMols[25][0] = "FeO";
  cname[26]="Co";
  cname[27]="Ni";
  cname[28]="Cu";
  cname[29]="Zn";
  cname[30]="Ga";
  cname[31]="Kr";
  cname[32]="Rb";
  cname[33]="Sr";
  cname[34]="Y";
  cname[35]="Zr";
  cname[36]="Nb";
  cname[37]="Ba";
  cname[38]="La";
  cname[39]="Cs";

//Diatomic molecules:
  int nMols = 18;
//  var nMols = 1;
  String[] mname = new String[nMols];
  String[] mnameA = new String[nMols];
  String[] mnameB = new String[nMols];

//CAUTION: The molecular number densities, N_AB, will be computed, and will deplete the atomic species, in this order!
// Put anything where A or B is Hydrogen FIRST - HI is an inexhaustable reservoir at low T
// Then rank molecules according to largest of A and B abundance, "weighted" by dissociation energy - ??
//
// For constituent atomic species, A and B, always designate as 'A' whichever element participates in the
//  *fewest other* molecuels - we'll put A on the LHS of the molecular Saha equation

  mname[0] = "H2";
  mnameA[0] = "H";
  mnameB[0] = "H";
  mname[1] = "H2+";
  mnameA[1] = "H";
  mnameB[1] = "H";
  mname[2] = "OH";
  mnameA[2] = "O";
  mnameB[2] = "H";
  mname[3] = "CH";
  mnameA[3] = "C";
  mnameB[3] = "H";
  mname[4] = "NH";
  mnameA[4] = "N";
  mnameB[4] = "H";
  mname[5] = "MgH";
  mnameA[5] = "Mg";
  mnameB[5] = "H";
  mname[6] = "CaH";
  mnameA[6] = "Ca";
  mnameB[6] = "H";
  mname[7] = "O2";
  mnameA[7] = "O";
  mnameB[7] = "O";
  mname[8] = "CO";
  mnameA[8] = "C";
  mnameB[8] = "O";
  mname[9] = "C2";
  mnameA[9] = "C";
  mnameB[9] = "C";
  mname[10] = "NO";
  mnameA[10] = "N";
  mnameB[10] = "O";
  mname[11] = "CN";
  mnameA[11] = "C";
  mnameB[11] = "N";
  mname[12] = "N2";
  mnameA[12] = "N";
  mnameB[12] = "N";
  mname[13] = "FeO";
  mnameA[13] = "Fe";
  mnameB[13] = "O";
  mname[14] = "SiO";
  mnameA[14] = "Si";
  mnameB[14] = "O";
  mname[15] = "CaO";
  mnameA[15] = "Ca";
  mnameB[15] = "O";
  mname[16] = "TiO";
  mnameA[16] = "Ti";
  mnameB[16] = "O";
  mname[17] = "VO";
  mnameA[17] = "V";
  mnameB[17] = "O";


  String species;


//
//     FILE I/O Section
//
//External line list input file approach:

String dataPath = "./InputData/";
String lineListFile = dataPath + asciiListStr + ".dat";
//String lineListFile = "gsLineList.dat";
//Path path = Paths.get(dataPath + lineListFile); //java.nio.file not available in Java 6
Charset charset = Charset.forName("US-ASCII");
String pattern = "0.0000000000000000";
//String pattern = "###.####";
DecimalFormat myFormatter = new DecimalFormat(pattern);

// We have Java SE 6 - we don't have the java.nio package!
//From http://www.deepakgaikwad.net/index.php/2009/11/23/reading-text-file-line-by-line-in-java-6.html
//

System.out.println(" *********************************************** ");
System.out.println("  ");
System.out.println("  ");
System.out.println("BEFORE FILE READ");
System.out.println("  ");
System.out.println("  ");
System.out.println(" *********************************************** ");


        FileLineByLine fileLBL = new FileLineByLine();

        String masterLineString = fileLBL.readFileLineByLine(lineListFile);

System.out.println(" *********************************************** ");
System.out.println("  ");
System.out.println("  ");
System.out.println("AFTER FILE READ");
System.out.println("  ");
System.out.println("  ");
System.out.println(" *********************************************** ");

        String[] arrayLineString = masterLineString.split("%%"); 
//Number of lines MUST be the ONLY entry on the first line 

       // int numLineList = Integer.parseInt(arrayLineString[0]);
        //System.out.println("arrayLineString[0] " + arrayLineString[0]);
        int list2Length = arrayLineString.length - 1; //useful for checking if something's wrong?
        int numLineList = list2Length;
        System.out.println("numLineList " + numLineList + " list2Length " + list2Length); 
//        for (int i = 0; i < 5; i++){
//           System.out.println(arrayLineString[i]);
//        }

 //Plez' line list is too large for me to insert header info - no header.
 // content records start on line 0
 // No block structure - just one record per line on every line 
      // String startKey = "START:";
       String testField= "";
       int startLine = 0; //initialization
      // for (int i = 1; i < list2Length; i++){
      //      //System.out.println("i " + i + " arrayLineString[i] " + arrayLineString[i]); 
      //      testField = arrayLineString[i].substring(0, 6);
      //      if (testField.equals(startKey)){
      //            break;  //We found it
      //          }
      //      startLine++;
      //   } 
//            startLine++; //one more
     System.out.println("list2Length " + list2Length + " numLineList " + numLineList + " startLine " + startLine); 
     //System.out.println("arrayLineString[startLine] " + arrayLineString[startLine]); 

//Okay, here we go:
        System.out.println("numLineList " + numLineList);


 int list2_ptr = 0; //pointer into line list2 that we're populating
 int array_ptr = 0; //pointer into array containing line list2 data file line-by-line Strings

//TiO list from Plez is not organized into blocks:    
//     //First line in block of six is always a blank separator line:
// int numBlocks = (list2Length - (startLine+1))/6 - 1;
//// int rmndr = (list2Length - (startLine+1)) % 6;
// int rmndr = 0; //for now - something's wrong
// System.out.println("numBlocks " + numBlocks + " rmndr " + rmndr); 
//
// Here's what we have in Plez TiO line list linelist_reduced48_all_deltacorr_lab.dat from
// lambda_air(A)   gf         Elow(cm-1)  vl  Jl    Nl  syml  Eup(cm-1) vu   Ju    Nu  symu  gamrad    mol trans branch
// sample line:  
// 9496135.0778 1.771248E-14  22555.3291  14 138.0 139.0  0  22565.8568   1 139.0 139.0  0 2.443384E+05 'TiO X E QR23  '
 String myString, subField1, subField2, systemName, log10gfString, gfString, gwL;  //useful helper
 double eInvcm, eEv, lambdaA, lambdanm, log10gf, log10GammaRad;
 boolean blankFlag;

//Default initialization:
  log10gf = -14.0; 
  log10gfString = "-14.0";
  systemName = " ";
  myString  = " ";
  lambdaA = 3000.0;
  lambdanm = 300.0;

//These are the *output* separators:
 String newField = " | "; //field separator - consistent with NIST ascii output 
 String newRecord = "%%"; //record separator
 String masterStringOut = ""; //initialize master string for output 
 int numFields = 17; //number of INPUT fields in NIST ascii dump
 // Input filds:
 // 0: element + ion stage, 1: lambda_0, 2: A_ij, 3: f, 4: log(gf), 5: "Acc." - ??, 6: E_i - E_j, 7: J_i, 8: J_j
   //String[] thisRecord = new String[numFields];
   String thisRecord;

//TiO list from Plez is not organized into blocks:    
//     for (int iBlock = 0; iBlock < numBlocks; iBlock++){

      // int offset = startLine + 6 * iBlock + 1;
       //for (int i = 1; i < 6; i++){
       for (int i = 0; i < numLineList; i++){
      //testing for (int i = 0; i < 10000; i++){  //for testing

       ////    array_ptr = offset + i;
           array_ptr = i;
  //System.out.println("i " + i + " array_ptr " + array_ptr);
  //System.out.println("arrayLineString " + arrayLineString[array_ptr]); 

//Cannot use split() - splits at *Every single* white space character!
  // fields separated with just white space
          //  thisRecord = arrayLineString[array_ptr].split(" "); 
    //for consistency with treatment when we CAN use split():
          thisRecord = arrayLineString[array_ptr];
//Get the electronic transition from ifields [14] and [15] first - we'll only bother with the
//other fields and add the transition to the output list if it's a system we're interested in
           //Fields [14] and [15] identify the electronic transition (upper and lower systems) - kluge them
           // them into one string identifying the transition:
           //testField = thisRecord[14]; 
           testField = thisRecord.substring(108, 109); 
           blankFlag = true;
               subField1 = testField.trim();
               if (subField1.length() > 0){
                  blankFlag = false; 
                 }
           if (blankFlag){
               subField1 = " ";
               //System.out.println("blankFlag triggered, myString = " + myString); 
             } else {
               subField1 = testField.trim();
             }
           //testField = thisRecord[15]; 
           testField = thisRecord.substring(110, 111); 
           blankFlag = true;
               subField2 = testField.trim();
               if (subField2.length() > 0){
                  blankFlag = false; 
                 }
           if (blankFlag){
               subField2 = " ";
               //System.out.println("blankFlag triggered, myString = " + myString); 
             } else {
               subField2 = testField.trim();
             }
            systemName = subField1 + "-" + subField2;
            //System.out.println("systemName " + systemName);
           //Get the wavelength - it's in A 
           //myString = thisRecord[0];
           myString = thisRecord.substring(2, 14);
           myString = myString.trim();
//Convert to nm for consistency with NIST atomic line list handling
           lambdaA = Double.parseDouble(myString);
           //base 10 loggf value:
           //myString = thisRecord[1];
           gfString = thisRecord.substring(15, 27);
           //We need to be ready for blank fields - checking for this in Java is hard!
           //
           //I don't think Plez loads up his line list data with blank fields the way NIST
           //does (!), so we probably don't need these checks, but since we've inherited them,
           //let's keep 'em to be safe:
           blankFlag = true;
               if (gfString.trim().length() > 0){
                  blankFlag = false; 
                 }
           if (blankFlag){
               log10gfString = "-14.0";   //base 10
               log10gf = -14.0;
               } else { 
               log10gf = Math.log10(Double.parseDouble(gfString));
               log10gfString = Double.toString(log10gf); 
               }

   //Only parse the other fields of this record and add the transition to the output list IF
   // systemName corresponds to a system we want:
//Apply wavelength selection:
//Apply loggf selection:
    
  // if ( (systemName.equals("X-A")) 
  //   || (systemName.equals("a-c"))
  //   || (systemName.equals("X-C")) ){
   if ( (systemName.equals("a-c")) 
   && ( (lambdaA > 3600.0) && (lambdaA < 9000.0) )   
   && ( log10gf > -6.0 )   ){ 

           lambdanm = 0.10 * lambdaA;
           myString = Double.toString(lambdanm);
           masterStringOut = masterStringOut + myString.trim() + newField;
           //System.out.println("lambdanm " + myString.trim());
           masterStringOut = masterStringOut + log10gfString.trim() + newField;
           //System.out.println("loggf " + myString.trim());
           //Lower level excitation energy in cm^-1
           //myString = thisRecord[2];
           myString = thisRecord.substring(29, 39);
           blankFlag = true;
               if (myString.trim().length() > 0){
                  blankFlag = false; 
                 }
           if (blankFlag){
               myString = "0.0"; 
             }  else { 
               myString.trim();
//Convert to eV for consistency with NIST atomic line list treatment
               eInvcm = Double.parseDouble(myString); 
               eEv = 1.23984e-4* eInvcm;
               myString = Double.toString(eEv);
             }
           masterStringOut = masterStringOut + myString.trim() + newField;
           //System.out.println("lowE " + myString.trim());
    //       //Lower E-level vibrational quantum number, v_l (v"):
    //       //myString = thisRecord[3]; 
    //       myString = thisRecord.substring(41, 43);
    //       //We need to be ready for blank fields - checking for this in Java is hard!
    //       blankFlag = true;
    //           if (myString.trim().length() > 0){
    //              blankFlag = false; 
    //             }
    //       if (blankFlag == true){
    //           myString = "0"; 
    //         } else { 
    //           myString.trim();
    //         }
    //       masterStringOut = masterStringOut + myString.trim() + newField;
           //System.out.println("lowV " + myString.trim());
    //       //Lower E-level rotational quantum number, J_l (J"):
    //       //myString = thisRecord[4]; 
    //       myString = thisRecord.substring(44, 49);
    //       //We need to be ready for blank fields - checking for this in Java is hard!
    //       blankFlag = true;
    //           if (myString.trim().length() > 0){
    //              blankFlag = false; 
    //             }
    //       if (blankFlag == true){
    //           myString = "0"; 
    //         } else { 
    //           myString.trim();
    //         }
    //       masterStringOut = masterStringOut + myString.trim() + newField;
           //System.out.println("lowJ " + myString.trim());
    //       //Lower E-level quantum number, N_l (N"):
    //       //myString = thisRecord[5]; 
    //       myString = thisRecord.substring(50, 55);
    //       //We need to be ready for blank fields - checking for this in Java is hard!
    //       blankFlag = true;
    //           if (myString.trim().length() > 0){
    //              blankFlag = false; 
    //             }
    //       if (blankFlag == true){
    //           myString = "0"; 
    //         } else { 
    //           myString.trim();
    //         }
    //       masterStringOut = masterStringOut + myString.trim() + newField;
           //System.out.println("lowN " + myString.trim());
    //       //Lower E-level symmetry:
    //       //myString = thisRecord[6]; 
    //       myString = thisRecord.substring(56, 58);
    //       //We need to be ready for blank fields - checking for this in Java is hard!
    //       blankFlag = true;
    //           if (myString.trim().length() > 0){
    //              blankFlag = false; 
    //             }
    //       if (blankFlag == true){
    //           myString = "0"; 
    //         } else { 
    //           myString.trim();
    //         }
    //       masterStringOut = masterStringOut + myString.trim() + newField;
           //System.out.println("lowSym " + myString.trim());
           //To DO: Should statisitcal weight of lower E level be computable from the
           // v, J, N, and sym values just read in??
           //For now: 
           gwL = "1.0"; 
           masterStringOut = masterStringOut + gwL + newField; 
     //      //Upper level excitation energy in cm^-1
     //      //myString = thisRecord[7];
     //      myString = thisRecord.substring(60, 70);
     //      blankFlag = true;
     //          if (myString.trim().length() > 0){
     //             blankFlag = false; 
     //            }
     //      if (blankFlag){
     //          myString = "0.0"; 
     //        }  else { 
     //          myString.trim();
////Convert to eV for consistency with NIST atomic line list treatment
     //          eInvcm = Double.parseDouble(myString); 
     //          eEv = 1.23984e-4* eInvcm;
     //          myString = Double.toString(eEv);
     //        }
     //      masterStringOut = masterStringOut + myString.trim() + newField;
           //System.out.println("hiE " + myString.trim());
     //      //Upper E-level vibrational quantum number, v_l (v"):
     //      //myString = thisRecord[8]; 
     //      myString = thisRecord.substring(72, 74);
     //      //We need to be ready for blank fields - checking for this in Java is hard!
     //      //testLength = bounds[3] - bounds[2];
     //      blankFlag = true;
     //          if (myString.trim().length() > 0){
     //             blankFlag = false; 
     //            }
     //      if (blankFlag == true){
     //          myString = "0"; 
     //        } else { 
     //          myString.trim();
     //        }
     //      masterStringOut = masterStringOut + myString.trim() + newField;
           //System.out.println("hiV " + myString.trim());
     //      //Upper E-level rotational quantum number, J_l (J"):
     //      //myString = thisRecord[9]; 
     //      myString = thisRecord.substring(75, 80);
     //      //We need to be ready for blank fields - checking for this in Java is hard!
     //      //testLength = bounds[3] - bounds[2];
     //      blankFlag = true;
     //          if (myString.trim().length() > 0){
     //             blankFlag = false; 
     //            }
     //      if (blankFlag == true){
     //          myString = "0"; 
     //        } else { 
     //          myString.trim();
     //        }
     //      masterStringOut = masterStringOut + myString.trim() + newField;
           //System.out.println("hiJ " + myString.trim());
     //      //Upper E-level quantum number, N_l (N"):
     //      //myString = thisRecord[10]; 
     //      myString = thisRecord.substring(81, 86);
     //      //We need to be ready for blank fields - checking for this in Java is hard!
     //      blankFlag = true;
     //          if (myString.trim().length() > 0){
     //             blankFlag = false; 
     //            }
     //      if (blankFlag == true){
     //          myString = "0"; 
     //        } else { 
     //          myString.trim();
     //        }
     //      masterStringOut = masterStringOut + myString.trim() + newField;
           //System.out.println("hiN " + myString.trim());
     //      //Upper E-level symmetry:
     //      //myString = thisRecord[11]; 
     //      myString = thisRecord.substring(87, 89);
     //      //We need to be ready for blank fields - checking for this in Java is hard!
     //      blankFlag = true;
     //          if (myString.trim().length() > 0){
     //             blankFlag = false; 
     //            }
     //      if (blankFlag == true){
     //          myString = "0"; 
     //        } else { 
     //          myString.trim();
     //        }
     //      masterStringOut = masterStringOut + myString.trim() + newField;
           //
           //System.out.println("hisym " + myString.trim());
// What Plez calls "gamma radiation" (ie. is this an Einstein A_ji coefficient??)
           //myString = thisRecord[12]; 
           myString = thisRecord.substring(90, 102);
           //We need to be ready for blank fields - checking for this in Java is hard!
           blankFlag = true;
               if (myString.trim().length() > 0){
                  blankFlag = false; 
                 }
           if (blankFlag){
               myString = "6.0"; 
             } else { 
               myString.trim();
//We need to take the log:
               log10GammaRad = Math.log10(Double.parseDouble(myString));
               myString = Double.toString(log10GammaRad); 
               myString.trim();
             }
           masterStringOut = masterStringOut + myString.trim() + newField;
           //System.out.println("gamrad " + myString.trim());
//Field [13] is just the name of the molecule with a single quote mark in front of it:
           //testField = thisRecord[13]; 
           testField = thisRecord.substring(104, 107);
           blankFlag = true;
               testField = testField.trim();
               if (testField.length() > 0){
                  blankFlag = false; 
                 }
           if (blankFlag){
               myString = " ";
             } else {
               //myString = testField.substring(1, 3); //lower E level 
               myString = testField.trim(); //lower E level 
             }
           masterStringOut = masterStringOut + myString.trim() + newField;
           //System.out.println("mol " + myString.trim());

           masterStringOut = masterStringOut + systemName + newField;
           //Field [16] is the transition branch (ie. P, Q, R) 
           //testField = thisRecord[16]; 
           testField = thisRecord.substring(112, 116); 
           blankFlag = true;
               testField = testField.trim();
               if (testField.length() > 0){
                  blankFlag = false; 
                 }
           if (blankFlag){
               testField = " ";
             } else {
               myString = testField.trim();
             }

           masterStringOut = masterStringOut + myString.trim() + newRecord;
           //System.out.println("brnch " + myString.trim());
//

    //We've gotten everything we need from the Plez line list:
           list2_ptr++;


  } //major if condition of electronic transition systemName 
       
       } //i loop 

//   } //iBlock loop

  int numLines2 = list2_ptr;
  System.out.println("numLines2 " + numLines2);

//check:
//System.out.println("masterStringOut " + masterStringOut);

//Okay - what kind of mess did we make...
// System.out.println("We processed " +  numLines2 + " lines");
// System.out.println("list2Element  list2Stage  list2Lam0  list2Logf  list2GwL  list2ChiL  list2ChiI1  list2ChiI2  list2Mass");


//
// END FILE I/O SECTION


  byte[] barray = masterStringOut.getBytes();
  //byte[] barray = masterStringOut.getBytes("UTF-8")
// what do I do with this??   throws UnsupportedEncodingException; 
  System.out.println(" ");
  System.out.println("*************************");
  System.out.println(" ");
  System.out.println("This needs to be detected by GrayStar3Server.java: ");
  System.out.println(" ");
  System.out.println("size of barray " + barray.length);
  System.out.println(" ");
  System.out.println("*************************");
  System.out.println(" ");
 // System.out.println("barray " + barray);
 //
 ByteFileWrite.writeFileBytes(byteListStr, barray); 
 //Under construction
//
    } // end main()

        //

} //end class LineListServer
