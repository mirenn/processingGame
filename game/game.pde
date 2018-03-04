//画面遷移
int gseq;

//クリックした箇所
float clickpx = 0;
float clickpy = 0;

//経過時間
int gameTitleTime = 0; //ゲームタイトル
int count = 0;//ゲームタイトルの画面遷移
int gameOverTime = 0; //ゲームオーバー

//画面サイズ
int dispWidth = 400;
int dispHeight = 500;

//ボールのベクトル
PVector location;
PVector velocity;
PVector gravity;
float x2 = 0;
float y2 = 0;
float theta = 0;
float v = 10;
float vx = 0;
float vy = 0;

//ドラッグの判定
boolean isdragged = false;

//時間
float time = 10;
String timer;

//画面遷移で円が広がる初期速度
int speed = 5;

//バスケットゴールのサイズ
float goalWidth = 150;
float goalHeight = 75;
float basketWidth = 80;
float basketHeight = 5;

//シュート
boolean shoot = false;
int shootCount = 0;
int shootStartTime = 0;
int shootElapsedTime;//シュート経過時間

void setup(){
  size(400,500);
  noStroke();
  gameInit();
  
  location = new PVector(random(10,100),100);
  velocity = new PVector(random(0.5,1.5), random(-3,1));
  gravity = new PVector(0,0.2);
}


void draw(){
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

//ゲームスタート
void gameInit(){
  gseq=0;
}

//ゲームタイトル
void gameTitle(){
  gameTitleTime += 1;
  titleDisp();
  if(mousePressed==true && gseq==0 && gameTitleTime > 30){
      clickpx = mouseX;
      clickpy = mouseY;
      gseq = 1;
      gameTitleTime = 0;
  }
}

//ゲームタイトルからプレイ画面への遷移
void gameTitletoPlay(){
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

//プレイ画面
void gamePlay(){
  background(247, 215, 107);
  basketGoalDisp();
  timeDisp();
  time -= (float)1/60;
  
  location.add(velocity);
  velocity.add(gravity);

  //反射
  if ((location.x > width) || (location.x < 0)) {
    velocity.x = velocity.x * -1;
  }
  if (location.y > height-24) {
    velocity.y = velocity.y * -0.6; 
    location.y = height-24;
  }

  //振動を止める
  if(abs(velocity.y)<0.4 && abs(location.y-height+24)<0.8){
     location.y = height - 23.9;
  }
  //摩擦
  velocity.x *= 0.99;

  ballDisp();

  if(mousePressed == true){
    mouseReleased();
    isdragged = true;
  }
  //ドラッグ解除
  if(mousePressed == false && isdragged == true){
    ballPlusSpeed(velocity, vx, vy);
    isdragged = false;
  }
  shoot = shootJudge(location, velocity);
  if(time <= 0){
    gseq = 3;
  }
}

//ゲームオーバー
void gameOver(){
  time = 0;
  gameOverTime += 1;
  background(247, 215, 107, 0.1);
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
      //ボールのベクトル設定しなおし
      location = new PVector(random(10,100),100);
      velocity = new PVector(random(0.5,1.5), random(-3,1));
      gravity = new PVector(0,0.2);
      gameOverTime = 0;
      shootCount = 0;
      gseq = 0;
    }
  }
}

//球を表示
void ballDisp(){
  stroke(255);
  strokeWeight(2);
  fill(127);
  if(shoot == true){
    fill(255);
  }
  ellipse(location.x,location.y,48,48);
}

//タイトル表示
void titleDisp(){
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

//ボールへ線を引く
void mouseReleased(){
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

//ボールに速度を追加
void ballPlusSpeed(PVector ballVel, float vx, float vy){
  ballVel.x += vx;
  ballVel.y += vy;
}

//バスケットゴールの表示
void basketGoalDisp(){
  noFill();
  stroke(0);
  rect(width/2-goalWidth/2, height/2-goalHeight/2, goalWidth, goalHeight, 7);
  noFill();
  stroke(255);
  ellipse(width/2, height/2, basketWidth, basketHeight);
}

//シュートの判定
boolean shootJudge(PVector ballLocation, PVector ballVel){

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

//スコアの表示
void scoreDisp(int shootCount){
  textSize(20);
  fill(0);
  text("score:"+shootCount, 20,30);
}

//ボールの再出現
void ballReset(){
  location = new PVector(random(10,100),100);
  velocity = new PVector(random(0.5,1.5), random(-3,1));
  gravity = new PVector(0,0.2);
}

//シュート経過時間
int shootElapsedTime(int shootStartTime){
  shootElapsedTime = frameCount - shootStartTime;
  return shootElapsedTime;
}

//時間表示
void timeDisp(){
  timer = nf(time, 2, 2);
  textSize(25);
  fill(0);
  text("Time:"+timer, dispWidth-60-90, 30);
}