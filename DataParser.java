/*
  Jeffrey Shen, Alex Justicz
  This class takes the text files located in raw, parses them, and outputs them in one csv file (data.csv)
*/

import java.util.ArrayList;

public class DataParser{

  //Takes a file and returns an ArrayList containing each line
  public static ArrayList<String> readFile(String filename){
    ArrayList<String> result = new ArrayList<String>();
    EasyReader reader = new EasyReader(filename);
    while(!reader.eof()){
      String line = reader.readLine();
      if(line != null) result.add(line.trim());
    }

    reader.close();
    return result;
  }

  //Searches an ArrayList for a String
  public static int searchInList(ArrayList<String> arr, String query){
    for(int i = 0; i < arr.size(); i++){
      if(arr.get(i).indexOf(query) != -1) return i;
    }
    return -1;
  }

  public static void main(String[] args) {
    EasyWriter outputFile = new EasyWriter("data.csv");
    String[] states = "Alabama, Alaska, Arizona, Arkansas, California, Colorado, Connecticut, Delaware, District of Columbia, Florida, Georgia, Hawaii, Idaho, Illinois, Indiana, Iowa, Kansas, Kentucky, Louisiana, Maine, Maryland, Massachusetts, Michigan, Minnesota, Mississippi, Missouri, Montana, Nebraska, Nevada, New Hampshire, New Jersey, New Mexico, New York, North Carolina, North Dakota, Ohio, Oklahoma, Oregon, Pennsylvania, Rhode Island, South Carolina, South Dakota, Tennessee, Texas, Utah, Vermont, Virginia, Washington, West Virginia, Wisconsin, Wyoming".split(", ");
    int[] years = {1790, 1800, 1810, 1820, 1830, 1840, 1850, 1860, 1870, 1880, 1890, 1900, 1910, 1920, 1930, 1940, 1950, 1960, 1970, 1980, 1990, 2000, 2010};

    //Create table headers
    outputFile.print("year");
    for(String state: states){
      outputFile.print("," + state);
    }
    outputFile.println();

    //Fill up table
    ArrayList<String> firstPeriodFree = readFile("raw/1790-1860.txt");
    ArrayList<String> firstPeriodEnslaved = readFile("raw/enslaved1790-1860.txt");
    ArrayList<String> secondPeriod = readFile("raw/1870-1950.txt");
    ArrayList<String> thirdPeriod = readFile("raw/1960-2010.txt");

    for(int i = 0; i < years.length; i++){
      outputFile.print("" + years[i]);
      for(int j = 0; j < states.length; j++){
        int lineNumber = 12 * j + 3 + i;
        if(years[i] <= 1860){
            String freeLineContents = firstPeriodFree.get(lineNumber); //Regular

            boolean isTerritory = false;
            if(freeLineContents.indexOf("background-color:#D8D8D8") != -1) isTerritory = true;

            String stateName = states[j];

            String populationStr = freeLineContents.replaceAll("<[^>]*>", "");
            int population = 0;
            if(!populationStr.equals("&#160;")) population = Integer.parseInt(populationStr.replaceAll(",", ""));

            //Enslaved
            int indexOfState = searchInList(firstPeriodEnslaved, stateName);
            if(indexOfState != -1){
              String enslavedPopulationStr = firstPeriodEnslaved.get(indexOfState + 1 + i).replaceAll("<[^>]*>", "").replaceAll(",", "");
              if(enslavedPopulationStr.length() > 0) population += Integer.parseInt(enslavedPopulationStr);
            }
            if(isTerritory) outputFile.print(",T" + population);
            else outputFile.print("," + population);

        }
        else if(years[i] <= 1950){
          lineNumber -= 9;
          String lineContents = secondPeriod.get(lineNumber);
          boolean isTerritory = false;

          if(lineContents.indexOf("background-color:#D8D8D8") != -1) isTerritory = true;

          String populationStr = lineContents.replaceAll("<[^>]*>", "");
          int population = 0;
          if(!populationStr.equals("&#160;")) population = Integer.parseInt(populationStr.replaceAll(",", ""));

          if(isTerritory) outputFile.print(",T" + population);
          else outputFile.print("," + population);
        }
        else{
          lineNumber = 9 * j + i - 15;
          String lineContents = thirdPeriod.get(lineNumber);
          boolean isTerritory = false;

          if(lineContents.indexOf("background-color:#D8D8D8") != -1) isTerritory = true;

          String populationStr = lineContents.replaceAll("<[^>]*>", "");
          int population = 0;
          if(!populationStr.equals("&#160;")) population = Integer.parseInt(populationStr.replaceAll(",", ""));

          if(isTerritory) outputFile.print(",T" + population);
          else outputFile.print("," + population);
        }
      }
      outputFile.println();
    }

    outputFile.close();
  }
}
