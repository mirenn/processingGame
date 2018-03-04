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

public class game extends PApplet {

//\u753b\u9762\u9077\u79fb
int gseq;

//\u30af\u30ea\u30c3\u30af\u3057\u305f\u7b87\u6240
float clickpx = 0;
float clickpy = 0;

//\u7d4c\u904e\u6642\u9593
int gameTitleTime = 0; //\u30b2\u30fc\u30e0\u30bf\u30a4\u30c8\u30eb
int count = 0;//\u30b2\u30fc\u30e0\u30bf\u30a4\u30c8\u30eb\u306e\u753b\u9762\u9077\u79fb
int gameOverTime = 0; //\u30b2\u30fc\u30e0\u30aa\u30fc\u30d0\u30fc

//\u753b\u9762\u30b5\u30a4\u30ba
int dispWidth = 400;
int dispHeight = 500;

//\u30dc\u30fc\u30eb\u306e\u30d9\u30af\u30c8\u30eb
PVector location;
PVector velocity;
PVector gravity;
float x2 = 0;
float y2 = 0;
float theta = 0;
float v = 10;
float vx = 0;
float vy = 0;

//\u30c9\u30e9\u30c3\u30b0\u306e\u5224\u5b9a
boolean isdragged = false;

//\u6642\u9593
float time = 10;
String timer;

//\u753b\u9762\u9077\u79fb\u3067\u5186\u304c\u5e83\u304c\u308b\u521d\u671f\u901f\u5ea6
int speed = 5;

//\u30d0\u30b9\u30b1\u30c3\u30c8\u30b4\u30fc\u30eb\u306e\u30b5\u30a4\u30ba
float goalWidth = 150;
float goalHeight = 75;
float basketWidth = 80;
float basketHeight = 5;

//\u30b7\u30e5\u30fc\u30c8
boolean shoot = false;
int shootCount = 0;
int shootStartTime = 0;
int shootElapsedTime;//\u30b7\u30e5\u30fc\u30c8\u7d4c\u904e\u6642\u9593

public void setup(){
  
  noStroke();
  gameInit();
  
  location = new PVector(random(10,100),100);
  velocity = new PVector(random(0.5f,1.5f), random(-3,1));
  gravity = new PVector(0,0.2f);
}


public void draw(){
  background(255);
  if(gseq == 0){
    gameTitle();
  }else if(gseq == 1){
    gameTitletoPlay();
  }else if(gseq == 2){
    gamePlay();
  }else if(gseq == 3){
    gameOver();
  }
}

//\u30b2\u30fc\u30e0\u30b9\u30bf\u30fc\u30c8
public void gameInit(){
  gseq=0;
}

//\u30b2\u30fc\u30e0\u30bf\u30a4\u30c8\u30eb
public void gameTitle(){
  gameTitleTime += 1;
  titleDisp();
  if(mousePressed==true && gseq==0 && gameTitleTime > 30){
      clickpx = mouseX;
      clickpy = mouseY;
      gseq = 1;
      gameTitleTime = 0;
  }
}

//\u30b2\u30fc\u30e0\u30bf\u30a4\u30c8\u30eb\u304b\u3089\u30d7\u30ec\u30a4\u753b\u9762\u3078\u306e\u9077\u79fb
public void gameTitletoPlay(){
  titleDisp();

  if(count<=2*dispHeight){
    count += speed;
    if(count%2 == 0){
      speed += 2;
    }
  }
  fill(247, 215, 107);
  ellipse(clickpx, clickpy, count, count);
  
  if(count>=2*dispHeight){
    gseq = 2;
    count = 0;
    time = 10;
    speed = 5;
  }
}

//\u30d7\u30ec\u30a4\u753b\u9762
public void gamePlay(){
  background(247, 215, 107);
  basketGoalDisp();
  timeDisp();
  time -= (float)1/60;
  
  location.add(velocity);
  velocity.add(gravity);

  //\u53cd\u5c04
  if ((location.x > width) || (location.x < 0)) {
    velocity.x = velocity.x * -1;
  }
  if (location.y > height-24) {
    velocity.y = velocity.y * -0.6f; 
    location.y = height-24;
  }

  //\u632f\u52d5\u3092\u6b62\u3081\u308b
  if(abs(velocity.y)<0.4f && abs(location.y-height+24)<0.8f){
     location.y = height - 23.9f;
  }
  //\u6469\u64e6
  velocity.x *= 0.99f;

  ballDisp();

  if(mousePressed == true){
    mouseReleased();
    isdragged = true;
  }
  //\u30c9\u30e9\u30c3\u30b0\u89e3\u9664
  if(mousePressed == false && isdragged == true){
    ballPlusSpeed(velocity, vx, vy);
    isdragged = false;
  }
  shoot = shootJudge(location, velocity);
  if(time <= 0){
    gseq = 3;
  }
}

//\u30b2\u30fc\u30e0\u30aa\u30fc\u30d0\u30fc
public void gameOver(){
  time = 0;
  gameOverTime += 1;
  background(247, 215, 107, 0.1f);
  scoreDisp(shootCount);
  timeDisp();
  textSize(30);
  fill(0);
  text("Time UP!", dispWidth/2-60, 80);
  if(gameOverTime > 120){
    textSize(30);
    fill(0);
    text("score:"+shootCount, dispWidth/2-48, 120);
  }
  if(gameOverTime > 160){
    if(shootCount < 5){
      textSize(30);
      fill(0);
      text("Not so bad!", dispWidth/2-80, 200);
    }else{
      textSize(30);
      fill(0);
      text("Great!", dispWidth/2-44, 200);
    }
  }
  if(gameOverTime > 300){
    textSize(30);
    fill(0);
    text("Click to Retry", dispWidth/2-100, 240);
    if(mousePressed == true){
      //\u30dc\u30fc\u30eb\u306e\u30d9\u30af\u30c8\u30eb\u8a2d\u5b9a\u3057\u306a\u304a\u3057
      location = new PVector(random(10,100),100);
      velocity = new PVector(random(0.5f,1.5f), random(-3,1));
      gravity = new PVector(0,0.2f);
      gameOverTime = 0;
      shootCount = 0;
      gseq = 0;
    }
  }
}

//\u7403\u3092\u8868\u793a
public void ballDisp(){
  stroke(255);
  strokeWeight(2);
  fill(127);
  if(shoot == true){
    fill(255);
  }
  ellipse(location.x,location.y,48,48);
}

//\u30bf\u30a4\u30c8\u30eb\u8868\u793a
public void titleDisp(){
  noStroke();
  fill(0);
  textSize(40);
  text("ball minigame", dispWidth/2-130, dispHeight/3);
  fill(239, 134, 107);
  textSize(20);
  text("Click to Start", dispWidth/2-65,dispHeight/2);
  colorMode(RGB,256);
  fill(247, 180, 107);
  rect(0,400, 400,60);
  fill(239, 134, 107);
  rect(0,460, 400,60);
}

//\u30dc\u30fc\u30eb\u3078\u7dda\u3092\u5f15\u304f
public void mouseReleased(){
  float x2 = mouseX;
  float y2 = mouseY;
  
  float dx = x2 - location.x;
  float dy = y2 - location.y;
  theta = atan(dy/dx);

  if(dx<0) theta = theta + PI;
  vx = v * cos(theta);
  vy = v * sin(theta);
  
  stroke( 255, 0, 0, 100 );
  fill( 255, 0, 0 , 100 );
  if(gseq == 2){
    line( location.x, location.y, mouseX, mouseY );
  }
}

//\u30dc\u30fc\u30eb\u306b\u901f\u5ea6\u3092\u8ffd\u52a0
public void ballPlusSpeed(PVector ballVel, float vx, float vy){
  ballVel.x += vx;
  ballVel.y += vy;
}

//\u30d0\u30b9\u30b1\u30c3\u30c8\u30b4\u30fc\u30eb\u306e\u8868\u793a
public void basketGoalDisp(){
  noFill();
  stroke(0);
  rect(width/2-goalWidth/2, height/2-goalHeight/2, goalWidth, goalHeight, 7);
  noFill();
  stroke(255);
  ellipse(width/2, height/2, basketWidth, basketHeight);
}

//\u30b7\u30e5\u30fc\u30c8\u306e\u5224\u5b9a
public boolean shootJudge(PVector ballLocation, PVector ballVel){

  scoreDisp(shootCount);
  shootElapsedTime = shootElapsedTime(shootStartTime);

  if(ballVel.y>0){
    if(width/2 - basketWidth/2<ballLocation.x && ballLocation.x<width/2+basketWidth/2){
      if(height/2-20-basketHeight/2<ballLocation.y && ballLocation.y<height/2+20+basketHeight/2){
        if(shootElapsedTime > 60){
          shoot = true;
          shootCount += 1;
          shootStartTime = frameCount;
        }
      }
    }
  }

  if(shoot == true){
    if(shootElapsedTime(shootStartTime) > 10){
      textSize(30);
      fill(239, 134, 107);
      text("Shoot!", dispWidth/2-44, 60);
    }
    if(shootElapsedTime(shootStartTime) > 20){
      textSize(30);
      fill(239, 134, 107);
      text("+2sec", dispWidth/2-48, 60+30);
    }
    if(shootElapsedTime(shootStartTime) == 29){
      time += 2;
    }
    if(shootElapsedTime(shootStartTime) > 30){
      shoot = false;
      delay(800);
      ballReset();
     }
  }
  return shoot;
}

//\u30b9\u30b3\u30a2\u306e\u8868\u793a
public void scoreDisp(int shootCount){
  textSize(20);
  fill(0);
  text("score:"+shootCount, 20,30);
}

//\u30dc\u30fc\u30eb\u306e\u518d\u51fa\u73fe
public void ballReset(){
  location = new PVector(random(10,100),100);
  velocity = new PVector(random(0.5f,1.5f), random(-3,1));
  gravity = new PVector(0,0.2f);
}

//\u30b7\u30e5\u30fc\u30c8\u7d4c\u904e\u6642\u9593
public int shootElapsedTime(int shootStartTime){
  shootElapsedTime = frameCount - shootStartTime;
  return shootElapsedTime;
}

//\u6642\u9593\u8868\u793a
public void timeDisp(){
  timer = nf(time, 2, 2);
  textSize(25);
  fill(0);
  text("Time:"+timer, dispWidth-60-90, 30);
}
  public void settings() {  size(400,500); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "game" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
