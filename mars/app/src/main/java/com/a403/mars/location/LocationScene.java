package com.a403.mars.location;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.a403.mars.location.rendering.LocationNode;
import com.a403.mars.location.sensor.DeviceOrientation;
import com.a403.mars.location.sensor.LocationManager;
import com.a403.mars.location.utils.LocationUtils;
import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.Pose;
import com.google.ar.core.Session;
import com.google.ar.sceneform.ArSceneView;

import java.util.ArrayList;

public class LocationScene {

    private String TAG = "LocationScene";
    public ArSceneView mArSceneView;
    public LocationManager locationManager;
    public DeviceOrientation deviceOrientation;
    public Context mContext;
    public Activity mActivity;
    public ArrayList<LocationMarker> mLocationMarkers = new ArrayList<>();
    // Anchors are currently re-drawn on an interval. There are likely better
    // ways of doing this, however it's sufficient for now. Anchor刷新间隔
    private int anchorRefreshInterval = 1000 * 5; // 5 seconds
    // Limit of where to draw markers within AR scene.
    // They will auto scale, but this helps prevents rendering issues 在AR Scene中绘制标识的距离范围
    private int distanceLimit = 20;
    private boolean offsetOverlapping = false;
    // Bearing adjustment. Can be set to calibrate with true north
    private int bearingAdjustment = 0;
    private boolean anchorsNeedRefresh = true;
    private boolean minimalRefreshing = false;
    private boolean refreshAnchorsAsLocationChanges = false;
    private Handler mHandler = new Handler();
    Runnable anchorRefreshTask = new Runnable() {
        @Override
        public void run() {
            anchorsNeedRefresh = true;
            mHandler.postDelayed(anchorRefreshTask, anchorRefreshInterval);
        }
    };
    private boolean debugEnabled = false;
    private Session mSession;
    public LocationScene(Context mContext, Activity mActivity, ArSceneView mArSceneView) {
        Log.i(TAG, "Location Scene initiated.");
        this.mContext = mContext;
        this.mActivity = mActivity;
        this.mSession = mArSceneView.getSession();
        this.mArSceneView = mArSceneView;

        startCalculationTask();

        locationManager = new LocationManager(mContext);
        deviceOrientation = new DeviceOrientation(this);
        deviceOrientation.resume();
    }

    public boolean isDebugEnabled() {
        return debugEnabled;
    }

    public void setDebugEnabled(boolean debugEnabled) {
        this.debugEnabled = debugEnabled;
    }

    public boolean minimalRefreshing() {
        return minimalRefreshing;
    }

    public void setMinimalRefreshing(boolean minimalRefreshing) {
        this.minimalRefreshing = minimalRefreshing;
    }

    public boolean refreshAnchorsAsLocationChanges() {
        return refreshAnchorsAsLocationChanges;
    }

    public void setRefreshAnchorsAsLocationChanges(boolean refreshAnchorsAsLocationChanges) {
        if (refreshAnchorsAsLocationChanges) {
            stopCalculationTask();
        } else {
            startCalculationTask();
        }
        refreshAnchors();
        this.refreshAnchorsAsLocationChanges = refreshAnchorsAsLocationChanges;
    }

    public int getAnchorRefreshInterval() {
        return anchorRefreshInterval;
    }

    /**
     * Set the interval at which anchors should be automatically re-calculated.
     *
     * @param anchorRefreshInterval
     */
    public void setAnchorRefreshInterval(int anchorRefreshInterval) {
        this.anchorRefreshInterval = anchorRefreshInterval;
        stopCalculationTask();
        startCalculationTask();
    }

    public void clearMarkers() {
        for (LocationMarker lm : mLocationMarkers) {
            if (lm.anchorNode != null) {
                lm.anchorNode.getAnchor().detach();
                lm.anchorNode.setEnabled(false);
                lm.anchorNode = null;
            }
        }
        mLocationMarkers = new ArrayList<>();
    }

    /**
     * The distance cap for distant markers.
     * ARCore doesn't like markers that are 2000km away :/
     *
     * @return
     */
    public int getDistanceLimit() {
        return distanceLimit;
    }

    /**
     * The distance cap for distant markers.
     * ARCore doesn't like markers that are 2000km away :/
     * Default 20
     */
    public void setDistanceLimit(int distanceLimit) {
        this.distanceLimit = distanceLimit;
    }

    public boolean shouldOffsetOverlapping() {
        return offsetOverlapping;
    }

    /**
     * Attempts to raise markers vertically when they overlap.
     * Needs work!
     *
     * @param offsetOverlapping
     */
    public void setOffsetOverlapping(boolean offsetOverlapping) {
        this.offsetOverlapping = offsetOverlapping;
    }

    public void processFrame(Frame frame) {
        refreshAnchorsIfRequired(frame);
    }

    /**
     * Force anchors to be re-calculated
     */
    public void refreshAnchors() {
        anchorsNeedRefresh = true;
    }

    private void refreshAnchorsIfRequired(Frame frame) {
        if (anchorsNeedRefresh) {
            Log.i(TAG, "Refreshing anchors...");
            anchorsNeedRefresh = false;

            if (locationManager.currentLocation == null) {
                Log.i(TAG, "Location not yet established.");
                return;
            }

            for (int i = 0; i < mLocationMarkers.size(); i++) {
                try {

                    int markerDistance = (int) Math.round(
                            LocationUtils.distance(
                                    mLocationMarkers.get(i).latitude,
                                    locationManager.currentLocation.getLatitude(),
                                    mLocationMarkers.get(i).longitude,
                                    locationManager.currentLocation.getLongitude())
                    );

                    if (markerDistance > mLocationMarkers.get(i).getOnlyRenderWhenWithin()) {
                        // Don't render if this has been set and we are too far away.
                        Log.i(TAG, "Not rendering. Marker distance: " + markerDistance + " Max render distance: " + mLocationMarkers.get(i).getOnlyRenderWhenWithin());
                        continue;
                    }

                    float markerBearing = deviceOrientation.currentDegree + (float) LocationUtils.bearing(
                            locationManager.currentLocation.getLatitude(),
                            locationManager.currentLocation.getLongitude(),
                            mLocationMarkers.get(i).latitude,
                            mLocationMarkers.get(i).longitude);

                    // Bearing adjustment can be set if you are trying to
                    // correct the heading of north - setBearingAdjustment(10)
                    markerBearing = markerBearing + bearingAdjustment;
                    markerBearing = markerBearing % 360;

                    double rotation = Math.floor(markerBearing);

                    // When pointing device upwards (camera towards sky)
                    // the compass bearing can flip.
                    // In experiments this seems to happen at pitch~=-25
                    if (deviceOrientation.pitch > -25)
                        rotation = rotation * Math.PI / 180;

                    int renderDistance = markerDistance;

                    // Limit the distance of the Anchor within the scene.
                    // Prevents rendering issues.
                    if (renderDistance > distanceLimit)
                        renderDistance = distanceLimit;

                    // Adjustment to add markers on horizon, instead of just directly in front of camera
                    double heightAdjustment = 0;
                    // Math.round(renderDistance * (Math.tan(Math.toRadians(deviceOrientation.pitch)))) - 1.5F;

                    // Raise distant markers for better illusion of distance
                    // Hacky - but it works as a temporary measure
                    int cappedRealDistance = markerDistance > 500 ? 500 : markerDistance;
                    if (renderDistance != markerDistance)
                        heightAdjustment += 0.005F * (cappedRealDistance - renderDistance);

                    float x = 0;
                    float z = -renderDistance;

                    float zRotated = (float) (z * Math.cos(rotation) - x * Math.sin(rotation));
                    float xRotated = (float) -(z * Math.sin(rotation) + x * Math.cos(rotation));

                    // Current camera height
                    float y = frame.getCamera().getDisplayOrientedPose().ty();

                    if (mLocationMarkers.get(i).anchorNode != null &&
                            mLocationMarkers.get(i).anchorNode.getAnchor() != null) {
                        mLocationMarkers.get(i).anchorNode.getAnchor().detach();
                        mLocationMarkers.get(i).anchorNode.setAnchor(null);
                        mLocationMarkers.get(i).anchorNode.setEnabled(false);
                        mLocationMarkers.get(i).anchorNode = null;
                    }

                    // Don't immediately assign newly created anchor in-case of exceptions
                    Anchor newAnchor = mSession.createAnchor(
                            frame.getCamera().getPose()
                                    .compose(Pose.makeTranslation(xRotated, y + (float) heightAdjustment, zRotated)));


                    mLocationMarkers.get(i).anchorNode = new LocationNode(newAnchor, mLocationMarkers.get(i), this);
                    mLocationMarkers.get(i).anchorNode.setParent(mArSceneView.getScene());
                    mLocationMarkers.get(i).anchorNode.addChild(mLocationMarkers.get(i).node);

                    if (mLocationMarkers.get(i).getRenderEvent() != null) {
                        mLocationMarkers.get(i).anchorNode.setRenderEvent(mLocationMarkers.get(i).getRenderEvent());
                    }

                    mLocationMarkers.get(i).anchorNode.setScaleModifier(mLocationMarkers.get(i).getScaleModifier());
                    mLocationMarkers.get(i).anchorNode.setScalingMode(mLocationMarkers.get(i).getScalingMode());
                    mLocationMarkers.get(i).anchorNode.setGradualScalingMaxScale(mLocationMarkers.get(i).getGradualScalingMaxScale());
                    mLocationMarkers.get(i).anchorNode.setGradualScalingMinScale(mLocationMarkers.get(i).getGradualScalingMinScale());
                    mLocationMarkers.get(i).anchorNode.setHeight(mLocationMarkers.get(i).getHeight());

                    if (minimalRefreshing)
                        mLocationMarkers.get(i).anchorNode.scaleAndRotate();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            System.gc();
        }
    }

    /**
     * Adjustment for compass bearing.
     *
     * @return
     */
    public int getBearingAdjustment() {
        return bearingAdjustment;
    }

    /**
     * Adjustment for compass bearing.
     * You may use this for a custom method of improving precision.
     *
     * @param i
     */
    public void setBearingAdjustment(int i) {
        bearingAdjustment = i;
        anchorsNeedRefresh = true;
    }

    /**
     * Resume sensor services. Important!
     */
    public void resume() {
        deviceOrientation.resume();
    }

    /**
     * Pause sensor services. Important!
     */
    public void pause() {
        deviceOrientation.pause();
    }

    void startCalculationTask() {
        anchorRefreshTask.run();
    }

    void stopCalculationTask() {
        mHandler.removeCallbacks(anchorRefreshTask);
    }
}
