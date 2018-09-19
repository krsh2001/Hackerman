package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.*;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

/**
 * @author David Nash
 * Date: 2017-05-29
 *
 * The CollisionDetector class takes a .tmx file and, using its data, generates physics bodies for all of its objects.
 *
 * @version 1.8.1
 */
public class CollisionDetector extends ApplicationAdapter {
    /**
     * The Routers the detector finds.
     */
    private Router[] routers;
    /**
     * The guards the detector finds.
     */
    private Guard guards[];

    /**
     * The elevator to beat the level.
     */
    private Sprite elevator;

    /**
     * The CollisionDetector constructor initializes the walls, followed by the routers and hackable objects, and finally the guards and victory sections.
     * @param world The World, used to create the bodies.
     * @param filename The .tmx file's name.
     * @param game The game, to be passed to the routers.
     */
    CollisionDetector(World world, String filename, Hackerman game) {
        Body bod;
        Body wallArr[];
        BodyDef def;
        FixtureDef fix;
        MapObjects walls, rout, hack, guard, victory;
        MapProperties prop;
        MapObject m, r;
        Shape shape;
        TmxMapLoader mapLoader = new TmxMapLoader();

        //Loading the wall layer and creating an array of bodies
        walls = mapLoader.load(filename).getLayers().get("wall").getObjects();
        wallArr = new Body[walls.getCount()];

        //loops through every wall and defines it as a static object
        for (int i = 0; i < walls.getCount(); i++) {
            def = new BodyDef();
            def.fixedRotation = true;
            def.type = BodyDef.BodyType.StaticBody;
            def.position.set(new Vector2(0, 0));
            wallArr[i] = world.createBody(def);

            m = walls.get(i);
            if (m instanceof PolylineMapObject) {
                shape = getPolyline((PolylineMapObject) m);
            } else if (m instanceof RectangleMapObject) shape = getRect((RectangleMapObject) m);
            else if (m instanceof CircleMapObject) shape = getEllipse((EllipseMapObject) m);
            else
                throw new ClassCastException("This is not the correct type of object!");

            fix = new FixtureDef();
            fix.density = 1;
            fix.shape = shape;
            fix.restitution = 0;
            wallArr[i].createFixture(fix);

            wallArr[i].setUserData("wall");
        }

        //Loads the routers and hacks found
        rout = mapLoader.load(filename).getLayers().get("routers").getObjects();
        hack = mapLoader.load(filename).getLayers().get("hacks").getObjects();
        routers = new Router[rout.getCount()];

        //Iterates through every router and defines it as a static body, and assigns its hackable objects
        for (int i = 0; i < routers.length; i++) {
            r = rout.get(i);
            def = new BodyDef();
            fix = new FixtureDef();

            if (r instanceof RectangleMapObject)
                shape = getRect((RectangleMapObject) r);
            else if (r instanceof EllipseMapObject)
                shape = getEllipse((EllipseMapObject) r);
            else if (r instanceof PolylineMapObject)
                shape = getPolyline((PolylineMapObject) r);
            else
                throw (new ClassCastException("-_-"));

            def.fixedRotation = true;
            def.type = BodyDef.BodyType.StaticBody;
            def.position.set(0, 0);
            bod = world.createBody(def);

            fix.shape = shape;
            fix.density = 1;
            fix.restitution = 0;
            bod.createFixture(fix);

            routers[i] = new Router(r, bod, game);
            //Iterates through the hackable objects, and initializes them with their correct router
            for (int x = 0; x < hack.getCount(); x++) {
                m = hack.get(x);
                if (!(r.getName().equals(m.getName())))
                    continue;

                if (m instanceof RectangleMapObject)
                    shape = getRect((RectangleMapObject) m);
                else if (m instanceof EllipseMapObject)
                    shape = getEllipse((EllipseMapObject) m);
                 else if (m instanceof PolylineMapObject)
                    shape = getPolyline((PolylineMapObject) m);
                else if (m instanceof PolygonMapObject)
                    shape = getPolygon((PolygonMapObject) m);
                else {
                    System.out.print(m.getName());
                    throw (new ClassCastException("U dun goofed"));
                }

                def = new BodyDef();
                def.type = BodyDef.BodyType.StaticBody;
                def.fixedRotation = true;
                def.position.set(0, 0);
                bod = world.createBody(def);

                fix = new FixtureDef();
                fix.restitution = 0;
                fix.density = 1;
                fix.shape = shape;
                bod.createFixture(fix);

                //Initializes them based on their type
                prop = m.getProperties();
                if (prop.get("Type").equals("door")) {
                    routers[i].add(new Door(bod, m));
                }
                else if (prop.get("Type").equals("cam"))
                    routers[i].add (new Camera(bod,m));
            }
        }

        //Loads the guard layer
        guard = mapLoader.load(filename).getLayers().get("guards").getObjects();
        guards = new Guard[guard.getCount()];
        //Iterates through the guards and initializes them
        for (int i = 0; i < guards.length; i++) {
            prop = guard.get(i).getProperties();
            guards[i] = new Guard(new Sprite(new Texture("Assets/Guard/guard.png")), (Float) prop.get("constant"), (Float) prop.get("start"), (Float) prop.get("end"), (Boolean) prop.get("XY"), world);
        }

        //Loads the victory layer
        victory = mapLoader.load(filename).getLayers().get("victory").getObjects();
        //Iterates through the victory objects and defines them as fixed sensors
        for (int i = 0 ; i < victory.getCount(); i++)
        {
            m = victory.get(i);
            Body body;
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.fixedRotation = true;
            bodyDef.position.set(0,0);
            body = world.createBody(bodyDef);

            FixtureDef fixtureDef = new FixtureDef();
            if (m instanceof RectangleMapObject)
                shape = getRect((RectangleMapObject) m);
            else if (m instanceof EllipseMapObject)
                shape = getEllipse((EllipseMapObject) m);
            else if (m instanceof PolylineMapObject)
                shape = getPolyline((PolylineMapObject) m);
            else if (m instanceof PolygonMapObject)
                shape= getPolygon((PolygonMapObject) m);
            else
            {
                throw (new ClassCastException("AAAAAAAAAH"));
            }

            fixtureDef.shape = shape;
            fixtureDef.isSensor = true;
            body.createFixture(fixtureDef);
            body.setUserData("victory");

            elevator = new Sprite(new Texture("Assets/Maps/elevator.png"));
            elevator.setPosition((Float)m.getProperties().get("X"), (Float)m.getProperties().get("Y"));
        }
    }

    /**
     * The getEllipse method converts the circle in an EllipseMapObject to a physics shape.
     * @param circObj The object to be converted.
     * @return The object as a CircleShape.
     */
    private CircleShape getEllipse(EllipseMapObject circObj) {
        Ellipse ellipse = circObj.getEllipse();
        CircleShape ell = new CircleShape();
        ell.setRadius(ellipse.width / 2 / 20f);
        ell.setPosition(new Vector2((ellipse.x + ell.getRadius()) / 20f, (ellipse.y + ell.getRadius()) / 20f));
        return ell;
    }

    /**
     * The getRect method converts the rectangle in a RectangleMapObject to a physics shape.
     * @param rectObj The object to be converted.
     * @return The object as a PolygonShape.
     */
    private PolygonShape getRect(RectangleMapObject rectObj) {
        Rectangle rect = rectObj.getRectangle();
        PolygonShape poly = new PolygonShape();
        Vector2 size = new Vector2((rect.x + rect.width * 0.5f) / 20f, (rect.y + rect.height * 0.5f) / 20f);
        poly.setAsBox(rect.width * 0.5f / 20f, rect.height * 0.5f / 20f, size, 0.0f);
        return poly;
    }
    /**
     * The getPolygon method converts the polygon in a PolygonMapObject to a physics shape.
     * @param polyObj The object to be converted.
     * @return The object as a PolygonShape.
     */
    private PolygonShape getPolygon (PolygonMapObject polyObj)
    {
        PolygonShape poly = new PolygonShape();
        float[] vertices = polyObj.getPolygon().getTransformedVertices();
        for (int i = 0 ; i < vertices.length ; ++i)
            vertices[i] /= B2DVars.PIXELS_TO_METERS;
        poly.set(vertices);
        return poly;
    }

    /**
     * The getPolyline method converts the polyline in a PolylineMapObject to a physics shape.
     * @param line The object to be converted.
     * @return The object as a ChainShape.
     */
    private ChainShape getPolyline(PolylineMapObject line) {
        float[] vertices = line.getPolyline().getTransformedVertices();
        Vector2[] vVertices = new Vector2[vertices.length / 2];
        for (int i = 0; i < vertices.length / 2; i++) {
            vVertices[i] = new Vector2();
            vVertices[i].x = vertices[i * 2] / B2DVars.PIXELS_TO_METERS;
            vVertices[i].y = vertices[i * 2 + 1] / B2DVars.PIXELS_TO_METERS;
        }
        ChainShape ch = new ChainShape();
        ch.createChain(vVertices);
        return ch;
    }

    /**
     * The getGuards method returns the list of guards.
     * @return The list of guards.
     */
    Guard[] getGuards() {
        return guards;
    }

    /**
     * The getRouters method returns the list of routers.
     * @return The list of routers.
     */
    Router[] getRouters() {
        return routers;
    }

    /**
     * Returns the elevator.
     * @return The elevator.
     */
    public Sprite getElevator() {
        return elevator;
    }
}