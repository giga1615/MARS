package com.a403.mars.location;

import com.a403.mars.location.rendering.LocationNode;
import com.a403.mars.location.rendering.LocationNodeRender;
import com.google.ar.sceneform.Node;

public class LocationMarker {
    // Location in real-world terms 实地经纬度
    public double longitude;
    public double latitude;

    // Location in AR terms AR中的位置结点
    public LocationNode anchorNode;

    // Node to render AR要渲染的结点
    public Node node;

    // Called on each frame if not null AR中的渲染事件
    private LocationNodeRender renderEvent;
    private float scaleModifier = 1F;
    private float height = 0F;
    private int onlyRenderWhenWithin = Integer.MAX_VALUE;
    private ScalingMode scalingMode = ScalingMode.FIXED_SIZE_ON_SCREEN;
    private float gradualScalingMinScale = 0.8F;
    private float gradualScalingMaxScale = 1.4F;

    public LocationMarker(double longitude, double latitude, Node node) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.node = node;
    }

    public float getGradualScalingMinScale() {
        return gradualScalingMinScale;
    }

    public void setGradualScalingMinScale(float gradualScalingMinScale) {
        this.gradualScalingMinScale = gradualScalingMinScale;
    }

    public float getGradualScalingMaxScale() {
        return gradualScalingMaxScale;
    }

    public void setGradualScalingMaxScale(float gradualScalingMaxScale) {
        this.gradualScalingMaxScale = gradualScalingMaxScale;
    }

    /**
     * Only render this marker when within [onlyRenderWhenWithin] metres
     *
     * @return - metres or -1
     */
    public int getOnlyRenderWhenWithin() {
        return onlyRenderWhenWithin;
    }

    /**
     * Only render this marker when within [onlyRenderWhenWithin] metres
     *
     * @param onlyRenderWhenWithin - metres
     */
    public void setOnlyRenderWhenWithin(int onlyRenderWhenWithin) {
        this.onlyRenderWhenWithin = onlyRenderWhenWithin;
    }

    /**
     * Height based on camera height
     *
     * @return - height in metres
     */
    public float getHeight() {
        return height;
    }

    /**
     * Height based on camera height
     *
     * @param height - height in metres
     */
    public void setHeight(float height) {
        this.height = height;
    }

    /**
     * How the markers should scale
     *
     * @return - ScalingMode
     */
    public ScalingMode getScalingMode() {
        return scalingMode;
    }

    /**
     * Whether the marker should scale, regardless of distance.
     *
     * @param scalingMode - ScalingMode.X
     */
    public void setScalingMode(ScalingMode scalingMode) {
        this.scalingMode = scalingMode;
    }

    /**
     * Scale multiplier
     *
     * @return - multiplier
     */
    public float getScaleModifier() {
        return scaleModifier;
    }

    /**
     * Scale multiplier
     *
     * @param scaleModifier - multiplier
     */
    public void setScaleModifier(float scaleModifier) {
        this.scaleModifier = scaleModifier;
    }

    /**
     * Called on each frame
     *
     * @return - LocationNodeRender (event)
     */
    public LocationNodeRender getRenderEvent() {
        return renderEvent;
    }

    /**
     * Called on each frame.
     */
    public void setRenderEvent(LocationNodeRender renderEvent) {
        this.renderEvent = renderEvent;
    }

    public enum ScalingMode {
        FIXED_SIZE_ON_SCREEN,
        NO_SCALING,
        GRADUAL_TO_MAX_RENDER_DISTANCE
    }
}
