package com.arcana.utils;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Vector;

import com.arcana.entities.Entity;

public class Physics {
	
	public static boolean collision(Entity a, Entity b){
		return a.getBounds().intersects(b.getBounds());
	}
	
	public static Vector<Point2D> getIntersectionPoints(Entity e1, Entity e2){
		Rectangle2D rect1 = e1.getBounds();
		Rectangle2D rect2 = e2.getBounds();
		if(collision(e1, e2))
			return getIntersectionPoints(rect1, rect2);
		else 
			return null;
	}
	
	public static Vector<Point2D> getIntersectionPoints(Rectangle2D rect1, Rectangle2D rect2){
		if(!rect1.intersects(rect2)) return null;
		Rectangle2D intersection = rect1.createIntersection(rect2);
		
		Point2D[] rect1Points = new Point2D[4];
		Point2D[] rect2Points = new Point2D[4];
		Point2D[] intersectPoints = new Point2D[4];
		
		rect1Points[0] = new Point2D.Double(rect1.getMinX(), rect1.getMinY());
		rect1Points[1] = new Point2D.Double(rect1.getMaxX(), rect1.getMinY());
		rect1Points[2] = new Point2D.Double(rect1.getMinX(), rect1.getMaxY());
		rect1Points[3] = new Point2D.Double(rect1.getMaxX(), rect1.getMaxY());
		
		rect2Points[0] = new Point2D.Double(rect2.getMinX(), rect2.getMinY());
		rect2Points[1] = new Point2D.Double(rect2.getMaxX(), rect2.getMinY());
		rect2Points[2] = new Point2D.Double(rect2.getMinX(), rect2.getMaxY());
		rect2Points[3] = new Point2D.Double(rect2.getMaxX(), rect2.getMaxY());
		
		intersectPoints[0] = new Point2D.Double(intersection.getMinX(), intersection.getMinY());
		intersectPoints[1] = new Point2D.Double(intersection.getMaxX(), intersection.getMinY());
		intersectPoints[2] = new Point2D.Double(intersection.getMinX(), intersection.getMaxY());
		intersectPoints[3] = new Point2D.Double(intersection.getMaxX(), intersection.getMaxY());
		
		for(Point2D p : rect1Points){
			for(int i = 0; i < intersectPoints.length; i++){
				if(intersectPoints[i] != null && p.getX() == intersectPoints[i].getX() && p.getY() == intersectPoints[i].getY()){
					intersectPoints[i] = null;
				}
			}
		}
		for(Point2D p : rect2Points){
			for(int i = 0; i < intersectPoints.length; i++){
				if(intersectPoints[i] != null && p.getX() == intersectPoints[i].getX() && p.getY() == intersectPoints[i].getY()){
					intersectPoints[i] = null;
				}
			}
		}
		
		Point2D[] newPoints = new Point2D[2];
		for(Point2D intersectP: intersectPoints){
			for(int i = 0; i < newPoints.length; i++){
				if(intersectP != null && newPoints[i] == null){
					newPoints[i] = intersectP;
					break;
				}
			}
		}
		Vector<Point2D> result = new Vector<Point2D>();
		for(int i = 0; i < newPoints.length; i++){
			result.addElement(newPoints[i]);
		}
		
		return result;
	}
	
	
}
