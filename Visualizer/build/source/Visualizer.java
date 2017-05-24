import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Visualizer extends PApplet {

/*
  Visualizer.pde
  Jeffrey Shen, Alex Justicz

  Processing file which displays the population data in the form of a map.
*/

PShape map;
PShape[] states;
Table table;
int year;
PFont bitter;
XML[] xml;
int lastMillis;
boolean paused = false;
PImage play, pause, backwards, forwards, reset;
int totalPopulation;

public void setup() { //Initialize values
  
  map = loadShape("us.svg");
  states = map.getChildren();
  table = loadTable("data.csv", "header");
  year = 1790;
  bitter = createFont("Bitter-Bold.ttf", 16);
  xml = loadXML("us.svg").getChildren("path");
  lastMillis = millis();

  //Load Icons
  play = loadImage("play.png");
  pause = loadImage("pause.png");
  backwards = loadImage("backwards.png");
  forwards = loadImage("forwards.png");
  reset = loadImage("reset.png");
}

public String getPopulationAtYear(int year, String state){ //Returns the element (of type String) at a certain row and column in the table
  if(state.length() > 0) return table.getRow((year - 1790) / 10).getString(state);
  else return "";
}

public String getStateName(PShape state){ //Returns the name of the state if you are given the PShape element
  for(XML xmlElement : xml){
    if(xmlElement.getString("data-id").equals(state.getName())) return xmlElement.getString("data-name");
  }
  return "";
}

public void draw() { //Method called every few milliseconds which updates the display
  totalPopulation = 0;
  background(129, 207, 224);
  fill(0);
  textFont(bitter);
  text("Year \u25B6: " + year, 20, 36); //Update the year

  //Display icons
  image(backwards, 20, 40, 30, 30);
  if(paused) image(play, 55, 40, 30, 30);
  else image(pause, 55, 40, 30, 30);
  image(forwards, 90, 40, 30, 30);
  image(reset, 125, 40, 30, 30);

  //Display map
  shape(map, 0, 0, 1000, 589);

  //Increment year by 10 every 1.2 seconds
  if(millis() - lastMillis > 1200 && year < 2010){
    if(!paused) year += 10;
    lastMillis = millis();
  }

  //Go through each state and update color based on population accordingly
  for(PShape state : states){
    String stateName = getStateName(state);

    String population = getPopulationAtYear(year, stateName);

    //Special logic for West Virginia
    if(year <= 1860 && stateName.equals("West Virginia")){
      stateName = "Virginia";
      population = "" + (Integer.parseInt(population.replace("T", "")) + Integer.parseInt(getPopulationAtYear(year, "Virginia")));
    } else if(year <= 1860 && stateName.equals("Virginia")){
      population = "" + (Integer.parseInt(population) + Integer.parseInt(getPopulationAtYear(year, "West Virginia").replace("T", "")));
    }

    if(population.indexOf("T") != -1 && !stateName.equals("West Virginia")) state.setFill(color(108, 122, 137));
    else {
      int populationNum = Integer.parseInt(population.replace("T", ""));
      if(year >= 1860 || !stateName.equals("West Virginia")){
        totalPopulation += populationNum;
      }

      if(populationNum < 10000){
        state.setFill(color(200, 247, 197));
      }
      else if(populationNum < 50000){
        state.setFill(color(154, 215, 150));
      }
      else if(populationNum < 100000){
        state.setFill(color(126, 199, 121));
      }
      else if(populationNum < 250000){
        state.setFill(color(100, 187, 94));
      }
      else if(populationNum < 500000){
        state.setFill(color(71, 159, 65));
      }
      else if(populationNum < 1000000){
        state.setFill(color(51, 143, 45));
      }
      else if(populationNum < 2500000){
        state.setFill(color(42, 133, 36));
      }
      else if(populationNum < 5000000){
        state.setFill(color(26, 115, 21));
      }
      else if(populationNum < 10000000){
        state.setFill(color(14, 96, 9));
      }
      else if(populationNum < 25000000){
        state.setFill(color(8, 79, 3));
      }
      else{
        state.setFill(color(4, 54, 1));
      }
    }

    //Hover control
    if(state.contains(mouseX, mouseY)){
      fill(0);
      if(mouseX > 800) rect(mouseX - 200, mouseY, 200, 40);
      else rect(mouseX, mouseY, 200, 40);
      fill(255);
      textSize(12);
      if(mouseX > 800){
        text("Name: " + stateName, mouseX + 4 - 200, mouseY + 16);
        text("Population: " + displayWithCommas(population.replace("T", "")), mouseX + 4 - 200, mouseY + 30);
      }
      else{
        text("Name: " + stateName, mouseX + 4, mouseY + 16);
        text("Population: " + displayWithCommas(population.replace("T", "")), mouseX + 4, mouseY + 30);
      }
    }
  }

  fill(0);
  textSize(16);

  //Display Title
  textAlign(CENTER);
  text("Total Population: " + displayWithCommas("" + totalPopulation), 500, 40);
  textAlign(LEFT);
}

//Format a number with commas every 3 places
public String displayWithCommas(String population){
  String result = "";
  for(int i = 0; i < population.length(); i++){
    result += "" + population.charAt(i);
    if((population.length() - 1 - i) % 3 == 0 && i != population.length() - 1){
      result += ",";
    }
  }
  return result;
}

//Controls click logic
public void mouseReleased() {
  if(mouseY >= 40 && mouseY <= 70){
    if(mouseX >= 20 && mouseX <= 50){
      if(year > 1790) year -= 10;
    }
    else if(mouseX >= 55 && mouseX <= 85){
      if(paused) paused = false;
      else paused = true;
    }
    else if(mouseX >= 90 && mouseX <= 120){
      if(year < 2010) year += 10;
    }
    else if(mouseX >= 125 && mouseX <= 155){
      paused = false;
      year = 1790;
    }
  }
}
  public void settings() {  size(1000, 589); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Visualizer" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
