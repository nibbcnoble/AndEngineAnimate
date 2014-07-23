import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.Entity;
import org.andengine.entity.scene.Scene;

import android.util.Log;

public class MoveObject {
	Scene scene;
	private IUpdateHandler revolveHandler;
	private IUpdateHandler moveObjectToPointHandler;
	static Entity object;
	boolean objectMoving = false;
	
	public boolean isObjectMoving() {
		return objectMoving;
	}

	/**
	 * Class Constructor
	 * @param eng  the Engine you are using for your game
	 * @param act the BaseGameActivity for your game
	 * @param scn the scene for which this object is going to be attached
	 * @param object the object that will be manipulated 
	 */
	public MoveObject(Scene scn,Entity obj) {
		scene = scn;
		object = obj;
	}
	
	/**
	 * Start revolving the object around a certain fixed point
	 * @param x  holds the x position that the object will revolve around
	 * @param y holds the y position that the object will revolve around
	 * @param speed the speed which the object will move.  1 is equal to moving .12 pixels per second at 60fps
	 * @param clockwise set either to 1 for clockwise and -1 for counter clockwise 
	 */
	public void startRevolve(final float x,final float y, final float speed, final float clockwise) {
		
		float aX =object.getX()+60;
		float aY = object.getY()+60;
		float distX = Math.abs(x - aX);
		float distY = Math.abs(y - aY);
		final float radius = (float) Math.sqrt((distX*distX)+(distY*distY));
		final float angle = (float) (Math.atan2(aY - y, aX - x));
		
		revolveHandler = new IUpdateHandler() {
			float ang  =angle;
			float rad =radius;
			@Override
			public void onUpdate(float pSecondsElapsed) {
				if (clockwise == 1) {
					ang+=.002*speed;
				} else if (clockwise == -1) {
					ang-=.002*speed;
				} 
					object.setPosition(x+(float)(rad*Math.cos(ang)-60),y+ (float)(rad*Math.sin(ang)-60));
			}
			@Override public void reset(){}
		};
		if (!objectMoving) {
			scene.registerUpdateHandler(revolveHandler);	
			objectMoving = true;
		} else {
			Log.e("moveObject.java Object Moving", "You cannot move this object while it is already in motion");
		}
	}
	public void stopObject() {
		scene.unregisterUpdateHandler(revolveHandler);	
		scene.unregisterUpdateHandler(moveObjectToPointHandler);	
		objectMoving = false;
	}
	
	/**
	 * Moves an object to a fixed point.  It moves the object to within a margin of error that is close 
	 * to the speed and then sets the objects position exactly to the set x and y values to ensure accuracy.
	 * @param x  the x coordinate that the object is moving towards
	 * @param y the y coordinate that the object is moving towards
	 * @param speed the speed which the object will move.  1 is equal to moving .12 pixels per second at 60fps
	 */
	public void moveObjectToPoint(final float x, final float y,final float speed) {
		final float xVel = Math.abs(x-object.getX())*speed;
		final float yVel = Math.abs(y-object.getY())*speed;
		moveObjectToPointHandler = new IUpdateHandler() {
			@Override
			public void onUpdate(float pSecondsElapsed) {
				if (x > object.getX()) {
					object.setX(object.getX()+xVel);
				} else {
					object.setX(object.getX()-xVel);
				}
				
				if (y > object.getY()) {
					object.setY(object.getY()+yVel);
				} else {
					object.setY(object.getY()-yVel);
				}
				
				// this is a compacted distance formula which checks to see of the object is close enough to the margin of error which is the speed+.01f
				if (  Math.sqrt((Math.abs(x - object.getX()))*(Math.abs(x - object.getX()))+ (Math.abs(y - object.getY()))*(Math.abs(y - object.getY())))<= speed+0.01f) {
					object.setPosition(x, y);
					scene.unregisterUpdateHandler(this);
					objectMoving = false;
				}
			}
			@Override public void reset(){}
		};
		if (!objectMoving) {
			scene.registerUpdateHandler(moveObjectToPointHandler);		
			objectMoving = true;
		} else {
			Log.e("moveObject.java Object Moving", "You cannot move this object while it is already in motion");
		}
	
	}
}
