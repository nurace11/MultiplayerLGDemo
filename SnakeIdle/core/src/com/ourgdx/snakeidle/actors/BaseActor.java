package com.ourgdx.snakeidle.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.math.Intersector.MinimumTranslationVector;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;

public class BaseActor extends Group {
    private static Rectangle worldBounds;

    protected Vector2 position;

    protected Animation<TextureRegion> animation;
    protected float elapsedTime;
    private boolean animationPaused;

    private Polygon boundaryPolygon;

    public BaseActor(float x, float y, Stage s){
        super();

        this.position = new Vector2(x - getWidth()/2, y - getHeight()/2);
        setPosition(position.x, position.y);

        animation = null;
        elapsedTime = 0;
        animationPaused = false;

        s.addActor(this);
    }

    @Override
    public void act(float dt) {
        super.act(dt);
        setPosition(position.x, position.y);

        if (!animationPaused){
            elapsedTime += dt;
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        Color c = getColor();
        batch.setColor(c.r, c.g, c.b, c.a);

        if(animation != null && isVisible()){
            batch.draw(animation.getKeyFrame(elapsedTime),
                    position.x, position.y, getOriginX(), getOriginY(),
                    getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
        }
        //batch.draw(texture, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY());
    }

    public void die(){
        this.remove();
    }

    public void setVector(float x, float y){
        position.x = x;
        position.y = y;
    }

    public static void setWorldBounds(float width, float height){
        worldBounds = new Rectangle(0, 0, width, height);
    }

    public static void setWorldBounds(BaseActor other){
        setWorldBounds(other.getWidth(), other.getHeight());
    }

    public void boundToWorld(){
        if (position.x < 0)
            position.x = 0;

        if(position.x + getWidth() > worldBounds.width)
            position.x = (worldBounds.width - getWidth());

        if(position.y < 0)
            position.y = 0;

        if(position.y + getHeight() > worldBounds.height)
            position.y = (worldBounds.height - getHeight());
    }

    public void setBoundaryRectangle(){
        float w = getWidth();
        float h = getHeight();
        float[] vertices = {0,0, w,0, w,h, 0,h};
        boundaryPolygon = new Polygon(vertices);
    }

    public void setBoundaryPolygon(int numSides){
        float w = getWidth();
        float h = getHeight();

        float[] vertices = new float[2*numSides];
        for(int i = 0; i < numSides; i++){
            float angle = i * 6.28f / numSides;
            vertices[2*i] = w/2 * MathUtils.cos(angle) + w/2;
            vertices[2*i+1] = h/2 * MathUtils.sin(angle)+ h/2;
        }

        boundaryPolygon = new Polygon(vertices);
    }

    public Polygon getBoundaryPolygon(){
        boundaryPolygon.setPosition( position.x, position.y);
        boundaryPolygon.setOrigin( getOriginX(), getOriginY());
        boundaryPolygon.setRotation( getRotation());
        boundaryPolygon.setScale( getScaleX(), getScaleY());
        return boundaryPolygon;
    }

    public boolean overlaps(BaseActor other){
        Polygon poly1 = this.getBoundaryPolygon();
        Polygon poly2 = other.getBoundaryPolygon();

        if ( !poly1.getBoundingRectangle().overlaps(poly2.getBoundingRectangle()))
            return false;

        return Intersector.overlapConvexPolygons( poly1, poly2 );
    }

    public Vector2 preventOverlap(BaseActor other)
    {
        Polygon poly1 = this.getBoundaryPolygon();
        Polygon poly2 = other.getBoundaryPolygon();
        // initial test to improve performance
        if ( !poly1.getBoundingRectangle().overlaps(poly2.getBoundingRectangle()) )
            return null;
        Intersector.MinimumTranslationVector mtv = new Intersector.MinimumTranslationVector();
        boolean polygonOverlap = Intersector.overlapConvexPolygons(poly1, poly2, mtv);
        if ( !polygonOverlap )
            return null;
        //position.add(mtv.normal.x * mtv.depth, mtv.normal.y * mtv.depth);
        if (mtv.normal.x * mtv.depth != 0 || mtv.normal.y * mtv.depth != 0) {
            this.position.x += mtv.normal.x * mtv.depth;
            this.position.y += mtv.normal.y * mtv.depth;
            positionChanged();
        }
        //this.position.x += mtv.normal.x * mtv.depth;
        //this.position.y += mtv.normal.y * mtv.depth;
        this.moveBy( mtv.normal.x * mtv.depth, mtv.normal.y * mtv.depth );
        return mtv.normal;
    }

    public void alignCamera(){
        Camera cam = this.getStage().getCamera();
        Viewport v = this.getStage().getViewport();

        cam.viewportHeight = Gdx.graphics.getHeight(); //Gdx.graphics.getHeight() / 1.2f;
        cam.viewportWidth= Gdx.graphics.getWidth();//Gdx.graphics.getWidth() / 1.2f;

        //cam.position.set( this.getX() + this.getOriginX(), this.getY() + this.getOriginY(), 0);
        cam.position.set( this.position.x + this.getOriginX(), this.position.y + this.getOriginY(), 0);

        cam.position.x = MathUtils.clamp(cam.position.x, cam.viewportWidth/2, worldBounds.width - cam.viewportWidth/2);
        cam.position.y = MathUtils.clamp(cam.position.y, cam.viewportHeight/2, worldBounds.height - cam.viewportHeight/2);

        cam.update();
    }

    public void centerAtPosition(float x, float y){
        setPosition( x - getWidth() / 2, y - getHeight()/2);
    }

    public void centerAtActor(BaseActor other){
        centerAtPosition(other.position.x + other.getWidth()/2, other.position.y + other.getHeight()/2);
    }

    public void setOpacity(float opacity){
        this.getColor().a = opacity;
    }

    public void setAnimation(Animation<TextureRegion> anim){
        animation = anim;
        TextureRegion tr = animation.getKeyFrame(0);
        float w = tr.getRegionWidth();
        float h = tr.getRegionHeight();
        setSize( w, h );
        setOrigin( w/2, h/2);

        if(boundaryPolygon == null){
            setBoundaryRectangle();
        }
    }

    public void setAnimationPaused(boolean pause){
        animationPaused = pause;
    }

    public Animation<TextureRegion> loadAnimationFromFiles(String[] fileNames, float frameDuration, boolean loop){
        int fileCount = fileNames.length;
        Array<TextureRegion> textureArray = new Array<>();

        for(int n = 0; n < fileCount; n++){
            String fileName = fileNames[n];
            Texture texture = new Texture( Gdx.files.internal(fileName) );
            texture.setFilter( Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            textureArray.add( new TextureRegion( texture ));
        }

        Animation<TextureRegion> anim = new Animation<>(frameDuration, textureArray);

        if(loop)
            anim.setPlayMode(Animation.PlayMode.LOOP);
        else
            anim.setPlayMode(Animation.PlayMode.NORMAL);

        if( animation == null)
            setAnimation(anim);

        return anim;
    }

    public Animation<TextureRegion> loadTexture(String fileName){
        String[] fileNames = new String[1];
        fileNames[0] = fileName;
        return loadAnimationFromFiles(fileNames, 1, true);
    }

    public Vector2 getPosition(){
        return position;
    }
}
