import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.Timer;

@SuppressWarnings("serial")
public class Astroids extends Applet implements KeyListener, ActionListener{

	Spacecraft ship;
	Graphics offg;
	Image offscreen;
	Timer timer;
	VectorSprite counter;
	boolean upKey;
	boolean leftKey;
	boolean rightKey;
	boolean spacebar;
	int score;
	AudioClip firebullet, explodeasteroid,explodeship, moveship;

	/* create Asteroids*/
	//Asteroid rock;
	ArrayList<Asteroid> asteroidList;
	ArrayList<Bullet> bulletList;

	public void init(){
		this.setSize(1350,650);
		this.addKeyListener(this);
		ship=new Spacecraft();
		timer=new Timer(20,this);
		offscreen=createImage(this.getWidth(),this.getHeight());
		offg=offscreen.getGraphics();

		/* create Asteroids*/
		//rock = new Asteroid();
		asteroidList = new ArrayList();
		bulletList = new ArrayList();

		for(int i = 0; i < 10; i++)
		{
			/* add a new Asteroids each time*/
			asteroidList.add(new Asteroid());
		}

		explodeasteroid = getAudioClip(getCodeBase(),"explode0.wav");
		explodeship = getAudioClip(getCodeBase(),"explode1.wav");
		firebullet = getAudioClip(getCodeBase(),"laser79.wav");
		moveship = getAudioClip(getCodeBase(),"thruster.wav");
	}

	public void start(){
		timer.start();
	}//checked

	public void stop(){
		timer.stop();
	}//checked

	public void actionPerformed(ActionEvent e){
		Respawn();
		keyCheck();
		ship.updatePosition();

		//rock.updatePosition(); //not needed with array list
		/* create Asteroids*/
		for(int i=0; i<asteroidList.size(); i++)
		{
			asteroidList.get(i).updatePosition();
		}
		for(int i=0; i<bulletList.size(); i++)
		{
			bulletList.get(i).updatePosition();
		}
		checkCollision();
		checkAsteroidDestruction();
	}//checked 

	public boolean collision(VectorSprite thing1, VectorSprite thing2){
		int x,y;
		for(int i=0;i<thing1.drawShape.npoints;i++){
			x=thing1.drawShape.xpoints[i];
			y=thing1.drawShape.ypoints[i];
			if (thing2.drawShape.contains(x,y)){
				return true;
			}
		}
		for(int i=0;i<thing2.drawShape.npoints;i++){
			x=thing2.drawShape.xpoints[i];
			y=thing2.drawShape.ypoints[i];
			if (thing1.drawShape.contains(x,y)){
				return true;
			}
		}
		return false;
	}
	public void checkCollision(){
		for(int i=0;i<asteroidList.size();i++){
			if(collision(ship,asteroidList.get(i)) && ship.active){
				ship.hit();
				score -= 20;
			}
			for(int j=0;j<bulletList.size();j++){
				if(collision(bulletList.get(j),asteroidList.get(i))){
					bulletList.get(j).active=false;
					asteroidList.get(i).active=false;
					score += 20;
				}
			}
		}
	}

	public boolean isRespawnSafe(){
		int x,y,h;
		for(int i=0;i<asteroidList.size();i++){
			x=(int) (asteroidList.get(i).xposition-650);
			y=(int) (asteroidList.get(i).yposition-300);
			h=(int) Math.sqrt(x*x+y*y);
			if(h<100){
				return false;
			}
		}
		return true;
	}
	public void Respawn(){
		if(ship.active==false && ship.counter>5 && isRespawnSafe()){
			ship.reset();
		}
	}
	
	public void checkAsteroidDestruction() 
	{ 
		for (int i = 0; i < asteroidList.size(); i++) 
		{ 
			if (asteroidList.get(i).active == false) 
			{ 
				asteroidList.remove(i); 
			} 
		} 
	} 

	public void paint(Graphics g){
		offg.setColor(Color.black);
		offg.fillRect (0,0,1350,650);
		offg.setColor(Color.green);
		if (ship.active){
			ship.paint(offg);
		}
		offg.drawString("Lives: "+ship.lives,1300,12);
		offg.drawString("Score: "+score,0,12);
		offg.setColor(Color.white);
		/* paint Asteroids*/
		//rock.paint(offg);
		/* create Asteroids*/
		for (int i = 0; i < asteroidList.size(); i++)
		{
			asteroidList.get(i).paint(offg);
		}	
		offg.setColor(Color.red);
		for (int i = 0; i < bulletList.size(); i++)
		{
			bulletList.get(i).paint(offg);
		}	
		if (ship.lives <= 0)
		{
			offg.drawString("Game Over - You Lose!  Good day, sir!", 380, 300);
		}
		else if (asteroidList.isEmpty())
		{
			offg.drawString("Game Over - You Win!", 400, 300);
		}
		g.drawImage(offscreen,0,0,this);
		repaint();
	}//checked 

	public void fireBull(){
		if(ship.counter >5&&ship.active){
			bulletList.add(new Bullet(ship.drawShape.xpoints[0],ship.drawShape.ypoints[0],ship.angle));
		}
	}
	public void update(Graphics g){
		paint(g);
	}//checked 

	public void keyCheck(){
		if (upKey)
		{
			ship.accelerate();
		}

		if (leftKey)
		{
			ship.rotateLeft();
		}

		if (rightKey)
		{
			ship.rotateRight();
		}
		if (spacebar) {
			bulletList.add(new Bullet(ship.xposition, ship.yposition, ship.angle));
			firebullet.play();
		}
	}

	public void keyPressed(KeyEvent e){
		if (e.getKeyCode() == KeyEvent.VK_D)
		{
			rightKey = true;
		}

		if (e.getKeyCode() == KeyEvent.VK_A)
		{
			leftKey = true;
		}

		if (e.getKeyCode() == KeyEvent.VK_W)
		{
			upKey = true;
		}
		if(e.getKeyCode()==KeyEvent.VK_SPACE){
			spacebar = true;
		}
	}//checked 

	public void keyReleased(KeyEvent e){
		if (e.getKeyCode() == KeyEvent.VK_D)
		{
			rightKey = false;
		}//checked 

		if (e.getKeyCode() == KeyEvent.VK_A)
		{
			leftKey = false;
		}//checked

		if (e.getKeyCode() == KeyEvent.VK_W)
		{
			upKey = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_SPACE)
		{
			spacebar = false;
		}
	}//checked 

	public void keyTyped(KeyEvent a){
	}
}

public class Asteroid extends VectorSprite {
	int size;

	public Asteroid(){
		shape = new Polygon();
		shape.addPoint(30, 3);
		shape.addPoint(5, 35);
		shape.addPoint(-25, 10);
		shape.addPoint(-17, -15);
		shape.addPoint(20, -35);

		drawShape = new Polygon();
		drawShape.addPoint(30, 3);
		drawShape.addPoint(5, 35);
		drawShape.addPoint(-25, 10);
		drawShape.addPoint(-17, -15);
		drawShape.addPoint(20, -35);

		
        double h, a;
        h = Math.random() + 0.5;
        a = Math.random()* 2*Math.PI;
        xspeed = Math.cos(a)*h;
        yspeed = Math.sin(a)*h;

        h = Math.random() * 400 + 100;
        a = Math.random()* 2*Math.PI;
        xposition = Math.cos(a)*h + 450;
        yposition = Math.sin(a)*h + 300;

        ROTATE = Math.random() / 2 - 0.25;
        active = true;
	}

	public void updatePosition(){
		angle += ROTATE;
		super.updatePosition();
	}
}

import java.awt.Polygon;

public class Bullet extends VectorSprite{
	public Bullet(double x, double y, double a)
	{
		shape = new Polygon(); 
		shape.addPoint(-5, 0); 
		shape.addPoint(0, 0); 
		shape.addPoint(0, 0); 
		shape.addPoint(0, 0); 

		drawShape = new Polygon(); 
		drawShape.addPoint(0, 0); 
		drawShape.addPoint(0, 0);  
		drawShape.addPoint(0, 0); 
		drawShape.addPoint(0, 0); 
		xposition = x;
		yposition = y;
		angle = a;
		THRUST = 10;

		xspeed = Math.cos(angle)*THRUST;
		yspeed = Math.sin(angle)*THRUST;

		active = true;
	}
}

import java.awt.Polygon;

public class Spacecraft extends VectorSprite{

	int lives;
	public Spacecraft(){
		shape=new Polygon();
		shape.addPoint(10,0);
		shape.addPoint(-20,-10);
		shape.addPoint(-20,10);
		
		drawShape= new Polygon();
		drawShape.addPoint(10,0);
		drawShape.addPoint(-20,-10);
		drawShape.addPoint(-20,10);
		xposition=650;
		yposition=400;
		ROTATE=0.1;
		THRUST=0.3;
		active=true; 
		lives = 3;
	}

	public void accelerate(){
		xspeed+=Math.cos(angle)*THRUST;
		yspeed+=Math.sin(angle)*THRUST;
	}

	public void rotateLeft(){
		angle-=ROTATE;
	}

	public void rotateRight(){
		angle+=ROTATE;
	}
	public void hit(){
		lives--;
		active=false;
		counter=0;
	}
	public void reset(){
		xspeed=0;
		yspeed=0;
		xposition=650;
		yposition=450;
		active=true;
		angle=0;
		}
	}

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;

public class VectorSprite {
	double xposition;
	double xspeed;
	double yposition;
	double yspeed;
	double angle;
	double ROTATE, THRUST;
	Polygon shape, drawShape; 
	boolean active;
	int counter;
	
	public void paint(Graphics g){
		g.drawPolygon(drawShape);
		//g.setColor(Color.green);
		//g.fillPolygon(xPoints,yPoints,nPoints);
	}

	public void updatePosition(){
		counter++;
		xposition+=xspeed;
		yposition+=yspeed;
		wraparound();
		int x,y;
		
		//drawShape.translate;
		for (int i = 0; i < shape.npoints; i++)
		{
			x = (int)Math.round(shape.xpoints[i]*Math.cos(angle) - shape.ypoints[i]*Math.sin(angle));
			y = (int)Math.round(shape.xpoints[i]*Math.sin(angle) + shape.ypoints[i]*Math.cos(angle));
			drawShape.xpoints[i] = x;
			drawShape.ypoints[i] = y;
		} 
		drawShape.invalidate();
		drawShape.translate((int)Math.round(xposition), (int)Math.round(yposition));
	}
	public void wraparound(){
		if(xposition > 1350){
			xposition = 0;
		}
		if(xposition < 0){
			xposition = 1350;
		}
		if(yposition > 650){
			yposition = 0;
		}
		if(yposition < 0){
			yposition = 650;
		}
	}
}
